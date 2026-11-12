package com.ruoyi.web.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Example;
import com.ruoyi.common.annotation.Excel;

import java.util.Date;

public class ErpMould {
    public static final String STATUS_CREATED = "新模(排产中)";
    public static final String STATUS_MADE = "新模完成(待试模)";
    public static final String STATUS_ACCEPTED = "验收合格(已入库)";
    public static final String STATUS_REPAIRING = "模具故障送修中";
    public static final String STATUS_REPAIRED = "维修好返厂待试模";
    public static final String STATUS_DISTRIBUTED = "已外发";
    public static final String STATUS_PRODUCE_THEN_REPAIR = "先生产后修模";
    public static final String STATUS_TRIALED = "已试模";
    public static final String[] STATUS_LIST = {
            STATUS_CREATED,
            STATUS_MADE,
            STATUS_ACCEPTED,
            STATUS_REPAIRING,
            STATUS_REPAIRED,
            STATUS_DISTRIBUTED,
            STATUS_PRODUCE_THEN_REPAIR,
            STATUS_TRIALED
    };
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

    @Excel(name = "模具图片", cellType = Excel.ColumnType.IMAGE, height = 80)
    private String pictureUrl;

    public String getPictureUrl() { return pictureUrl; }
    public void setPictureUrl(String pictureUrl) { this.pictureUrl = pictureUrl; }

    @TableField(condition = SqlCondition.LIKE)
    @Excel(name = "模具状态")
    private String status;

    @Excel(name = "备注")
    private String remark;

    @Excel(name = "试模日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date trialDate;

    @Excel(name = "生产商ID")
    @Example(value = "1")
    private Long manufacturerId;

    @Excel(name = "模房ID（-1代表未分配）")
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
