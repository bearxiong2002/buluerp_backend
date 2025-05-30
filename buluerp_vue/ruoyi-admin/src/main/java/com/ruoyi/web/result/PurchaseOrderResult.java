package com.ruoyi.web.result;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.web.domain.ErpPurchaseOrderInvoice;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;
import java.util.List;

@ApiModel("采购单返回类")
public class PurchaseOrderResult {

    @ApiModelProperty(value = "采购单ID",required = false)
    private Integer id;
    @ApiModelProperty(value = "采购计划ID",dataType = "int",required = true)
    private Integer purchaseId;
    @ApiModelProperty(value = "订单金额",dataType = "int",required = true)
    private Double amount;
    @ApiModelProperty(dataType = "DateTime",value = "创建时间",required = false)
    private LocalDateTime createTime;
    @ApiModelProperty(dataType = "String",value = "创建用户",required = false)
    private String createUser;

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    @ApiModelProperty(value = "发票url",dataType = "String",required = false)
    private List<ErpPurchaseOrderInvoice> invoice;

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

    public List<ErpPurchaseOrderInvoice> getInvoice() {
        return invoice;
    }

    public void setInvoice(List<ErpPurchaseOrderInvoice> invoice) {
        this.invoice = invoice;
    }
}