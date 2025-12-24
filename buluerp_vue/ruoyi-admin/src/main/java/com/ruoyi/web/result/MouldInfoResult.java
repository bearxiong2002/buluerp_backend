package com.ruoyi.web.result;

import java.util.Date;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;

public class MouldInfoResult {

    @Excel(name = "模具ID")
    private Long id;

    @Excel(name = "模具编号")
    private String mouldNumber;

    @Excel(name = "模具状态")
    private String status;

    @Excel(name = "试模日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date trialDate;

    @Excel(name = "生产商名称")
    private String manufacturerName;

    @Excel(name = "模房ID（-1代表未分配）")
    private Long mouldHouseId;

    public MouldInfoResult() {
    }

    public MouldInfoResult(Long id, String mouldNumber, String status, Date trialDate, String manufacturerName, Long mouldHouseId) {
        this.id = id;
        this.mouldNumber = mouldNumber;
        this.status = status;
        this.trialDate = trialDate;
        this.manufacturerName = manufacturerName;
        this.mouldHouseId = mouldHouseId;
    }

    /**
     * 获取
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取
     * @return mouldNumber
     */
    public String getMouldNumber() {
        return mouldNumber;
    }

    /**
     * 设置
     * @param mouldNumber
     */
    public void setMouldNumber(String mouldNumber) {
        this.mouldNumber = mouldNumber;
    }

    /**
     * 获取
     * @return status
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取
     * @return trialDate
     */
    public Date getTrialDate() {
        return trialDate;
    }

    /**
     * 设置
     * @param trialDate
     */
    public void setTrialDate(Date trialDate) {
        this.trialDate = trialDate;
    }

    /**
     * 获取
     * @return manufacturerName
     */
    public String getManufacturerName() {
        return manufacturerName;
    }

    /**
     * 设置
     * @param manufacturerName
     */
    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    /**
     * 获取
     * @return mouldHouseId
     */
    public Long getMouldHouseId() {
        return mouldHouseId;
    }

    /**
     * 设置
     * @param mouldHouseId
     */
    public void setMouldHouseId(Long mouldHouseId) {
        this.mouldHouseId = mouldHouseId;
    }

    public String toString() {
        return "MouldInfoResult{id = " + id + ", mouldNumber = " + mouldNumber + ", status = " + status + ", trialDate = " + trialDate + ", manufacturerName = " + manufacturerName + ", mouldHouseId = " + mouldHouseId + "}";
    }
}
