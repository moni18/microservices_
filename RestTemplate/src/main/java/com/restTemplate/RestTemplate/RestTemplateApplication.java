package com.restTemplate.RestTemplate;

import com.restTemplate.RestTemplate.model.User;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;



@SpringBootApplication
public class RestTemplateApplication {

	private static String baseUrl = "http://94.198.50.185:7081/api/users";
	private static final RestTemplate restTemplate = new RestTemplate();


	public static void main(String[] args) {
		//SpringApplication.run(RestTemplateApplication.class, args);
		StringBuilder finalCode = new StringBuilder();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		ResponseEntity<String> response = restTemplate
				.exchange(baseUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);

		String sessionId = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE).split(";")[0];

		User newUser = new User(3L, "James", "Brown", (byte)25);
		headers.set(HttpHeaders.COOKIE, sessionId);
		HttpEntity<User> entity = new HttpEntity<>(newUser, headers);
		response = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, String.class);
		finalCode.append(response.getBody());

		newUser.setName("Thomas");
		newUser.setLastName("Shelby");
		response = restTemplate.exchange(baseUrl, HttpMethod.PUT, entity, String.class);
		finalCode.append(response.getBody());

		response = restTemplate.exchange(baseUrl+"/3", HttpMethod.DELETE, entity, String.class);
		finalCode.append(response.getBody());

		System.out.println("Итоговый код: " + finalCode);
	}
}
