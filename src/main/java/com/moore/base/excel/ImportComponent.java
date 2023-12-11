package com.moore.base.excel;

import com.alibaba.excel.context.AnalysisContext;

import java.util.Map;

/**
 * add data
 *
 * @author moore
 */
public interface ImportComponent<E> {

    /**
     * data handle
     *
     * @param list  data
     */
    void insertData(EasyExcelCommonListener<E> list);

    /**
     * exception data handle
     *
     * @param listener  listener
     */
    default void insertException(EasyExcelCommonListener<E> listener) {};

    /**
     * excel header check
     *
     * @param headMap   map key: index value: excel header
     * @param context   context
     */
    default void checkHeadMap(Map<Integer, String> headMap, AnalysisContext context) {};
}
