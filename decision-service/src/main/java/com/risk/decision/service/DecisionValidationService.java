package com.risk.decision.service;

import com.risk.decision.dto.DecisionRequest;
import com.risk.decision.dto.FactorParams;
import com.risk.decision.model.CalculationMethod;
import com.risk.decision.repository.CalculationMethodRepository;
import com.risk.enums.FactorClassification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DecisionValidationService {

    public void validateFactorDataConsistency(DecisionRequest request) {

        // 1. Створення мапи для швидкого доступу: Factor NAME -> FactorClassification
        // Ми використовуємо name як унікальний ключ.
        Map<String, FactorClassification> factorTypeMap = request.factorParams().stream()
                .collect(Collectors.toMap(FactorParams::name, FactorParams::type));

        // 2. Ітерація та перевірка узгодженості
        request.alternatives().stream()
                .flatMap(alternative -> alternative.values().stream())
                .forEach(eval -> {

                    String factorName = eval.factorName();
                    FactorClassification type = factorTypeMap.get(factorName);

                    // Перевірка, чи взагалі існує фактор з таким ім'ям
                    if (type == null) {
                        throw new IllegalArgumentException("Factor name '" + factorName + "' found in evaluation values is missing in factor parameters.");
                    }

                    boolean rawValueProvided = eval.rawValue() != null;
                    boolean scoreProvided = eval.score() != null;

                    // Ми припускаємо, що XOR вже пройшов, і перевіряємо, чи відповідає ТИП наданому полю.

                    // Правило: Об'єктивний фактор (вимагає rawValue)
                    if (type == FactorClassification.OBJECTIVE && !rawValueProvided) {
                        // Якщо тип OBJECTIVE, але надано score (або обидва null, але це порушить XOR)
                        throw new IllegalArgumentException(
                                "Фактор '" + factorName + "' є Об'єктивним, але відсутнє 'rawValue' (або надано 'score')."
                        );
                    }

                    // Правило: Суб'єктивний фактор (вимагає score)
                    if (type == FactorClassification.SUBJECTIVE && !scoreProvided) {
                        // Якщо тип SUBJECTIVE, але надано rawValue (або обидва null)
                        throw new IllegalArgumentException(
                                "Фактор '" + factorName + "' є Суб'єктивним, але відсутній 'score' (або надано 'rawValue')."
                        );
                    }
                });
    }


    public CalculationMethod validateMethodByName(CalculationMethodRepository calculationMethodRepository,
                                                  DecisionRequest request){
        return calculationMethodRepository.findByName(request.method().toString())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Calculation method '" + request.method() + "' is not supported."
                ));
    }
}
