package test.com.rk.usermanagement.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.rk.usermanagement.model.User;
import com.rk.usermanagement.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/application-config-test.xml")
public class UserServiceIntegrationTest {

	@Autowired
	private UserService userService;

	@Test
	public void getAllUsersTest() throws InterruptedException, ExecutionException {
		assertNotNull(userService);

		Future<List<User>> users = userService.getAllUsers();
		validateUserCollection(users, AssertType.ASSERT_FALSE);
	}

	@Test
	public void findUserTest() throws InterruptedException, ExecutionException {
		assertNotNull(userService);

		Future<User> user = userService.findUser("sam@test.com");
		validateUser(user, AssertType.ASSERT_NOT_NULL);

		Future<User> user2 = userService.findUser("notFound@test.com");
		validateUser(user2, AssertType.ASSERT_NULL);
	}

	@Test
	public void createUserTest() throws InterruptedException, ExecutionException {
		assertNotNull(userService);

		User newUser = new User();
		newUser.setEmail("newUser@test.com");
		newUser.setName("newUser");
		newUser.setRole(Arrays.asList("tester", "qa"));
		userService.addUser(newUser);

		Future<User> user = userService.findUser(newUser.getEmail());
		validateUser(user, AssertType.ASSERT_NOT_NULL);
	}

	@Test
	public void updateUserTest() throws InterruptedException, ExecutionException {
		assertNotNull(userService);

		Future<User> userBeforeUpdate = userService.findUser("sam@test.com");
		validateUser(userBeforeUpdate, AssertType.ASSERT_NOT_NULL);

		List<String> oldRoles = userBeforeUpdate.get().getRole();
		assertFalse(oldRoles.isEmpty());

		userBeforeUpdate.get().setRole(Arrays.asList("supervisor", "admin"));
		Future<User> userAfterUpdate = userService.updateUser(userBeforeUpdate.get());
		validateUser(userAfterUpdate, AssertType.ASSERT_NOT_NULL);

		List<String> newRoles = userAfterUpdate.get().getRole();
		assertFalse(newRoles.isEmpty());
		assertFalse(oldRoles.equals(newRoles));
	}

	@Test
	public void deleteUserTest() throws InterruptedException, ExecutionException {
		assertNotNull(userService);

		Future<User> userToDelete = userService.findUser("sid@test.com");
		validateUser(userToDelete, AssertType.ASSERT_NOT_NULL);

		userService.deleteUser(userToDelete.get());
		Thread.sleep(5000);
		Future<User> userAfterDelete = userService.findUser(userToDelete.get().getEmail());
		validateUser(userAfterDelete, AssertType.ASSERT_NULL);
	}

	private void validateUser(Future<User> user, AssertType assertType)
			throws InterruptedException, ExecutionException {

		while (true) {
			if (user.isDone()) {
				switch (assertType) {
				case ASSERT_NULL:
					assertNull(user.get());
					break;
				case ASSERT_NOT_NULL:
					assertNotNull(user.get());
					break;
				case ASSERT_TRUE:
					// TODO
					break;
				case ASSERT_FALSE:
					// TODO
					break;
				default:
					// TODO
					break;
				}

				break;
			}
			Thread.sleep(1000);
		}
	}

	private void validateUserCollection(Future<List<User>> users, AssertType assertType)
			throws InterruptedException, ExecutionException {

		while (true) {
			if (users.isDone()) {
				switch (assertType) {
				case ASSERT_NULL:
					// TODO
					break;
				case ASSERT_NOT_NULL:
					// TODO
					break;
				case ASSERT_TRUE:
					// TODO
					break;
				case ASSERT_FALSE:
					assertFalse(users.get().isEmpty());
					break;
				default:
					// TODO
					break;
				}

				break;
			}
			Thread.sleep(1000);
		}
	}

	private enum AssertType {
		ASSERT_NULL, ASSERT_NOT_NULL, ASSERT_TRUE, ASSERT_FALSE
	}
}
