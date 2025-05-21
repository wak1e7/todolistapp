package com.example.todolistapp;

import com.example.todolistapp.dto.TodoRequest;
import com.example.todolistapp.dto.TodoResponse;
import com.example.todolistapp.dto.UserRegistrationRequest;
import com.example.todolistapp.entity.Todo;
import com.example.todolistapp.entity.User;
import com.example.todolistapp.repository.TodoRepository;
import com.example.todolistapp.repository.UserRepository;
import com.example.todolistapp.service.TodoServiceImpl;
import com.example.todolistapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodolistappApplicationTests {

    @Mock private TodoRepository todoRepo;
    @Mock private UserRepository userRepo;
    @Mock private PasswordEncoder encoder;

    @InjectMocks private TodoServiceImpl todoService;
    @InjectMocks private UserService userService;

    private User user;
    private Todo existingTodo;
    private UserRegistrationRequest regReq;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(1L)
                .username("juan")
                .email("juan@mail.com")
                .password("encoded")
                .role("ROLE_USER")
                .build();

        existingTodo = Todo.builder()
                .id(42L)
                .title("Tarea")
                .description("Descripción")
                .completed(false)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        regReq = new UserRegistrationRequest();
        regReq.setUsername("ana");
        regReq.setEmail("ana@mail.com");
        regReq.setPassword("pass123");
    }

    //TodoServiceImpl tests

    @Test
    void createTodo_success() {
        var req = new TodoRequest();
        req.setTitle("Nueva");
        req.setDescription("Desc");
        req.setCompleted(true);

        when(userRepo.findByUsername("juan")).thenReturn(Optional.of(user));

        when(todoRepo.save(any(Todo.class)))
                .thenAnswer(invocation -> {
                    Todo t = invocation.getArgument(0);
                    t.setId(100L);              // ahora funciona
                    return t;
                });

        TodoResponse resp = todoService.createTodo("juan", req);

        assertThat(resp.getId()).isEqualTo(100L);
        assertThat(resp.getTitle()).isEqualTo("Nueva");
        assertThat(resp.isCompleted()).isTrue();
        assertThat(resp.getUserId()).isEqualTo(user.getId());

        verify(todoRepo).save(any(Todo.class));
    }

    @Test
    void getAllTodos_returnsList() {
        when(userRepo.findByUsername("juan")).thenReturn(Optional.of(user));
        when(todoRepo.findByUserId(user.getId()))
                .thenReturn(List.of(existingTodo));

        List<TodoResponse> list = todoService.getAllTodos("juan");

        assertThat(list).hasSize(1)
                .first()
                .extracting(TodoResponse::getId, TodoResponse::getTitle)
                .containsExactly(42L, "Tarea");
    }

    @Test
    void getTodoById_notOwner_throws() {
        User other = User.builder().id(2L).username("otra").build();
        existingTodo.setUser(other);

        when(userRepo.findByUsername("juan")).thenReturn(Optional.of(user));
        when(todoRepo.findById(42L)).thenReturn(Optional.of(existingTodo));

        assertThatThrownBy(() -> todoService.getTodoById("juan", 42L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("permiso");
    }

    @Test
    void updateTodo_success() {
        var req = new TodoRequest();
        req.setTitle("Modif");
        req.setDescription("Desc2");
        req.setCompleted(null);

        when(userRepo.findByUsername("juan")).thenReturn(Optional.of(user));
        when(todoRepo.findById(42L)).thenReturn(Optional.of(existingTodo));

        TodoResponse updated = todoService.updateTodo("juan", 42L, req);

        assertThat(updated.getTitle()).isEqualTo("Modif");
        assertThat(updated.isCompleted()).isFalse();
        verify(todoRepo, never()).delete(any());
    }

    @Test
    void deleteTodo_notOwner_throws() {
        User other = User.builder().id(2L).username("otra").build();
        existingTodo.setUser(other);

        when(userRepo.findByUsername("juan")).thenReturn(Optional.of(user));
        when(todoRepo.findById(42L)).thenReturn(Optional.of(existingTodo));

        assertThatThrownBy(() -> todoService.deleteTodo("juan", 42L))
                .isInstanceOf(AccessDeniedException.class);
    }

    //UserService tests

    @Test
    void register_success() {
        when(userRepo.existsByUsername("ana")).thenReturn(false);
        when(userRepo.existsByEmail("ana@mail.com")).thenReturn(false);
        when(encoder.encode("pass123")).thenReturn("hashed");

        userService.register(regReq);

        ArgumentCaptor<User> capt = ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(capt.capture());
        User saved = capt.getValue();
        assertThat(saved.getUsername()).isEqualTo("ana");
        assertThat(saved.getPassword()).isEqualTo("hashed");
        assertThat(saved.getRole()).isEqualTo("ROLE_USER");
    }

    @Test
    void register_duplicateUsername_throws() {
        when(userRepo.existsByUsername("ana")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(regReq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("username ya existe");
    }

    @Test
    void register_duplicateEmail_throws() {
        when(userRepo.existsByUsername("ana")).thenReturn(false);
        when(userRepo.existsByEmail("ana@mail.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(regReq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email ya está registrado");
    }
}