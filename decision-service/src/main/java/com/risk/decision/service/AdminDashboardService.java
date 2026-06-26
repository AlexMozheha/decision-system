package com.risk.decision.service;


import com.risk.api.dto.UserResponse;
import com.risk.decision.client.UserServiceClient;
import com.risk.decision.dto.AdminScenarioDto;
import com.risk.decision.model.Decision;
import com.risk.decision.repository.DecisionRepository;
import com.risk.enums.DecisionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final DecisionRepository repository;
    private final UserServiceClient userClient;

    public Page<AdminScenarioDto> getAllScenariosForAdmin(String search, DecisionStatus status, Pageable pageable) {

        List<Integer> searchedUserIds = null;
        Map<Integer, String> localUserCache = java.util.Collections.emptyMap();

        if (search != null && !search.isBlank()) {
            try {
                List<UserResponse> users = userClient.getUserByName(search);
                if (users != null && !users.isEmpty()) {
                    searchedUserIds = users.stream().map(UserResponse::id).toList();

                    localUserCache = users.stream().collect(Collectors.toMap(
                            UserResponse::id,
                            UserResponse::fullName
                    ));
                } else {
                    searchedUserIds = List.of(-1);
                }
            } catch (Exception e) {
                searchedUserIds = null;
            }
        }

        Page<Decision> decisionPage = repository.findAllScenariosForAdmin(search, status, searchedUserIds, pageable);

        if (localUserCache.isEmpty()) {
            List<Integer> idsOnCurrentPage = decisionPage.getContent().stream()
                    .map(Decision::getUserId)
                    .distinct()
                    .toList();

            try {
                localUserCache = userClient.getUsersNamesByIds(idsOnCurrentPage);
            } catch (Exception e) {
                localUserCache = java.util.Collections.emptyMap();
            }
        }

        final Map<Integer, String> finalUserMap = localUserCache;

        return decisionPage.map(decision -> {
            ZonedDateTime calculatedAt = null;
            if (decision.getStatus().getName() == DecisionStatus.CALCULATED
                    && decision.getCalculationResults() != null
                    && !decision.getCalculationResults().isEmpty()) {
                calculatedAt = decision.getCalculationResults().getFirst().getCalculatedAt();
            }

            String investorName = Optional.ofNullable(finalUserMap.get(decision.getUserId()))
                    .orElse("Unknown");

            return new AdminScenarioDto(
                    decision.getId(),
                    decision.getName(),
                    investorName,
                    decision.getCreatedAt(),
                    decision.getStatus().getName().name(),
                    calculatedAt
            );
        });
    }

}
