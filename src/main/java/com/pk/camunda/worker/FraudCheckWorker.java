package com.pk.camunda.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FraudCheckWorker {

    private static final Logger LOG = LoggerFactory.getLogger(FraudCheckWorker.class);

    @JobWorker(type = "fraud-check", autoComplete = false)
    public void handleFraudCheck(final JobClient client, final ActivatedJob job, @Variable String applicationId) {
        LOG.info("Processing Fraud Check for application ID: {}", applicationId);

        try {
            // Simulate fraud evaluation
            boolean isFraud = false;
            if (applicationId != null && applicationId.contains("FRAUD")) {
                isFraud = true;
            }
            
            // Completing the job successfully with new variables
            LOG.info("Fraud Check complete. fraudDetected: {}", isFraud);
            client.newCompleteCommand(job.getKey())
                  .variables(Map.of("fraudDetected", isFraud))
                  .send()
                  .exceptionally(throwable -> {
                      LOG.error("Failed to complete job", throwable);
                      return null;
                  });

        } catch (Exception e) {
            // In case of system errors, trigger a business error or fail the job for a retry
            LOG.error("Error evaluating fraud score", e);
            client.newFailCommand(job.getKey())
                  .retries(job.getRetries() - 1)
                  .errorMessage(e.getMessage())
                  .send();
        }
    }

    @JobWorker(type = "verify-document-worker", autoComplete = true)
    public Map<String, Object> handleDocumentVerification(@Variable String applicationId) {
        LOG.info("Verifying documents for application: {}", applicationId);
        // Simulate document verification
        return Map.of("documentValid", true);
    }

    @JobWorker(type = "script-worker-escalation", autoComplete = true)
    public void escalateSLA(@Variable String managerId) {
        LOG.warn("SLA Breached! Escalating task for manager: {}", managerId);
        // Simulate an escalation action (e.g. sending an urgent email)
    }
    
    @JobWorker(type = "cleanup-worker", autoComplete = true)
    public void cleanupResources(@Variable String applicationId) {
        LOG.info("Application {} cancelled. Cleaning up resources...", applicationId);
    }
}
