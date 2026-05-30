package com.risk.decision.service;

import com.risk.decision.model.Alternative;
import com.risk.decision.repository.AlternativeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AlternativeService {

    private final AlternativeRepository alternativeRepository;

    public AlternativeService(AlternativeRepository alternativeRepository) {
        this.alternativeRepository = alternativeRepository;
    }

    public Map<Integer, String> findNamesByIds(List<Integer> ids) {
        return alternativeRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Alternative::getId, Alternative::getName));
    }

    public Map<Integer, String> preloadAlternativeNames(List<Integer> ids) {
        return findNamesByIds(ids);
    }
}
