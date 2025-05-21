package com.example.todolistapp.service;

import com.example.todolistapp.dto.TodoRequest;
import com.example.todolistapp.dto.TodoResponse;

import java.util.List;

public interface TodoService {
    TodoResponse createTodo(String username, TodoRequest request);
    List<TodoResponse> getAllTodos(String username);
    List<TodoResponse> getPendingTodos(String username);
    TodoResponse getTodoById(String username, Long id);
    TodoResponse updateTodo(String username, Long id, TodoRequest request);
    TodoResponse patchTodoCompletion(String username, Long id, boolean completed);
    void deleteTodo(String username, Long id);
}
