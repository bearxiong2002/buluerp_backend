package com.ruoyi.web.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 已完成订单归档列表数据。
 */
@ApiModel("已完成订单归档数据")
public class ProductionArchiveOrderResult {
    @Excel(name = "订单ID")
    private Long id;

    @Excel(name = "内部编号")
    private String innerId;

    @Excel(name = "外部编号")
    private String outerId;

    @Excel(name = "客户")
    private String customerName;

    @Excel(name = "产品名称")
    private String productName;

    @Excel(name = "产品编号")
    private String productCode;

    @Excel(name = "数量")
    private Long quantity;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Excel(name = "交货时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date deliveryTime;

    @ApiModelProperty("订单状态值，归档数据固定为已完成状态")
    private Integer status;

    @Excel(name = "订单状态")
    private String statusLabel;

    @Excel(name = "操作人")
    private String operator;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Excel(name = "布产记录数")
    private Long scheduleCount;

    @Excel(name = "排产记录数")
    private Long arrangeCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInnerId() {
        return innerId;
    }

    public void setInnerId(String innerId) {
        this.innerId = innerId;
    }

    public String getOuterId() {
        return outerId;
    }

    public void setOuterId(String outerId) {
        this.outerId = outerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public void setStatusLabel(String statusLabel) {
        this.statusLabel = statusLabel;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getScheduleCount() {
        return scheduleCount;
    }

    public void setScheduleCount(Long scheduleCount) {
        this.scheduleCount = scheduleCount;
    }

    public Long getArrangeCount() {
        return arrangeCount;
    }

    public void setArrangeCount(Long arrangeCount) {
        this.arrangeCount = arrangeCount;
    }
}