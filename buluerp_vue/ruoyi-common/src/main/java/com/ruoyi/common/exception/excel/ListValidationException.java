package com.ruoyi.common.exception.excel;

import java.util.List;

public class ListValidationException extends RuntimeException {
    private List<ListRowErrorInfo> errorInfoList;

    public ListValidationException(List<ListRowErrorInfo> errorInfoList, String message) {
        super(message);
        this.errorInfoList = errorInfoList;
    }

    public ListValidationException(List<ListRowErrorInfo> errorInfoList) {
        super("Excel导入失败，部分数据无效");
        this.errorInfoList = errorInfoList;
    }

    public List<ListRowErrorInfo> getErrorInfoList() {
        return errorInfoList;
    }
}
