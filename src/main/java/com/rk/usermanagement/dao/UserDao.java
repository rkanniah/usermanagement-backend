package com.rk.usermanagement.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rk.usermanagement.model.User;

public interface UserDao extends JpaRepository<User, Long> {

	User findByEmail(String email);
}
