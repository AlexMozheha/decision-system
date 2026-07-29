package com.risk.decision.service;


import com.risk.api.dto.UserResponse;
import com.risk.decision.client.UserServiceClient;
import com.risk.decision.dto.AdminScenarioDto;
import com.risk.decision.dto.OrderToCalculation;
import com.risk.decision.model.Decision;
import com.risk.decision.model.Status;
import com.risk.decision.repository.DecisionMapper;
import com.risk.decision.repository.DecisionRepository;
import com.risk.decision.repository.StatusRepository;
import com.risk.enums.DecisionStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardService {

    private final UserServiceClient userClient;
    private final StatusRepository statusRepository;
    private final DecisionRepository decisionRepository;
    private final DecisionMapper decisionMapper;

    @Transactional(readOnly = true)
    public Page<AdminScenarioDto> getAllScenariosForAdmin(String search, DecisionStatus filterStatus, Pageable pageable) {

        List<Integer> searchedUserIds = null;
        List<DecisionStatus> statusesForQuery;
        Map<Integer, String> localUserCache = java.util.Collections.emptyMap();

        String sanitizedSearch = (search != null) ? search.strip() : null;

        if (sanitizedSearch != null && !sanitizedSearch.isEmpty()) {
            try {
                List<UserResponse> users = userClient.getUserByName(sanitizedSearch);
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

        if (filterStatus != null) {
            if (filterStatus == DecisionStatus.READY_FOR_CALCULATION) {
                statusesForQuery = List.of(DecisionStatus.READY_FOR_CALCULATION, DecisionStatus.DRAFT);
            } else {
                statusesForQuery = List.of(filterStatus);
            }
        } else {
            statusesForQuery = null;
        }

        Page<Decision> decisionPage = decisionRepository.findAllScenariosForAdmin(sanitizedSearch, statusesForQuery, searchedUserIds, pageable);

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

            String statusNameToFrontend = decision.getStatus().getName().name();
            if (decision.getStatus().getName() == DecisionStatus.DRAFT) {
                statusNameToFrontend = DecisionStatus.READY_FOR_CALCULATION.name();
            }

            return new AdminScenarioDto(
                    decision.getId(),
                    decision.getName(),
                    investorName,
                    decision.getCreatedAt(),
                    statusNameToFrontend,
                    calculatedAt
            );
        });
    }

    @Transactional
    public void lockScenarioForCalculation(Integer decisionId) {
        Decision decision = decisionRepository.findById(decisionId)
                .orElseThrow(() -> new EntityNotFoundException("Decision not found"));

        if (decision.getStatus().getName() == DecisionStatus.CALCULATED) {
            throw new IllegalStateException("Decision is already calculated");
        }

        Status calculatingStatus = statusRepository.findByName(DecisionStatus.CALCULATING)
                .orElseThrow(() -> new RuntimeException("Status CALCULATING not found"));

        decision.setStatus(calculatingStatus);
        decisionRepository.save(decision);
    }


    @Transactional(readOnly = true)
    public OrderToCalculation getDecisionOrder(Integer decisionId) {
        Decision decision = decisionRepository.findAlternativesByDecisionId(decisionId)
                .orElseThrow(() -> new EntityNotFoundException("Decision not found"));


        if (decision.getStatus().getName() == DecisionStatus.CALCULATED) {
            throw new IllegalStateException("Cannot edit a decision that is already calculated");
        }

        String investorName = null;
        String investorEmail = null;
        try {
            UserResponse user = userClient.getUserById(decision.getUserId());
            if (user != null) {
                investorName = user.fullName();
                investorEmail = user.email();
            }
        } catch (Exception e) {
            log.warn("Could not fetch investor info for userId {}: {}", decision.getUserId(), e.getMessage());
        }

        String statusName = decision.getStatus().getName().name();

        return decisionMapper.toOrderToCalculation(decision, investorName, investorEmail, statusName);
    }



}
