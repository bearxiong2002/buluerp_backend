package com.ruoyi.web.request.audit;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * 审核请求体
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
@ApiModel("审核请求体")
public class AuditRequest {
    
    /** 审核状态：1=通过，-1=拒绝 */
    @NotNull
    @ApiModelProperty(value = "审核状态", notes = "1=通过，-1=拒绝", required = true, example = "1")
    private Integer accept;
    
    /** 审核意见 */
    @ApiModelProperty(value = "审核意见", notes = "审核时的备注说明", example = "审核通过")
    private String auditComment;
    
    public Integer getConfirm() {
        return accept;
    }
    
    public void setConfirm(Integer confirm) {
        this.accept = confirm;
    }
    
    public String getAuditComment() {
        return auditComment;
    }
    
    public void setAuditComment(String auditComment) {
        this.auditComment = auditComment;
    }
    
    @Override
    public String toString() {
        return "AuditRequest{" +
                "confirm=" + accept +
                ", auditComment='" + auditComment + '\'' +
                '}';
    }
} 