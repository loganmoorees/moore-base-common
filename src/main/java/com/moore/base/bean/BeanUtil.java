package com.moore.base.bean;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * bean util
 *
 * @author moore
 */
@Slf4j
public final class BeanUtil {

    /**
     * source 转 target
     *
     * @param source    object
     * @param targetCls class
     * @param <TARGET>  target
     * @return          target
     */
    public static <TARGET> TARGET copy(Object source, Class<TARGET> targetCls) {
        if (source == null) {
            return null;
        } else {
            TARGET entity;
            try {
                entity = targetCls.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                log.error("bean copy create instance error:", e);
                return null;
            }
            BeanUtils.copyProperties(source, entity);
            return entity;
        }
    }

    /**
     * source List 转 target List
     */
    public static <R> List<R> copyList(List<?> sourceList, Class<R> target) {
        return sourceList.stream().map(source -> copy(source, target)).collect(Collectors.toList());
    }

    /**
     * MybatisPlus分页类型转换
     */
    public static <R> IPage<R> copyPage(IPage<?> sourceList, Class<R> target) {
        IPage<R> iPage = new Page<>();
        BeanUtils.copyProperties(sourceList, iPage);
        return iPage.setRecords(copyList(sourceList.getRecords(), target));
    }
}
