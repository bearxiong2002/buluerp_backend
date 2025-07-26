package com.ruoyi.web.request.purchasecollection;

import com.ruoyi.common.annotation.Example;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.validation.Save;
import com.ruoyi.common.validation.Update;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class AddPurchaseCollectionFromInfoRequest {

    @ApiModelProperty(value = "外购信息ID")
    @Excel(name = "外购信息ID")
    @Example("1")
    Long purchaseInfoId;

    @ApiModelProperty(value = "设计总表ID")
    @Excel(name = "设计总表ID")
    @Example("1")
    Long designPatternId;

    @ApiModelProperty(value = "订单编号")
    @Excel(name = "订单编号")
    @Example("123456")
    String orderCode;

    @ApiModelProperty(value = "下单时间")
    @Excel(name = "下单时间")
    @Example("2023-01-01")
    Date orderTime;

    @ApiModelProperty(value = "采购数量")
    @Excel(name = "采购数量")
    @NotNull(groups = {Save.class}, message = "采购数量不能为空")
    @Range(min = 1, groups = {Save.class, Update.class}, message = "采购数量必须大于0")
    @Example("100")
    private Long purchaseQuantity;

    @ApiModelProperty(value = "货期，供应商承诺的交货时间")
    @Excel(name = "货期")
    @Example("2023-01-01")
    private String deliveryTime;

    @ApiModelProperty(value = "备注")
    @Excel(name = "备注")
    @Example("暂无")
    private String remarks;

    public Long getDesignPatternId() {
        return designPatternId;
    }

    public void setDesignPatternId(Long designPatternId) {
        this.designPatternId = designPatternId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Long getPurchaseInfoId() {
        return purchaseInfoId;
    }

    public void setPurchaseInfoId(Long purchaseInfoId) {
        this.purchaseInfoId = purchaseInfoId;
    }

    public Long getPurchaseQuantity() {
        return purchaseQuantity;
    }

    public void setPurchaseQuantity(Long purchaseQuantity) {
        this.purchaseQuantity = purchaseQuantity;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }
}
