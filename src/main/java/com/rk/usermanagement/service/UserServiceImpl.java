package com.rk.usermanagement.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rk.usermanagement.dao.UserDao;
import com.rk.usermanagement.exception.ResourceDatabaseOperationException;
import com.rk.usermanagement.exception.ResourceNotFoundException;
import com.rk.usermanagement.model.User;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	public UserDao userDao;

	@Override
	@Async("myExecutor")
	public Future<User> addUser(User user) {

		try {
			return CompletableFuture.completedFuture(userDao.save(user));
		} catch (DataAccessException dae) {
			throw new ResourceDatabaseOperationException("error creating user", dae);
		}
	}

	@Override
	@Async("myExecutor")
	public Future<User> updateUser(User userToUpdate) {

		User user = userDao.findByEmail(userToUpdate.getEmail());
		if (user == null) {
			throw new ResourceNotFoundException("user to update not found");
		}

		try {
			return CompletableFuture.completedFuture(userDao.save(userToUpdate));
		} catch (DataAccessException dae) {
			throw new ResourceDatabaseOperationException("error updating user", dae);
		}
	}

	@Override
	@Async("myExecutor")
	public void deleteUser(User user) {

		try {
			userDao.delete(user);
		} catch (DataAccessException dae) {
			throw new ResourceDatabaseOperationException("error deleting user", dae);
		}
	}

	@Override
	@Async("myExecutor")
	public Future<List<User>> getAllUsers() {

		try {
			return CompletableFuture.completedFuture(userDao.findAll());
		} catch (DataAccessException dae) {
			throw new ResourceDatabaseOperationException("error retrieving all users", dae);
		}
	}

	@Override
	@Async("myExecutor")
	public Future<User> findUser(String email) {

		try {
			return CompletableFuture.completedFuture(userDao.findByEmail(email));
		} catch (DataAccessException dae) {
			throw new ResourceDatabaseOperationException("error retrieving user by email", dae);
		}
	}
}
