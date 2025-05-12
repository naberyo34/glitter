package com.example.glitter.domain.Post;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.glitter.generated.Post;
import com.example.glitter.generated.PostDynamicSqlSupport;
import com.example.glitter.generated.PostMapper;

@Repository
public class PostRepository {
  @Autowired
  private PostMapper postMapper;

  /**
   * uuid から投稿を取得する
   * 
   * @param uuid
   * @return 合致する投稿 (存在しない場合は null)
   */
  public Optional<Post> findByUuid(String uuid) {
    return postMapper.selectOne(c -> c.where(PostDynamicSqlSupport.uuid, isEqualTo(uuid)));
  }

  /**
   * ユーザー ID とドメインに紐づく投稿のリストを取得する
   * 
   * @param userId
   * @return 投稿のリスト
   */
  public List<Post> findPostsByUserIdAndDomain(String userId, String userDomain) {
    return postMapper.select((c) -> c.where(PostDynamicSqlSupport.userId, isEqualTo(userId))
        .and(PostDynamicSqlSupport.domain, isEqualTo(userDomain))
        .orderBy(PostDynamicSqlSupport.createdAt.descending()));
  }

  /**
   * 投稿を追加する
   * 
   * @param post
   * @return 追加した投稿
   */
  public Post insert(Post post) {
    // UUID はPostgreSQL 側で生成してくれるが、return するためにここで決めておきたい
    if (post.getUuid() == null) {
      post.setUuid(UUID.randomUUID().toString());
    }
    postMapper.insertSelective(post);
    // uuid から確実に取得できるはずなので get して返す
    return postMapper.selectByPrimaryKey(post.getUuid()).get();
  }
}
