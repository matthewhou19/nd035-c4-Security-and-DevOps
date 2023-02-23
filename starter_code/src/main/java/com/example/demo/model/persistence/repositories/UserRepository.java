package com.example.demo.model.persistence.repositories;

import com.example.demo.model.persistence.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.persistence.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);
	List<User> findAllByRolesContaining(Role role);
}
