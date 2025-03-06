package com.example.demo.domain;

import jakarta.annotation.Generated;

public class Task {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T09:15:24.207661+09:00", comments="Source field: public.task.id")
    private String id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T09:15:24.208178+09:00", comments="Source field: public.task.value")
    private String value;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T09:15:24.208019+09:00", comments="Source field: public.task.id")
    public String getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T09:15:24.208167+09:00", comments="Source field: public.task.id")
    public void setId(String id) {
        this.id = id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T09:15:24.208186+09:00", comments="Source field: public.task.value")
    public String getValue() {
        return value;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T09:15:24.208194+09:00", comments="Source field: public.task.value")
    public void setValue(String value) {
        this.value = value;
    }
}