package com.pk.camunda.dto;

public class LoanApplicationRequest {
    private String applicantName;
    private String applicationId;
    private Integer creditScore;
    private Boolean documentValid;
    private Boolean fraudDetected;
    private Integer loanAmount;
    private String managerId;

    // Default constructor
    public LoanApplicationRequest() {}

    // Getters and Setters
    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public Integer getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(Integer creditScore) {
        this.creditScore = creditScore;
    }

    public Boolean getDocumentValid() {
        return documentValid;
    }

    public void setDocumentValid(Boolean documentValid) {
        this.documentValid = documentValid;
    }

    public Boolean getFraudDetected() {
        return fraudDetected;
    }

    public void setFraudDetected(Boolean fraudDetected) {
        this.fraudDetected = fraudDetected;
    }

    public Integer getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(Integer loanAmount) {
        this.loanAmount = loanAmount;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }
}
