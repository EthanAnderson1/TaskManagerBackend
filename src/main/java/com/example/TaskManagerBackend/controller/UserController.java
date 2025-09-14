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
import org.springframework.web.bind.annotation.ResponseStatus;
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
    public User getMethodName(@RequestParam("username") String username) {
        return userService.getUser(username);
    }
    
    
    @PostMapping("createuser")
    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    public User postUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    @PostMapping("login")
    public String login(@RequestBody User user){
        return userService.verify(user);
    }

    
    @DeleteMapping("deleteuser")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void deleteUser(@RequestParam("username") String username){
        userService.removeUser(username);
    }
}
