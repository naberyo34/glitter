package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Task;
import com.example.demo.mapper.TaskRepository;

@RestController
@RequestMapping("/tasks")
public class TaskController {
  @Autowired
  private TaskRepository taskRepository;

  @GetMapping("/{id}")
  public Task getTask(@PathVariable Integer id) {
    return taskRepository.findById(id);
  }

  @GetMapping("/in_progress")
  public List<Task> geInProgressTasks() {
    return taskRepository.findInProgress();
  }

  @GetMapping("/done")
  public List<Task> getDoneTasks() {
    return taskRepository.findDone();
  }
}
