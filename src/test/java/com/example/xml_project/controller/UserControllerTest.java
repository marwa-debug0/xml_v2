package com.example.xml_project.controller;

import com.example.xml_project.model.User;
import com.example.xml_project.service.UserService;
import com.example.xml_project.service.XmlValidatorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration de la couche HTTP — UserController.
 *
 * @WebMvcTest charge uniquement le contexte Spring MVC (pas la DB).
 * Les services sont simulés par des @MockBean.
 */
@WebMvcTest(UserController.class)
@DisplayName("Tests HTTP — UserController")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;       // Simule les requêtes HTTP sans serveur réel

    @MockBean
    private UserService userService;

    @MockBean
    private XmlValidatorService xmlValidatorService;

    @Autowired
    private ObjectMapper objectMapper;

    // ─────────────────────────────────────────────
    // GET /api/users
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/users — doit retourner 200 avec la liste des utilisateurs")
    void getAllUsers_shouldReturn200WithList() throws Exception {
        User alice = new User(1L, "Alice", "alice@mail.com", "motdepasse");
        when(userService.getAllUsers()).thenReturn(List.of(alice));

        mockMvc.perform(get("/api/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[0].email").value("alice@mail.com"));
    }

    // ─────────────────────────────────────────────
    // GET /api/users/{id}
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/users/{id} — doit retourner 200 si l'utilisateur existe")
    void getUserById_shouldReturn200_whenFound() throws Exception {
        User alice = new User(1L, "Alice", "alice@mail.com", "motdepasse");
        when(userService.getUserById(1L)).thenReturn(Optional.of(alice));

        mockMvc.perform(get("/api/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    @DisplayName("GET /api/users/{id} — doit retourner 404 si introuvable")
    void getUserById_shouldReturn404_whenNotFound() throws Exception {
        when(userService.getUserById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    // ─────────────────────────────────────────────
    // POST /api/users
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/users — doit retourner 201 Created avec l'utilisateur créé")
    void createUser_shouldReturn201() throws Exception {
        User input  = new User(null, "Alice", "alice@mail.com", "motdepasse");
        User saved  = new User(1L,   "Alice", "alice@mail.com", "motdepasse");
        when(userService.createUser(any())).thenReturn(saved);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    // ─────────────────────────────────────────────
    // PUT /api/users/{id}
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("PUT /api/users/{id} — doit retourner 200 si la mise à jour réussit")
    void updateUser_shouldReturn200_whenFound() throws Exception {
        User updated = new User(1L, "Alicia", "alicia@mail.com", "newpass");
        when(userService.updateUser(any(), any())).thenReturn(Optional.of(updated));

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alicia"));
    }

    @Test
    @DisplayName("PUT /api/users/{id} — doit retourner 404 si introuvable")
    void updateUser_shouldReturn404_whenNotFound() throws Exception {
        when(userService.updateUser(any(), any())).thenReturn(Optional.empty());
        User dummy = new User(null, "X", "x@x.com", "xxxx");

        mockMvc.perform(put("/api/users/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dummy)))
                .andExpect(status().isNotFound());
    }

    // ─────────────────────────────────────────────
    // DELETE /api/users/{id}
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /api/users/{id} — doit retourner 204 si supprimé")
    void deleteUser_shouldReturn204_whenFound() throws Exception {
        when(userService.deleteUser(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} — doit retourner 404 si introuvable")
    void deleteUser_shouldReturn404_whenNotFound() throws Exception {
        when(userService.deleteUser(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/users/99"))
                .andExpect(status().isNotFound());
    }
}
