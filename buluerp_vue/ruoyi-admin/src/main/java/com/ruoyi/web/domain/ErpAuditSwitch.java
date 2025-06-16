package com.ruoyi.web.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * 审核开关对象 erp_audit_switch
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
@ApiModel("审核开关")
@TableName("erp_audit_switch")
public class ErpAuditSwitch 
{

    /** 主键 */
    @ApiModelProperty("主键ID")
    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 审核类型 */
    @ApiModelProperty("审核类型")
    @NotNull(message = "审核类型不能为空")
    private Integer auditType;

    /** 开关状态 0=关闭审核，1=开启审核 */
    @ApiModelProperty("开关状态 0=关闭审核，1=开启审核")
    @NotNull(message = "开关状态不能为空")
    private Integer status;

    public void setId(Integer id) 
    {
        this.id = id;
    }

    public Integer getId() 
    {
        return id;
    }

    public void setAuditType(Integer auditType) 
    {
        this.auditType = auditType;
    }

    public Integer getAuditType() 
    {
        return auditType;
    }

    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return "ErpAuditSwitch{" +
                "id=" + id +
                ", auditType=" + auditType +
                ", status=" + status +
                '}';
    }
} 