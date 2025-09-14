package com.example.TaskManagerBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.TaskManagerBackend.models.Task;
@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    
}
