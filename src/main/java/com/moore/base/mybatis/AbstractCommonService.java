package com.moore.base.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.moore.base.bean.BeanUtil;
import com.moore.base.bean.PageParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractCommonService<M extends BaseMapper<T>, T, D extends PageParam, V> extends ServiceImpl<M, T> implements CommonService<T, D, V> {

    ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();

    Class<V> vClazz = (Class<V>) superClass.getActualTypeArguments()[3];

    Class<T> tClazz = (Class<T>) superClass.getActualTypeArguments()[1];

    @Autowired
    protected M m;


    @Override
    public IPage<V> pageList(D d) {
        LambdaQueryWrapper<T> tLambdaQueryWrapper = getLambdaQueryWrapper(d);

        IPage<T> pageList = m.selectPage(new Page<>(d.getCurrentPage(), d.getPageSize()), tLambdaQueryWrapper);
        return Optional.ofNullable(pageList)
                .map(page -> {
                    IPage<V> viPage = BeanUtil.copyPage(page, vClazz);
                    List<V> voList = viPage.getRecords();

                    beforePageResultsReturnProcessor(voList, d);

                    beforePageResultsReturnProcessor(voList);

                    voList.forEach(this::beforePageResultReturnProcessor);

                    return viPage;

                })
                .orElseGet(Page::new);
    }

    protected LambdaQueryWrapper<T> getLambdaQueryWrapper(D d) {
        LambdaQueryWrapper<T> tLambdaQueryWrapper = new LambdaQueryWrapper<>();

        beforePageQueryConditionProcessor(tLambdaQueryWrapper, d);
        return tLambdaQueryWrapper;
    }


    @Override
    public V detail(D d, Long id) {
        return Optional.ofNullable(getById(id))
                .map(t -> {
                    V v = BeanUtil.copy(t, vClazz);

                    beforeDetailReturnProcessor(v);

                    return v;
                })
                .orElseThrow(NullPointerException::new);

    }

    @Override
    @Transactional
    public Long add(D d) {
        beforeInsertDtoProcessor(d);

        T t = D2T(d);
        beforeInsertProcessor(t);

        this.save(t);

        afterInsertProcessor(t, d);

        return 1L;
    }

    private T D2T(D d) {
        T t = null;
        try {
            t = tClazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.info(Arrays.toString(e.getStackTrace()));
        }
        assert t != null;
        BeanUtils.copyProperties(d, t);
        return t;
    }

    @Override
    @Transactional
    public Boolean update(D d) {
        beforeUpdateDtoProcessor(d);
        T t = D2T(d);

        beforeUpdateProcessor(t);
        return this.updateById(t);
    }

    @Override
    public List<V> list(D d) {
        LambdaQueryWrapper<T> tLambdaQueryWrapper = getLambdaQueryWrapper(d);

        return Optional.ofNullable(
                m.selectList(tLambdaQueryWrapper)
        ).map(tList -> {
            List<V> voList = BeanUtil.copyList(tList, vClazz);

            beforeListResultsReturnProcessor(voList);

            beforeListResultsReturnProcessor(voList, d);

            return voList;
        }).orElseGet(Lists::newArrayList);
    }

    @Override
    public V getOne(D d) {
        List<V> voList = list(d);
        Assert.isTrue(Objects.equals(voList.size(), 1),"没有查到或者查到多个数据");
        return voList.get(0);
    }

    @Override
    public Integer count(D d) {
        LambdaQueryWrapper<T> tLambdaQueryWrapper = getLambdaQueryWrapper(d);
        return m.selectCount(tLambdaQueryWrapper);
    }

    @Override
    public List<V> listWithoutReturnProcessor(D d) {
        LambdaQueryWrapper<T> tLambdaQueryWrapper = getLambdaQueryWrapper(d);

        return Optional.ofNullable(
                        m.selectList(tLambdaQueryWrapper)
                ).map(tList -> BeanUtil.copyList(tList, vClazz))
                .orElseGet(Lists::newArrayList);
    }

    @Override
    public <K, U> Map<K, U> listMap(D d, Function<? super V, ? extends K> keyMapper, Function<? super V, ? extends U> valueMapper) {
        return listMap(d, keyMapper, valueMapper, v -> true);
    }

    @Override
    public <K, U> Map<K, U> listMap(D d, Function<? super V, ? extends K> keyMapper, Function<? super V, ? extends U> valueMapper, Predicate<? super V> predicate) {
        return listWithoutReturnProcessor(d).stream().filter(predicate).collect(Collectors.toMap(keyMapper, valueMapper));
    }

    @Override
    public <U> List<U> list(D d, Function<? super V, ? extends U> mapper) {
        return listWithoutReturnProcessor(d).stream().map(mapper).distinct().collect(Collectors.toList());
    }

    @Override
    public <U> List<U> listWithProcessor(D d, Function<? super V, ? extends U> mapper) {
        return list(d).stream().map(mapper).distinct().collect(Collectors.toList());
    }

    @Override
    public Boolean delete(D d) {
        return updateById(BeanUtil.copy(d, tClazz));
    }
}
