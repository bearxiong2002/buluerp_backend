package com.ruoyi.web.request.purchase;

import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@ApiModel("更新采购订单请求")
public class UpdatePurchaseOrderRequest extends BaseEntity {

    @ApiModelProperty(value = "订单ID",required = true)
    private Integer id;

    @ApiModelProperty(value = "采购单ID",required = false)
    private Integer purchaseId;

    @ApiModelProperty(value = "订单金额",required = false)
    private Double amount;

    @ApiModelProperty(value = "发票",dataType = "File",required = false)
    private List<MultipartFile> invoice;

    public List<MultipartFile> getInvoice() {
        return invoice;
    }

    public void setInvoice(List<MultipartFile> invoice) {
        this.invoice = invoice;
    }

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
}