package com.pk.camunda;

import io.camunda.zeebe.spring.client.annotation.Deployment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
/*@Deployment(resources = {
		"classpath:*.bpmn",
		"classpath:*.form"
})*/
@Deployment(resources = {
		"classpath:document_verification.bpmn",
		"classpath:loan_approval_process.bpmn",
		"classpath:manager-approval-form.form",
		"classpath:loan-decision.dmn"
})
public class CamundaApplication {

	public static void main(String[] args) {
		SpringApplication.run(CamundaApplication.class, args);
	}

}
