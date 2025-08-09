package com.ruoyi.web.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import io.swagger.annotations.ApiModel;

import java.util.Date;

@TableName(value = "erp_manufacturer")
@ApiModel("厂家")
public class ErpManufacturer {

    @TableId(type = IdType.AUTO)
    @Excel(name = "厂家id")
    private Long id;
    @Excel(name = "创建用户id")
    private Long createUserId;
    @Excel(name = "厂家名称")
    private String name;
    @Excel(name = "联系方式")
    private String tel;
    @Excel(name = "邮箱地址")
    private String email;
    @Excel(name = "客户备注")
    private String remark;
    @Excel(name = "创建时间",dateFormat = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createTime;

    public ErpManufacturer(Long id, Long createUserId, String name, String tel, String email, String remark, Date createTime) {
        this.id = id;
        this.createUserId = createUserId;
        this.name = name;
        this.tel = tel;
        this.email = email;
        this.remark = remark;
        this.createTime = createTime;
    }
    public ErpManufacturer() {}

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
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

    // 私有构造方法，通过 Builder 创建对象
    private ErpManufacturer(Builder builder) {
        this.id = builder.id;
        this.createUserId = builder.createUserId;
        this.name = builder.name;
        this.tel = builder.tel;
        this.email = builder.email;
        this.remark = builder.remark;
    }

    public static class Builder {
        private Long id;
        private Long createUserId;
        private String name;
        private String tel;
        private String email;
        private String remark;

        // 设置 createUserId 的方法
        public Builder withCreateUserId(Long createUserId) {
            this.createUserId = createUserId;
            return this;
        }

        // 设置 id 的方法
        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withTel(String tel) {
            this.tel = tel;
            return this;
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withRemark(String remark) {
            this.remark = remark;
            return this;
        }

        public ErpManufacturer build() {
            return new ErpManufacturer(this);
        }
    }
}
