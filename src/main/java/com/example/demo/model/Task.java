package com.example.demo.model;

import jakarta.annotation.Generated;

public class Task {
    @Generated(value = "org.mybatis.generator.api.MyBatisGenerator", date = "2025-03-02T18:08:55.282954+09:00", comments = "Source field: public.task.id")
    private String id;

    @Generated(value = "org.mybatis.generator.api.MyBatisGenerator", date = "2025-03-02T18:08:55.283499+09:00", comments = "Source field: public.task.value")
    private String value;

    @Generated(value = "org.mybatis.generator.api.MyBatisGenerator", date = "2025-03-02T18:08:55.283337+09:00", comments = "Source field: public.task.id")
    public String getId() {
        return id;
    }

    @Generated(value = "org.mybatis.generator.api.MyBatisGenerator", date = "2025-03-02T18:08:55.283487+09:00", comments = "Source field: public.task.id")
    public void setId(String id) {
        this.id = id;
    }

    @Generated(value = "org.mybatis.generator.api.MyBatisGenerator", date = "2025-03-02T18:08:55.28351+09:00", comments = "Source field: public.task.value")
    public String getValue() {
        return value;
    }

    @Generated(value = "org.mybatis.generator.api.MyBatisGenerator", date = "2025-03-02T18:08:55.283531+09:00", comments = "Source field: public.task.value")
    public void setValue(String value) {
        this.value = value;
    }
}