package com.example.todolistapp.service;

import com.example.todolistapp.dto.TodoRequest;
import com.example.todolistapp.dto.TodoResponse;
import com.example.todolistapp.entity.Todo;
import com.example.todolistapp.entity.User;
import com.example.todolistapp.repository.TodoRepository;
import com.example.todolistapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepo;
    private final UserRepository userRepo;

    public TodoServiceImpl(TodoRepository todoRepo, UserRepository userRepo) {
        this.todoRepo = todoRepo;
        this.userRepo = userRepo;
    }

    private User mustFindUser(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    private Todo mustFindTodo(Long id) {
        return todoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Todo no encontrado"));
    }

    private TodoResponse toResponse(Todo t) {
        return TodoResponse.builder()
                .id(t.getId())
                .title(t.getTitle())
                .description(t.getDescription())
                .completed(t.isCompleted())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .userId(t.getUser().getId())
                .build();
    }

    @Override
    @Transactional
    public TodoResponse createTodo(String username, TodoRequest req) {
        User user = mustFindUser(username);
        Todo t = Todo.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .completed(req.getCompleted() != null && req.getCompleted())
                .user(user)
                .build();
        Todo saved = todoRepo.save(t);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TodoResponse> getAllTodos(String username) {
        User user = mustFindUser(username);
        return todoRepo.findByUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TodoResponse> getPendingTodos(String username) {
        User user = mustFindUser(username);
        return todoRepo.findPendingByUser(user.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TodoResponse getTodoById(String username, Long id) {
        User user = mustFindUser(username);
        Todo t = mustFindTodo(id);
        if (!t.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("No tienes permiso sobre esta tarea");
        }
        return toResponse(t);
    }

    @Override
    @Transactional
    public TodoResponse updateTodo(String username, Long id, TodoRequest req) {
        User user = mustFindUser(username);
        Todo t = mustFindTodo(id);
        if (!t.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("No tienes permiso para editar esta tarea");
        }
        t.setTitle(req.getTitle());
        t.setDescription(req.getDescription());
        t.setCompleted(req.getCompleted() != null && req.getCompleted());
        // updatedAt se ajusta en @PreUpdate
        return toResponse(t);
    }

    @Override
    @Transactional
    public TodoResponse patchTodoCompletion(String username, Long id, boolean completed) {
        User user = mustFindUser(username);
        Todo t = mustFindTodo(id);
        if (!t.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("No tienes permiso para modificar esta tarea");
        }
        t.setCompleted(completed);
        return toResponse(t);
    }

    @Override
    @Transactional
    public void deleteTodo(String username, Long id) {
        User user = mustFindUser(username);
        Todo t = mustFindTodo(id);
        if (!t.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("No tienes permiso para eliminar esta tarea");
        }
        todoRepo.delete(t);
    }
}
