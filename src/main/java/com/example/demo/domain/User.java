package com.example.demo.domain;

import jakarta.annotation.Generated;

public class User {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.091669+09:00", comments="Source field: public.user.id")
    private String id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.091723+09:00", comments="Source field: public.user.username")
    private String username;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.091762+09:00", comments="Source field: public.user.profile")
    private String profile;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.091852+09:00", comments="Source field: public.user.email")
    private String email;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.091896+09:00", comments="Source field: public.user.password")
    private String password;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.091938+09:00", comments="Source field: public.user.role")
    private String role;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.09169+09:00", comments="Source field: public.user.id")
    public String getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.091707+09:00", comments="Source field: public.user.id")
    public void setId(String id) {
        this.id = id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.091738+09:00", comments="Source field: public.user.username")
    public String getUsername() {
        return username;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.091751+09:00", comments="Source field: public.user.username")
    public void setUsername(String username) {
        this.username = username;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.091774+09:00", comments="Source field: public.user.profile")
    public String getProfile() {
        return profile;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.091786+09:00", comments="Source field: public.user.profile")
    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.091866+09:00", comments="Source field: public.user.email")
    public String getEmail() {
        return email;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.091878+09:00", comments="Source field: public.user.email")
    public void setEmail(String email) {
        this.email = email;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.091911+09:00", comments="Source field: public.user.password")
    public String getPassword() {
        return password;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.091928+09:00", comments="Source field: public.user.password")
    public void setPassword(String password) {
        this.password = password;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.09195+09:00", comments="Source field: public.user.role")
    public String getRole() {
        return role;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.091963+09:00", comments="Source field: public.user.role")
    public void setRole(String role) {
        this.role = role;
    }
}