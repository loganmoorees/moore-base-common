package com.moore.base.excel;

import org.springframework.web.multipart.MultipartFile;

/**
 * 导入导出抽象方法
 *
 * @author moore
 */
public abstract class AbstractExcelCommonHandler<T> implements ExcelCommonHandler<T> {

    @Override
    public void excelImport(T t, MultipartFile file) {
        initParam(t);
    }

    protected abstract void initParam(T t);
}
