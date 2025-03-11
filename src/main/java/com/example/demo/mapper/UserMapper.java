package com.example.demo.mapper;

import static com.example.demo.mapper.UserDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import com.example.demo.domain.User;
import jakarta.annotation.Generated;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.delete.DeleteDSLCompleter;
import org.mybatis.dynamic.sql.select.CountDSLCompleter;
import org.mybatis.dynamic.sql.select.SelectDSLCompleter;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.mybatis.dynamic.sql.update.UpdateDSLCompleter;
import org.mybatis.dynamic.sql.update.UpdateModel;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import org.mybatis.dynamic.sql.util.mybatis3.CommonCountMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonDeleteMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonInsertMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonUpdateMapper;
import org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils;

@Mapper
public interface UserMapper extends CommonCountMapper, CommonDeleteMapper, CommonInsertMapper<User>, CommonUpdateMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.092568+09:00", comments="Source Table: public.user")
    BasicColumn[] selectList = BasicColumn.columnList(id, username, profile, email, password, role);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.092228+09:00", comments="Source Table: public.user")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="UserResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="username", property="username", jdbcType=JdbcType.VARCHAR),
        @Result(column="profile", property="profile", jdbcType=JdbcType.VARCHAR),
        @Result(column="email", property="email", jdbcType=JdbcType.VARCHAR),
        @Result(column="password", property="password", jdbcType=JdbcType.VARCHAR),
        @Result(column="role", property="role", jdbcType=JdbcType.VARCHAR)
    })
    List<User> selectMany(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.092267+09:00", comments="Source Table: public.user")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("UserResult")
    Optional<User> selectOne(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.092289+09:00", comments="Source Table: public.user")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, user, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.092307+09:00", comments="Source Table: public.user")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, user, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.092325+09:00", comments="Source Table: public.user")
    default int deleteByPrimaryKey(String id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.092343+09:00", comments="Source Table: public.user")
    default int insert(User row) {
        return MyBatis3Utils.insert(this::insert, row, user, c ->
            c.map(id).toProperty("id")
            .map(username).toProperty("username")
            .map(profile).toProperty("profile")
            .map(email).toProperty("email")
            .map(password).toProperty("password")
            .map(role).toProperty("role")
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.092478+09:00", comments="Source Table: public.user")
    default int insertMultiple(Collection<User> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, user, c ->
            c.map(id).toProperty("id")
            .map(username).toProperty("username")
            .map(profile).toProperty("profile")
            .map(email).toProperty("email")
            .map(password).toProperty("password")
            .map(role).toProperty("role")
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.092524+09:00", comments="Source Table: public.user")
    default int insertSelective(User row) {
        return MyBatis3Utils.insert(this::insert, row, user, c ->
            c.map(id).toPropertyWhenPresent("id", row::getId)
            .map(username).toPropertyWhenPresent("username", row::getUsername)
            .map(profile).toPropertyWhenPresent("profile", row::getProfile)
            .map(email).toPropertyWhenPresent("email", row::getEmail)
            .map(password).toPropertyWhenPresent("password", row::getPassword)
            .map(role).toPropertyWhenPresent("role", row::getRole)
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.092587+09:00", comments="Source Table: public.user")
    default Optional<User> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, user, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.092606+09:00", comments="Source Table: public.user")
    default List<User> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, user, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.092625+09:00", comments="Source Table: public.user")
    default List<User> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, user, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.092641+09:00", comments="Source Table: public.user")
    default Optional<User> selectByPrimaryKey(String id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.09266+09:00", comments="Source Table: public.user")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, user, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.092678+09:00", comments="Source Table: public.user")
    static UpdateDSL<UpdateModel> updateAllColumns(User row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(id).equalTo(row::getId)
                .set(username).equalTo(row::getUsername)
                .set(profile).equalTo(row::getProfile)
                .set(email).equalTo(row::getEmail)
                .set(password).equalTo(row::getPassword)
                .set(role).equalTo(row::getRole);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.09271+09:00", comments="Source Table: public.user")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(User row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(id).equalToWhenPresent(row::getId)
                .set(username).equalToWhenPresent(row::getUsername)
                .set(profile).equalToWhenPresent(row::getProfile)
                .set(email).equalToWhenPresent(row::getEmail)
                .set(password).equalToWhenPresent(row::getPassword)
                .set(role).equalToWhenPresent(row::getRole);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.09274+09:00", comments="Source Table: public.user")
    default int updateByPrimaryKey(User row) {
        return update(c ->
            c.set(username).equalTo(row::getUsername)
            .set(profile).equalTo(row::getProfile)
            .set(email).equalTo(row::getEmail)
            .set(password).equalTo(row::getPassword)
            .set(role).equalTo(row::getRole)
            .where(id, isEqualTo(row::getId))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-11T16:15:48.092845+09:00", comments="Source Table: public.user")
    default int updateByPrimaryKeySelective(User row) {
        return update(c ->
            c.set(username).equalToWhenPresent(row::getUsername)
            .set(profile).equalToWhenPresent(row::getProfile)
            .set(email).equalToWhenPresent(row::getEmail)
            .set(password).equalToWhenPresent(row::getPassword)
            .set(role).equalToWhenPresent(row::getRole)
            .where(id, isEqualTo(row::getId))
        );
    }
}