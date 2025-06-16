package com.ruoyi.web.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统通知对象 erp_notification
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
@TableName("erp_notification")
public class ErpNotification implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 通知ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 通知类型 */
    @Excel(name = "通知类型")
    @TableField("notification_type")
    private String notificationType;

    /** 通知标题 */
    @Excel(name = "通知标题")
    @TableField("title")
    private String title;

    /** 通知内容 */
    @Excel(name = "通知内容")
    @TableField("content")
    private String content;

    /** 接收用户ID */
    @Excel(name = "接收用户ID")
    @TableField("user_id")
    private Long userId;

    /** 接收用户名 */
    @Excel(name = "接收用户名")
    @TableField("user_name")
    private String userName;

    /** 发送用户ID */
    @Excel(name = "发送用户ID")
    @TableField("sender_id")
    private Long senderId;

    /** 发送用户名 */
    @Excel(name = "发送用户名")
    @TableField("sender_name")
    private String senderName;

    /** 关联业务ID */
    @Excel(name = "关联业务ID")
    @TableField("business_id")
    private Long businessId;

    /** 关联业务类型 */
    @Excel(name = "关联业务类型")
    @TableField("business_type")
    private String businessType;

    /** 通知状态（0:未读 1:已读 2:已删除） */
    @Excel(name = "通知状态", readConverterExp = "0=未读,1=已读,2=已删除")
    @TableField("status")
    private Integer status;

    /** 推送状态（0:未推送 1:已推送 2:推送失败） */
    @Excel(name = "推送状态", readConverterExp = "0=未推送,1=已推送,2=推送失败")
    @TableField("push_status")
    private Integer pushStatus;

    /** 推送时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "推送时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @TableField("push_time")
    private Date pushTime;

    /** 阅读时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "阅读时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @TableField("read_time")
    private Date readTime;

    /** 创建者 */
    @Excel(name = "创建者")
    @TableField("create_by")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    private Date createTime;

    /** 备注 */
    @Excel(name = "备注")
    @TableField("remark")
    private String remark;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setNotificationType(String notificationType) 
    {
        this.notificationType = notificationType;
    }

    public String getNotificationType() 
    {
        return notificationType;
    }

    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setUserName(String userName) 
    {
        this.userName = userName;
    }

    public String getUserName() 
    {
        return userName;
    }

    public void setSenderId(Long senderId) 
    {
        this.senderId = senderId;
    }

    public Long getSenderId() 
    {
        return senderId;
    }

    public void setSenderName(String senderName) 
    {
        this.senderName = senderName;
    }

    public String getSenderName() 
    {
        return senderName;
    }

    public void setBusinessId(Long businessId) 
    {
        this.businessId = businessId;
    }

    public Long getBusinessId() 
    {
        return businessId;
    }

    public void setBusinessType(String businessType) 
    {
        this.businessType = businessType;
    }

    public String getBusinessType() 
    {
        return businessType;
    }

    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }

    public void setPushStatus(Integer pushStatus) 
    {
        this.pushStatus = pushStatus;
    }

    public Integer getPushStatus() 
    {
        return pushStatus;
    }

    public void setPushTime(Date pushTime) 
    {
        this.pushTime = pushTime;
    }

    public Date getPushTime() 
    {
        return pushTime;
    }

    public void setReadTime(Date readTime) 
    {
        this.readTime = readTime;
    }

    public Date getReadTime() 
    {
        return readTime;
    }

    public void setCreateBy(String createBy) 
    {
        this.createBy = createBy;
    }

    public String getCreateBy() 
    {
        return createBy;
    }

    public void setCreateTime(Date createTime) 
    {
        this.createTime = createTime;
    }

    public Date getCreateTime() 
    {
        return createTime;
    }

    public void setRemark(String remark) 
    {
        this.remark = remark;
    }

    public String getRemark() 
    {
        return remark;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("notificationType", getNotificationType())
            .append("title", getTitle())
            .append("content", getContent())
            .append("userId", getUserId())
            .append("userName", getUserName())
            .append("senderId", getSenderId())
            .append("senderName", getSenderName())
            .append("businessId", getBusinessId())
            .append("businessType", getBusinessType())
            .append("status", getStatus())
            .append("pushStatus", getPushStatus())
            .append("pushTime", getPushTime())
            .append("readTime", getReadTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("remark", getRemark())
            .toString();
    }
} 