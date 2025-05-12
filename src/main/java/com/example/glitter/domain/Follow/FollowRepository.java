package com.example.glitter.domain.Follow;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.example.glitter.generated.Follow;
import com.example.glitter.generated.FollowDynamicSqlSupport;
import com.example.glitter.generated.FollowMapper;

@Repository
public class FollowRepository {
  @Autowired
  private FollowMapper followMapper;

  @Value("${env.domain}")
  private String domain;

  /**
   * ユーザーIDとドメインからフォローしているユーザーを全件取得する
   * 
   * @param userId
   * @param userDomain
   * @return
   */
  public List<Follow> findFollowing(String userId, String userDomain) {
    return followMapper.select(c -> c
        .where(FollowDynamicSqlSupport.followerId, isEqualTo(userId))
        .and(FollowDynamicSqlSupport.followerDomain, isEqualTo(userDomain)));
  }

  /**
   * ユーザーIDとドメインからフォローされているユーザーを全件取得する
   * 
   * @param userId フォローされているユーザーのID
   * @param userDomain フォローされているユーザーのドメイン
   * @return フォロー情報のリスト
   */
  public List<Follow> findFollowers(String userId, String userDomain) {
    return followMapper.select(c -> c
        .where(FollowDynamicSqlSupport.followeeId, isEqualTo(userId))
        .and(FollowDynamicSqlSupport.followeeDomain, isEqualTo(userDomain)));
  }

  /**
   * フォローを追加する
   * 
   * @param follow フォロー情報
   * @return 追加したフォロー情報
   */
  public Follow insert(Follow follow) {
    followMapper.insertSelective(follow);
    return follow;
  }

  /**
   * フォローを削除する
   * 
   * @param followerId フォローしているユーザーのID
   * @param followerDomain フォローしているユーザーのドメイン
   * @param followeeId フォローされているユーザーのID
   * @param followeeDomain フォローされているユーザーのドメイン
   * @return 削除した行数
   */
  public int delete(String followerId, String follwerDomain, String followeeId, String followeeDomain) {
    return followMapper.delete(c -> c
        .where(FollowDynamicSqlSupport.followerId, isEqualTo(followerId))
        .and(FollowDynamicSqlSupport.followerDomain, isEqualTo(follwerDomain))
        .and(FollowDynamicSqlSupport.followeeId, isEqualTo(followeeId))
        .and(FollowDynamicSqlSupport.followeeDomain, isEqualTo(followeeDomain)));
  }

  /**
   * 指定したユーザーが別のユーザーをフォローしているかを取得する
   * 
   * @param followerId
   * @param followerDomain
   * @param followeeId
   * @param followeeDomain
   * @return 対象のリレーション
   */
  public Optional<Follow> findFollowRelation(String followerId, String followerDomain, String followeeId, String followeeDomain) {
    return followMapper.selectOne(c -> c
        .where(FollowDynamicSqlSupport.followerId, isEqualTo(followerId))
        .and(FollowDynamicSqlSupport.followerDomain, isEqualTo(followerDomain))
        .and(FollowDynamicSqlSupport.followeeId, isEqualTo(followeeId))
        .and(FollowDynamicSqlSupport.followeeDomain, isEqualTo(followeeDomain)));
  }
}
