package com.example.glitter.domain.Post;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.Data;

@Data
public class PostCreatedEvent {
  @Autowired
  private PostDto post;
}
