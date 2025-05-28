package com.ruoyi.web.exception;

public class ImportException extends RuntimeException {
    private int rowNumber;
    private String errorMsg;
    private String rawData;

    public ImportException(int rowNumber, String errorMsg, String rawData) {
        super(String.format("第%d行错误: %s", rowNumber, errorMsg));
        this.rowNumber = rowNumber;
        this.errorMsg = errorMsg;
        this.rawData = rawData;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }
}