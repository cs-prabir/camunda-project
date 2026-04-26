package com.pk.camunda.worker;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Job Worker implementation for Loan Approval System.
 * Handles all service tasks defined in BPMN.
 */
@Component
public class FraudCheckWorker {

    private static final Logger LOG = LoggerFactory.getLogger(FraudCheckWorker.class);

    @JobWorker(type = "credit-check", autoComplete = true)
    public Map<String, Object> handleCreditCheck(@Variable String applicationId, @Variable Integer creditScore) {
        LOG.info("Processing Credit Check for application: {}. Current score: {}", applicationId, creditScore);
        // Simulate credit scoring logic
        int finalScore = creditScore != null ? creditScore : 500;
        LOG.info("Credit Check complete for {}. Final Score: {}", applicationId, finalScore);
        return Map.of("creditScore", finalScore);
    }

    @JobWorker(type = "fraud-check", autoComplete = true)
    public Map<String, Object> handleFraudCheck(@Variable String applicationId) {
        LOG.info("Processing Fraud Check for application: {}", applicationId);
        boolean isFraud = applicationId != null && applicationId.contains("FRAUD");
        LOG.info("Fraud Check complete. fraudDetected: {}", isFraud);
        return Map.of("fraudDetected", isFraud);
    }

    @JobWorker(type = "insurance-provision", autoComplete = true)
    public void handleInsuranceProvision(@Variable String applicationId) {
        LOG.info("Provisioning insurance for application: {}", applicationId);
    }

    @JobWorker(type = "request-docs-worker", autoComplete = true)
    public void handleRequestDocs(@Variable String applicationId) {
        LOG.info("Requesting additional documents for application: {}", applicationId);
    }

    @JobWorker(type = "verify-document-worker", autoComplete = true)
    public Map<String, Object> handleDocumentVerification(@Variable String applicationId) {
        LOG.info("Verifying documents for application: {}", applicationId);
        return Map.of("documentValid", true);
    }

    @JobWorker(type = "escalation-worker", autoComplete = true)
    public void handleEscalation(@Variable String applicationId, @Variable String managerId) {
        LOG.warn("SLA Breached for application {}! Escalating to manager: {}", applicationId, managerId);
    }

    @JobWorker(type = "cleanup", autoComplete = true)
    public void handleCleanup(@Variable String applicationId) {
        LOG.info("Application {} terminated. Cleaning up resources...", applicationId);
    }

    @JobWorker(type = "log-complaint", autoComplete = true)
    public void handleLogComplaint(@Variable String applicationId) {
        LOG.info("Logging complaint for application: {}", applicationId);
    }

    @JobWorker(type = "instance-pulse", autoComplete = true)
    public void handleInstancePulse(@Variable String applicationId) {
        LOG.info("Health check pulse for application: {}", applicationId);
    }
}
