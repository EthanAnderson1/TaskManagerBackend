package com.example.TaskManagerBackend.controller;

import java.util.List;
import com.example.TaskManagerBackend.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.example.TaskManagerBackend.models.Task;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping("tasks")
    public List<Task> getTasks(){
        return taskService.getAllTasks();
    }

    @GetMapping("task/{id}")
    public Task getTask(@RequestParam int id) {
        return taskService.getTask(id);
    }

    @PostMapping("task")
    public Task postTask(@RequestBody Task task) {
        return taskService.createTask(task);
    }
    @PutMapping("task/{id}")
    public Task putTask(@PathVariable int id, @RequestBody Task newTask) {
       return taskService.updateTask(id, newTask);
    }
}
