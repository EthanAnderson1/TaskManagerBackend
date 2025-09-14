package com.example.TaskManagerBackend.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.example.TaskManagerBackend.models.User;
import com.example.TaskManagerBackend.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("Test getUsers method")
    public void testGetUsers() throws Exception {
        // Arrange
        when(userService.getAllUsers()).thenReturn(Arrays.asList(new User(1,"testUser", "testPassword","User"),
                                                            new User(2,"anotherUser", "anotherPassword", "User")));

        // Act and Assert
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testUser"))
                .andExpect(jsonPath("$[1].username").value("anotherUser"));
    }


    @Test
    @DisplayName("Test getUser method")
    public void testGetUser() throws Exception {
        // Arrange
        when(userService.getUser("testUser")).thenReturn(new User(1,"testUser", "testPassword","User"));
        // Act and Assert
        mockMvc.perform(get("/user").param("username", "testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"));
    }
    
    @Test
    @DisplayName("Test postUser method")
    public void testPostUser() throws Exception {
        // Arrange
        when(userService.addUser(new User(1,"newUser", "newPassword","User")))
                .thenReturn(new User(1,"newUser", "newPassword","User"));
        // Act and Assert
        mockMvc.perform(post("/createuser")
                .contentType("application/json")
                .content("{\"username\":\"newUser\", \"password\":\"newPassword\", \"role\":\"User\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Test login method")
    public void testLogin() throws Exception {
        // Arrange
        when(userService.verify(new User(1,"testUser", "testPassword","User")))
                .thenReturn("mockedToken");
        // Act and Assert
        mockMvc.perform(post("/login")
                .contentType("application/json")
                .content("{\"username\":\"testUser\", \"password\":\"testPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("mockedToken"));
    }

    @Test
    @DisplayName("Test deleteUser method")
    public void testDeleteUser() throws Exception {
        // Arrange
        Mockito.doNothing().when(userService).removeUser("newUser");
        // Act and Assert
                
        mockMvc.perform(delete("/deleteuser").param("username", "newUser"))
                .andExpect(status().isNoContent());
    }

@Test
@DisplayName("DELETE /deleteuser/{username} should return 404 when user not found")
void testDeleteUser_NotFound() throws Exception {
    // Arrange
    Mockito.doThrow(new RuntimeException("User not found"))
           .when(userService).removeUser("unknown");

    // Act & Assert
    mockMvc.perform(delete("/deleteuser/unknown"))
            .andExpect(status().isNotFound());
}

    
}
