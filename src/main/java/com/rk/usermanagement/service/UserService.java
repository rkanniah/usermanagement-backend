package com.rk.usermanagement.service;

import java.util.List;
import java.util.concurrent.Future;

import com.rk.usermanagement.model.User;

public interface UserService {

	Future<User> addUser(User user);

	Future<User> updateUser(User userToUpdate);

	void deleteUser(User user);

	Future<List<User>> getAllUsers();

	Future<User> findUser(String email);
}
