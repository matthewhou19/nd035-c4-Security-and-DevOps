package com.example.demo.model.requests;

import com.example.demo.model.persistence.Role;
import com.example.demo.model.persistence.RoleEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public class CreateUserRequest {

	@JsonProperty
	private String username;
	@JsonProperty
	private String password;

	@JsonProperty
	private String confirmPassword;

	@JsonProperty
	private Set<RoleEnum> roles;

	public Set<RoleEnum> getRoles() {
		return roles;
	}

	public void setRoles(Set<RoleEnum> roles) {
		this.roles = roles;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
