package com.risk.decision.controller;

import com.risk.decision.dto.DecisionResponse;
import com.risk.decision.service.DecisionResultProcessor;
import com.risk.decision.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/decisions")
@RequiredArgsConstructor
public class ResultController {

    private final ReportService reportService;
    private final DecisionResultProcessor decisionResultProcessor;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/report")
    public ResponseEntity<String> generateReport(
            @PathVariable("id") Integer id,
            @RequestParam(value = "isUk", defaultValue = "true") boolean isUk) {
        reportService.generateAndSaveReport(id,  isUk);
        return ResponseEntity.ok("The report has been successfully generated and saved on the server.");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'INVESTOR')")
    @GetMapping("/{id}/report/download")
    public ResponseEntity<byte[]> downloadReport(@PathVariable("id") Integer id) {

        byte[] pdfContent = reportService.getReadyReportFile(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        headers.setContentDispositionFormData("attachment", "decision_report_" + id + ".pdf");
        headers.setContentLength(pdfContent.length);

        return ResponseEntity.ok().headers(headers).body(pdfContent);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'INVESTOR')")
    @GetMapping("/{id}/calculation/result")
    public ResponseEntity<DecisionResponse> calculateResult(
            @PathVariable("id") Integer id,
            @RequestParam(value = "isUk", defaultValue = "true") boolean isUk) {
        return ResponseEntity.ok(decisionResultProcessor.makeResultToCharts(id, isUk));
    }


}
