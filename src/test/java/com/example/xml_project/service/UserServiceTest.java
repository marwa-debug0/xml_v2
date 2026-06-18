package com.example.xml_project.service;

import com.example.xml_project.model.User;
import com.example.xml_project.repository.UserRepository;
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

// @ExtendWith : enables Mockito for this test
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Alice", "alice@mail.com", "1234");
    }

    @Test
    void getAllUsers_shouldReturnList() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> result = userService.getAllUsers();

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
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUser_shouldUpdate_whenExists() {
        User updated = new User(null, "Bob", "bob@mail.com", "5678");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<User> result = userService.updateUser(1L, updated);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Bob");
        assertThat(result.get().getEmail()).isEqualTo("bob@mail.com");
    }

    @Test
    void updateUser_shouldReturnEmpty_whenNotExists() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<User> result = userService.updateUser(99L, user);

        assertThat(result).isEmpty();
        verify(userRepository, never()).save(any());
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
        verify(userRepository, never()).deleteById(any());
    }
}
