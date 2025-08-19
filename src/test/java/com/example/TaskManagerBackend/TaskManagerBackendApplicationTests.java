package com.example.TaskManagerBackend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.TaskManagerBackend.controller.TaskController;
import com.example.TaskManagerBackend.controller.UserController;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
class TaskManagerBackendApplicationTests {

	@Autowired
	private UserController userController;

	@Autowired
	private TaskController taskController;

	@Test
	void contextLoads() {
		assertThat(userController).isNotNull();
		assertThat(taskController).isNotNull();
	}

}
