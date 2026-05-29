package com.ruoyi.web.request.productionschedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Example;
import com.ruoyi.common.validation.Save;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class AddProductionScheduleFromProductRequest {

    @ApiModelProperty(value = "产品ID", required = true)
    @NotNull(message = "产品ID未填写", groups = Save.class)
    private Long productId;

    @ApiModelProperty(value = "订单编号", required = true)
    @NotNull(message = "订单编号未填写", groups = Save.class)
    private String orderCode;

    @ApiModelProperty(value = "布产数量（套数）", required = true)
    @NotNull(message = "布产数量未填写", groups = Save.class)
    private Integer quantity;

    @ApiModelProperty(value = "布产时间", required = true)
    @NotNull(message = "布产时间未填写", groups = Save.class)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date productionTime;

    @ApiModelProperty("出货时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date shipmentTime;

    @ApiModelProperty("原材料供应商")
    private String supplier;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getOrderCode() { return orderCode; }
    public void setOrderCode(String orderCode) { this.orderCode = orderCode; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Date getProductionTime() { return productionTime; }
    public void setProductionTime(Date productionTime) { this.productionTime = productionTime; }

    public Date getShipmentTime() { return shipmentTime; }
    public void setShipmentTime(Date shipmentTime) { this.shipmentTime = shipmentTime; }

    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }
}
