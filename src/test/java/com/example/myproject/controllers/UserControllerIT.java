package com.example.myproject.controllers;

import com.example.myproject.entities.User;
import com.example.myproject.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@GraphQlTest
public class UserControllerIT {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockBean
    private UserService userService;

    @Test
    public void getUsers() {
        String query = """
            query {
                getUsers {
                    id
                    name
                }
            }
        """;

        User user1 = new User();
        user1.setId(1L);
        user1.setName("foo");
        User user2 = new User();
        user2.setId(2L);
        user2.setName("bar");
        when(userService.getUsers()).thenReturn(List.of(user1, user2));

        graphQlTester.document(query)
                .execute()
                .path("getUsers[0].id").entity(Long.class).isEqualTo(1L)
                .path("getUsers[0].name").entity(String.class).isEqualTo("foo")
                .path("getUsers[1].id").entity(Long.class).isEqualTo(2L)
                .path("getUsers[1].name").entity(String.class).isEqualTo("bar");
    }

    @Test
    public void getUser() {
        String query = """
            query {
                getUser(id: 1) {
                    id
                    name
                }
            }
        """;

        User user1 = new User();
        user1.setId(1L);
        user1.setName("foo");
        when(userService.getUser(1L)).thenReturn(Optional.of(user1));

        graphQlTester.document(query)
                .execute()
                .path("getUser.id").entity(Long.class).isEqualTo(1L)
                .path("getUser.name").entity(String.class).isEqualTo("foo");
    }

    @Test
    public void getUserNotFound() {
        String query = """
            query {
                getUser(id: 1) {
                    id
                    name
                }
            }
        """;

        when(userService.getUser(1L)).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND"));

        graphQlTester.document(query)
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
        String mutation = """
            mutation($name: String!) {
                addUser(userRequest: { name: $name}) {
                    id
                    name
                }
            }
        """;

        User user1 = new User();
        user1.setId(1L);
        user1.setName("foo");
        when(userService.addUser(any(User.class))).thenReturn(user1);

        graphQlTester.document(mutation).variable("name", "foo")
                .execute()
                .path("addUser.id").entity(Long.class).isEqualTo(1L)
                .path("addUser.name").entity(String.class).isEqualTo("foo");
    }

    @Test
    public void deleteUser() {
        String mutation = """
            mutation {
                deleteUser(id: 1)
            }
        """;

        graphQlTester.document(mutation)
                .execute()
                .path("deleteUser").entity(String.class).isEqualTo("User with id 1 has been deleted");

        verify(userService).deleteUser(1L);
    }
}

