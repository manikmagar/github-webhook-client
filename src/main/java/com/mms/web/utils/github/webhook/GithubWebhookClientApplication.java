package com.mms.web.utils.github.webhook;

import java.io.File;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.ObjectMapper;

@EnableAutoConfiguration
@Controller
@SpringBootApplication
public class GithubWebhookClientApplication {
	
	@Autowired
	private Environment env;
	
	@Value("${application.version}")
	private String appVersion;
	
	@Value("${github.user.agent.prefix}")
	private String githubUserAgentPrefix;
	
	private String secretKey;
	
	@Value("${github.publish.message.hint}")
	private String publishMessageHint;
	
	public void setGithubUserAgentPrefix(String prefix){
		this.githubUserAgentPrefix =prefix;
	}
	
	public GithubWebhookClientApplication(){
		this(System.getenv("GHSecretKey"));
	}
	
	public GithubWebhookClientApplication(String key){
		secretKey = key;
		Objects.requireNonNull(secretKey, "Github Secret Key is required");
	}
	
	@RequestMapping(path="/util/github-webhook-client", method=RequestMethod.POST)
	public ResponseEntity<String> handle(@RequestHeader("X-Hub-Signature") String signature,
			@RequestBody String payload, @RequestHeader("User-Agent") String userAgent) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Github-Webhook-Client-Version", appVersion);

		if(Objects.isNull(userAgent) || !userAgent.startsWith(githubUserAgentPrefix)){
			return new ResponseEntity<>("Invalid request.", headers, HttpStatus.BAD_REQUEST);
		}
		
		if (signature == null) {
			return new ResponseEntity<>("No signature given.", headers, HttpStatus.BAD_REQUEST);
		}

		String computed = String.format("sha1=%s", HmacUtils.hmacSha1Hex(secretKey, payload));
		
		if (!MessageDigest.isEqual(signature.getBytes(), computed.getBytes())) {
			return new ResponseEntity<>("Invalid signature.", headers, HttpStatus.UNAUTHORIZED);
		}
			
		Map<?,?> repo=null;

		try {
			Map<?, ?> payloadMap = new ObjectMapper().readValue(payload, Map.class);
			repo = (Map<?, ?>) payloadMap.get("repository");
			String repoName = (String)repo.get("full_name");
			
			String repoKey = "REPO_" + repoName.replace("/", "_").toUpperCase() + "_SHELL";
			//If we environment is set
			if (Objects.nonNull(env)){
				String shellPath = env.getProperty(repoKey);
				if(shellPath != null && new File(shellPath).exists()) {
					if(!Objects.isNull(publishMessageHint) && !publishMessageHint.isEmpty()) {
						if(payloadMap.containsKey("head_commit")){
							String message = (String) ((Map<?,?>)payloadMap.get("head_commit")).get("message");
							if(!Objects.isNull(message) && !message.isEmpty() && message.contains(publishMessageHint)){
								callShellScript(shellPath);
							} else {
								System.out.println("Publish Message hint not found in message, not creating trigger file");
							}
						}
					} else {
						System.out.println("Publish Message hint is not specified. Call shell script");
						callShellScript(shellPath);
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			StringBuilder response = new StringBuilder();
			response.append("Unable to parse response.");
			return new ResponseEntity<>(response.toString(), headers, HttpStatus.BAD_REQUEST);
		}
		
		
		int bytes = payload.getBytes().length;
		StringBuilder response = new StringBuilder();
		response.append("Signature Verified.");
		response.append(String.format("Received %d bytes.", bytes));
		response.append(String.format("Github Webhook client version - %s.", appVersion));
		return new ResponseEntity<>(response.toString(), headers, HttpStatus.OK);
	}
	
	public void callShellScript(String repoShellPath) throws Exception{
		ShellScriptUtil.executeScript(repoShellPath);
	}

	public static void main(String[] args) {
		SpringApplication.run(GithubWebhookClientApplication.class, args);
	}
}
