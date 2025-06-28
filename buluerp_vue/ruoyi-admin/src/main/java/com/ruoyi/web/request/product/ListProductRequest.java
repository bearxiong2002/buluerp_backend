package com.ruoyi.web.request.product;

import com.ruoyi.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

@ApiModel(value = "查询产品列表请求类")
public class ListProductRequest {
    @ApiModelProperty(dataType = "Long",value = "产品id",required = false)
    private Long id;
    @ApiModelProperty(dataType = "int",value = "订单id",required = false)
    private Integer orderId;
    @ApiModelProperty(dataType = "String",value = "产品名称",required = false)
    private String name;
    @ApiModelProperty(dataType = "String",value = "创建用户名称",required = false)
    private String createUsername;
    @ApiModelProperty(dataType = "Datetime",value = "创建时间起始",required = false)
    private LocalDateTime createTimeFrom;
    @ApiModelProperty(dataType = "Datetime",value = "创建时间终止",required = false)
    private LocalDateTime createTimeTo;
    @ApiModelProperty(dataType = "int",value = "产品设计是否完成 0未完成 1完成",required = false)
    private Integer designStatus;
    private String innerId;
    private String outerId;

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

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getDesignStatus() {
        return designStatus;
    }

    public void setDesignStatus(Integer designStatus) {
        this.designStatus = designStatus;
    }

    public String getCreateUsername() {
        return createUsername;
    }

    public void setCreateUsername(String createUsername) {
        this.createUsername = createUsername;
    }

    public LocalDateTime getCreateTimeFrom() {
        return createTimeFrom;
    }

    public void setCreateTimeFrom(LocalDateTime createTimeFrom) {
        this.createTimeFrom = createTimeFrom;
    }

    public LocalDateTime getCreateTimeTo() {
        return createTimeTo;
    }

    public void setCreateTimeTo(LocalDateTime createTimeTo) {
        this.createTimeTo = createTimeTo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
