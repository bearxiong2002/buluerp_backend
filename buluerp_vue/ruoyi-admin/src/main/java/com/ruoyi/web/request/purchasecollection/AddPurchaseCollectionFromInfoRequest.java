package com.ruoyi.web.request.purchasecollection;

import com.ruoyi.common.validation.Save;
import com.ruoyi.common.validation.Update;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class AddPurchaseCollectionFromInfoRequest {

    @ApiModelProperty(value = "外购信息ID")
    Long purchaseInfoId;

    @ApiModelProperty(value = "设计总表ID")
    Long designPatternId;

    @ApiModelProperty(value = "下单时间")
    Date orderTime;

    @ApiModelProperty(value = "采购数量")
    @NotNull(groups = {Save.class}, message = "采购数量不能为空")
    @Range(min = 1, groups = {Save.class, Update.class}, message = "采购数量必须大于0")
    private Long purchaseQuantity;

    @ApiModelProperty(value = "颜色编号，产品的颜色编号")
    private String colorCode;

    @ApiModelProperty(value = "货期，供应商承诺的交货时间")
    private String deliveryTime;

    @ApiModelProperty(value = "备注")
    private String remarks;

    public Long getDesignPatternId() {
        return designPatternId;
    }

    public void setDesignPatternId(Long designPatternId) {
        this.designPatternId = designPatternId;
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

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
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
