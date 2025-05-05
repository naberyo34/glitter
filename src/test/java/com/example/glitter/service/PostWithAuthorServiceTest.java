package com.example.glitter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.glitter.domain.Post.PostRepository;
import com.example.glitter.domain.Post.PostWithAuthor;
import com.example.glitter.domain.User.UserNotFoundException;
import com.example.glitter.domain.User.UserRepository;
import com.example.glitter.generated.Post;
import com.example.glitter.generated.User;

@ExtendWith(MockitoExtension.class)
public class PostWithAuthorServiceTest {
  @Mock
  private PostRepository postRepository;
  @Mock
  private UserRepository userRepository;
  @InjectMocks
  private PostWithAuthorService postWithAuthorService;

  @Test
  void IDから投稿を取得できる() throws Exception {
    // モックデータの準備
    Post mockPost = new Post();
    mockPost.setUuid("uuid_1");
    mockPost.setUserId("test_user");
    mockPost.setDomain("example.com");
    mockPost.setContent("テスト投稿");

    User mockUser = new User();
    mockUser.setUserId("test_user");
    mockUser.setDomain("example.com");
    mockUser.setUsername("テストユーザー");

    when(postRepository.findByUuid("uuid_1")).thenReturn(Optional.of(mockPost));
    when(userRepository.findByUserIdAndDomain("test_user", "example.com")).thenReturn(Optional.of(mockUser));

    // テスト実行
    Optional<PostWithAuthor> post = postWithAuthorService.findByPostId("uuid_1");

    // 検証
    assertThat(post).isPresent();
    assertEquals("uuid_1", post.get().getUuid());
    assertEquals("テスト投稿", post.get().getContent());
    assertEquals("test_user", post.get().getUser().getUserId());
    assertEquals("テストユーザー", post.get().getUser().getUsername());
  }

  @Test
  void 存在しないIDの投稿を取得したときemptyが返る() throws Exception {
    // モックデータの準備
    when(postRepository.findByUuid("uuid_999")).thenReturn(Optional.empty());

    // テスト実行
    Optional<PostWithAuthor> post = postWithAuthorService.findByPostId("uuid_999");

    // 検証
    assertThat(post).isEmpty();
  }

  @Test
  void ユーザーに紐づく投稿リストを取得できる() throws Exception {
    User mockUser = new User();
    mockUser.setUserId("test_user");
    mockUser.setDomain("example.com");

    Post mockPost1 = new Post();
    mockPost1.setUuid("uuid_1");
    mockPost1.setUserId("test_user");
    mockPost1.setDomain("example.com");
    mockPost1.setContent("テスト投稿1");

    Post mockPost2 = new Post();
    mockPost2.setUuid("uuid_2");
    mockPost2.setUserId("test_user");
    mockPost2.setDomain("example.com");
    mockPost2.setContent("テスト投稿2");

    List<Post> mockPosts = List.of(mockPost1, mockPost2);

    when(userRepository.findByUserIdAndDomain("test_user", "example.com")).thenReturn(Optional.of(mockUser));
    when(postRepository.findPostsByUserIdAndDomain("test_user", "example.com")).thenReturn(mockPosts);

    List<PostWithAuthor> posts = postWithAuthorService.findPostsByUserIdAndDomain("test_user", "example.com");

    assertThat(posts).isNotEmpty();
    assertEquals(2, posts.size());
    assertEquals("uuid_1", posts.get(0).getUuid());
    assertEquals("テスト投稿1", posts.get(0).getContent());
    assertEquals("uuid_2", posts.get(1).getUuid());
    assertEquals("テスト投稿2", posts.get(1).getContent());

    posts.forEach(post -> {
      assertEquals("test_user", post.getUser().getUserId());
    });
  }

  @Test
  void ユーザーの投稿が1件もなかった場合場合空のリストが返る() throws Exception {
    User mockUser = new User();
    mockUser.setUserId("test_user");
    mockUser.setDomain("example.com");

    when(userRepository.findByUserIdAndDomain("test_user", "example.com")).thenReturn(Optional.of(mockUser));
    when(postRepository.findPostsByUserIdAndDomain("test_user", "example.com")).thenReturn(new ArrayList<>());

    List<PostWithAuthor> posts = postWithAuthorService.findPostsByUserIdAndDomain("test_user", "example.com");

    assertThat(posts).isEmpty();
  }

  @Test
  void 存在しないユーザーの投稿を取得しようとした場合例外が返る() throws Exception {
    try {
      postWithAuthorService.findPostsByUserIdAndDomain("test_user", "example.com");
      fail();
    } catch (Exception e) {
      assertThat(e).isInstanceOf(UserNotFoundException.class);
    }
  }
}
