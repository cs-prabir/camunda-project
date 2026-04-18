package com.pk.camunda.controller;

import com.pk.camunda.dto.LoanApplicationRequest;
import io.camunda.zeebe.client.ZeebeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/loan")
public class ProcessController {

    @Autowired
    private ZeebeClient zeebeClient;
    @PostMapping("/submit")
    public ResponseEntity<String> submitApplication(@RequestBody LoanApplicationRequest request) {
        String applicationId = request.getApplicationId() != null ? request.getApplicationId() : UUID.randomUUID().toString();

        // Prepare variables map
        Map<String, Object> variables = new HashMap<>();
        variables.put("applicantName", request.getApplicantName());
        variables.put("applicationId", applicationId);
        variables.put("creditScore", request.getCreditScore());
        variables.put("documentValid", request.getDocumentValid());
        variables.put("fraudDetected", request.getFraudDetected());
        variables.put("loanAmount", request.getLoanAmount());
        variables.put("managerId", request.getManagerId());

        // Start process via Message Start Event
        zeebeClient.newPublishMessageCommand()
            .messageName("LoanApplicationSubmitted")
            .correlationKey(applicationId)
            .variables(variables)
            .send()
            .join();

        return ResponseEntity.ok("Application submitted successfully. Application ID: " + applicationId);
    }

    @PostMapping("/cancel/{applicationId}")
    public ResponseEntity<String> cancelApplication(@PathVariable String applicationId) {
        // Correlate message with event subprocess
        zeebeClient.newPublishMessageCommand()
            .messageName("CancelApplication")
            .correlationKey(applicationId)
            .send()
            .join();

        return ResponseEntity.ok("Cancellation requested for: " + applicationId);
    }

    @PostMapping("/feedback/{applicationId}")
    public ResponseEntity<String> submitFeedback(@PathVariable String applicationId, @RequestBody Map<String, Object> feedback) {
        // Correlate message to Intermediate Catch Event
        zeebeClient.newPublishMessageCommand()
            .messageName("CustomerFeedback")
            .correlationKey(applicationId)
            .variables(feedback)
            .send()
            .join();

        return ResponseEntity.ok("Feedback submitted for: " + applicationId);
    }

/*
    @PostMapping("/migrate-all")
    public ResponseEntity<String> migrateAll(@RequestParam long sourceDefinitionKey, @RequestParam long targetDefinitionKey) {
        // Search for all active process instances of the source definition using ZeebeClient
        // In 8.5+, the filter method is processDefinitionKey and the instance key is accessed via getKey()
        var instances = zeebeClient.newProcessInstanceQuery()
                .filter(f -> f.processDefinitionKey(sourceDefinitionKey))
                .send()
                .join();

        int count = 0;
        for (var instance : instances.items()) {
            zeebeClient.newMigrateProcessInstanceCommand(instance.getKey())
                    .targetProcessDefinitionKey(targetDefinitionKey)
                    .send()
                    .join();
            count++;
        }

        return ResponseEntity.ok("Migrated " + count + " instances from " + sourceDefinitionKey + " to " + targetDefinitionKey);
    }*/
    /*@PostMapping("/migrate-all")
    public ResponseEntity<String> migrateAll(@RequestParam long sourceDefinitionKey, @RequestParam long targetDefinitionKey) {
        // Search for all active process instances of the source definition using ZeebeClient
        var instances = zeebeClient.newProcessInstanceQuery()
                .filter(f ->
                        f.processDefinitionKey(sourceDefinitionKey).state("ACTIVE"))
                .send()
                .join();

        int count = 0;
        for (var instance : instances.items()) {
            zeebeClient.newMigrateProcessInstanceCommand(instance.getProcessInstanceKey())
                    .targetProcessDefinitionKey(targetDefinitionKey)
                    .send()
                    .join();
            count++;
        }

        return ResponseEntity.ok("Migrated " + count + " instances from " + sourceDefinitionKey + " to " + targetDefinitionKey);
    }

*/



    @PostMapping("/check-credit")
    public ResponseEntity<Map<String, Object>> checkCreditScore(@RequestBody LoanApplicationRequest request) {
        // Simulate external API call
        if (request.getCreditScore() > 1000) {
    Map<String, Object> body = new HashMap<>();
    body.put("errorCode", "CREDIT_API_ERROR");
    body.put("errorMessage", "Low credit score");

    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
}

        Map<String, Object> response = new HashMap<>();
        response.put("applicationId", request.getDocumentValid());
        response.put("creditScore", request.getCreditScore());
        response.put("IsApproved", isApproved(request.getCreditScore().toString()));

        return ResponseEntity.ok(response);

    }

    private boolean isApproved(String creditScore) {
        return Integer.parseInt(creditScore) >= 600;
    }
}
