package com.example.todolistapp.repository;

import com.example.todolistapp.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    @Query("SELECT t FROM Todo t WHERE t.completed = false AND t.user.id = :userId")
    List<Todo> findPendingByUser(@Param("userId") Long userId);

    List<Todo> findByUserId(Long userId);
}

