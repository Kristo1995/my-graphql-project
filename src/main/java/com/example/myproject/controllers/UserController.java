package com.example.myproject.controllers;

import com.example.myproject.entities.User;
import com.example.myproject.entities.UserRequest;
import com.example.myproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @QueryMapping
    public List<User> getUsers() {
       return userService.getUsers();
    }

    @QueryMapping
    public Optional<User> getUser(@Argument Long id) {
        return userService.getUser(id);
    }

    @MutationMapping
    public User addUser(@Argument UserRequest userRequest) {
        User user = new User();
        user.setName(userRequest.getName());
        return userService.addUser(user);
    }

    @MutationMapping
    public String deleteUser(@Argument Long id) {
        userService.deleteUser(id);
        return "User with id " + id + " has been deleted";
    }
}
