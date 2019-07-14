package com.rk.web;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.rk.usermanagement.dao.UserDao;
import com.rk.usermanagement.exception.ResourceDatabaseOperationException;
import com.rk.usermanagement.exception.ResourceNotFoundException;
import com.rk.usermanagement.model.User;
import com.rk.usermanagement.service.UserService;

@Path("/users")
@Component
public class UserResource {

	private static final Logger LOGGER = Logger.getLogger(UserResource.class.getName());

	@Autowired
	private UserService userService;

	@Autowired
	private UserDao userDao;

	@POST
	@Path("add/")
	public Response addUser(User user) {

		LOGGER.info("Adding a user...");

		if (user != null && CollectionUtils.isEmpty(user.getRole())) {
			return Response.status(Response.Status.BAD_REQUEST).entity("role cannot be empty or null!").build();
		}

		try {
			LOGGER.info("Adding a user userDao..." + userDao);
			Future<User> newUser = userService.addUser(user);
			return Response.status(Response.Status.CREATED).entity(newUser.get()).build();

		} catch (ResourceDatabaseOperationException | InterruptedException | ExecutionException dbe) {
			LOGGER.log(Level.SEVERE, "Create DB operation error...", dbe);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Create DB operation error...")
					.build();
		}
	}

	@PUT
	@Path("update/")
	public Response updateUser(User user) {

		LOGGER.info("Updating user...");

		if (user != null && CollectionUtils.isEmpty(user.getRole())) {
			return Response.status(Response.Status.BAD_REQUEST).entity("role cannot be empty or null for update!")
					.build();
		}

		try {
			Future<User> updatedUser = userService.updateUser(user);
			return Response.status(Response.Status.ACCEPTED).entity(updatedUser.get()).build();

		} catch (ResourceNotFoundException nfe) {
			return Response.status(Response.Status.NOT_MODIFIED).entity("User not found for update: " + user).build();
		} catch (ResourceDatabaseOperationException | InterruptedException | ExecutionException dbe) {
			LOGGER.log(Level.SEVERE, "Update DB operation error...", dbe);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Update DB operation error...")
					.build();
		}
	}

	@POST
	@Path("delete/")
	public Response deleteUser(User user) {

		LOGGER.info("Deleting user...");

		try {
			userService.deleteUser(user);
			return Response.status(Response.Status.ACCEPTED).build();

		} catch (ResourceDatabaseOperationException dbe) {
			LOGGER.log(Level.SEVERE, "Delete DB operation error...", dbe);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Delete DB operation error...")
					.build();
		}
	}

	@GET
	@Path("displayAll/")
	public Response getAllUsers() {

		LOGGER.info("Retrieving all users...");

		try {
			Future<List<User>> users = userService.getAllUsers();

			if (CollectionUtils.isEmpty(users.get())) {
				return Response.status(Response.Status.NOT_FOUND).entity("No users were found").build();
			}

			return Response.ok(new GenericEntity<List<User>>(users.get()) {
			}).build();

		} catch (ResourceDatabaseOperationException | InterruptedException | ExecutionException dbe) {
			LOGGER.log(Level.SEVERE, "Retrieve all DB operation error...", dbe);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Retrieve all DB operation error...")
					.build();
		}
	}

	@GET
	@Path("find/")
	public Response findUser(@QueryParam("email") String email) {

		LOGGER.info("Retrieving a single user...");

		try {
			Future<User> user = userService.findUser(email);
			if (user == null) {
				return Response.status(Response.Status.NOT_FOUND).entity("User not found for email: " + email).build();
			}

			return Response.ok().entity(user.get()).build();

		} catch (ResourceDatabaseOperationException | InterruptedException | ExecutionException dbe) {
			LOGGER.log(Level.SEVERE, "Retrieve DB operation error...", dbe);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Retrieve DB operation error...")
					.build();
		}
	}
}
