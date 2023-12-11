package com.moore.base.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.read.metadata.holder.ReadRowHolder;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.moore.base.annotations.ExcelLength;
import com.moore.base.annotations.ExcelNotBlank;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * easy excel listener
 *
 * @param <T> parse entity object
 * @author    moore
 */
@Slf4j
public class EasyExcelCommonListener<T> extends AnalysisEventListener<T> {

    private final int batchCount;
    private final ImportComponent<T> importService;
    private final List<T> list = Lists.newArrayList();
    private final List<String> failList = Lists.newArrayList();

    private EasyExcelCommonListener(int batchCount, ImportComponent<T> importService) {
        this.importService = importService;
        this.batchCount = batchCount;
    }

    public static <T> EasyExcelCommonListener<T> getInstance(int batchCount, ImportComponent<T> importService) {
        return new EasyExcelCommonListener<>(batchCount, importService);
    }

    public static <T> EasyExcelCommonListener<T> getInstance(ImportComponent<T> importService) {
        return new EasyExcelCommonListener<>(500, importService);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        if (list.size() == 0) {
            return;
        }
        importService.insertData(this);
        importService.insertException(this);
        log.info("excel parsing completed");
        list.clear();
    }

    @Override
    @SneakyThrows
    public void invoke(T data, AnalysisContext context) {
        final ReadRowHolder readRowHolder = context.readRowHolder();
        final Integer rowIndex = readRowHolder.getRowIndex() + 1;

        validation(data, rowIndex);

        log.info("parse excel get data is: {}", JSON.toJSONString(data));
        list.add(data);

        if (list.size() >= batchCount) {
            importService.insertData(this);
            list.clear();
        }
    }

    /**
     * All listeners receive this method when anyone Listener does an error report. If an exception is thrown here, the
     * entire read will terminate.
     *
     * @param exception
     * @param context
     * @throws Exception
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        log.error("excel exception, continue parsing the next line: {}", exception.getMessage());
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException)exception;
            String message = String.format("第{%d}行，第{%d}列解析异常", excelDataConvertException.getRowIndex() + 1,
                    excelDataConvertException.getColumnIndex() + 1);
            log.error(message);
            failList.add(message + ": " + exception.getMessage());
        }
    }

    /**
     * Returns the header as a map.Override the current method to receive header data.
     *
     * @param headMap   map key: index value: excel header
     * @param context   context
     */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        importService.checkHeadMap(headMap, context);
    }

    /**
     * data validation
     *
     * @param t         data
     * @param rowIndex  row index
     * @throws IllegalAccessException
     */
    private void validation(T t, Integer rowIndex) throws IllegalAccessException {
        Class<?> clazz = t.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(ExcelProperty.class)) {
                return;
            }
            final int index = field.getAnnotation(ExcelProperty.class).index() + 1;
            Object obj = field.get(field.getName());
            if (field.isAnnotationPresent(ExcelNotBlank.class) && obj == null) {
                throw new IllegalAccessException(String.format("Excel第{%d}行，第{%d}列数据不能为空", rowIndex, index));
            }

            if (field.isAnnotationPresent(ExcelLength.class) && obj != null) {
                ExcelLength annotation = field.getAnnotation(ExcelLength.class);
                String value = (String) obj;
                Assert.state(
                        value.length() <= annotation.max() && value.length() >= annotation.min(),
                        String.format("Excel第{%d}行，第{%d}列数据为:{%s} 超出了长度要求", rowIndex, index, value)
                );
            }
        }
    }
}
