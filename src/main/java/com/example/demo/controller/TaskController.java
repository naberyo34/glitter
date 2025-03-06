package com.example.demo.controller;

import java.util.List;
import com.example.demo.domain.Task;
import com.example.demo.mapper.TaskRepository;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class TaskController {
  private final TaskRepository taskRepository;

  public TaskController(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  @GetMapping("/tasks")
  public List<Task> getTasks() {
    return taskRepository.findAll();
  }
}
