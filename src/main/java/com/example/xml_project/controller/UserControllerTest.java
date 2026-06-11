package com.example.xml_project.restapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.xml_project.restapi.model.User;
import com.example.xml_project.restapi.service.UserService;
import com.example.xml_project.restapi.service.XmlValidatorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest : charge uniquement la couche HTTP (pas la DB)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc; // simule les requêtes HTTP

    @MockBean
    private UserService userService;

    @MockBean
    private XmlValidatorService xmlValidatorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllUsers_shouldReturn200() throws Exception {
        User user = new User(1L, "Alice", "alice@mail.com", "1234");
        when(userService.getAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/users")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Alice"));
    }

    @Test
    void getUserById_shouldReturn404_whenNotFound() throws Exception {
        when(userService.getUserById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_shouldReturn201() throws Exception {
        User user = new User(null, "Alice", "alice@mail.com", "1234");
        User saved = new User(1L, "Alice", "alice@mail.com", "1234");
        when(userService.createUser(any())).thenReturn(saved);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteUser_shouldReturn204() throws Exception {
        when(userService.deleteUser(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }
}