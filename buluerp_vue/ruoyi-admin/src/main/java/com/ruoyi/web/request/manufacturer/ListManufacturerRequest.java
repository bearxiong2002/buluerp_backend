package com.ruoyi.web.request.manufacturer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;
import java.util.Date;

@ApiModel(value = "修改厂家请求类")
public class ListManufacturerRequest {

    @ApiModelProperty(dataType = "Long",value = "厂家id(可选)",required = false)
    private Long id;

    @ApiModelProperty(dataType = "String",value = "厂家名称",required = false)
    private String name;

    @ApiModelProperty(dataType = "String",value = "联系方式",required = false)
    private String tel;

    @ApiModelProperty(dataType = "String",value = "邮箱地址",required = false)
    private String email;

    @ApiModelProperty(dataType = "Date",value = "创建时间起始",required = false)
    private Date createTimeFrom;
    @ApiModelProperty(dataType = "Date",value = "创建时间终止",required = false)
    private Date createTimeTo;

    @ApiModelProperty(dataType = "String",value = "客户备注",required = false)
    private String remark;

    public ListManufacturerRequest() {
    }

    public Date getCreateTimeFrom() {
        return createTimeFrom;
    }

    public void setCreateTimeFrom(Date createTimeFrom) {
        this.createTimeFrom = createTimeFrom;
    }

    public Date getCreateTimeTo() {
        return createTimeTo;
    }

    public void setCreateTimeTo(Date createTimeTo) {
        this.createTimeTo = createTimeTo;
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

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public ListManufacturerRequest(Long id, String name, String tel, String email, String remark) {
        this.id = id;
        this.name = name;
        this.tel = tel;
        this.email = email;
        this.remark = remark;
    }
}
