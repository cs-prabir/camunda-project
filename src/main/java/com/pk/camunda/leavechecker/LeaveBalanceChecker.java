package com.pk.camunda.leavechecker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
public class LeaveBalanceChecker {
    /* @JobWorker( type = "leave_balance_check",autoComplete = true)
     public Map<String, Boolean> validateLeave(@Variable ){
         Map<String, Boolean> result = new HashMap<>();
         result.put("docGenResponse", "SUCCESS");

         return result;


     }*/
    @JobWorker(type = "leave_balance_check" )
    public void checkLeaveBalance(
            final JobClient client,
            final ActivatedJob job,
            @Variable("startDate") String startDate,
            @Variable("endDate") String endDate,
            @Variable("name") String name,
            @Variable("email") String email
    ) {


        System.out.println("Received Start Date: " + startDate);
        System.out.println("Received End Date: " + endDate);
        System.out.println("Received Name: " + name);
        System.out.println("Email Name: " + email);
        try {
// Your business logic...
            boolean isLeaveAllowed = false;

            if (email != null && email.startsWith("p")) {
                isLeaveAllowed = true;
                client.newCompleteCommand(job.getKey())
                        .variables(Map.of("isLeaveAllowed", isLeaveAllowed))
                        .send()
                        .join();
            } else {

                client.newThrowErrorCommand(job.getKey())
                        .errorCode("ERR_BALANCE")
                        .errorMessage("Insufficient balance")
                        .send()
                        .join();
            }
        } catch (Exception e) {
            int retries = job.getRetries();

            if (retries > 0) {
                client.newFailCommand(job.getKey())
                        .retries(retries - 1)
                        .retryBackoff(Duration.ofSeconds(10))
                        .errorMessage("Temporary issue")
                        .send()
                        .join();
            }
        }

    }//send_reminder_email

    @JobWorker(type = "send_reminder_email")
    public Map<String, String> sendReminderEmail(final JobClient client,
                                                 final ActivatedJob job) {

        Map<String, Object> variablesAsMap = job.getVariablesAsMap();
        System.out.println(variablesAsMap);
        System.out.println("**************START****************");
        System.out.println("Jobkey : "+job.getKey());
        System.out.println("ProcessInstanceKey : "+job.getProcessInstanceKey());
        System.out.println("ProcessDefinationKey : "+job.getProcessDefinitionKey());
        System.out.println("BusinessnKey : "+ job.getVariablesAsMap().containsKey("businessKey"));
        System.out.println("**************END****************");
        Map<String, String> result = new HashMap<>();
        result.put("empName", "Prabir");
        result.put("empEmail", "pk.ck@gmail.com");
        return result;


    }//rest_check
    @JobWorker(type = "rest_check")
    public void restCheck(final JobClient client,
                                                 final ActivatedJob job) {

        Map<String, Object> variablesAsMap = job.getVariablesAsMap();
        System.out.println(variablesAsMap);

    }
}
