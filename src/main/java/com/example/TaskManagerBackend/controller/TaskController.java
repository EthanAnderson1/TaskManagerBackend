package com.example.TaskManagerBackend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.TaskManagerBackend.models.Task;
import com.example.TaskManagerBackend.repository.TaskRepository;

@Service
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    public List<Task> getTasks(){
        return taskRepository.findAll();
    }
    
}
