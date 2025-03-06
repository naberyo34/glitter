package com.example.demo.mapper;

import static com.example.demo.mapper.TaskDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import com.example.demo.domain.Task;
import jakarta.annotation.Generated;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.delete.DeleteDSLCompleter;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.select.CountDSLCompleter;
import org.mybatis.dynamic.sql.select.SelectDSLCompleter;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.mybatis.dynamic.sql.update.UpdateDSLCompleter;
import org.mybatis.dynamic.sql.update.UpdateModel;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import org.mybatis.dynamic.sql.util.mybatis3.CommonCountMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonDeleteMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonUpdateMapper;
import org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils;

@Mapper
public interface TaskMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.035827+09:00", comments="Source Table: public.task")
    BasicColumn[] selectList = BasicColumn.columnList(id, value, isDone);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.032626+09:00", comments="Source Table: public.task")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @Options(useGeneratedKeys=true,keyProperty="row.id")
    int insert(InsertStatementProvider<Task> insertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.033694+09:00", comments="Source Table: public.task")
    @InsertProvider(type=SqlProviderAdapter.class, method="insertMultipleWithGeneratedKeys")
    @Options(useGeneratedKeys=true,keyProperty="records.id")
    int insertMultiple(@Param("insertStatement") String insertStatement, @Param("records") List<Task> records);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.033924+09:00", comments="Source Table: public.task")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="TaskResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="value", property="value", jdbcType=JdbcType.VARCHAR),
        @Result(column="is_done", property="isDone", jdbcType=JdbcType.BIT)
    })
    List<Task> selectMany(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.034223+09:00", comments="Source Table: public.task")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("TaskResult")
    Optional<Task> selectOne(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.034383+09:00", comments="Source Table: public.task")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, task, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.034551+09:00", comments="Source Table: public.task")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, task, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.034696+09:00", comments="Source Table: public.task")
    default int deleteByPrimaryKey(Integer id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.034875+09:00", comments="Source Table: public.task")
    default int insert(Task row) {
        return MyBatis3Utils.insert(this::insert, row, task, c ->
            c.map(value).toProperty("value")
            .map(isDone).toProperty("isDone")
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.035231+09:00", comments="Source Table: public.task")
    default int insertMultiple(Collection<Task> records) {
        return MyBatis3Utils.insertMultipleWithGeneratedKeys(this::insertMultiple, records, task, c ->
            c.map(value).toProperty("value")
            .map(isDone).toProperty("isDone")
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.035469+09:00", comments="Source Table: public.task")
    default int insertSelective(Task row) {
        return MyBatis3Utils.insert(this::insert, row, task, c ->
            c.map(value).toPropertyWhenPresent("value", row::getValue)
            .map(isDone).toPropertyWhenPresent("isDone", row::getIsDone)
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.036185+09:00", comments="Source Table: public.task")
    default Optional<Task> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, task, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.036332+09:00", comments="Source Table: public.task")
    default List<Task> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, task, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.036471+09:00", comments="Source Table: public.task")
    default List<Task> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, task, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.036618+09:00", comments="Source Table: public.task")
    default Optional<Task> selectByPrimaryKey(Integer id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.036803+09:00", comments="Source Table: public.task")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, task, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.036976+09:00", comments="Source Table: public.task")
    static UpdateDSL<UpdateModel> updateAllColumns(Task row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(value).equalTo(row::getValue)
                .set(isDone).equalTo(row::getIsDone);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.037138+09:00", comments="Source Table: public.task")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(Task row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(value).equalToWhenPresent(row::getValue)
                .set(isDone).equalToWhenPresent(row::getIsDone);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.037337+09:00", comments="Source Table: public.task")
    default int updateByPrimaryKey(Task row) {
        return update(c ->
            c.set(value).equalTo(row::getValue)
            .set(isDone).equalTo(row::getIsDone)
            .where(id, isEqualTo(row::getId))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-06T20:58:37.037491+09:00", comments="Source Table: public.task")
    default int updateByPrimaryKeySelective(Task row) {
        return update(c ->
            c.set(value).equalToWhenPresent(row::getValue)
            .set(isDone).equalToWhenPresent(row::getIsDone)
            .where(id, isEqualTo(row::getId))
        );
    }
}