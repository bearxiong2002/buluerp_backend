package com.ruoyi.web.request.purchase;

import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@ApiModel("新增采购订单请求")
public class AddPurchaseOrderRequest extends BaseEntity {

    @ApiModelProperty(value = "采购计划ID",dataType = "int",required = true)
    private Integer purchaseId;

    @ApiModelProperty(value = "订单金额",dataType = "int",required = true)
    private Double amount;

    @ApiModelProperty(
            value = "发票文件(支持多个)",
            dataType = "__file"
    )
    private MultipartFile[] invoice;

    // Getters and Setters
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

    public MultipartFile[] getInvoice() {
        return invoice;
    }

    public void setInvoice(MultipartFile[] invoice) {
        this.invoice = invoice;
    }
}