package com.ruoyi.common.exception.excel;

import com.ruoyi.common.domain.ExcelRowErrorInfo;

import java.util.List;

public class ExcelImportException extends RuntimeException {
    private List<ExcelRowErrorInfo> errorInfoList;

    public ExcelImportException(List<ExcelRowErrorInfo> errorInfoList, String message) {
        super(message);
        this.errorInfoList = errorInfoList;
    }

    public ExcelImportException(List<ExcelRowErrorInfo> errorInfoList) {
        super("Excel导入失败，部分数据无效");
        this.errorInfoList = errorInfoList;
    }

    public List<ExcelRowErrorInfo> getErrorInfoList() {
        return errorInfoList;
    }
}
