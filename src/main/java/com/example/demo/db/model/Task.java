package com.example.demo.db.model;

import jakarta.annotation.Generated;

public class Task {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-02T15:03:16.784107+09:00", comments="Source field: public.task.id")
    private String id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-02T15:03:16.78461+09:00", comments="Source field: public.task.value")
    private String value;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-02T15:03:16.784454+09:00", comments="Source field: public.task.id")
    public String getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-02T15:03:16.784596+09:00", comments="Source field: public.task.id")
    public void setId(String id) {
        this.id = id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-02T15:03:16.784623+09:00", comments="Source field: public.task.value")
    public String getValue() {
        return value;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-02T15:03:16.784635+09:00", comments="Source field: public.task.value")
    public void setValue(String value) {
        this.value = value;
    }
}