package com.example.demo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.demo.domain.Post.PostDto;
import com.example.demo.domain.User.UserDto;
import com.example.demo.domain.User.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
public class UserControllerTest {
  private MockMvc mockMvc;
  @Mock
  private UserService userService;

  @InjectMocks
  private UserController userController;

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
    ObjectMapper objectMapper = new ObjectMapper();
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
}
