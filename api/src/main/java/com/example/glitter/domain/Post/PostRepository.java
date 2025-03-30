package com.example.glitter.domain.Post;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.glitter.generated.Post;
import com.example.glitter.generated.PostDynamicSqlSupport;
import com.example.glitter.generated.PostMapper;

@Repository
public class PostRepository {
  @Autowired
  private PostMapper postMapper;

  /**
   * ID から投稿を取得する
   * 
   * @param id
   * @return 合致する投稿 (存在しない場合は null)
   */
  @Transactional
  public Optional<Post> findById(Long id) {
    return postMapper.selectOne(c -> c.where(PostDynamicSqlSupport.id, isEqualTo(id)));
  }

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

  /**
   * 投稿を追加する
   * @return 追加した投稿
   */
  @Transactional
  public Post insert(Post post) {
    postMapper.insert(post);
    return post;
  }
}
