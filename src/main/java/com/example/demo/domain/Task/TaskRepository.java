package com.example.demo.domain.Task;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import java.util.List;

import org.mybatis.dynamic.sql.select.SelectDSLCompleter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.generated.Task;
import com.example.demo.generated.TaskDynamicSqlSupport;
import com.example.demo.generated.TaskMapper;


@Repository
public class TaskRepository {
  @Autowired
  private TaskMapper taskMapper;

  /**
   * タスクを追加する
   * @param value タスクの内容
   */
  @Transactional
  public void add(String value) {
    Task task = new Task();
    task.setValue(value);
    task.setIsDone(false);
    taskMapper.insert(task);
  }

  /**
   * タスクを更新する
   * @param task
   */
  @Transactional
  public void updateById(Task task) {
    taskMapper.updateByPrimaryKey(task);
  }

  /**
   * id からタスクを取得する
   * @param id
   * @return 合致するタスク (存在しない場合は null)
   */
  @Transactional
  public Task findById(Integer id) {
    return taskMapper.selectByPrimaryKey(id).orElse(null);
  }

  /**
   * id から取得したタスクの完了状態をトグルする
   * @param id
   * @return
   */
  @Transactional
  public Task toggleStatusById(Integer id) {
    Task task = taskMapper.selectByPrimaryKey(id).orElse(null);
    if (task == null) {
      return null;
    }
    task.setIsDone(!task.getIsDone());
    taskMapper.updateByPrimaryKey(task);
    return task;
  }

  /**
   * すべてのタスクを取得する
   * 
   * @return タスクのリスト
   */
  @Transactional
  public List<Task> findAll() {
    return taskMapper.select(SelectDSLCompleter.allRowsOrderedBy(TaskDynamicSqlSupport.id.descending()));
  };

  /**
   * 未完了のタスクを取得する
   * 
   * @return タスクのリスト
   */
  @Transactional
  public List<Task> findInProgress() {
    return taskMapper.select(c -> c.where(TaskDynamicSqlSupport.isDone, isEqualTo(false)).orderBy(TaskDynamicSqlSupport.id.descending()));
  }

  /**
   * 完了したタスクを取得する
   * 
   * @return タスクのリスト
   */
  @Transactional
  public List<Task> findDone() {
    return taskMapper.select(c -> c.where(TaskDynamicSqlSupport.isDone, isEqualTo(true)).orderBy(TaskDynamicSqlSupport.id.descending()));
  }
}
