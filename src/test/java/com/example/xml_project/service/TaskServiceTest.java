package com.example.xml_project.service;

import com.example.xml_project.model.Task;
import com.example.xml_project.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task(1L, "Write report", false);
    }

    @Test
    void getAllTasks_shouldReturnList() {
        when(taskRepository.findAll()).thenReturn(List.of(task));

        List<Task> result = taskService.getAllTasks();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Write report");
    }

    @Test
    void getTaskById_shouldReturnTask_whenExists() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Optional<Task> result = taskService.getTaskById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().isCompleted()).isFalse();
    }

    @Test
    void getTaskById_shouldReturnEmpty_whenNotExists() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(taskService.getTaskById(99L)).isEmpty();
    }

    @Test
    void createTask_shouldSaveAndReturn() {
        when(taskRepository.save(task)).thenReturn(task);

        Task result = taskService.createTask(task);

        assertThat(result.getTitle()).isEqualTo("Write report");
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void updateTask_shouldUpdate_whenExists() {
        Task updated = new Task(null, "Submit report", true);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<Task> result = taskService.updateTask(1L, updated);

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Submit report");
        assertThat(result.get().isCompleted()).isTrue();
    }

    @Test
    void updateTask_shouldReturnEmpty_whenNotExists() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(taskService.updateTask(99L, task)).isEmpty();
        verify(taskRepository, never()).save(any());
    }

    @Test
    void deleteTask_shouldReturnTrue_whenExists() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        assertThat(taskService.deleteTask(1L)).isTrue();
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteTask_shouldReturnFalse_whenNotExists() {
        when(taskRepository.existsById(99L)).thenReturn(false);

        assertThat(taskService.deleteTask(99L)).isFalse();
        verify(taskRepository, never()).deleteById(any());
    }
}
