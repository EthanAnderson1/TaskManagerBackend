package com.example.TaskManagerBackend.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.TaskManagerBackend.models.User;
import com.example.TaskManagerBackend.service.UserService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("users")
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("user")
    public User getMethodName(@RequestParam String username) {
        return userService.getUser(username);
    }
    
    
    @PostMapping("user")
    public User postUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    @DeleteMapping("user/{username}")
    public void deleteUser(@RequestParam String username){
        userService.removeUser(username);
    }
}
