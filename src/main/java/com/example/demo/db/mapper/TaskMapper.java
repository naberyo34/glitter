package com.example.demo.db.mapper;

import static com.example.demo.db.mapper.TaskDynamicSqlSupport.*;

import com.example.demo.db.model.Task;
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
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-02T15:03:16.789029+09:00", comments="Source Table: public.task")
    BasicColumn[] selectList = BasicColumn.columnList(id, value);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-02T15:03:16.786442+09:00", comments="Source Table: public.task")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="TaskResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.CHAR),
        @Result(column="value", property="value", jdbcType=JdbcType.VARCHAR)
    })
    List<Task> selectMany(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-02T15:03:16.787191+09:00", comments="Source Table: public.task")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("TaskResult")
    Optional<Task> selectOne(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-02T15:03:16.78735+09:00", comments="Source Table: public.task")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, task, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-02T15:03:16.787492+09:00", comments="Source Table: public.task")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, task, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-02T15:03:16.787882+09:00", comments="Source Table: public.task")
    default int insert(Task row) {
        return MyBatis3Utils.insert(this::insert, row, task, c ->
            c.map(id).toProperty("id")
            .map(value).toProperty("value")
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-02T15:03:16.788457+09:00", comments="Source Table: public.task")
    default int insertMultiple(Collection<Task> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, task, c ->
            c.map(id).toProperty("id")
            .map(value).toProperty("value")
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-02T15:03:16.788673+09:00", comments="Source Table: public.task")
    default int insertSelective(Task row) {
        return MyBatis3Utils.insert(this::insert, row, task, c ->
            c.map(id).toPropertyWhenPresent("id", row::getId)
            .map(value).toPropertyWhenPresent("value", row::getValue)
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-02T15:03:16.789357+09:00", comments="Source Table: public.task")
    default Optional<Task> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, task, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-02T15:03:16.789495+09:00", comments="Source Table: public.task")
    default List<Task> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, task, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-02T15:03:16.789637+09:00", comments="Source Table: public.task")
    default List<Task> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, task, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-02T15:03:16.789892+09:00", comments="Source Table: public.task")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, task, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-02T15:03:16.790067+09:00", comments="Source Table: public.task")
    static UpdateDSL<UpdateModel> updateAllColumns(Task row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(id).equalTo(row::getId)
                .set(value).equalTo(row::getValue);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2025-03-02T15:03:16.79027+09:00", comments="Source Table: public.task")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(Task row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(id).equalToWhenPresent(row::getId)
                .set(value).equalToWhenPresent(row::getValue);
    }
}