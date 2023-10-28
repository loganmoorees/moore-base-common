package com.moore.base.excel;

import org.springframework.web.multipart.MultipartFile;

public interface ExcelCommonHandler<T> {

    void excelImport(T t, MultipartFile file);

    void excelExport(T t);
}
