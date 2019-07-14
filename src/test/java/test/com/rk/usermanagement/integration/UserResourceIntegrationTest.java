package test.com.rk.usermanagement.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;

import com.rk.usermanagement.model.User;
import com.rk.usermanagement.service.UserService;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;

public class UserResourceIntegrationTest extends JerseyTest {

	@Override
	protected AppDescriptor configure() {

		ClientConfig config = new DefaultClientConfig();
		config.getClasses().add(UserService.class);

		return new WebAppDescriptor.Builder("restful.server.resource")
				.contextParam("contextConfigLocation", "classpath:/application-config-test.xml").contextPath("/")
				.servletClass(SpringServlet.class)
				.initParam("com.sun.jersey.config.property.packages", "com.rk.usermanagement")
				.contextListenerClass(ContextLoaderListener.class).requestListenerClass(RequestContextListener.class)
				.clientConfig(config).build();
	}

	@Test
	public void getAllUsersTest() {

		WebResource webResource = resource();
		List<User> users = webResource.path("users/displayAll").accept(MediaType.APPLICATION_JSON)
				.get(new GenericType<List<User>>() {
				});

		assertFalse(CollectionUtils.isEmpty(users));
	}

	@Test
	public void findUserTest() {

		WebResource webResource = resource();
		ClientResponse response = webResource.path("users/find").queryParam("email", "sam@test.com")
				.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

		assertEquals(HttpStatus.OK.value(), response.getStatus());
	}

	@Test
	public void addUserTest() {

		User newUser = new User();
		newUser.setName("Dagon");
		newUser.setEmail("dagon@opec.com");
		newUser.setRole(Arrays.asList("user", "superuser"));

		WebResource webResource = resource();
		ClientResponse response = webResource.path("users/add").type("application/json")
				.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, newUser);

		assertEquals(HttpStatus.CREATED.value(), response.getStatus());
	}

	@Test
	public void updateUserTest() {

		User updateUser = new User();
		updateUser.setId(1L);
		updateUser.setName("Samuel");
		updateUser.setEmail("sam@test.com");
		updateUser.setRole(Arrays.asList("qa", "oss"));

		WebResource webResource = resource();
		ClientResponse response = webResource.path("users/update").type("application/json")
				.accept(MediaType.APPLICATION_JSON).put(ClientResponse.class, updateUser);

		assertEquals(HttpStatus.ACCEPTED.value(), response.getStatus());
	}

	@Test
	public void deleteUserTest() {

		User updateUser = new User();
		updateUser.setId(3L);
		updateUser.setName("Sid");
		updateUser.setEmail("sid@test.com");
		updateUser.setRole(Arrays.asList("tester"));

		WebResource webResource = resource();
		ClientResponse response = webResource.path("users/delete").type("application/json")
				.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, updateUser);

		assertEquals(HttpStatus.ACCEPTED.value(), response.getStatus());
	}
}
