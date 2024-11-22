package com.example.myproject.services;

import com.example.myproject.entities.User;
import com.example.myproject.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("foo");
        User user2 = new User();
        user2.setId(2L);
        user2.setName("bar");
        List<User> users = List.of(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getUsers();

        assertEquals(users, result);
    }

    @Test
    void getUser() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("foo");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        User result = userService.getUser(1L);

        assertEquals(user1, result);
    }

    @Test
    void getUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(HttpClientErrorException.class, () -> userService.getUser(1L));
    }

    @Test
    void addUser() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("foo");
        when(userRepository.save(any(User.class))).thenReturn(user1);

        User result = userService.addUser(user1);

        assertEquals(user1, result);
    }

    @Test
    void deleteUser() {
        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }
}