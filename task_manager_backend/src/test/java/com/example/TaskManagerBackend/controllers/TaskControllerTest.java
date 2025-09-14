package com.example.TaskManagerBackend.controllers;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import com.example.TaskManagerBackend.models.Priority;
import com.example.TaskManagerBackend.models.Status;
import com.example.TaskManagerBackend.models.Task;
import com.example.TaskManagerBackend.service.TaskService;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;
    
    @Test
    @DisplayName("Test to get all tasks")
    public void getAllTasks() throws Exception{
        // Arrange
         when(taskService.getAllTasks())
                .thenReturn(Arrays.asList(new Task(1, "test", "test", Status.OPEN, LocalDateTime.now(), Priority.LOW),
                                           new Task(2, "test2", "test2", Status.INPROGRESS, LocalDateTime.now(), Priority.MEDIUM),
                                           new Task(3, "test3", "test3", Status.CLOSED, LocalDateTime.now(), Priority.HIGH)));
        
        // Act and Assert
        mockMvc.perform(get("/tasks")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("test"))
                .andExpect(jsonPath("$[0].description").value("test"))
                .andExpect(jsonPath("$[0].status").value(Status.OPEN.toString()))
                .andExpect(jsonPath("$[0].priority").value(Priority.LOW.toString()))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("test2"))
                .andExpect(jsonPath("$[1].description").value("test2"))
                .andExpect(jsonPath("$[1].status").value(Status.INPROGRESS.toString()))
                .andExpect(jsonPath("$[1].priority").value(Priority.MEDIUM.toString()))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].title").value("test3"))
                .andExpect(jsonPath("$[2].description").value("test3"))
                .andExpect(jsonPath("$[2].status").value(Status.CLOSED.toString()))
                .andExpect(jsonPath("$[2].priority").value(Priority.HIGH.toString()));
        
    }

    @Test
    @DisplayName("Test to get a task")
    public void getTask() throws Exception {
        // Arrange
         when(taskService.getTask(1))
                .thenReturn(new Task(1, "test", "test", Status.OPEN, LocalDateTime.now(), Priority.LOW));
        // Act and Assert
        mockMvc.perform(get("/task/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("test"))
                .andExpect(jsonPath("$.description").value("test"))
                .andExpect(jsonPath("$.status").value(Status.OPEN.toString()))
                .andExpect(jsonPath("$.priority").value(Priority.LOW.toString()))
                ;
        
    }

    @Test
    @DisplayName("Test to post a task")
    public void postTask() throws Exception {
        // Arrange
        Task newTask = new Task(1, "test", "test", Status.OPEN, LocalDateTime.now(), Priority.LOW);
        when(taskService.createTask(newTask)).thenReturn(newTask);

        // Act and Assert
        mockMvc.perform(post("/createtask")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"test\", \"description\":\"test\", \"status\":\"OPEN\", \"priority\":\"LOW\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Test to update a task")
    public void updateTask() throws Exception {
        // Arrange
        Task updatedTask = new Task(1, "updated title", "updated description", Status.INPROGRESS, LocalDateTime.now(), Priority.MEDIUM);
        when(taskService.updateTask(1, updatedTask)).thenReturn(updatedTask);

        // Act and Assert
        mockMvc.perform(put("/updatetask/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"updated title\", \"description\":\"updated description\", \"status\":\"INPROGRESS\", \"priority\":\"MEDIUM\"}"))
                .andExpect(status().isOk());
                
    }
}
