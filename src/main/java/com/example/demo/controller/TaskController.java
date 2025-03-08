package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Task;
import com.example.demo.mapper.TaskRepository;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/task")
public class TaskController {
  @Autowired
  private TaskRepository taskRepository;

  @Operation(
    summary = "タスクの全件取得",
    description = "全てのタスクを取得します。"
  )
  @GetMapping("/all")
  public List<Task> findAll() {
    return taskRepository.findAll();
  }

  @Operation(
    summary = "タスクの取得（未着手）",
    description = "未着手のタスクを取得します。"
  )
  @GetMapping("/in_progress")
  public List<Task> findInProgress() {
    return taskRepository.findInProgress();
  }

  @Operation(
    summary = "タスクの取得（完了）",
    description = "完了したタスクを取得します。"
  )
  @GetMapping("/done")
  public List<Task> findDone() {
    return taskRepository.findDone();
  }

  @Operation(
    summary = "タスクの取得",
    description = "指定したIDのタスクを取得します。"
  )
  @GetMapping("/{id}")
  public Task findTask(@PathVariable Integer id) {
    return taskRepository.findById(id);
  }
}
