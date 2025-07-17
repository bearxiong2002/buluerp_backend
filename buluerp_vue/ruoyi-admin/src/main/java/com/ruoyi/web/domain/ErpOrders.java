package com.ruoyi.web.domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.common.annotation.Example;
import com.ruoyi.common.validation.NullOrNotBlank;
import com.ruoyi.common.validation.Save;
import com.ruoyi.common.validation.Update;
import com.ruoyi.web.annotation.AutoLogIdentifier;
import com.ruoyi.web.annotation.AutoLogIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 订单对象 erp_orders
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
@ApiModel("订单")
public class ErpOrders extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    @Excel(name = "内部编号")
    @Example("BLK20250528000001")
    @AutoLogIdentifier
    @NotBlank(groups = {Save.class}, message = "内部编号不能为空")
    @NullOrNotBlank(groups = {Update.class}, message = "内部编号不能为空")
    private String innerId;

    @Excel(name = "外部编号")
    @Example("OUT20250528000001")
    @NotBlank(groups = {Save.class}, message = "外部编号不能为空")
    @NullOrNotBlank(groups = {Update.class}, message = "外部编号不能为空")
    private String outerId;

    /** 操作人ID（外键引用用户表） */
    @Excel(name = "操作人姓名", type = Excel.Type.IMPORT)
    private String operator;

    /** 数量 */
    @Excel(name = "数量")
    @NotNull(groups = {Save.class}, message = "数量格式有误")
    @Range(min = 1, message = "数量不能小于1", groups = {Save.class, Update.class})
    @Example("100")
    private Long quantity;

    /** 交货期限 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    // @NotNull(groups = {Save.class}, message = "交货期限格式有误")
    @Excel(name = "交货期限", width = 30, dateFormat = "yyyy-MM-dd")
    @Example("2021-01-01")
    private Date deliveryDeadline;

    /** 交货时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "交货时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Example("2021-01-01")
    private Date deliveryTime;

    /** 状态（0:创建 1:已发货 2:已完成等） */
    @Excel(name = "状态", dictType = "erp_order_status")
    @Example("1")
    // @NotNull(groups = {Save.class}, message = "状态值格式有误")
    @AutoLogIgnore // 订单状态变更单独记录日志
    private Integer status;

    /** 客户ID（外键引用客户表） */
    @Excel(name = "客户ID")
    @Example("1")
    private Long customerId;

    /** 产品ID（外键引用产品表） */
    @Excel(name = "产品ID")
    // @Example("1")
    private String productId;

    /** 布产ID（外键引用布产表） */
    @Excel(name = "布产ID")
    // @Example("1")
    private Long productionId;

    /** 外购ID（外键引用采购表） */
    @Excel(name = "外购ID")
    // @Example("1")
    private Long purchaseId;

    /** 分包ID（外键引用分包商表） */
    @Excel(name = "分包ID")
    // @Example("1")
    private Long subcontractId;

    @Excel(name = "其它基本信息")
    @Example("无")
    private String remark;

    @ApiModelProperty("是否所有采购已完成(只读)")
    private Boolean allPurchased;

    @ApiModelProperty("是否所有布产已完成(只读)")
    private Boolean allScheduled;

    private String customerName;

    @ApiModelProperty(value = "通过设计总表关联的产品，仅用于响应")
    private ErpProducts product;

    @JsonIgnore // 不再使用
    private List<ErpOrdersProduct> products;

    @ApiModelProperty(value = "审核状态 0=待审核，1=审核中，-1=审核被拒绝，2=审核通过")
    private Integer auditStatus;

    public Integer getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setQuantity(Long quantity) 
    {
        this.quantity = quantity;
    }

    public Long getQuantity() 
    {
        return quantity;
    }

    public void setDeliveryDeadline(Date deliveryDeadline) 
    {
        this.deliveryDeadline = deliveryDeadline;
    }

    public Date getDeliveryDeadline() 
    {
        return deliveryDeadline;
    }

    public void setDeliveryTime(Date deliveryTime) 
    {
        this.deliveryTime = deliveryTime;
    }

    public Date getDeliveryTime() 
    {
        return deliveryTime;
    }

    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }

    public void setCustomerId(Long customerId) 
    {
        this.customerId = customerId;
    }

    public Long getCustomerId() 
    {
        return customerId;
    }

    public void setProductId(String productId)
    {
        this.productId = productId;
    }

    public String getProductId()
    {
        return productId;
    }

    public void setProductionId(Long productionId) 
    {
        this.productionId = productionId;
    }

    public Long getProductionId() 
    {
        return productionId;
    }

    public void setPurchaseId(Long purchaseId) 
    {
        this.purchaseId = purchaseId;
    }

    public Long getPurchaseId() 
    {
        return purchaseId;
    }

    public void setSubcontractId(Long subcontractId) 
    {
        this.subcontractId = subcontractId;
    }

    public Long getSubcontractId() 
    {
        return subcontractId;
    }

    public String generateInnerId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(getCreateTime());
        return "BLK"
                + date
                + String.format("%06d", getId() % 1000000);
    }

    public String generateOuterId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(getCreateTime());
        return "OUT"
                + date
                + String.format("%06d", getId() % 1000000);
    }

    public static final String INNER_ID_PLACEHOLDER = "BLK##############";
    public static final String OUTER_ID_PLACEHOLDER = "OUT##############";

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

    @Override
    public String getRemark() {
        return remark;
    }

    @Override
    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    @Deprecated // 使用设计总表进行关联
    public List<ErpOrdersProduct> getProducts() {
        return products;
    }

    @Deprecated // 使用设计总表进行关联
    public void setProducts(List<ErpOrdersProduct> products) {
        this.products = products;
    }

    public ErpProducts getProduct() {
        return product;
    }

    public void setProduct(ErpProducts product) {
        this.product = product;
    }

    public Boolean getAllPurchased() {
        return allPurchased;
    }

    public void setAllPurchased(Boolean allPurchased) {
        this.allPurchased = allPurchased;
    }

    public Boolean getAllScheduled() {
        return allScheduled;
    }

    public void setAllScheduled(Boolean allScheduled) {
        this.allScheduled = allScheduled;
    }
}
