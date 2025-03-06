package com.example.demo.domain;

import jakarta.annotation.Generated;

public class Task {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.030097+09:00", comments="Source field: public.task.id")
    private Integer id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.03063+09:00", comments="Source field: public.task.value")
    private String value;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.030658+09:00", comments="Source field: public.task.is_done")
    private Boolean isDone;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.030475+09:00", comments="Source field: public.task.id")
    public Integer getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.030623+09:00", comments="Source field: public.task.id")
    public void setId(Integer id) {
        this.id = id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.030636+09:00", comments="Source field: public.task.value")
    public String getValue() {
        return value;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.030649+09:00", comments="Source field: public.task.value")
    public void setValue(String value) {
        this.value = value;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.030663+09:00", comments="Source field: public.task.is_done")
    public Boolean getIsDone() {
        return isDone;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.030668+09:00", comments="Source field: public.task.is_done")
    public void setIsDone(Boolean isDone) {
        this.isDone = isDone;
    }
}