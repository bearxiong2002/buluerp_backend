package com.ruoyi.web.request.design;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel(value = "新增设计总表请求类")
public class AddDesignPatternsRequest {
    @JsonIgnore
    private Long id; // 添加这个字段并忽略，防止 Jackson 反序列化时错误映射
    
    @NotNull(message = "订单号不能为空")
    @Excel(name = "订单号")
    @ApiModelProperty(dataType = "String",value = "订单号",required = true)
    private String orderId;
    
    @NotNull(message = "产品号不能为空")
    @Excel(name = "产品号")
    @ApiModelProperty(dataType = "String",value = "产品内部编码",required = true)
    private String productId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
