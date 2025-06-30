package com.ruoyi.web.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Example;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.validation.Save;
import com.ruoyi.common.validation.Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@ApiModel("分包表")
public class ErpPackagingList extends BaseEntity {
    @Excel(name = "序号")
    @ApiModelProperty(value = "序号 [GET|list|PUT|DELETE|response]")
    private Long id;

    @Excel(name = "订单ID")
    @Example("BLK20250528000001")
    @ApiModelProperty(value = "订单编号 [list|POST|PUT|response]")
    @NotNull(message = "订单编号不能为空", groups = {Save.class})
    private String orderCode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", type = Excel.Type.EXPORT, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间 [list|response]")
    private Date creationTime;

    @Excel(name = "操作人", type = Excel.Type.EXPORT)
    @ApiModelProperty(value = "操作人 [list|response]")
    private String operator;

    @Excel(name = "产品编号")
    @Example("1")
    @ApiModelProperty(value = "产品编号 [list|POST|PUT|response]")
    @NotNull(message = "产品编号不能为空", groups = {Save.class})
    private Long productId;

    @Excel(name = "产品中文名称")
    @Example("厨房八件套")
    @ApiModelProperty(value = "产品中文名称 [list|POST|PUT|response]")
    private String productNameCn;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发布日期", dateFormat = "yyyy-MM-dd")
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

    @TableField(exist = false)
    @ApiModelProperty(value = "分包袋列表 [response]")
    private List<ErpPackagingBag> bagList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductNameCn() {
        return productNameCn;
    }

    public void setProductNameCn(String productNameCn) {
        this.productNameCn = productNameCn;
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

    public @Range(min = 0, message = "配件总数不能小于0", groups = {Save.class, Update.class}) Integer getAccessoryTotal() {
        return accessoryTotal;
    }

    public void setAccessoryTotal(@Range(min = 0, message = "配件总数不能小于0", groups = {Save.class, Update.class}) Integer accessoryTotal) {
        this.accessoryTotal = accessoryTotal;
    }

    public @Range(min = 0, max = 1, message = "值无效：是否有说明书。有效值为“是”或“否”", groups = {Save.class, Update.class}) Integer getIsManual() {
        return isManual;
    }

    public void setIsManual(@Range(min = 0, max = 1, message = "值无效：是否有说明书。有效值为“是”或“否”", groups = {Save.class, Update.class}) Integer isManual) {
        this.isManual = isManual;
    }

    public @Range(min = 0, max = 1, message = "值无效：是否有说明书。有效值为“是”或“否”", groups = {Save.class, Update.class}) Integer getIsMinifigure() {
        return isMinifigure;
    }

    public void setIsMinifigure(@Range(min = 0, max = 1, message = "值无效：是否有说明书。有效值为“是”或“否”", groups = {Save.class, Update.class}) Integer isMinifigure) {
        this.isMinifigure = isMinifigure;
    }

    public @Range(min = 0, max = 1, message = "值无效：是否有说明书。有效值为“是”或“否”", groups = {Save.class, Update.class}) Integer getIsTool() {
        return isTool;
    }

    public void setIsTool(@Range(min = 0, max = 1, message = "值无效：是否有说明书。有效值为“是”或“否”", groups = {Save.class, Update.class}) Integer isTool) {
        this.isTool = isTool;
    }

    public String getProductionLine() {
        return productionLine;
    }

    public void setProductionLine(String productionLine) {
        this.productionLine = productionLine;
    }

    public List<ErpPackagingBag> getBagList() {
        return bagList;
    }

    public void setBagList(List<ErpPackagingBag> bagList) {
        this.bagList = bagList;
    }
}
