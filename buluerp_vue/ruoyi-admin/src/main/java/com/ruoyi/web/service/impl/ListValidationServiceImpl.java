package com.ruoyi.web.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.excel.ListValidationException;
import com.ruoyi.common.exception.excel.ListRowErrorInfo;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.validation.Save;
import com.ruoyi.web.service.IListValidationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public <Type> List<ListRowErrorInfo> collectErrors(List<Type> list, InsertConsumer<Type> insertConsumer) {
        List<ListRowErrorInfo> listRowErrorInfos = collectValidationErrors(list);
        listRowErrorInfos.addAll(collectInsertionErrors(list, insertConsumer));
        return listRowErrorInfos;
    }

    @Override
    public <Type> List<ListRowErrorInfo> collectErrors(List<Type> list, InsertFunction<Type> insertFunction) {
        List<ListRowErrorInfo> listRowErrorInfos = collectValidationErrors(list);
        listRowErrorInfos.addAll(collectInsertionErrors(list, insertFunction));
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
}
