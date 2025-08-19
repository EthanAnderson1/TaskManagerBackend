package com.example.TaskManagerBackend;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import com.example.TaskManagerBackend.models.User;
import com.example.TaskManagerBackend.service.UserService;

@TestComponent
public class UserServiceTest extends MockitoExtension{

    @Mock
    UserService userService;

    @Test
    void createUser(){
        User user = new User(0, "user","password", "user");
        userService.addUser(user);
    }
}
