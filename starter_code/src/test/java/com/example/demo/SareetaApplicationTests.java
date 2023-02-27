package com.example.demo;

import com.example.demo.model.persistence.RoleEnum;
import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;


@RunWith(SpringRunner.class)

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,classes = SareetaApplication.class)
public class SareetaApplicationTests {
	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	private HttpHeaders httpHeaders;

	@Before
	public void getJwt() {
		CreateUserRequest createUserRequest =generateCUR(RoleEnum.User);
		createUser(createUserRequest);
		String jwt = signIn(createUserRequest.getUsername(), createUserRequest.getPassword());
		httpHeaders = new HttpHeaders();
		httpHeaders.set("Authorization", jwt);
		//System.out.println(jwt);
	}


	@Test
	public void contextLoads() {
	}

	public String signIn(String username, String password)  {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		userDTO user = new userDTO();
		user.password = password;
		user.username = username;


		HttpEntity<userDTO> requestHttpEntity = new HttpEntity<>(user, httpHeaders);
		String url = "http://localhost:" + port + "/login";
		ResponseEntity<User> responseEntity = restTemplate.postForEntity(url, requestHttpEntity, User.class);
		HttpHeaders responseEntityHeaders = responseEntity.getHeaders();
		String jwt =responseEntityHeaders.getFirst(HttpHeaders.AUTHORIZATION);
		return  jwt;
	}

	private class userDTO{
		@JsonProperty
		private String username;
		@JsonProperty
		private String password;


	}


	public void createUser(CreateUserRequest createUserRequest) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<CreateUserRequest> requestHttpEntity = new HttpEntity<>(createUserRequest, httpHeaders);
		String url = "http://localhost:" + port + "/api/user/create";
		ResponseEntity<User> responseEntity = restTemplate.postForEntity(url, requestHttpEntity, User.class);
	}


	public CreateUserRequest generateCUR(RoleEnum roleEnum) {
		CreateUserRequest createUserRequest = new CreateUserRequest();
		createUserRequest.setUsername("username");
		createUserRequest.setPassword("password");
		createUserRequest.setConfirmPassword("password");
		Set<RoleEnum> set = new HashSet<>();
		set.add(roleEnum);
		createUserRequest.setRoles(set);
		return createUserRequest;
	}

}
