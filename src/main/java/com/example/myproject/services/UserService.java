package com.example.myproject.services;

import com.example.myproject.entities.User;
import com.example.myproject.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private static final String USER_NOT_FOUND = "USER_NOT_FOUND";

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Cacheable(value = "allUsersCache", key = "0")
    public List<User> getUsers() {
        log.info("Fetching users from database");
        return userRepository.findAll();
    }

    @Cacheable(value = "singleUserCache", key = "#id")
    public User getUser(@PathVariable Long id) {
        log.info("Fetching user with id {} from database", id);
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, USER_NOT_FOUND);
        }
    }

    @CacheEvict(value = "allUsersCache", key = "0")
    public User addUser(@RequestBody User user) {
        log.info("Adding {} to database", user.getName());
        return userRepository.save(user);
    }

    @Caching(evict = {
            @CacheEvict(value = "allUsersCache", key = "0"),
            @CacheEvict(value = "singleUserCache", key = "#id")
    })
    public void deleteUser(@PathVariable Long id) {
        log.info("Deleting user with id {} from database", id);
        userRepository.deleteById(id);
    }
}
