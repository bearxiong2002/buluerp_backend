package com.ruoyi.web.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Example;
import com.ruoyi.common.validation.NullOrNotBlank;
import com.ruoyi.common.validation.Save;
import com.ruoyi.common.validation.Update;
import io.swagger.annotations.ApiModel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 【请填写功能名称】对象 erp_customers
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
@ApiModel("客户")
public class ErpCustomers extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd", type = Excel.Type.EXPORT)
    private Date creatTime;

    /** $column.columnComment */
    @Excel(name = "姓名")
    @NotNull(groups = {Save.class}, message = "姓名不能为空")
    @NullOrNotBlank(groups = {Save.class, Update.class}, message = "姓名不能为空")
    @Example("张三")
    private String name;

    @Excel(name = "联系方式")
    @Pattern(regexp = "^((\\+86)|(86))?1[3-9]\\d{9}$|^(0[1-9]\\d{1,2}-?)?\\d{7,8}(-\\d{3,5})?$", groups = {Save.class, Update.class}, message = "手机号码格式有误")
    @Example("13888888888")
    private String contact;

    @Excel(name = "邮箱")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$", groups = {Save.class, Update.class}, message = "邮箱格式有误")
    @Example("123456@qq.com")
    private String email;

    @Excel(name = "备注")
    @Example("无")
    private String remarks;

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setCreatTime(Date creatTime) 
    {
        this.creatTime = creatTime;
    }

    public Date getCreatTime() 
    {
        return creatTime;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("creatTime", getCreatTime())
            .append("updateTime", getUpdateTime())
            .append("name", getName())
            .toString();
    }
}
