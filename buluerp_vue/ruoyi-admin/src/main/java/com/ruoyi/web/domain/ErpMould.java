package com.ruoyi.web.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Example;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

public class ErpMould {
    public static final String STATUS_CREATED = "创建(待制作)";
    public static final String STATUS_MADE = "制作完成(待验收)";
    public static final String STATUS_ACCEPTED = "验收通过";
    public static final String STATUS_REPAIRING = "维修中";
    public static final String STATUS_DONE = "试模完成";
    public static final String[] STATUS_LIST = {STATUS_CREATED, STATUS_MADE, STATUS_ACCEPTED, STATUS_REPAIRING, STATUS_DONE};
    public static boolean isStatusValid(String status) {
        for (String s : STATUS_LIST) {
            if (s.equals(status)) {
                return true;
            }
        }
        return false;
    }

    @TableId(type = IdType.AUTO)
    @Excel(name = "模具ID")
    private Long id;

    @TableField(condition = SqlCondition.LIKE)
    @Excel(name = "模具编号")
    @Example(value = "M001")
    private String mouldNumber;

    @TableField(condition = SqlCondition.LIKE)
    @Excel(name = "模具状态")
    @Example(value = "创建(待制作)")
    private String status;

    @Excel(name = "试用日期")
    @Example(value = "2023-01-01")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date trialDate;

    @Excel(name = "生产商ID")
    @Example(value = "1")
    private Long manufacturerId;

    @Excel(name = "模房ID（-1代表未分配）")
    @Example(value = "1")
    private Long mouldHouseId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMouldNumber() {
        return mouldNumber;
    }

    public void setMouldNumber(String mouldNumber) {
        this.mouldNumber = mouldNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getTrialDate() {
        return trialDate;
    }

    public void setTrialDate(Date trialDate) {
        this.trialDate = trialDate;
    }

    public Long getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(Long manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public Long getMouldHouseId() {
        return mouldHouseId;
    }

    public void setMouldHouseId(Long mouldHouseId) {
        this.mouldHouseId = mouldHouseId;
    }
}
