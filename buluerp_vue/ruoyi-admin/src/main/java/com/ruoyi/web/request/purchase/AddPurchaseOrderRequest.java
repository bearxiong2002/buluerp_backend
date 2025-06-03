package com.ruoyi.web.request.purchase;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.web.controller.tool.ExcelImageImportUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@ApiModel("新增采购订单请求")
public class AddPurchaseOrderRequest extends BaseEntity {

    @Excel(name = "采购计划ID")
    @NotNull(message = "采购id不能为空")
    @ApiModelProperty(value = "采购计划ID",dataType = "int",required = true)
    private Integer purchaseId;

    @Excel(name = "订单金额")
    @NotNull(message = "金额不能为空")
    @ApiModelProperty(value = "订单金额",dataType = "double",required = true)
    private Double amount;

    @ApiModelProperty(
            value = "发票文件(支持多个)",
            dataType = "__file"
    )
    private MultipartFile[] invoice;

    private List<String> tempInvoiceImages = new ArrayList<>();

    /**
     * 添加发票图片（Base64格式）
     */
    public void addInvoiceImage(String base64Image) {
        if (StringUtils.isNotBlank(base64Image)) {
            this.tempInvoiceImages.add(base64Image);
        }
    }

    /**
     * 将图片转换为MultipartFile数组
     */
    public void convertImagesToMultipartFiles() {
        if (tempInvoiceImages != null && !tempInvoiceImages.isEmpty()) {
            this.invoice = tempInvoiceImages.stream()
                    .map(ExcelImageImportUtil::convertBase64ToMultipartFile)
                    .filter(Objects::nonNull)
                    .toArray(MultipartFile[]::new);
        } else {
            this.invoice = new MultipartFile[0];
        }
    }

    /**
     * 获取临时图片数量（用于错误跟踪）
     */
    public int getInvoiceImageCount() {
        return tempInvoiceImages != null ? tempInvoiceImages.size() : 0;
    }

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

    @Override
    public String toString() {
        return "AddPurchaseOrderRequest{" +
                "purchaseId=" + purchaseId +
                ", amount=" + amount +
                ", invoiceCount=" + (invoice != null ? invoice.length : 0) +
                ", tempImageCount=" + getInvoiceImageCount() +
                '}';
    }
}