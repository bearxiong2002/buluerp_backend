package com.ruoyi.web.service.impl;

import com.ruoyi.common.annotation.Example;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.excel.ListValidationException;
import com.ruoyi.common.exception.excel.ListRowErrorInfo;
import com.ruoyi.common.exception.excel.UnsupportedExampleTypeException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.utils.reflect.ReflectUtils;
import com.ruoyi.common.validation.Save;
import com.ruoyi.web.service.IListValidationService;
import io.swagger.annotations.ApiModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ListValidationServiceImpl implements IListValidationService {
    @Resource
    private Validator validator;

    @Override
    public <Type> List<Type> validateExcel(MultipartFile file, Class<Type> clazz) throws IOException {
        ExcelUtil<Type> util = new ExcelUtil<>(clazz);
        List<Type> list = util.importExcel(file.getInputStream());
        List<ListRowErrorInfo> errorList = collectValidationErrors(list);
        if (!errorList.isEmpty()) {
            throw new ListValidationException(errorList);
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <Type> void importExcel(MultipartFile file, Class<Type> clazz, InsertConsumer<Type> insertConsumer) throws IOException {
        importExcel(
                file,
                clazz,
                (entity) -> {
                    insertConsumer.accept(entity);
                    return 1;
                }
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <Type> void importExcel(MultipartFile file, Class<Type> clazz, InsertFunction<Type> insertFunction) throws IOException {
        List<Type> list = validateExcel(file, clazz);
        checkInsertion(list, insertFunction);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <Type> List<ListRowErrorInfo> collectErrors(List<Type> list, InsertConsumer<Type> insertConsumer) {
        List<ListRowErrorInfo> listRowErrorInfos = collectValidationErrors(list);
        if (listRowErrorInfos.isEmpty()) {
            listRowErrorInfos = collectInsertionErrors(list, insertConsumer);
        }
        return listRowErrorInfos;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <Type> List<ListRowErrorInfo> collectErrors(List<Type> list, InsertFunction<Type> insertFunction) {
        List<ListRowErrorInfo> listRowErrorInfos = collectValidationErrors(list);
        if (listRowErrorInfos.isEmpty()) {
            listRowErrorInfos = collectInsertionErrors(list, insertFunction);
        }
        return listRowErrorInfos;
    }

    @Override
    public <Type> void check(List<Type> list, InsertConsumer<Type> insertConsumer) {
        List<ListRowErrorInfo> errorList = collectErrors(list, insertConsumer);
        if (!errorList.isEmpty()) {
            throw new ListValidationException(errorList);
        }
    }

    @Override
    public <Type> void check(List<Type> list, InsertFunction<Type> insertFunction) {
        List<ListRowErrorInfo> errorList = collectErrors(list, insertFunction);
        if (!errorList.isEmpty()) {
            throw new ListValidationException(errorList);
        }
    }

    @Override
    public <Type> List<ListRowErrorInfo> collectValidationErrors(List<Type> list) {
        List<ListRowErrorInfo> errorList = new ArrayList<>();
        int rowIndex = 1;
        for (Type row : list) {
            Set<ConstraintViolation<Type>> constraints = validator.validate(row, Save.class);
            if (!constraints.isEmpty()) {
                ListRowErrorInfo errorInfo = new ListRowErrorInfo();
                errorInfo.setRowNum(rowIndex);
                errorInfo.setErrorMsg(
                        constraints.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("\n"))
                );
                errorList.add(errorInfo);
            }
            rowIndex++;
        }
        return errorList;
    }

    @Override
    public <Type> void checkValidation(List<Type> list) {
        List<ListRowErrorInfo> errorList = collectValidationErrors(list);
        if (!errorList.isEmpty()) {
            throw new ListValidationException(errorList);
        }
    }

    @Override
    public <Type> List<ListRowErrorInfo> collectInsertionErrors(List<Type> list, InsertConsumer<Type> insertConsumer) {
        return collectInsertionErrors(list, (entity) -> {
            insertConsumer.accept(entity);
            return 1;
        });
    }

    @Override
    public <Type> List<ListRowErrorInfo> collectInsertionErrors(List<Type> list, InsertFunction<Type> insertFunction) {
        List<ListRowErrorInfo> errorList = new ArrayList<>();
        int rowIndex = 1;
        for (Type row : list) {
            try {
                Object result = insertFunction.apply(row);
                Class<?> resultType = result.getClass();
                if (resultType == Integer.class) {
                    if ((int) result == 0) {
                        throw new ServiceException("操作失败");
                    }
                } else if (resultType == Boolean.class) {
                    if (!(boolean) result) {
                        throw new ServiceException("操作失败");
                    }
                }
            } catch (Exception e) {
                ListRowErrorInfo errorInfo = new ListRowErrorInfo();
                errorInfo.setRowNum(rowIndex);
                errorInfo.setErrorMsg(e.getMessage());
                errorList.add(errorInfo);
            }
            rowIndex++;
        }
        return errorList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <Type> void checkInsertion(List<Type> list, InsertConsumer<Type> insertConsumer) {
        List<ListRowErrorInfo> errorList = collectInsertionErrors(list, insertConsumer);
        if (!errorList.isEmpty()) {
            throw new ListValidationException(errorList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <Type> void checkInsertion(List<Type> list, InsertFunction<Type> insertFunction) {
        List<ListRowErrorInfo> errorList = collectInsertionErrors(list, insertFunction);
        if (!errorList.isEmpty()) {
            throw new ListValidationException(errorList);
        }
    }

    @Override
    public <Type> ExcelUtil<Type> createTemplateExcelUtil(Class<Type> clazz) {
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

    @Override
    public <T> T createExample(Class<T> clazz) throws InstantiationException, IllegalAccessException {
        T example = clazz.newInstance();
        List<Field> fields = new ArrayList<>();
        Class<?> superClass = clazz;
        while (superClass != null) {
            fields.addAll(Arrays.asList(superClass.getDeclaredFields()));
            superClass = superClass.getSuperclass();
        }
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
                } else if (value.equals(Example.CREATE_RECURSIVE)) {
                    if (Collection.class.isAssignableFrom(type)) {
                        Collection<Object> list;
                        if (type == List.class) {
                            list = new ArrayList<>();
                        } else if (type == Set.class) {
                            list = new HashSet<>();
                        } else if (!type.isInterface()) {
                            list = (Collection<Object>) type.newInstance();
                        } else {
                            throw new UnsupportedExampleTypeException("不支持的集合类型");
                        }
                        for (int i = 0; i < 3; i++) {
                            list.add(createExample(exampleAnnotation.elementType()));
                        }
                        ReflectUtils.invokeSetter(example, field.getName(), list);
                    } else {
                        ReflectUtils.invokeSetter(example, field.getName(), createExample(type));
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

    @Override
    public <T> void exportExample(HttpServletResponse response, Class<T> clazz) throws InstantiationException, IllegalAccessException {
        T example = createExample(clazz);
        ExcelUtil<T> util = createTemplateExcelUtil(clazz);
        String sheetName = "导入模板";
        if (clazz.isAnnotationPresent(ApiModel.class)) {
            sheetName = clazz.getAnnotation(ApiModel.class).value() + sheetName;
        }
        util.exportExcel(response, Collections.singletonList(example), sheetName);
    }
}
