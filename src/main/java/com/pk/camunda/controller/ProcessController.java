package com.pk.camunda.controller;

import com.pk.camunda.dto.LoanApplicationRequest;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.EvaluateDecisionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Controller for Loan Approval System.
 * Triggers process instances via messages and handles direct DMN evaluation.
 */
@RestController
@RequestMapping("/api/loan")
public class ProcessController {

    @Autowired
    private ZeebeClient zeebeClient;

    @PostMapping("/submit")
    public ResponseEntity<String> submitApplication(@RequestBody LoanApplicationRequest request) {
        String applicationId = request.getApplicationId() != null ? request.getApplicationId() : UUID.randomUUID().toString();

        Map<String, Object> variables = new HashMap<>();
        variables.put("applicantName", request.getApplicantName());
        variables.put("applicationId", applicationId);
        variables.put("creditScore", request.getCreditScore() != null ? request.getCreditScore() : 500);
        variables.put("loanAmount", request.getLoanAmount() != null ? request.getLoanAmount() : 5000);
        variables.put("addInsurance", true);
        variables.put("managerId", request.getManagerId() != null ? request.getManagerId() : "manager-1");

        zeebeClient.newPublishMessageCommand()
                .messageName("LoanApplicationSubmitted")
                .correlationKey(applicationId)
                .variables(variables)
                .send()
                .join();

        return ResponseEntity.ok("Application submitted successfully. Application ID: " + applicationId);
    }

    @PostMapping("/receive-docs/{applicationId}")
    public ResponseEntity<String> receiveDocuments(@PathVariable String applicationId) {
        zeebeClient.newPublishMessageCommand()
                .messageName("LoanDocumentsReceived")
                .correlationKey(applicationId)
                .send()
                .join();
        return ResponseEntity.ok("Documents received for: " + applicationId);
    }

    @PostMapping("/policy-update/{applicationId}")
    public ResponseEntity<String> updatePolicy(@PathVariable String applicationId) {
        zeebeClient.newPublishMessageCommand()
                .messageName("LoanPolicyUpdated")
                .correlationKey(applicationId)
                .send()
                .join();
        return ResponseEntity.ok("Policy update received for: " + applicationId);
    }

    @PostMapping("/feedback/{applicationId}")
    public ResponseEntity<String> submitFeedback(@PathVariable String applicationId, @RequestBody Map<String, Object> feedback) {
        zeebeClient.newPublishMessageCommand()
                .messageName("CustomerFeedback")
                .correlationKey(applicationId)
                .variables(feedback)
                .send()
                .join();
        return ResponseEntity.ok("Feedback submitted for: " + applicationId);
    }

    @PostMapping("/complaint/{applicationId}")
    public ResponseEntity<String> submitComplaint(@PathVariable String applicationId) {
        zeebeClient.newPublishMessageCommand()
                .messageName("CustomerComplaint")
                .correlationKey(applicationId)
                .send()
                .join();
        return ResponseEntity.ok("Complaint logged for: " + applicationId);
    }

    @PostMapping("/withdraw/{applicationId}")
    public ResponseEntity<String> withdrawApplication(@PathVariable String applicationId) {
        zeebeClient.newPublishMessageCommand()
                .messageName("CustomerWithdrawal")
                .correlationKey(applicationId)
                .send()
                .join();
        return ResponseEntity.ok("Withdrawal requested for: " + applicationId);
    }

    @PostMapping("/cancel/{applicationId}")
    public ResponseEntity<String> cancelApplication(@PathVariable String applicationId) {
        zeebeClient.newPublishMessageCommand()
                .messageName("CancelApplication")
                .correlationKey(applicationId)
                .send()
                .join();
        return ResponseEntity.ok("Cancellation requested for: " + applicationId);
    }

    @PostMapping("/evaluate-decision")
    public ResponseEntity<Map<String, Object>> evaluateDecision(@RequestBody LoanApplicationRequest request) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("loanAmount", request.getLoanAmount());
        variables.put("creditScore", request.getCreditScore());

        EvaluateDecisionResponse response = zeebeClient.newEvaluateDecisionCommand()
                .decisionId("loan-decision")
                .variables(variables)
                .send()
                .join();

        Map<String, Object> result = new HashMap<>();
        result.put("decisionId", response.getDecisionId());
        result.put("decisionOutput", response.getDecisionOutput());
        result.put("isApproved", response.getDecisionOutput().contains("true"));

        return ResponseEntity.ok(result);
    }
}
