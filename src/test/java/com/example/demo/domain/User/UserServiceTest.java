package com.example.demo.domain.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import com.example.demo.domain.Post.PostDto;
import com.example.demo.domain.Post.PostRepository;
import com.example.demo.generated.Post;
import com.example.demo.generated.User;

@SpringBootTest
public class UserServiceTest {
  @Mock
  private UserRepository userRepository;
  @Mock
  private PostRepository postRepository;

  @InjectMocks
  private UserService userService;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    User userWithPost = new User();
    userWithPost.setId("user_with_post");
    userWithPost.setUsername("投稿を持っているユーザー");
    userWithPost.setPassword("password");
    userWithPost.setEmail("withpost@example.com");
    userWithPost.setProfile("プロフィール");

    User userWithoutPost = new User();
    userWithoutPost.setId("user_without_post");
    userWithoutPost.setUsername("投稿を持っていないユーザー");
    userWithoutPost.setPassword("password");
    userWithoutPost.setEmail("withoutpost@example.com");
    userWithoutPost.setProfile("プロフィール");

    Post post = new Post();
    long id = 1;
    post.setId(id);
    post.setUserId(userWithPost.getId());
    post.setContent("投稿");
    post.setCreatedAt(new Date());

    when(userRepository.findById("user_with_post")).thenReturn(Optional.of(userWithPost));
    when(userRepository.findById("user_without_post")).thenReturn(Optional.of(userWithoutPost));
    when(userRepository.findById("not_exist_user")).thenReturn(Optional.empty());

    when(userRepository.getSessionUser()).thenReturn(Optional.of(userWithPost));

    when(postRepository.findByUserId("user_with_post")).thenReturn(List.of(post));
    when(postRepository.findByUserId("user_without_post")).thenReturn(List.of());
  }

  @Test
  void IDからユーザーを取得したときユーザーのDTOが返る() throws Exception {
    Optional<UserDto> user = userService.findById("user_with_post");
    // User ではなく UserDto が返っていることを確認
    assertThat(user).isPresent().get().isInstanceOf(UserDto.class);

    // 取得ユーザーが正しいことも確認しておく
    user.ifPresent((u) -> {
      assertEquals(u.getUsername(), "投稿を持っているユーザー");
    });
  }

  @Test
  void 存在しないユーザーを取得したときemptyが返る() throws Exception {
    Optional<UserDto> user = userService.findById("not_exist_user");
    assertThat(user).isEmpty();
  }

  @Test
  @WithMockUser(username = "投稿を持っているユーザー")
  void セッションユーザーを取得したときユーザーDTOが返る() throws Exception {
    Optional<UserDto> user = userService.getSessionUser();
    // User ではなく UserDto が返っていることを確認
    assertThat(user).isPresent().get().isInstanceOf(UserDto.class);

    // 取得ユーザーが正しいことも確認しておく
    user.ifPresent((u) -> {
      assertEquals(u.getUsername(), "投稿を持っているユーザー");
    });
  }

  @Test
  void ユーザーに紐づく投稿を取得できる() throws Exception {
    List<PostDto> posts = userService.getUserPosts("user_with_post");
    assertThat(posts).isNotEmpty();
  }

  @Test
  void ユーザーが投稿を持たない場合は空のリストを返す() throws Exception {
    List<PostDto> posts = userService.getUserPosts("user_without_post");
    assertThat(posts).isEmpty();
  }
}
