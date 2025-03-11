package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.User;
import com.example.demo.mapper.UserMapper;

@Repository
public class UserRepository {
  @Autowired
  private UserMapper userMapper;

  /**
   * ID からユーザーを取得する
   * 
   * @param id
   * @return 合致するユーザー (存在しない場合は null)
   */
  @Transactional
  public User findById(String id) {
    return userMapper.selectByPrimaryKey(id).orElse(null);
  }
}
