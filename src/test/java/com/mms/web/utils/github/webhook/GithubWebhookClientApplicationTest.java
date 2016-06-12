package com.mms.web.utils.github.webhook;

import static org.junit.Assert.assertThat;

import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GithubWebhookClientApplicationTest {
	
	private GithubWebhookClientApplication webhookClient;

	private String testPayload = "{" +
								    "\"head_commit\": {" +
								        "\"message\":\"Test Payload\"" +
								    "}," +
								    "\"repository\": {" +
								        "\"full_name\":\"manikmagar/test\"" +
								    "}" +	
								  "}";
	
	private String signature = "sha1=15bb3cdf6ce2248be2c60a15b68439f6e13e23cf";
	
	@Before
	public void setUpBefore() throws Exception {
		webhookClient = new GithubWebhookClientApplication("GitSecret");
	}

	@Test
	public void testResponseValid() {
		ResponseEntity<String> entity = webhookClient.handle(signature, testPayload);
		assertThat(entity.getStatusCode(), IsEqual.equalTo(HttpStatus.OK));
	}
	
	@Test
	public void testResponseInvalidSignature() {
		ResponseEntity<String> entity = webhookClient.handle("wrong-signature", testPayload);
		assertThat(entity.getStatusCode(), IsEqual.equalTo(HttpStatus.UNAUTHORIZED));
	}
	
	@Test
	public void testResponseInvalidJSONPayload() {
		webhookClient = new GithubWebhookClientApplication("GitSecret","/tmp");
		ResponseEntity<String> entity = webhookClient.handle("sha1=3c1d14852e9be18b2aca3d8973dc6caec246f169", "Invalid formatted payload");
		assertThat(entity.getStatusCode(), IsEqual.equalTo(HttpStatus.BAD_REQUEST));
	}

}
