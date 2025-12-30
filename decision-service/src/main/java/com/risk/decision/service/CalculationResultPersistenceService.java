package com.risk.decision.service;


import com.risk.dto.AlternativeResult;
import com.risk.dto.CalculationResponse;
import com.risk.decision.model.CalculationResult;
import com.risk.decision.model.Decision;
import com.risk.decision.repository.AlternativeRepository;
import com.risk.decision.repository.CalculationResultRepository;
import com.risk.decision.repository.DecisionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalculationResultPersistenceService {

    private final DecisionRepository decisionRepository;
    private final AlternativeRepository alternativeRepository;
    private final CalculationResultRepository calculationResultRepository;

    // Ми не будемо впроваджувати DecisionResultProcessor тут,
    // оскільки цей сервіс відповідає лише за ЗБЕРЕЖЕННЯ.

    /**
     * Перетворює CalculationResponse DTO на CalculationResult Entity та зберігає їх у БД.
     * @param response DTO, отриманий від Calculation Service.
     * @return Список збережених CalculationResult Entity.
     */
    @Transactional
    public List<CalculationResult> persistResults(CalculationResponse response) {

        // 1. Пошук Decision Entity (обов'язково для зв'язку)
        Decision decision = decisionRepository.findById(response.decisionId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Decision with ID " + response.decisionId() + " not found for saving results."
                ));

        // 2. Пошук Alternative Entities (оптимізація: шукаємо всі альтернативи одразу)
        List<Integer> alternativeIds = response.results().stream()
                .map(AlternativeResult::alternativeId)
                .toList();

        // Створюємо мапу ID -> Alternative для швидкого доступу
        Map<Integer, com.risk.decision.model.Alternative> alternativeMap = alternativeRepository.findAllById(alternativeIds)
                .stream()
                .collect(Collectors.toMap(com.risk.decision.model.Alternative::getId, alt -> alt));

        if (alternativeMap.size() != alternativeIds.size()) {
            // Це свідчить про те, що деякі Alternative ID з DTO не були знайдені в БД.
            throw new IllegalStateException("One or more Alternative IDs in the calculation response were not found in the database.");
        }

        ZonedDateTime calculatedAt = response.calculatedAt() != null ? response.calculatedAt() : ZonedDateTime.now();
        List<CalculationResult> resultsToSave = new ArrayList<>();

        // 3. Мапінг та збирання CalculationResult Entities
        for (AlternativeResult altResult : response.results()) {

            com.risk.decision.model.Alternative alternative = alternativeMap.get(altResult.alternativeId());

            CalculationResult entity = CalculationResult.builder()
                    // Decision та Alternative встановлюються як об'єкти
                    .decision(decision)
                    .alternative(alternative)

                    .weightedScore(altResult.weightedScore())
                    .riskAdjustedScore(altResult.riskAdjustedScore())
                    // Поле riskLevel залишається null, оскільки воно обчислюється пізніше
                    .riskLevel(null)
                    .calculatedAt(calculatedAt)
                    .build();

            resultsToSave.add(entity);

            // Встановлюємо зворотний зв'язок на Alternative Entity (не обов'язково для збереження,
            // але корисно для повноти ORM-графу, якщо транзакція триває)
            alternative.setCalculationResult(entity);
        }

        // 4. Збереження всіх результатів
        return calculationResultRepository.saveAll(resultsToSave);
    }
}
