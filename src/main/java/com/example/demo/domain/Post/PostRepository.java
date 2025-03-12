package com.example.demo.domain.Post;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.generated.Post;
import com.example.demo.generated.PostDynamicSqlSupport;
import com.example.demo.generated.PostMapper;

@Repository
public class PostRepository {
  @Autowired
  private PostMapper postMapper;

  /**
   * ユーザーIDに紐づく投稿を取得する
   * 
   * @param userId
   * @return 投稿のリスト
   */
  @Transactional
  public List<Post> findByUserId(String userId) {
    return postMapper.select((c) -> c.where(PostDynamicSqlSupport.userId, isEqualTo(userId))
        .orderBy(PostDynamicSqlSupport.createdAt.descending()));
  }
}
