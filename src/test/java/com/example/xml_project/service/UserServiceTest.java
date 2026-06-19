package com.example.xml_project.service;

import com.example.xml_project.model.User;
import com.example.xml_project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires de la couche service UserService.
 *
 * Utilise Mockito pour simuler le repository (aucune base de données réelle).
 * Pattern : GIVEN / WHEN / THEN pour une meilleure lisibilité.
 */
@ExtendWith(MockitoExtension.class)   // Active Mockito sans Spring
@DisplayName("Tests unitaires — UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;   // Simule la base de données

    @InjectMocks
    private UserService userService;         // Service à tester (avec mock injecté)

    private User alice;

    @BeforeEach
    void setUp() {
        alice = new User(1L, "Alice", "alice@mail.com", "motdepasse");
    }

    // ─────────────────────────────────────────────
    // getAllUsers()
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("getAllUsers — doit retourner la liste complète")
    void getAllUsers_shouldReturnList() {
        // GIVEN
        when(userRepository.findAll()).thenReturn(List.of(alice));

        // WHEN
        List<User> result = userService.getAllUsers();

        // THEN
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Alice");
        verify(userRepository, times(1)).findAll();
    }

    // ─────────────────────────────────────────────
    // getUserById()
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("getUserById — doit retourner l'utilisateur s'il existe")
    void getUserById_shouldReturnUser_whenExists() {
        // GIVEN
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));

        // WHEN
        Optional<User> result = userService.getUserById(1L);

        // THEN
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("alice@mail.com");
    }

    @Test
    @DisplayName("getUserById — doit retourner Optional vide si introuvable")
    void getUserById_shouldReturnEmpty_whenNotExists() {
        // GIVEN
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN
        Optional<User> result = userService.getUserById(99L);

        // THEN
        assertThat(result).isEmpty();
    }

    // ─────────────────────────────────────────────
    // createUser()
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("createUser — doit sauvegarder et retourner l'utilisateur")
    void createUser_shouldSaveAndReturn() {
        // GIVEN
        when(userRepository.save(alice)).thenReturn(alice);

        // WHEN
        User result = userService.createUser(alice);

        // THEN
        assertThat(result.getName()).isEqualTo("Alice");
        verify(userRepository, times(1)).save(alice);
    }

    // ─────────────────────────────────────────────
    // updateUser()
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("updateUser — doit modifier les champs si l'utilisateur existe")
    void updateUser_shouldUpdateFields_whenExists() {
        // GIVEN
        User updated = new User(null, "Alicia", "alicia@mail.com", "newpass");
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // WHEN
        Optional<User> result = userService.updateUser(1L, updated);

        // THEN
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Alicia");
        assertThat(result.get().getEmail()).isEqualTo("alicia@mail.com");
    }

    @Test
    @DisplayName("updateUser — doit retourner Optional vide si utilisateur introuvable")
    void updateUser_shouldReturnEmpty_whenNotExists() {
        // GIVEN
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN
        Optional<User> result = userService.updateUser(99L, alice);

        // THEN
        assertThat(result).isEmpty();
        verify(userRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────
    // deleteUser()
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("deleteUser — doit retourner true et appeler deleteById si l'utilisateur existe")
    void deleteUser_shouldReturnTrue_whenExists() {
        // GIVEN
        when(userRepository.existsById(1L)).thenReturn(true);

        // WHEN
        boolean result = userService.deleteUser(1L);

        // THEN
        assertThat(result).isTrue();
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteUser — doit retourner false et ne pas appeler deleteById si introuvable")
    void deleteUser_shouldReturnFalse_whenNotExists() {
        // GIVEN
        when(userRepository.existsById(99L)).thenReturn(false);

        // WHEN
        boolean result = userService.deleteUser(99L);

        // THEN
        assertThat(result).isFalse();
        verify(userRepository, never()).deleteById(any());
    }
}
