package com.example.demo.mapper;

import jakarta.annotation.Generated;
import java.sql.JDBCType;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class TaskDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T09:15:24.209502+09:00", comments="Source Table: public.task")
    public static final Task task = new Task();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T09:15:24.209573+09:00", comments="Source field: public.task.id")
    public static final SqlColumn<String> id = task.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T09:15:24.209723+09:00", comments="Source field: public.task.value")
    public static final SqlColumn<String> value = task.value;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T09:15:24.209549+09:00", comments="Source Table: public.task")
    public static final class Task extends AliasableSqlTable<Task> {
        public final SqlColumn<String> id = column("id", JDBCType.CHAR);

        public final SqlColumn<String> value = column("value", JDBCType.VARCHAR);

        public Task() {
            super("public.task", Task::new);
        }
    }
}