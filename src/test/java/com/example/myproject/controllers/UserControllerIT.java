package com.example.myproject.controllers;

import com.example.myproject.entities.User;
import com.example.myproject.services.ContractService;
import com.example.myproject.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@GraphQlTest(UserController.class)
public class UserControllerIT {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockBean
    private UserService userService;

    @MockBean
    private ContractService contractService;

    @Test
    public void users() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("foo");
        User user2 = new User();
        user2.setId(2L);
        user2.setName("bar");
        when(userService.getUsers()).thenReturn(List.of(user1, user2));
        when(contractService.getContractId(1L)).thenReturn("1ABC");
        when(contractService.getContractId(2L)).thenReturn("2ABC");

        graphQlTester
                .documentName("users")
                .execute()
                .path("users[0].id").entity(Long.class).isEqualTo(1L)
                .path("users[0].name").entity(String.class).isEqualTo("foo")
                .path("users[0].contractId").entity(String.class).isEqualTo("1ABC")
                .path("users[1].id").entity(Long.class).isEqualTo(2L)
                .path("users[1].name").entity(String.class).isEqualTo("bar")
                .path("users[1].contractId").entity(String.class).isEqualTo("2ABC");
    }

    @Test
    public void user() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("foo");
        when(userService.getUser(1L)).thenReturn(user1);
        when(contractService.getContractId(1L)).thenReturn("1ABC");

        graphQlTester
                .documentName("user")
                .variable("id", 1L)
                .execute()
                .path("user.id").entity(Long.class).isEqualTo(1L)
                .path("user.name").entity(String.class).isEqualTo("foo")
                .path("user.contractId").entity(String.class).isEqualTo("1ABC");
    }

    @Test
    public void userNotFound() {
        when(userService.getUser(1L)).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND"));

        graphQlTester
                .documentName("user")
                .variable("id", 1L)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertThat(errors).hasSize(1); // Expecting exactly one error
                    assertThat(errors.getFirst().getMessage())
                            .isEqualTo("HTTP error occurred: 404 USER_NOT_FOUND"); // Validate the error message
                });
    }

    @Test
    public void addUser() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("foo");
        when(userService.addUser(any(User.class))).thenReturn(user1);

        graphQlTester
                .documentName("addUser")
                .variable("name", "foo")
                .execute()
                .path("addUser.id").entity(Long.class).isEqualTo(1L)
                .path("addUser.name").entity(String.class).isEqualTo("foo");
    }

    @Test
    public void deleteUser() {
        graphQlTester
                .documentName("deleteUser")
                .variable("id", 1L)
                .execute()
                .path("deleteUser").entity(String.class).isEqualTo("User with id 1 has been deleted");

        verify(userService).deleteUser(1L);
    }
}

