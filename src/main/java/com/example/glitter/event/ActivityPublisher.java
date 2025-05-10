package com.example.glitter.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.glitter.domain.Post.PostCreatedEvent;
import com.example.glitter.service.ActivitySendService;

@Component
public class ActivityPublisher {
  @Autowired
  private ActivitySendService activitySendService;

  @EventListener
  public void handlePostCreated(PostCreatedEvent event) {
    activitySendService.send(event);
  }
}
