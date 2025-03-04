package com.example.demo.model;

import jakarta.annotation.Generated;

public class Task {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T20:28:01.299774+09:00", comments="Source field: public.task.id")
    private String id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T20:28:01.300312+09:00", comments="Source field: public.task.value")
    private String value;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T20:28:01.300143+09:00", comments="Source field: public.task.id")
    public String getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T20:28:01.300295+09:00", comments="Source field: public.task.id")
    public void setId(String id) {
        this.id = id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T20:28:01.300329+09:00", comments="Source field: public.task.value")
    public String getValue() {
        return value;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-04T20:28:01.300345+09:00", comments="Source field: public.task.value")
    public void setValue(String value) {
        this.value = value;
    }
}