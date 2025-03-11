package com.example.demo.mapper;

import jakarta.annotation.Generated;
import java.sql.JDBCType;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class UserDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.092006+09:00", comments="Source Table: public.user")
    public static final User user = new User();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.092049+09:00", comments="Source field: public.user.id")
    public static final SqlColumn<String> id = user.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.092117+09:00", comments="Source field: public.user.username")
    public static final SqlColumn<String> username = user.username;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.09216+09:00", comments="Source field: public.user.profile")
    public static final SqlColumn<String> profile = user.profile;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.092179+09:00", comments="Source field: public.user.email")
    public static final SqlColumn<String> email = user.email;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.092195+09:00", comments="Source field: public.user.password")
    public static final SqlColumn<String> password = user.password;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.092209+09:00", comments="Source field: public.user.role")
    public static final SqlColumn<String> role = user.role;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.092031+09:00", comments="Source Table: public.user")
    public static final class User extends AliasableSqlTable<User> {
        public final SqlColumn<String> id = column("id", JDBCType.VARCHAR);

        public final SqlColumn<String> username = column("username", JDBCType.VARCHAR);

        public final SqlColumn<String> profile = column("profile", JDBCType.VARCHAR);

        public final SqlColumn<String> email = column("email", JDBCType.VARCHAR);

        public final SqlColumn<String> password = column("password", JDBCType.VARCHAR);

        public final SqlColumn<String> role = column("role", JDBCType.VARCHAR);

        public User() {
            super("public.user", User::new);
        }
    }
}