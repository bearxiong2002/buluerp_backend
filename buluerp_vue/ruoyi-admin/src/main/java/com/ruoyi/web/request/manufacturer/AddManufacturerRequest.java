package com.ruoyi.web.request.manufacturer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "新增厂家请求类")
public class AddManufacturerRequest {

    @ApiModelProperty(dataType = "String",value = "厂家名称",required = true)
    private String name;

    @ApiModelProperty(dataType = "String",value = "联系方式",required = false)
    private String tel;

    @ApiModelProperty(dataType = "String",value = "邮箱地址",required = false)
    private String email;

    @ApiModelProperty(dataType = "String",value = "客户备注",required = false)
    private String remark;

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

}
