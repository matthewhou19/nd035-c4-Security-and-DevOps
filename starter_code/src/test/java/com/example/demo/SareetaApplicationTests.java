package com.example.demo;

import com.example.demo.model.persistence.*;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.platform.commons.function.Try;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RunWith(SpringRunner.class)

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,classes = SareetaApplication.class)
public class SareetaApplicationTests {
	@LocalServerPort
	private int port;

	@Autowired
	ItemRepository itemRepository;

	@Autowired
	private TestRestTemplate restTemplate;

	private HttpHeaders httpHeaders;

	@Before
	public void getJwt() {
		initialize(RoleEnum.User, "username", "userPassword");
		itemRepository.save(generateItem("ItemName1"));
		itemRepository.save(generateItem("ItemName2"));
		itemRepository.save(generateItem("ItemName3"));
		itemRepository.save(generateItem("ItemName4"));
		itemRepository.save(generateItem("ItemName5"));
		//System.out.println(jwt);
	}




	@Test
	public void contextLoads() {
	}

	@Test
	public void userRoleTest() {
		String url = "http://localhost:" + port + "/api/user/allUsers";

		HttpEntity<userDTO> requestHttpEntity = new HttpEntity<>(null, httpHeaders);
		ResponseEntity<User>  responseEntity = restTemplate.exchange(url, HttpMethod.GET,requestHttpEntity, User.class );

		Assert.assertEquals(403, responseEntity.getStatusCodeValue());

		initialize(RoleEnum.Admin, "AdminName", "AdminPassword");
		requestHttpEntity = new HttpEntity<>(null, httpHeaders);
		ResponseEntity<List<User>> responseEntitys = restTemplate.exchange(url, HttpMethod.GET,requestHttpEntity, new ParameterizedTypeReference<List<User>>() {} );

		Assert.assertEquals(200, responseEntitys.getStatusCodeValue());
		Assert.assertEquals("username", responseEntitys.getBody().get(0).getUsername());
	}

	@Test
	public void userOrderTest() {
		addCart(1, 6L);
		addCart(2, 7L);
		addCart(3, 3L);
		addCart(4, 4L);
		addCart(5, 5L);
		submitOrder("username");
		List<UserOrder> userOrderList = orderHistory("username");
		Assert.assertEquals(1665,userOrderList.get(0).getTotal().intValueExact());
		Assert.assertEquals("ItemName4", userOrderList.get(0).getItems().get(0).getName());
	}


	private void initialize(RoleEnum roleEnum, String username, String password){
		CreateUserRequest createUserRequest =generateCUR(roleEnum, username, password);
		createUser(createUserRequest);
		String jwt = signIn(createUserRequest.getUsername(), createUserRequest.getPassword());
		httpHeaders = new HttpHeaders();

		httpHeaders.set("Authorization", jwt);
	}


	private String signIn(String username, String password)  {
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


	public CreateUserRequest generateCUR(RoleEnum roleEnum, String username, String password) {
		CreateUserRequest createUserRequest = new CreateUserRequest();
		createUserRequest.setUsername(username);
		createUserRequest.setPassword(password);
		createUserRequest.setConfirmPassword(password);
		Set<RoleEnum> set = new HashSet<>();
		set.add(roleEnum);
		createUserRequest.setRoles(set);
		return createUserRequest;
	}

	private Item generateItem(String itemName) {
		Item item = new Item();
		item.setDescription("Description for an item.");
		item.setPrice(new BigDecimal(111));
		item.setName(itemName);
		return item;
	}

	private ModifyCartRequest generateCartRequest(int num, long id) {
		ModifyCartRequest cartRequest = new ModifyCartRequest();
		cartRequest.setQuantity(num);
		cartRequest.setItemId(id);
		cartRequest.setUsername("username");
		return cartRequest;
	}

	private void addCart(int num, long id) {
		ModifyCartRequest cartRequest =generateCartRequest(num, id);
		String url = "http://localhost:" + port + "/api/cart/addToCart";
		HttpEntity<ModifyCartRequest> requestHttpEntity = new HttpEntity<>(cartRequest, httpHeaders);
		ResponseEntity<Cart> responseEntity = restTemplate.postForEntity(url, requestHttpEntity, Cart.class);
	}

	private void submitOrder(String username) {
		String url = "http://localhost:" + port + "/api/order/submit/" + username;
		HttpEntity<ModifyCartRequest> requestHttpEntity = new HttpEntity<>(null, httpHeaders);
		ResponseEntity<UserOrder> responseEntity = restTemplate.postForEntity(url, requestHttpEntity, UserOrder.class);

	}

	private List<UserOrder> orderHistory(String username) {
		String url = "http://localhost:" + port + "/api/order/history/" + username;
		HttpEntity<ModifyCartRequest> requestHttpEntity = new HttpEntity<>(null, httpHeaders);
		ResponseEntity<List<UserOrder>> responseEntity = restTemplate.exchange(url, HttpMethod.GET,requestHttpEntity, new ParameterizedTypeReference<List<UserOrder>>() {} );
		return responseEntity.getBody();
	}

}
