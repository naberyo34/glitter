package com.example.glitter.domain.Follow;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.glitter.generated.Follow;
import com.example.glitter.generated.FollowDynamicSqlSupport;
import com.example.glitter.generated.FollowMapper;

@Repository
public class FollowRepository {
  @Autowired
  private FollowMapper followMapper;

  /**
   * ユーザーIDからフォローしているユーザーを全件取得する
   * 
   * @param userId フォローしているユーザーのID
   * @return フォロー情報のリスト
   */
  @Transactional
  public List<Follow> findFollowing(String userId) {
    return followMapper.select(c -> c.where(FollowDynamicSqlSupport.followerId, isEqualTo(userId)));
  }

  /**
   * ユーザーIDからフォローされているユーザーを全件取得する
   * 
   * @param userId フォローされているユーザーのID
   * @return フォロー情報のリスト
   */
  @Transactional
  public List<Follow> findFollowers(String userId) {
    return followMapper.select(c -> c.where(FollowDynamicSqlSupport.followeeId, isEqualTo(userId)));
  }

  /**
   * フォローを追加する
   * 
   * @param follow フォロー情報
   * @return 追加したフォロー情報
   */
  @Transactional
  public Follow insert(Follow follow) {
    followMapper.insert(follow);
    return follow;
  }

  /**
   * フォローを削除する
   * 
   * @param followerId フォローしているユーザーのID
   * @param followeeId フォローされているユーザーのID
   * @return 削除した行数
   */
  @Transactional
  public int delete(String followerId, String followeeId) {
    return followMapper.delete(c -> c
        .where(FollowDynamicSqlSupport.followerId, isEqualTo(followerId))
        .and(FollowDynamicSqlSupport.followeeId, isEqualTo(followeeId)));
  }

  /**
   * 指定したユーザーが別のユーザーをフォローしているか確認する
   * 
   * @param followerId フォローしているユーザーのID
   * @param followeeId フォローされているユーザーのID
   * @return フォロー情報（存在しない場合はEmpty）
   */
  @Transactional
  public Optional<Follow> findByFollowerIdAndFolloweeId(String followerId, String followeeId) {
    return followMapper.selectOne(c -> c
        .where(FollowDynamicSqlSupport.followerId, isEqualTo(followerId))
        .and(FollowDynamicSqlSupport.followeeId, isEqualTo(followeeId)));
  }
}
