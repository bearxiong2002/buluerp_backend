package com.ruoyi.web.result;

import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel("采购单返回类")
public class PurchaseOrderResult extends BaseEntity {

    @ApiModelProperty(value = "采购单ID",required = false)
    private Integer id;

    @ApiModelProperty(value = "采购计划ID",dataType = "int",required = true)
    private Integer purchaseId;

    @ApiModelProperty(value = "订单金额",dataType = "int",required = true)
    private Double amount;

    @ApiModelProperty(value = "发票url",dataType = "String",required = false)
    private List<String > invoiceUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(Integer purchaseId) {
        this.purchaseId = purchaseId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public List<String> getInvoiceUrl() {
        return invoiceUrl;
    }

    public void setInvoiceUrl(List<String> invoiceUrl) {
        this.invoiceUrl = invoiceUrl;
    }
}