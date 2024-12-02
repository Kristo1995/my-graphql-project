package com.example.myproject.controllers;

import com.example.myproject.entities.User;
import com.example.myproject.services.ContractService;
import com.example.myproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserController {

    private static final String USER = "USER";

    private final UserService userService;
    private final ContractService contractService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, ContractService contractService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.contractService = contractService;
        this.passwordEncoder = passwordEncoder;
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
    public User addUser(@Argument String username, @Argument String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setAuthorities(List.of(new SimpleGrantedAuthority(USER)));
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
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
