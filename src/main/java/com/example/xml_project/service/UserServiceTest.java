package com.example.xml_project.restapi.service;

import com.example.xml_project.restapi.model.User;
import com.example.xml.restapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

// @ExtendWith : active Mockito dans ce test
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    // @Mock : simule le repository (pas de vraie DB)
    @Mock
    private UserRepository userRepository;

    // @InjectMocks : crée le service avec le mock injecté
    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Alice", "alice@mail.com", "1234");
    }

    @Test
    void getAllUsers_shouldReturnList() {
        // GIVEN : le repo retourne une liste avec un user
        when(userRepository.findAll()).thenReturn(List.of(user));

        // WHEN : on appelle le service
        List<User> result = userService.getAllUsers();

        // THEN : on vérifie le résultat
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Alice");
    }

    @Test
    void getUserById_shouldReturnUser_whenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("alice@mail.com");
    }

    @Test
    void getUserById_shouldReturnEmpty_whenNotExists() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void createUser_shouldSaveAndReturn() {
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.createUser(user);

        assertThat(result.getName()).isEqualTo("Alice");
        verify(userRepository, times(1)).save(user); // vérifie que save() a été appelé
    }

    @Test
    void deleteUser_shouldReturnTrue_whenExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        boolean result = userService.deleteUser(1L);

        assertThat(result).isTrue();
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_shouldReturnFalse_whenNotExists() {
        when(userRepository.existsById(99L)).thenReturn(false);

        boolean result = userService.deleteUser(99L);

        assertThat(result).isFalse();
        verify(userRepository, never()).deleteById(any()); // deleteById ne doit pas être appelé
    }
}