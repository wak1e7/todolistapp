package com.example.todolistapp.controller;

import com.example.todolistapp.dto.TodoRequest;
import com.example.todolistapp.dto.TodoResponse;
import com.example.todolistapp.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoSvc;

    public TodoController(TodoService todoSvc) {
        this.todoSvc = todoSvc;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<TodoResponse> create(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody TodoRequest req
    ) {
        TodoResponse out = todoSvc.createTodo(user.getUsername(), req);
        return ResponseEntity.status(201).body(out);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<TodoResponse>> list(
            @AuthenticationPrincipal UserDetails user
    ) {
        return ResponseEntity.ok(todoSvc.getAllTodos(user.getUsername()));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/pending")
    public ResponseEntity<List<TodoResponse>> listPending(
            @AuthenticationPrincipal UserDetails user
    ) {
        return ResponseEntity.ok(
                todoSvc.getPendingTodos(user.getUsername())
        );
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> getOne(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(todoSvc.getTodoById(user.getUsername(), id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> replace(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id,
            @Valid @RequestBody TodoRequest req
    ) {
        return ResponseEntity.ok(todoSvc.updateTodo(user.getUsername(), id, req));
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{id}")
    public ResponseEntity<TodoResponse> patchCompleted(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id,
            @RequestParam boolean completed
    ) {
        return ResponseEntity.ok(todoSvc.patchTodoCompletion(user.getUsername(), id, completed));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id
    ) {
        todoSvc.deleteTodo(user.getUsername(), id);
        return ResponseEntity.noContent().build();
    }
}
