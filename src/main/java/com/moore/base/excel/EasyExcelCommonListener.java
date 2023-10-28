package com.moore.base.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.Cell;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.read.metadata.holder.ReadRowHolder;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.moore.base.annotations.ExcelLength;
import com.moore.base.annotations.ExcelNotBlank;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * easy excel listener
 *
 * @param <T> parse entity object
 * @author    moore
 */
@Slf4j
public class EasyExcelCommonListener<T> extends AnalysisEventListener<T> {


    public int size = 0;
    public Object[] params;
    public List<T> list = new ArrayList<>();
    public List<Map<String, Cell>> errorRowList = Lists.newArrayList();
    public List<String> failList = Collections.synchronizedList(new ArrayList<>());

    private int indexSize;
    private int batchCount = 1000;
    private boolean initSizeFlag = true;
    private ImportComponent<T> importService;
    private List<Map<Integer, Cell>> originalRowList = Lists.newArrayList();

    private EasyExcelCommonListener(int batchCount, ImportComponent<T> importService, Object... params) {
        this.importService = importService;
        this.batchCount = batchCount;
        this.params = params;
    }

    public static <T> EasyExcelCommonListener<T> getInstance(int batchCount, ImportComponent<T> importService, Object... params) {
        return new EasyExcelCommonListener<T>(batchCount, importService, params);
    }

    @SneakyThrows
    @Override
    public void invoke(T data, AnalysisContext context) {
        ReadRowHolder readRowHolder = context.readRowHolder();
        Integer rowIndex = readRowHolder.getRowIndex() + 1;

        validation(data, rowIndex);
        initMapSize(data);
        log.info("parse excel get data is: {}", JSON.toJSONString(data));
        list.add(data);

        cellMap(context);

        if (list.size() >= batchCount) {
            importService.insertData(this);
            size += list.size();
            list.clear();
            originalRowList.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        if (list.size() > 0) {
            importService.insertData(this);
            size += list.size();
        }

        importService.insertException(this);
        log.info("excel parsing completed");
        list.clear();
    }

    private void validation(T t, Integer rowIndex) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        Class<?> clazz = t.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            ExcelLength excelLength = field.getAnnotation(ExcelLength.class);
            ExcelNotBlank excelNotBlank = field.getAnnotation(ExcelNotBlank.class);
            ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
            if (excelLength != null || excelNotBlank != null) {
                PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
                Method getMethod = pd.getReadMethod();
                Object invoke = getMethod.invoke(t);
                int index = excelProperty.index() + 1;
                if (invoke instanceof String) {
                    String str = (String) invoke;
                    Assert.isTrue(str.length() < excelLength.max() && str.length() >= excelLength.min(), String.format("Excel第{%d}行，第{%d}列数据为:{%s} 超出了长度要求", rowIndex, index, str));
                } else if (excelNotBlank != null) {
                    Assert.isTrue(!StringUtils.isEmpty(invoke), String.format("Excel第{%d}行，第{%d}列数据不能为空", rowIndex, index));
                }
            }
        }
    }

    private void cellMap(AnalysisContext context) {
        Map<Integer, Cell> cellMap = context.readRowHolder().getCellMap();
        Set<Integer> keySet = cellMap.keySet();
        Integer maxIndex = Collections.max(keySet);
        if (maxIndex < this.indexSize) {
            cellMap.put(this.indexSize, new WriteCellData<>());
        }
        originalRowList.add(cellMap);
    }

    private void initMapSize(T t) {
        if (initSizeFlag) {
            initSizeFlag = false;
            int indexSize = 0;
            Class<?> cls = t.getClass();
            try {
                Field excelColumnNum = cls.getDeclaredField("excelColumnNum");
                Object o = excelColumnNum.get(t);
                indexSize = Integer.parseInt(o.toString());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                Field[] fields = cls.getDeclaredFields();
                for (Field field : fields) {
                    ExcelProperty annotation = field.getAnnotation(ExcelProperty.class);
                    if (annotation != null) {
                        int index = annotation.index();
                        indexSize = Math.max(index, indexSize);
                    }
                }
            }
            this.indexSize = indexSize;
        }
    }
}
