package com.example.TaskManagerBackend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.TaskManagerBackend.models.Task;
import com.example.TaskManagerBackend.repository.TaskRepository;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;

    public Task createTask(Task task){
        task.setCreationDateTime(LocalDateTime.now());
        return taskRepository.save(task);
    }

    public Task updateTask(int id, Task newTask){
         return taskRepository.findById(id)
      .map(task -> {
        task.setTitle(newTask.getTitle());
        task.setStatus(newTask.getStatus());
        task.setPriority(newTask.getPriority());
        task.setDescription(newTask.getDescription());
        return taskRepository.save(task);
      }).get();
    }

    public Task getTask(int id) {
        return taskRepository.findById(id).get();
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
}
