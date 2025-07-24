package com.ruoyi.web.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Example;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.common.validation.Save;
import com.ruoyi.common.validation.Update;
import com.ruoyi.web.service.IListValidationService;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

@ApiModel("分包表")
public class ErpPackagingList {
    @Excel(name = "分包序号")
    @ApiModelProperty(value = "序号 [GET|list|PUT|DELETE|response]")
    private Long id;

    @Excel(name = "订单编号")
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

    @Excel(name = "产品货号")
    @Example("1")
    @ApiModelProperty(value = "产品编号 [list|response]")
    // @NotNull(message = "产品编号不能为空", groups = {Save.class})
    private String productId;

    @Excel(name = "中文名称")
    @Example("厨房八件套")
    @ApiModelProperty(value = "产品中文名称 [list|POST|PUT|response]")
    private String productNameCn;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发布日期", dateFormat = "yyyy-MM-dd")
    @DateTimeFormat("yyyy-MM-dd")
    @Example("2022-05-28")
    @ApiModelProperty(value = "发布日期 [list|POST|PUT|response]")
    private Date releaseDate;

    @Excel(name = "配件种类")
    @Example("AA")
    @ApiModelProperty(value = "配件种类 [list|POST|PUT|response]")
    private String accessoryType;

    @Excel(name = "配件总数/PCS")
    @Range(min = 0, message = "配件总数不能小于0", groups = {Save.class, Update.class})
    @Example("345")
    @ApiModelProperty(value = "配件总数 [list|POST|PUT|response]")
    private Integer accessoryTotal;

    @Excel(name = "说明书", readConverterExp = "true="  + ExcelData.CHECKED_CHAR + ",false=" + ExcelData.UNCHECKED_CHAR)
    @Example("true")
    @ApiModelProperty(value = "是否是说明书 [list|POST|PUT|response]")
    private Boolean isManual;

    @Excel(name = "人仔", readConverterExp = "true="  + ExcelData.CHECKED_CHAR + ",false=" + ExcelData.UNCHECKED_CHAR)
    @Example("false")
    @ApiModelProperty(value = "是否是人仔 [list|POST|PUT|response]")
    private Boolean isMinifigure;

    @Excel(name = "起件器", readConverterExp = "true="  + ExcelData.CHECKED_CHAR + ",false=" + ExcelData.UNCHECKED_CHAR)
    @Example("false")
    @ApiModelProperty(value = "是否是起件器 [list|POST|PUT|response]")
    private Boolean isTool;

    @Excel(name = "生产线")
    @Example("自动拉")
    @ApiModelProperty(value = "生产线 [list|POST|PUT|response]")
    private String productionLine;

    @TableField(exist = false)
    @ApiModelProperty(value = "分包袋列表 [response]")
    private List<ErpPackagingBag> bagList;

    @ApiModelProperty(value = "是否已完成 [list|POST|PUT|response]")
    private Boolean isDone;

    @ApiModelProperty(value = "状态 0=待审核，1=审核通过")
    private Integer status;

    @ApiModelProperty(value = "审核状态 0=待审核，1=审核中，-1=审核被拒绝，2=审核通过")
    private Integer auditStatus;

    public Integer getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
    }

    public static ErpPackagingList createExample() {
        try {
            ErpPackagingList e = IListValidationService.createExample(ErpPackagingList.class);
            List<ErpPackagingBag> bagList = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                bagList.add(ErpPackagingBag.createExample());
            }
            e.setBagList(bagList);
            return e;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

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

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
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

    public  Boolean getManual() {
        return isManual;
    }

    public  Boolean getIsManual() {
        return isManual;
    }

    public void setManual( Boolean manual) {
        isManual = manual;
    }

    public void setIsManual( Boolean manual) {
        isManual = manual;
    }

    public  Boolean getMinifigure() {
        return isMinifigure;
    }

    public  Boolean getIsMinifigure() {
        return isMinifigure;
    }

    public void setMinifigure( Boolean minifigure) {
        isMinifigure = minifigure;
    }

    public void setIsMinifigure( Boolean isMinifigure) {
        this.isMinifigure = isMinifigure;
    }

    public  Boolean getTool() {
        return isTool;
    }

    public  Boolean getIsTool() {
        return isTool;
    }

    public void setTool( Boolean tool) {
        isTool = tool;
    }

    public void setIsTool( Boolean isTool) {
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

    public Boolean getDone() {
        return isDone;
    }

    public void setDone(Boolean done) {
        isDone = done;
    }

    public static class ExcelData extends ErpPackagingList {

        public static final String CHECKED_CHAR = "☑";
        public static final String UNCHECKED_CHAR = "☐";

        private String manualChar;
        private String minifigureChar;
        private String toolChar;

        private String releaseDateStr;

        public ErpPackagingList toEntity() {
            ErpPackagingList entity = new ErpPackagingList();
            BeanUtils.copyBeanProp(entity, this);
            entity.setManual(Objects.equals(manualChar, CHECKED_CHAR));
            entity.setMinifigure(Objects.equals(minifigureChar, CHECKED_CHAR));
            entity.setTool(Objects.equals(toolChar, CHECKED_CHAR));
            try {
                entity.setReleaseDate(DateUtils.parseDate(releaseDateStr, "yyyy-MM-dd"));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            return entity;
        }

        public static ExcelData fromEntity(ErpPackagingList entity) {
            ExcelData data = new ExcelData();
            BeanUtils.copyBeanProp(data, entity);
            data.setManualChar(entity.getManual() ? CHECKED_CHAR : UNCHECKED_CHAR);
            data.setMinifigureChar(entity.getMinifigure() ? CHECKED_CHAR : UNCHECKED_CHAR);
            data.setToolChar(entity.getTool() ? CHECKED_CHAR : UNCHECKED_CHAR);
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
            data.setReleaseDateStr(f.format(entity.getReleaseDate()));
            return data;
        }

        public String getManualChar() {
            return manualChar;
        }

        public void setManualChar(String manualChar) {
            this.manualChar = manualChar;
        }

        public String getMinifigureChar() {
            return minifigureChar;
        }

        public void setMinifigureChar(String minifigureChar) {
            this.minifigureChar = minifigureChar;
        }

        public String getToolChar() {
            return toolChar;
        }

        public void setToolChar(String toolChar) {
            this.toolChar = toolChar;
        }

        public String getReleaseDateStr() {
            return releaseDateStr;
        }

        public void setReleaseDateStr(String releaseDateStr) {
            this.releaseDateStr = releaseDateStr;
        }
    }
}
