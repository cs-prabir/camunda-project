package com.pk.camunda.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;

@Component
public class CreditCheckWorker {

    @JobWorker(type = "credit-check", autoComplete = false)
    public void handleCreditCheck(JobClient client, ActivatedJob job) {

        Map<String, Object> variables = job.getVariablesAsMap();
        String applicationId = (String) variables.get("applicationId");

        try {
            // Simulate external API call
            int creditScore = (Integer)variables.get("creditScore");

            if (creditScore < 600) {
                // Business error → BPMN Error
                client.newThrowErrorCommand(job.getKey())
                        .errorCode("CREDIT_ERROR")
                        .errorMessage("Low credit score")
                        .send()
                        .join();
                return;
            }

            // Success
            Map<String, Object> result = Map.of(
                    "creditScore", creditScore,
                    "riskAccepted", true
            );

            client.newCompleteCommand(job.getKey())
                    .variables(result)
                    .send()
                    .join();

        } catch (Exception ex) {
            // Technical failure → retry
            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage(ex.getMessage())
                    .send()
                    .join();
        }
    }

    private int callCreditAPI(String applicationId) {
        // Simulate API
        return new Random().nextInt(300) + 500;
    }
}