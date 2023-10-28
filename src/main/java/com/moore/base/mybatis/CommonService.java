package com.moore.base.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 公共服务接口
 *
 * @author moore
 */
public interface CommonService<T, D, V> extends IService<T> {

    /**
     * 分页列表
     *
     * @param d dto
     * @return IPage
     */
    IPage<V> pageList(D d);

    /**
     * 详情接口
     *
     * @param d  dto
     * @param id id
     * @return vo
     */
    V detail(D d, Long id);

    /**
     * 新增接口
     *
     * @param d dto
     * @return 1L
     */
    Long add(D d);

    /**
     * 更新接口
     *
     * @param d dto
     * @return  boolean
     */
    Boolean update(D d);


    /**
     * 删除接口
     *
     * @param d dto
     * @return Boolean
     */
    Boolean delete(D d);

    /**
     * 根据查询条件返回列表接口
     *
     * @param d dto
     * @return voList
     */
    List<V> list(D d);

    /**
     * 根据查询条件返回一条数据，没有查到或者查到多个会抛出异常
     *
     * @param d dto
     * @return v
     */
    V getOne(D d);

    /**
     * 根据条件查询数量
     *
     * @param d dto
     * @return count
     */
    Integer count(D d);

    /**
     * 根据查询条件返回列表接口没有返回处理
     *
     * @param d dto
     * @return voList
     */
    List<V> listWithoutReturnProcessor(D d);

    /**
     * 根据条件查询 map
     *
     * @param d           dto
     * @param keyMapper   key 映射函数
     * @param valueMapper value 映射函数
     * @param <K>         map.key 类型
     * @param <U>         map.value 类型
     * @return map<K, U>
     */
    <K, U> Map<K, U> listMap(D d, Function<? super V, ? extends K> keyMapper, Function<? super V, ? extends U> valueMapper);

    /**
     * 根据条件查询 map
     *
     * @param d           dto
     * @param keyMapper   key 映射函数
     * @param valueMapper value 映射函数
     * @param <K>         map.key 类型
     * @param <U>         map.value 类型
     * @param predicate   过滤条件
     * @return map<K, U>
     */
    <K, U> Map<K, U> listMap(D d, Function<? super V, ? extends K> keyMapper, Function<? super V, ? extends U> valueMapper, Predicate<? super V> predicate);

    /**
     * 根据查询条件返回指定指字段
     *
     * @param d      dto
     * @param mapper 指定字段映射函数
     * @param <U>    list.value 类型
     * @return List<U>
     */
    <U> List<U> list(D d, Function<? super V, ? extends U> mapper);

    /**
     * 查询带返回处理的返回指定字段
     *
     * @param d      dto
     * @param mapper 指定字段映射函数
     * @param <U>    list.value 类型
     * @return List<U>
     */
    <U> List<U> listWithProcessor(D d, Function<? super V, ? extends U> mapper);


    /**
     * 查询前条件处理
     *
     * @param lambdaQueryWrapper 查询条件
     * @param d                  dto
     */
    default void beforePageQueryConditionProcessor(LambdaQueryWrapper<T> lambdaQueryWrapper, D d) {

    }


    /**
     * 返回前遍历处理 vo
     *
     * @param v vo
     */
    default void beforePageResultReturnProcessor(V v) {
    }


    /**
     * Page 返回前处理
     *
     * @param voList voList
     */
    default void beforePageResultsReturnProcessor(List<V> voList) {
    }

    /**
     * Page 返回前处理 dto
     *
     * @param voList voList
     * @param d      dto
     */
    default void beforePageResultsReturnProcessor(List<V> voList, D d) {
    }

    /**
     * list 返回前处理
     *
     * @param voList voList
     */
    default void beforeListResultsReturnProcessor(List<V> voList) {
    }

    /**
     * list 返回前处理 dto
     *
     * @param voList voList
     * @param d      dto
     */
    default void beforeListResultsReturnProcessor(List<V> voList, D d) {
    }

    /**
     * 详情 vo 返回前处理
     *
     * @param v vo
     */
    default void beforeDetailReturnProcessor(V v) {
    }

    /**
     * 新增前实体参数处理
     *
     * @param t t
     */
    default void beforeInsertProcessor(T t) {
    }

    /**
     * 新增前 Dto 参数处理
     *
     * @param d t
     */
    default void beforeInsertDtoProcessor(D d) {
    }

    /**
     * 新增之后其他的逻辑处理
     *
     * @param t t
     */
    default void afterInsertProcessor(T t, D d) {
    }

    /**
     * 更新前参数或者其他逻辑处理
     *
     * @param t t
     */
    default void beforeUpdateProcessor(T t) {
    }

    /**
     * 更新前 dto 参数或者其他逻辑处理
     *
     * @param d dto
     */
    default void beforeUpdateDtoProcessor(D d) {
    }
}
