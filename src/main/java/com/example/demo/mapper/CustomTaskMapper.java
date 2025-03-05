package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.example.demo.domain.Task;
import com.example.demo.mapper.TaskMapper;

@Mapper
public interface CustomTaskMapper extends TaskMapper {
  @Select("SELECT * FROM task")
  List<Task> selectAll();
}
