package com.ruoyi.web.request.manufacturer;

import com.ruoyi.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@ApiModel(value = "新增厂家请求类")
public class AddManufacturerRequest {

    @Excel(name = "厂家名称")
    @NotBlank(message = "厂家名称不能为空")
    @ApiModelProperty(dataType = "String",value = "厂家名称",required = true)
    private String name;
    @Excel(name = "联系方式")
    @ApiModelProperty(dataType = "String",value = "联系方式",required = false)
    private String tel;
    @Excel(name = "邮箱地址")
    @Email(message = "邮箱格式错误")
    @ApiModelProperty(dataType = "String",value = "邮箱地址",required = false)
    private String email;
    @Excel(name = "客户备注")
    @Size(max = 200, message = "备注长度不能超过200字符")
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
