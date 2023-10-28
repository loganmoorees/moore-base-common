package com.moore.base.excel;

import java.util.List;

/**
 * add data
 *
 * @author moore
 */
public interface ImportComponent<E> {

    void insertData(EasyExcelCommonListener<E> list);

    void insertException(EasyExcelCommonListener<E> listener);
}
