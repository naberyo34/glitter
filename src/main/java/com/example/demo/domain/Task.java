package com.example.demo.domain;

import jakarta.annotation.Generated;

public class Task {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-05T23:04:00.581733+09:00", comments="Source field: public.task.id")
    private String id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-05T23:04:00.582216+09:00", comments="Source field: public.task.value")
    private String value;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-05T23:04:00.58207+09:00", comments="Source field: public.task.id")
    public String getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-05T23:04:00.582205+09:00", comments="Source field: public.task.id")
    public void setId(String id) {
        this.id = id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-05T23:04:00.582225+09:00", comments="Source field: public.task.value")
    public String getValue() {
        return value;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-05T23:04:00.582235+09:00", comments="Source field: public.task.value")
    public void setValue(String value) {
        this.value = value;
    }
}