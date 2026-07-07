package com.risk.decision.service;


import com.risk.decision.dto.AlternativeResponse;
import com.risk.decision.model.CalculationResult;
import com.risk.decision.model.Decision;
import com.risk.decision.model.Report;
import com.risk.decision.repository.CalculationResultRepository;
import com.risk.decision.repository.DecisionRepository;
import com.risk.decision.repository.ReportRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final DecisionRepository decisionRepository;
    private final CalculationResultRepository calculationResultRepository;
    private final DecisionResultProcessor decisionResultProcessor;

    private final SpringTemplateEngine templateEngine;

    private static final String UPLOAD_DIR = "uploads/reports/";

    @Transactional
    public void generateAndSaveReport(Integer decisionId, boolean isUk) {

        Decision decision = decisionRepository.findById(decisionId).orElseThrow(() -> new EntityNotFoundException("Decision not found"));
        List<CalculationResult> results = calculationResultRepository.findResultsWithAlternativeByDecisionId(decisionId);

        if (results.isEmpty()) {
            throw new IllegalStateException("Cannot generate report: No calculation results found for this decision. Run calculation first.");
        }

        try{
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if(!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }

            String fileName = "report_decision_" + decisionId + "_" + System.currentTimeMillis() + ".pdf";
            Path filePath = uploadPath.resolve(fileName);

            BigDecimal maxScore = results.stream()
                    .map(CalculationResult::getRiskAdjustedScore)
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);

            BigDecimal threshold = maxScore.multiply(new BigDecimal("0.70"));

            List<AlternativeResponse> altResponses = results.stream()
                    .map(r -> new AlternativeResponse(
                            r.getAlternative().getName(),
                            r.getWeightedScore(),
                            r.getRiskAdjustedScore(),
                            r.getRiskLevel(),
                            r.getRiskAdjustedScore().compareTo(threshold) >= 0,
                            Collections.emptyMap()
                    ))
                    .sorted(Comparator.comparing(AlternativeResponse::riskAdjustedScore).reversed())
                    .toList();

            List<String> rawRecommendations = decisionResultProcessor.generateAnalyticalSummaryList(altResponses, isUk);

            List<String> cleanRecommendations = rawRecommendations.stream()
                    .map(text -> text.replace("**", ""))
                    .toList();

            Context context = new Context();
            context.setVariable("isUk", isUk);
            context.setVariable("decisionName", decision.getName());

            ZonedDateTime calcTime = results.get(0).getCalculatedAt();
            context.setVariable("calculatedAt", calcTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            context.setVariable("alternatives", altResponses);
            context.setVariable("recommendationTexts", cleanRecommendations);

            String htmlContent = templateEngine.process("report", context);

            try (OutputStream os = new FileOutputStream(filePath.toFile())) {
                ITextRenderer renderer = new ITextRenderer();
                renderer.setDocumentFromString(htmlContent);
                renderer.layout();
                renderer.createPDF(os);
            }

            Report report = reportRepository.findByDecision_Id(decisionId)
                    .orElse(Report.builder().decision(decision).build());

            report.setFilePath(filePath.toString());
            report.setCreatedAt(ZonedDateTime.now());

            reportRepository.save(report);
        } catch (IOException e){
            throw new RuntimeException("Error while saving a PDF file to disk", e);
        }

    }

    @Transactional(readOnly = true)
    public byte[] getReadyReportFile(Integer decisionId) {
        Report report = reportRepository.findByDecision_Id(decisionId)
                .orElseThrow(() -> new EntityNotFoundException("Report not generated yet by Administrator."));

        try {
            Path filePath = Paths.get(report.getFilePath());
            if (!Files.exists(filePath)) {
                throw new EntityNotFoundException("PDF file physically missing on disk.");
            }
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error while reading PDF file from disk", e);
        }
    }

}
