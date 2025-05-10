package com.example.glitter.service;

import org.springframework.stereotype.Service;

import com.example.glitter.domain.Post.PostCreatedEvent;

/**
 * Activity を送信するサービス
 */
@Service
public class ActivitySendService {
  // TODO: PostCreateEvent しか送れない作りになっているが、汎用化が必要
  public void send(PostCreatedEvent event) {
  }
}
