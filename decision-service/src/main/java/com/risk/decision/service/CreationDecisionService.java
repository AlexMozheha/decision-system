package com.risk.decision.service;


import com.risk.decision.dto.CreateDecisionRequest;
import com.risk.decision.dto.DecisionDraftResponse;
import com.risk.decision.model.Alternative;
import com.risk.decision.model.Decision;
import com.risk.decision.model.Status;
import com.risk.decision.repository.DecisionMapper;
import com.risk.decision.repository.DecisionRepository;
import com.risk.decision.repository.StatusRepository;
import com.risk.enums.DecisionStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreationDecisionService {

    private final DecisionRepository decisionRepository;
    private final StatusRepository statusRepository;
    private final DecisionMapper decisionMapper;

    @Transactional
    public Integer saveDecision(CreateDecisionRequest request, Integer userId) {
        Decision decision;

        if (request.id() != null) {
            decision = decisionRepository.findById(request.id())
                    .orElseThrow(() -> new EntityNotFoundException("Чернетку не знайдено"));

            if (decision.getStatus().getName() == DecisionStatus.CALCULATING) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "ALREADY_CALCULATING");
            }

            if (decision.getStatus().getName() == DecisionStatus.CALCULATED) {
                throw new IllegalStateException("Не можна редагувать розраховане рішення");
            }

            if (!decision.getUserId().equals(userId)) {
                throw new AccessDeniedException("Немає доступу до цієї чернетки");
            }

            if (decision.getAlternatives() != null) {
                decision.getAlternatives().forEach(alt -> alt.setDecision(null));
                decision.getAlternatives().clear();
            } else {
                decision.setAlternatives(new ArrayList<>());
            }
        } else {
            decision = new Decision();
            Status draftStatus = statusRepository.findByName(DecisionStatus.DRAFT)
                    .orElseThrow(() -> new RuntimeException("Status DRAFT not found"));
            decision.setStatus(draftStatus);
            decision.setCreatedAt(ZonedDateTime.now());
            decision.setUserId(userId);
            decision.setMaxScore(new BigDecimal("100.00"));
            decision.setAlternatives(new ArrayList<>());
        }

        decision.setName(request.name());
        decision.setComment(request.comment());

        final Decision finalDecision = decision;
        List<Alternative> newAlternatives = request.alternatives().stream()
                .map(dto -> {
                    Alternative alt = new Alternative();
                    alt.setName(dto.name());
                    BigDecimal riskPercent = BigDecimal.valueOf(dto.riskCoefficient())
                            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                    alt.setRiskCoefficient(riskPercent);
                    alt.setDecision(finalDecision);
                    return alt;
                }).toList();

        decision.getAlternatives().addAll(newAlternatives);

        Decision saved = decisionRepository.save(decision);
        return saved.getId();
    }

    @Transactional(readOnly = true)
    public DecisionDraftResponse getDecisionDraft(Integer decisionId, Integer currentUserId) {
        Decision decision = decisionRepository.findAlternativesByDecisionId(decisionId)
                .orElseThrow(() -> new EntityNotFoundException("Decision not found"));

        if (decision.getStatus().getName() == DecisionStatus.CALCULATING) {
            throw new AccessDeniedException("CALCULATING_NOW");
        }

        if (decision.getStatus().getName() == DecisionStatus.CALCULATED) {
            throw new IllegalStateException("Cannot edit a decision that is already calculated");
        }

        if (!decision.getUserId().equals(currentUserId)) {
            throw new AccessDeniedException("You don't have permission to access this decision");
        }

        return decisionMapper.toDraftResponse(decision);
    }



}
