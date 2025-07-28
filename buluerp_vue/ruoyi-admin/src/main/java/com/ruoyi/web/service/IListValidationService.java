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

    <Type> ExcelUtil<Type> createTemplateExcelUtil(Class<Type> clazz);

    <T> T createExample(Class<T> clazz) throws InstantiationException, IllegalAccessException;

    <T> void exportExample(HttpServletResponse response, Class<T> clazz) throws InstantiationException, IllegalAccessException;
}
