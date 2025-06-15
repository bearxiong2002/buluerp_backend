package com.ruoyi.web.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 审核记录对象 erp_audit_record
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
@TableName("erp_audit_record")
public class ErpAuditRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 审核类型,用于区别不同模块的审核 */
    @Excel(name = "审核类型")
    @TableField("audit_type")
    private Integer auditType;

    /** 审核对象id */
    @Excel(name = "审核对象ID")
    @TableField("audit_id")
    private Long auditId;

    /** 审核前状态 */
    @Excel(name = "审核前状态")
    @TableField("pre_status")
    private Integer preStatus;

    /** 请求更改为的状态 */
    @Excel(name = "目标状态")
    @TableField("to_status")
    private Integer toStatus;

    /** 审核状态 0=未审核，1=已通过，-1=未通过 */
    @Excel(name = "审核状态", readConverterExp = "0=未审核,1=已通过,-1=未通过")
    @TableField("confirm")
    private Integer confirm;

    /** 审核人 */
    @Excel(name = "审核人")
    @TableField("auditor")
    private String auditor;

    /** 审核完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "审核完成时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @TableField("check_time")
    private Date checkTime;

    /** 审核意见 */
    @TableField(exist = false)
    private String auditComment;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
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

    public void setAuditId(Long auditId) 
    {
        this.auditId = auditId;
    }

    public Long getAuditId() 
    {
        return auditId;
    }

    public void setPreStatus(Integer preStatus) 
    {
        this.preStatus = preStatus;
    }

    public Integer getPreStatus() 
    {
        return preStatus;
    }

    public void setToStatus(Integer toStatus) 
    {
        this.toStatus = toStatus;
    }

    public Integer getToStatus() 
    {
        return toStatus;
    }

    public void setConfirm(Integer confirm) 
    {
        this.confirm = confirm;
    }

    public Integer getConfirm() 
    {
        return confirm;
    }

    public void setAuditor(String auditor) 
    {
        this.auditor = auditor;
    }

    public String getAuditor() 
    {
        return auditor;
    }

    public void setCheckTime(Date checkTime) 
    {
        this.checkTime = checkTime;
    }

    public Date getCheckTime() 
    {
        return checkTime;
    }

    public void setAuditComment(String auditComment) 
    {
        this.auditComment = auditComment;
    }

    public String getAuditComment() 
    {
        return auditComment;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("auditType", getAuditType())
            .append("auditId", getAuditId())
            .append("preStatus", getPreStatus())
            .append("toStatus", getToStatus())
            .append("confirm", getConfirm())
            .append("auditor", getAuditor())
            .append("createTime", getCreateTime())
            .append("checkTime", getCheckTime())
            .append("auditComment", getAuditComment())
            .toString();
    }
} 