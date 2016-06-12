package com.mms.web.utils.github.webhook;

import java.io.File;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@SpringBootApplication
public class GithubWebhookClientApplication {
	
	@Value("${application.version}")
	private String appVersion;
	
	private String secretKey;
	
	private String baseTriggerPath;
	
	private String publishMessageHint;
	
	public GithubWebhookClientApplication(){
		this(System.getenv("GHSecretKey"),System.getenv("GHClientTriggerPath"));
	}
	
	public GithubWebhookClientApplication(String key){
		this(key,System.getenv("GHClientTriggerPath"));
	}
	public GithubWebhookClientApplication(String key, String triggerPath){
		System.out.println(System.getenv("GHSecretKey"));
		System.out.println(key);
		secretKey = key;
		Objects.requireNonNull(secretKey, "Github Secret Key is required");
		baseTriggerPath = triggerPath;
	}

	@RequestMapping(path="/util/github-webhook-client", method=RequestMethod.POST)
	public ResponseEntity<String> handle(@RequestHeader("X-Hub-Signature") String signature,
			@RequestBody String payload) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Webhook-Version", "1.0");

		if (signature == null) {
			return new ResponseEntity<>("No signature given.", headers, HttpStatus.BAD_REQUEST);
		}

		String computed = String.format("sha1=%s", HmacUtils.hmacSha1Hex(secretKey, payload));
		
		if (!MessageDigest.isEqual(signature.getBytes(), computed.getBytes())) {
			return new ResponseEntity<>("Invalid signature.", headers, HttpStatus.UNAUTHORIZED);
		}
		
		if(!Objects.isNull(baseTriggerPath) && !baseTriggerPath.isEmpty()){
			
			Map<?,?> repo=null;
	
			try {
				Map<?, ?> payloadMap = new ObjectMapper().readValue(payload, Map.class);
				repo = (Map<?, ?>) payloadMap.get("repository");
				String repoName = (String)repo.get("full_name");
				
				if(!Objects.isNull(publishMessageHint) && !publishMessageHint.isEmpty()) {
					//Publish Message hint is configured. Create trigger file if commit message has this hint.
				
					if(payloadMap.containsKey("head_commit")){
						String message = (String) ((Map<?,?>)payloadMap.get("head_commit")).get("message");
						if(!Objects.isNull(message) && !message.isEmpty() && message.contains(publishMessageHint)){
							createTriggerFile(repoName);
						} else {
							System.out.println("Publish Message hint not found in message, not creating trigger file");
						}
					}
				} else {
					System.out.println("Publish Message hint is not specified. Creating trigger file");
					createTriggerFile(repoName);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				StringBuilder response = new StringBuilder();
				response.append("Unable to parse response.");
				return new ResponseEntity<>(response.toString(), headers, HttpStatus.BAD_REQUEST);
			}
		}
		
		int bytes = payload.getBytes().length;
		StringBuilder response = new StringBuilder();
		response.append("Signature Verified.");
		response.append(String.format("Received %d bytes.", bytes));
		response.append(String.format("Github Webhook client version - %s.", appVersion));
		return new ResponseEntity<>(response.toString(), headers, HttpStatus.OK);
	}
	
	public void createTriggerFile(String repoName) throws Exception{
		String targetTrigger = baseTriggerPath + "/" + repoName;
		System.out.println("Creating new trigger folder -"+ targetTrigger);
		File folder = new File(targetTrigger);
		folder.mkdirs();
		File trigger = new File(targetTrigger + "/pushtrigger_"+ System.currentTimeMillis() +".txt");
		if(trigger.exists()) trigger.delete();
		trigger.createNewFile();
	}

	public static void main(String[] args) {
		SpringApplication.run(GithubWebhookClientApplication.class, args);
	}
}
