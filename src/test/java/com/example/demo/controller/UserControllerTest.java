package com.example.demo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.demo.domain.Post.PostDto;
import com.example.demo.domain.User.UserDto;
import com.example.demo.domain.User.UserService;
import com.example.demo.generated.User;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
public class UserControllerTest {
  private MockMvc mockMvc;
  @Mock
  private UserService userService;

  @InjectMocks
  private UserController userController;

  private ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  public void setup() throws ParseException {
    mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

    when(userService.findById("user_with_post"))
        .thenReturn(Optional.of(new UserDto("user_with_post", "投稿を持っているユーザー", "プロフィール", "withpost@example.com")));
    when(userService.findById("user_without_post")).thenReturn(
        Optional.of(new UserDto("user_without_post", "投稿を持っていないユーザー", "プロフィール", "withoutpost@example.com")));
    when(userService.findById("not_exist_user")).thenReturn(Optional.empty());

    long postId = 1;
    when(userService.getUserPosts("user_with_post"))
        .thenReturn(List.of(new PostDto(postId, "user_with_post", "投稿", new Date())));
    when(userService.getUserPosts("user_without_post")).thenReturn(List.of());

    when(userService.getSessionUser())
        .thenReturn(Optional.of(new UserDto("session_user", "セッションユーザー", "プロフィール", "session@example.com")));
    when(userService.add(any(User.class))).thenAnswer(invocation -> {
      User user = invocation.getArgument(0);
      if (user.getId() != null && user.getPassword() != null && user.getUsername() != null && user.getEmail() != null) {
        return Optional.of(new UserDto("new_user", "新しいユーザー", "プロフィール", "new@example.com"));
      } else {
        return Optional.empty();
      }
    });
  }

  @Test
  void IDからユーザーを取得したときユーザーのDTOが返る() throws Exception {
    mockMvc.perform(get("/user/user_with_post")).andExpect(status().isOk());
  }

  @Test
  void 存在しないユーザーを取得したとき404が返る() throws Exception {
    mockMvc.perform(get("/user/not_exist_user")).andExpect(status().isNotFound());
  }

  @Test
  void 投稿しているユーザーの投稿を取得したとき投稿のリストが返る() throws Exception {
    mockMvc.perform(get("/user/user_with_post/post")).andExpect(status().isOk()).andExpect((result) -> {
      String content = result.getResponse().getContentAsString();
      List<PostDto> posts = Arrays.asList(objectMapper.readValue(content, PostDto[].class));
      PostDto post = posts.get(0);
      // いちおう投稿内容を検証
      assertEquals(post.getContent(), "投稿");
    });
  }

  @Test
  void 投稿していないユーザーの投稿を取得したとき空のリストが返る() throws Exception {
    mockMvc.perform(get("/user/user_without_post/post")).andExpect(status().isOk()).andExpect((result) -> {
      String content = result.getResponse().getContentAsString();
      assertEquals(content, "[]");
    });
  }

  @Test
  void 存在しないユーザーの投稿を取得したとき404が返る() throws Exception {
    mockMvc.perform(get("/user/not_exist_user/post")).andExpect(status().isNotFound());
  }

  @Test
  void セッションユーザーを取得したときユーザーのDTOが返る() throws Exception {
    mockMvc.perform(get("/user/me")).andExpect(status().isOk());
  }

  @Test
  void 正しいパラメーターでユーザーを作成できる() throws Exception {
    User newUser = new User();
    newUser.setId("new_user");
    newUser.setPassword("password");
    newUser.setUsername("新しいユーザー");
    newUser.setEmail("new@example.com");
    newUser.setProfile("プロフィール");
    
    mockMvc.perform(
        post("/user").content(objectMapper.writeValueAsString(newUser))
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk()).andExpect((result) -> {
          String content = result.getResponse().getContentAsString();
          assertEquals(content, "{\"id\":\"new_user\",\"username\":\"新しいユーザー\",\"profile\":\"プロフィール\",\"email\":\"new@example.com\"}");
        });
  }

  @Test
  void 無効なパラメーターでユーザーを作成したとき400が返る() throws Exception {
    User invalidNewUser = new User();
    invalidNewUser.setId("new_user");
    invalidNewUser.setPassword("password");
    invalidNewUser.setUsername("新しいユーザー");
    // email がない

    mockMvc.perform(
        post("/user").content(objectMapper.writeValueAsString(invalidNewUser))
            .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
        .andExpect(status().isBadRequest());
  }
}
