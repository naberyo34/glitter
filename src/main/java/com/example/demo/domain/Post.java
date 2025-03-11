package com.example.demo.domain;

import jakarta.annotation.Generated;
import java.util.Date;

public class Post {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.092957+09:00", comments="Source field: public.post.id")
    private Integer id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.093005+09:00", comments="Source field: public.post.user_id")
    private String userId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.093047+09:00", comments="Source field: public.post.content")
    private String content;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.093076+09:00", comments="Source field: public.post.created_at")
    private Date createdAt;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.092985+09:00", comments="Source field: public.post.id")
    public Integer getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.092997+09:00", comments="Source field: public.post.id")
    public void setId(Integer id) {
        this.id = id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.093022+09:00", comments="Source field: public.post.user_id")
    public String getUserId() {
        return userId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.093037+09:00", comments="Source field: public.post.user_id")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.093055+09:00", comments="Source field: public.post.content")
    public String getContent() {
        return content;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.093064+09:00", comments="Source field: public.post.content")
    public void setContent(String content) {
        this.content = content;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.093089+09:00", comments="Source field: public.post.created_at")
    public Date getCreatedAt() {
        return createdAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.093098+09:00", comments="Source field: public.post.created_at")
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}