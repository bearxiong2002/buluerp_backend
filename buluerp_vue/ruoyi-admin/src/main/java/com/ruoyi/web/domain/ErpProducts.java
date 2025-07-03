package com.ruoyi.web.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import io.swagger.annotations.ApiModel;

import java.time.LocalDateTime;
import java.util.List;

@TableName(value = "erp_products")
@ApiModel("产品")
public class ErpProducts {

    /** $column.columnComment */
    @Excel(name = "产品id")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Excel(name = "订单id")
    private Integer orderId;

    @Excel(name= "内部编码")
    private String innerId;

    @Excel(name="外部编码")
    private String outerId;

    @Excel(name = "产品名")
    private String name;

    /** $column.columnComment */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "创建时间",dateFormat = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /** $column.columnComment */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "更新时间",dateFormat = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Excel(name = "图片", cellType = Excel.ColumnType.IMAGE, height = 80)
    private String pictureUrl;

    @Excel(name = "设计确认状态", readConverterExp = "1=已确认,0=未确认")
    private Integer designStatus;

    @Excel(name = "创建人")
    private String createUsername;

    @TableField(exist = false)
    private List<Integer> materialIds;

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

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getCreateUsername() {
        return createUsername;
    }

    public void setCreateUsername(String createUsername) {
        this.createUsername = createUsername;
    }

    public Integer getDesignStatus() {
        return designStatus;
    }

    public void setDesignStatus(Integer designStatus) {
        this.designStatus = designStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime creatTime) {
        this.createTime = creatTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public List<Integer> getMaterialIds() {
        return materialIds;
    }

    public void setMaterialIds(List<Integer> materialIds) {
        this.materialIds = materialIds;
    }
}
