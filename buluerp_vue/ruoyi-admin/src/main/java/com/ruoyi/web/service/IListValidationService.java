package com.ruoyi.web.service;

import com.ruoyi.common.annotation.Example;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.exception.excel.ListRowErrorInfo;
import com.ruoyi.common.exception.excel.UnsupportedExampleTypeException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.utils.reflect.ReflectUtils;
import io.swagger.annotations.ApiModel;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

public interface IListValidationService {
    @FunctionalInterface
    interface InsertConsumer<T> {
        void accept(T entity) throws Exception;
    }
    @FunctionalInterface
    interface InsertFunction<T> {
        Object apply(T entity) throws Exception;
    }

    <Type> List<Type> validateExcel(MultipartFile file, Class<Type> clazz) throws IOException;
    <Type> void importExcel(MultipartFile file, Class<Type> clazz, InsertConsumer<Type> insertConsumer) throws IOException;
    <Type> void importExcel(MultipartFile file, Class<Type> clazz, InsertFunction<Type> insertFunction) throws IOException;

    <Type> List<ListRowErrorInfo> collectErrors(List<Type> list, InsertConsumer<Type> insertConsumer);
    <Type> List<ListRowErrorInfo> collectErrors(List<Type> list, InsertFunction<Type> insertFunction);
    <Type> void check(List<Type> list, InsertConsumer<Type> insertConsumer);
    <Type> void check(List<Type> list, InsertFunction<Type> insertFunction);

    <Type> List<ListRowErrorInfo> collectValidationErrors(List<Type> list);
    <Type> void checkValidation(List<Type> list);

    <Type> List<ListRowErrorInfo> collectInsertionErrors(
            List<Type> list,
            InsertConsumer<Type> insertConsumer
    );
    <Type> List<ListRowErrorInfo> collectInsertionErrors(
            List<Type> list,
            InsertFunction<Type> insertFunction
    );
    <Type> void checkInsertion(
            List<Type> list,
            InsertConsumer<Type> insertConsumer
    );
    <Type> void checkInsertion(
            List<Type> list,
            InsertFunction<Type> insertFunction
    );

    static <Type> ExcelUtil<Type> createTemplateExcelUtil(Class<Type> clazz) {
        ExcelUtil<Type> util = new ExcelUtil<>(clazz);
        util.showColumn(
                Stream.concat(Arrays.stream(clazz.getDeclaredFields()),
                                Arrays.stream(clazz.getSuperclass().getDeclaredFields()))
                        .filter(field -> field.isAnnotationPresent(Example.class))
                        .map(Field::getName)
                        .toArray(String[]::new)
        );
        return util;
    }

    static <T> T createExample(Class<T> clazz) throws InstantiationException, IllegalAccessException {
        T example = clazz.newInstance();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Example exampleAnnotation = field.getAnnotation(Example.class);
            if (exampleAnnotation != null) {
                Class<?> type = field.getType();
                String value = exampleAnnotation.value();
                if (value.equals(Example.GEN_UUID)) {
                    if (String.class == type) {
                        ReflectUtils.invokeSetter(example, field.getName(), UUID.randomUUID().toString());
                    } else if (UUID.class == type) {
                        ReflectUtils.invokeSetter(example, field.getName(), UUID.randomUUID());
                    }
                } else if (String.class == type) {
                    ReflectUtils.invokeSetter(example, field.getName(), value);
                } else if (Integer.class == type) {
                    ReflectUtils.invokeSetter(example, field.getName(), Convert.toInt(value));
                } else if (Long.class == type) {
                    ReflectUtils.invokeSetter(example, field.getName(), Convert.toLong(value));
                } else if (Double.class == type) {
                    ReflectUtils.invokeSetter(example, field.getName(), Convert.toDouble(value));
                } else if (Float.class == type) {
                    ReflectUtils.invokeSetter(example, field.getName(), Convert.toFloat(value));
                } else if (BigDecimal.class == type) {
                    ReflectUtils.invokeSetter(example, field.getName(), Convert.toBigDecimal(value));
                } else if (Date.class == type) {
                    ReflectUtils.invokeSetter(example, field.getName(), DateUtils.parseDate(value));
                } else if (Boolean.class == type) {
                    ReflectUtils.invokeSetter(example, field.getName(), Convert.toBool(value));
                } else {
                    throw new UnsupportedExampleTypeException("不支持的示例值类型");
                }
            }
        }
        return example;
    }

    static <T> void exportExample(HttpServletResponse response, Class<T> clazz) throws InstantiationException, IllegalAccessException {
        T example = createExample(clazz);
        ExcelUtil<T> util = createTemplateExcelUtil(clazz);
        String sheetName = "导入模板";
        if (clazz.isAnnotationPresent(ApiModel.class)) {
            sheetName = clazz.getAnnotation(ApiModel.class).value() + sheetName;
        }
        util.exportExcel(response, Collections.singletonList(example), sheetName);
    }
}
