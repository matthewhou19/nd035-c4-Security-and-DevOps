package com.example.demo.model.persistence.repositories;

import com.example.demo.model.persistence.Role;
import com.example.demo.model.persistence.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
