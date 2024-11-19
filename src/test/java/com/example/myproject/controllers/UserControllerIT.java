package com.example.myproject.controllers;

import com.example.myproject.entities.User;
import com.example.myproject.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
public class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void getUsers() throws Exception {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("foo");
        User user2 = new User();
        user2.setId(2L);
        user2.setName("bar");
        when(userService.getUsers()).thenReturn(List.of(user1, user2));
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().string("[{\"id\":1,\"name\":\"foo\"},{\"id\":2,\"name\":\"bar\"}]"));
    }

    @Test
    public void getUser() throws Exception {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("foo");
        when(userService.getUser(1L)).thenReturn(Optional.of(user1));
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"id\":1,\"name\":\"foo\"}"));
    }

    @Test
    public void getUserNotFound() throws Exception {
        when(userService.getUser(1L)).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND"));
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("{\"message\":\"404 USER_NOT_FOUND\"}"));
    }

    @Test
    public void addUser() throws Exception {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("foo");
        when(userService.addUser(any(User.class))).thenReturn(user1);
        mockMvc.perform(post("/users").content("{\"name\":\"foo\"}").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"id\":1,\"name\":\"foo\"}"));
    }

    @Test
    public void deleteUser() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
        verify(userService).deleteUser(1L);
    }
}