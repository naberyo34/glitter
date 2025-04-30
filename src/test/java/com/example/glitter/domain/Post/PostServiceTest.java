package com.example.glitter.domain.Post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.glitter.domain.User.UserRepository;
import com.example.glitter.generated.Post;
import com.example.glitter.generated.User;

import jakarta.validation.ConstraintViolationException;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

  @Mock
  private PostRepository postRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private PostService postService;

  @Test
  void IDから投稿を取得できる() throws Exception {
    // モックデータの準備
    Post mockPost = new Post();
    mockPost.setId(1L);
    mockPost.setUserId("test_user");
    mockPost.setContent("テスト投稿");
    mockPost.setCreatedAt(new Date());

    User mockUser = new User();
    mockUser.setId("test_user");
    mockUser.setUsername("テストユーザー");

    when(postRepository.findById(1L)).thenReturn(Optional.of(mockPost));
    when(userRepository.findById("test_user")).thenReturn(Optional.of(mockUser));

    // テスト実行
    Optional<PostResponseDto> post = postService.findById(1L);

    // 検証
    assertThat(post).isPresent();
    assertEquals(1L, post.get().getId());
    assertEquals("テスト投稿", post.get().getContent());
    assertEquals("test_user", post.get().getUser().getId());
    assertEquals("テストユーザー", post.get().getUser().getUsername());
  }

  @Test
  void 存在しないIDの投稿を取得したとき空のOptionalが返る() throws Exception {
    // モックデータの準備
    when(postRepository.findById(999L)).thenReturn(Optional.empty());

    // テスト実行
    Optional<PostResponseDto> post = postService.findById(999L);

    // 検証
    assertThat(post).isNotPresent();
  }

  @Test
  void ユーザーに紐づく投稿を取得できる() throws Exception {
    // モックデータの準備
    Post mockPost1 = new Post();
    mockPost1.setId(1L);
    mockPost1.setUserId("test_user");
    mockPost1.setContent("テスト投稿1");
    mockPost1.setCreatedAt(new Date());

    Post mockPost2 = new Post();
    mockPost2.setId(2L);
    mockPost2.setUserId("test_user");
    mockPost2.setContent("テスト投稿2");
    mockPost2.setCreatedAt(new Date());

    List<Post> mockPosts = List.of(mockPost1, mockPost2);

    User mockUser = new User();
    mockUser.setId("test_user");
    mockUser.setUsername("テストユーザー");

    when(postRepository.findByUserId("test_user")).thenReturn(mockPosts);
    when(userRepository.findById("test_user")).thenReturn(Optional.of(mockUser));

    // テスト実行
    List<PostResponseDto> posts = postService.getPostsByUserId("test_user");

    // 検証
    assertThat(posts).isNotEmpty();
    assertEquals(2, posts.size());
    assertEquals(1L, posts.get(0).getId());
    assertEquals("テスト投稿1", posts.get(0).getContent());
    assertEquals(2L, posts.get(1).getId());
    assertEquals("テスト投稿2", posts.get(1).getContent());

    posts.forEach(post -> {
      assertEquals("test_user", post.getUser().getId());
      assertEquals("テストユーザー", post.getUser().getUsername());
    });
  }

  @Test
  void ユーザーの投稿がない場合空のリストが返る() throws Exception {
    // モックデータの準備 - 投稿が空の場合
    when(postRepository.findByUserId("test_user_2")).thenReturn(new ArrayList<>());

    // テスト実行
    List<PostResponseDto> posts = postService.getPostsByUserId("test_user_2");

    // 検証
    assertThat(posts).isEmpty();
  }

  @Test
  void 投稿を追加できる() throws Exception {
    // モックデータの準備
    Post newPost = new Post();
    newPost.setId(3L);
    newPost.setUserId("test_user");
    newPost.setContent("new post");
    newPost.setCreatedAt(new Date());

    when(postRepository.insert(any(Post.class))).thenReturn(newPost);

    // テスト実行
    PostParamsDto postParams = new PostParamsDto("test_user", "new post");
    Optional<PostDto> result = postService.add(postParams);

    // 検証
    assertThat(result).isPresent();
    assertEquals(3L, result.get().getId());
    assertEquals("test_user", result.get().getUserId());
    assertEquals("new post", result.get().getContent());

    verify(postRepository).insert(any(Post.class));
  }

  @Test
  void 空文字のみの投稿に失敗する() throws Exception {
    // モックの振る舞いを設定 - バリデーションエラー
    when(postRepository.insert(any(Post.class))).thenThrow(new ConstraintViolationException("空文字は投稿できません", null));

    // テスト実行と検証
    PostParamsDto postParams = new PostParamsDto("test_user", " ");
    assertThrows(ConstraintViolationException.class, () -> {
      postService.add(postParams);
    });

    verify(postRepository).insert(any(Post.class));
  }

  @Test
  void 不正なユーザーの投稿が失敗する() throws Exception {
    // モックの準備 - 不正なユーザーID
    when(postRepository.insert(any(Post.class))).thenThrow(new IllegalArgumentException("存在しないユーザーIDです"));

    // テスト実行と検証
    PostParamsDto postParams = new PostParamsDto("invalid_user", "new post");
    assertThrows(IllegalArgumentException.class, () -> {
      postService.add(postParams);
    });

    verify(postRepository).insert(any(Post.class));
  }
}
