package com.example.demo.mapper;

import static com.example.demo.mapper.TaskDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import com.example.demo.domain.Task;
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
public interface TaskMapper extends CommonCountMapper, CommonDeleteMapper, CommonInsertMapper<Task>, CommonUpdateMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-05T23:04:00.586715+09:00", comments="Source Table: public.task")
    BasicColumn[] selectList = BasicColumn.columnList(id, value);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-05T23:04:00.584132+09:00", comments="Source Table: public.task")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="TaskResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.CHAR, id=true),
        @Result(column="value", property="value", jdbcType=JdbcType.VARCHAR)
    })
    List<Task> selectMany(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-05T23:04:00.584909+09:00", comments="Source Table: public.task")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("TaskResult")
    Optional<Task> selectOne(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-05T23:04:00.585055+09:00", comments="Source Table: public.task")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, task, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-05T23:04:00.585193+09:00", comments="Source Table: public.task")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, task, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-05T23:04:00.585435+09:00", comments="Source Table: public.task")
    default int deleteByPrimaryKey(String id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-05T23:04:00.585593+09:00", comments="Source Table: public.task")
    default int insert(Task row) {
        return MyBatis3Utils.insert(this::insert, row, task, c ->
            c.map(id).toProperty("id")
            .map(value).toProperty("value")
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-05T23:04:00.58616+09:00", comments="Source Table: public.task")
    default int insertMultiple(Collection<Task> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, task, c ->
            c.map(id).toProperty("id")
            .map(value).toProperty("value")
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-05T23:04:00.586368+09:00", comments="Source Table: public.task")
    default int insertSelective(Task row) {
        return MyBatis3Utils.insert(this::insert, row, task, c ->
            c.map(id).toPropertyWhenPresent("id", row::getId)
            .map(value).toPropertyWhenPresent("value", row::getValue)
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-05T23:04:00.587049+09:00", comments="Source Table: public.task")
    default Optional<Task> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, task, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-05T23:04:00.587189+09:00", comments="Source Table: public.task")
    default List<Task> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, task, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-05T23:04:00.587318+09:00", comments="Source Table: public.task")
    default List<Task> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, task, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-05T23:04:00.58746+09:00", comments="Source Table: public.task")
    default Optional<Task> selectByPrimaryKey(String id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-05T23:04:00.587592+09:00", comments="Source Table: public.task")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, task, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-05T23:04:00.587753+09:00", comments="Source Table: public.task")
    static UpdateDSL<UpdateModel> updateAllColumns(Task row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(id).equalTo(row::getId)
                .set(value).equalTo(row::getValue);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-05T23:04:00.587913+09:00", comments="Source Table: public.task")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(Task row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(id).equalToWhenPresent(row::getId)
                .set(value).equalToWhenPresent(row::getValue);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-05T23:04:00.588105+09:00", comments="Source Table: public.task")
    default int updateByPrimaryKey(Task row) {
        return update(c ->
            c.set(value).equalTo(row::getValue)
            .where(id, isEqualTo(row::getId))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-05T23:04:00.588259+09:00", comments="Source Table: public.task")
    default int updateByPrimaryKeySelective(Task row) {
        return update(c ->
            c.set(value).equalToWhenPresent(row::getValue)
            .where(id, isEqualTo(row::getId))
        );
    }
}