package com.example.demo.controllers;

import java.util.List;
import java.util.Optional;

import com.example.demo.model.persistence.Role;
import com.example.demo.model.persistence.RoleEnum;
import com.example.demo.model.persistence.repositories.RoleRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {
	public static final Logger log = LoggerFactory.getLogger(UserController.class);
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private RoleRepository roleRepository;



	@GetMapping("/id/{id}")
	@PreAuthorize("hasRole('User') or hasRole('Admin')")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	@PreAuthorize("hasRole('User') or hasRole('Admin')")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);
		if(createUserRequest.getPassword().length()<7 ||
				!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())){
			log.error("Create user fail, with unusable password.");
			return ResponseEntity.badRequest().build();
		}

		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
		if (createUserRequest.getRoles() == null || createUserRequest.getRoles().size() == 0) {
			log.info("Create user failure, because of no roles in input.");
			return ResponseEntity.badRequest().build();
		}
		for (RoleEnum roleName : createUserRequest.getRoles()) {
			if (roleName != null) {
				Role role = new Role();
				role.setName(roleName);
				user.getRoles().add(role);
				roleRepository.save(role);
			}

		}
		if (user.getRoles().size() < 1) {
			log.error("Create user failure, can't decide the role.");
			return ResponseEntity.badRequest().build();
		}
		userRepository.save(user);
		log.info("User creation successfully");
		return ResponseEntity.ok(user);
	}

	@GetMapping("/allUsers")
	@PreAuthorize("hasAuthority('Admin')")
	public ResponseEntity<List<User>> getAllUser() {
		Role role = roleRepository.getByName(RoleEnum.User);
		List<User> userList =userRepository.findAllByRolesContaining(role);
		if (userList != null && userList.size() != 0) {
			return  ResponseEntity.ok(userList);
		}
		return ResponseEntity.notFound().build();
	}
}
