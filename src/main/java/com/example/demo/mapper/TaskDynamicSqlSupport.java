package com.example.demo.mapper;

import jakarta.annotation.Generated;
import java.sql.JDBCType;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class TaskDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.032095+09:00", comments="Source Table: public.task")
    public static final Task task = new Task();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.03216+09:00", comments="Source field: public.task.id")
    public static final SqlColumn<Integer> id = task.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.032311+09:00", comments="Source field: public.task.value")
    public static final SqlColumn<String> value = task.value;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.03232+09:00", comments="Source field: public.task.is_done")
    public static final SqlColumn<Boolean> isDone = task.isDone;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.03214+09:00", comments="Source Table: public.task")
    public static final class Task extends AliasableSqlTable<Task> {
        public final SqlColumn<Integer> id = column("id", JDBCType.INTEGER);

        public final SqlColumn<String> value = column("value", JDBCType.VARCHAR);

        public final SqlColumn<Boolean> isDone = column("is_done", JDBCType.BIT);

        public Task() {
            super("public.task", Task::new);
        }
    }
}