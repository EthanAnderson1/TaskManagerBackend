package com.example.TaskManagerBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.TaskManagerBackend.models.Task;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    
}
