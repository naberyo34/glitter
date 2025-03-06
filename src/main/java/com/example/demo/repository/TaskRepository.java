package com.example.demo.mapper;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.example.demo.domain.Task;
import com.example.demo.mapper.TaskMapper;
import org.mybatis.dynamic.sql.select.SelectDSLCompleter;

@Repository
public class TaskRepository {
  private final TaskMapper taskMapper;

  public TaskRepository(TaskMapper taskMapper) {
    this.taskMapper = taskMapper;
  }

  public List<Task> findAll() {
    return taskMapper.select(SelectDSLCompleter.allRows());
  };
}
