package com.ruoyi.web.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Example;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.validation.Save;
import com.ruoyi.common.validation.Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Range;

import java.util.Date;

@ApiModel("分包")
public class ErpPackagingList extends BaseEntity {
    @Excel(name = "序号")
    @ApiModelProperty(value = "序号 [GET|list|PUT|DELETE|response]")
    private Integer id;

    @Excel(name = "订单ID")
    @Example("BLK20250528000001")
    @ApiModelProperty(value = "订单编号 [list|POST|PUT|response]")
    private String orderCode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", type = Excel.Type.EXPORT)
    @ApiModelProperty(value = "创建时间 [list|response]")
    private Date creationTime;

    @Excel(name = "操作人", type = Excel.Type.EXPORT)
    @ApiModelProperty(value = "操作人 [list|response]")
    private String operator;

    @Excel(name = "产品编号")
    @Example("1")
    @ApiModelProperty(value = "产品编号 [list|POST|PUT|response]")
    private Long productId;

    @Excel(name = "产品中文名称")
    @Example("厨房八件套")
    @ApiModelProperty(value = "产品中文名称 [list|POST|PUT|response]")
    private String productNameCn;

    @Excel(name = "分包表编号")
    @Example("xxx")
    @ApiModelProperty(value = "分包表编号 [list|POST|PUT|response]")
    private String packagingListNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发布日期")
    @Example("2022-05-28")
    @ApiModelProperty(value = "发布日期 [list|POST|PUT|response]")
    private Date releaseDate;

    @Excel(name = "配件种类")
    @Example("345种")
    @ApiModelProperty(value = "配件种类 [list|POST|PUT|response]")
    private String accessoryType;

    @Excel(name = "配件总数/PCS")
    @Range(min = 0, message = "配件总数不能小于0", groups = {Save.class, Update.class})
    @Example("345")
    @ApiModelProperty(value = "配件总数 [list|POST|PUT|response]")
    private Integer accessoryTotal;

    @Excel(name = "是否是说明书", readConverterExp = "1=是,0=否")
    @Range(min = 0, max = 1, message = "值无效：是否有说明书。有效值为“是”或“否”", groups = {Save.class, Update.class})
    @Example("1")
    @ApiModelProperty(value = "是否是说明书 [list|POST|PUT|response]")
    private Integer isManual;

    @Excel(name = "是否是人仔", readConverterExp = "1=是,0=否")
    @Range(min = 0, max = 1, message = "值无效：是否有说明书。有效值为“是”或“否”", groups = {Save.class, Update.class})
    @Example("1")
    @ApiModelProperty(value = "是否是人仔 [list|POST|PUT|response]")
    private Integer isMinifigure;

    @Excel(name = "是否是起件器", readConverterExp = "1=是,0=否")
    @Range(min = 0, max = 1, message = "值无效：是否有说明书。有效值为“是”或“否”", groups = {Save.class, Update.class})
    @Example("1")
    @ApiModelProperty(value = "是否是起件器 [list|POST|PUT|response]")
    private Integer isTool;

    @Excel(name = "生产线")
    @Example("自动拉")
    @ApiModelProperty(value = "生产线 [list|POST|PUT|response]")
    private String productionLine;

    @Excel(name = "本袋重量（≈/KG）")
    @Range(min = 0, message = "本袋重量不能小于0", groups = {Save.class, Update.class})
    @Example("789")
    @ApiModelProperty(value = "本袋重量 [list|POST|PUT|response]")
    private Double bagWeight;

    @Excel(name = "本袋规格")
    @Example("789")
    @ApiModelProperty(value = "本袋规格 [list|POST|PUT|response]")
    private String bagSpecification;

    @Excel(name = "本包配件/种")
    @Example("789")
    @ApiModelProperty(value = "本包配件/种 [list|POST|PUT|response]")
    private String packageAccessories;

    @Excel(name = "本包数量/PCS")
    @Range(min = 0, message = "本包数量不能小于0", groups = {Save.class, Update.class})
    @Example("789")
    @ApiModelProperty(value = "本包数量 [list|POST|PUT|response]")
    private Integer packageQuantity;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getProductNameCn() {
        return productNameCn;
    }

    public void setProductNameCn(String productNameCn) {
        this.productNameCn = productNameCn;
    }

    public String getPackagingListNumber() {
        return packagingListNumber;
    }

    public void setPackagingListNumber(String packagingListNumber) {
        this.packagingListNumber = packagingListNumber;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getAccessoryType() {
        return accessoryType;
    }

    public void setAccessoryType(String accessoryType) {
        this.accessoryType = accessoryType;
    }

    public Integer getAccessoryTotal() {
        return accessoryTotal;
    }

    public void setAccessoryTotal(Integer accessoryTotal) {
        this.accessoryTotal = accessoryTotal;
    }

    public Integer getIsManual() {
        return isManual;
    }

    public void setIsManual(Integer manual) {
        isManual = manual;
    }

    public Integer getIsMinifigure() {
        return isMinifigure;
    }

    public void setIsMinifigure(Integer minifigure) {
        isMinifigure = minifigure;
    }

    public Integer getIsTool() {
        return isTool;
    }

    public void setIsTool(Integer tool) {
        isTool = tool;
    }

    public String getProductionLine() {
        return productionLine;
    }

    public void setProductionLine(String productionLine) {
        this.productionLine = productionLine;
    }

    public Double getBagWeight() {
        return bagWeight;
    }

    public void setBagWeight(Double bagWeight) {
        this.bagWeight = bagWeight;
    }

    public String getBagSpecification() {
        return bagSpecification;
    }

    public void setBagSpecification(String bagSpecification) {
        this.bagSpecification = bagSpecification;
    }

    public String getPackageAccessories() {
        return packageAccessories;
    }

    public void setPackageAccessories(String packageAccessories) {
        this.packageAccessories = packageAccessories;
    }

    public Integer getPackageQuantity() {
        return packageQuantity;
    }

    public void setPackageQuantity(Integer packageQuantity) {
        this.packageQuantity = packageQuantity;
    }

}
