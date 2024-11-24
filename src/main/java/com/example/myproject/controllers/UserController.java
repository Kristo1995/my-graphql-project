package com.example.myproject.controllers;

import com.example.myproject.entities.User;
import com.example.myproject.entities.UserRequest;
import com.example.myproject.services.ContractService;
import com.example.myproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserController {

    private final UserService userService;
    private final ContractService contractService;

    @Autowired
    public UserController(UserService userService, ContractService contractService) {
        this.userService = userService;
        this.contractService = contractService;
    }

    @QueryMapping
    public List<User> users() {
       return userService.getUsers();
    }

    @QueryMapping
    public User user(@Argument Long id) {
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

    @SchemaMapping
    public String contractId(User user) {
        return contractService.getContractId(user.getId());
    }
}
