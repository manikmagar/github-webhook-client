package com.mms.web.utils.github.webhook;

import static org.junit.Assert.assertThat;

import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class GithubWebhookClientApplicationTest {
	
	private GithubWebhookClientApplication webhookClient;
	
	private final String GITHUB_USER_AGENT_DUMMY ="GitHub-Hookshot/1234";

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
		webhookClient.setGithubUserAgentPrefix(GITHUB_USER_AGENT_DUMMY);
	}

	@Test
	public void testResponseValid() {
		ResponseEntity<String> entity = webhookClient.handle(signature, testPayload, GITHUB_USER_AGENT_DUMMY);
		assertThat(entity.getStatusCode(), IsEqual.equalTo(HttpStatus.OK));
	}
	
	@Test
	public void testShellCall() {
		ResponseEntity<String> entity = webhookClient.handle(signature, testPayload, GITHUB_USER_AGENT_DUMMY);
		assertThat(entity.getStatusCode(), IsEqual.equalTo(HttpStatus.OK));
	}
	
	@Test
	public void testResponseInvalidSignature() {
		ResponseEntity<String> entity = webhookClient.handle("wrong-signature", testPayload,GITHUB_USER_AGENT_DUMMY);
		assertThat(entity.getStatusCode(), IsEqual.equalTo(HttpStatus.UNAUTHORIZED));
	}
	
	@Test
	public void testResponseInvalidJSONPayload() {
		ResponseEntity<String> entity = webhookClient.handle("sha1=3c1d14852e9be18b2aca3d8973dc6caec246f169", "Invalid formatted payload",GITHUB_USER_AGENT_DUMMY);
		assertThat(entity.getStatusCode(), IsEqual.equalTo(HttpStatus.BAD_REQUEST));
	}
	
	@Test
	public void testInvalidUserAgent(){
		ResponseEntity<String> entity = webhookClient.handle("sha1=3c1d14852e9be18b2aca3d8973dc6caec246f169", "Invalid formatted payload","INVALID_USER_AGENT");
		assertThat(entity.getStatusCode(), IsEqual.equalTo(HttpStatus.BAD_REQUEST));
	}

}
