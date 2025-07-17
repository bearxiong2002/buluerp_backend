package com.ruoyi.web.request.audit;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 批量审核请求体
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
@ApiModel("批量审核请求体")
public class BatchAuditRequest {
    
    /** 审核记录ID列表 */
    @NotEmpty(message = "审核记录ID列表不能为空")
    @ApiModelProperty(value = "审核记录ID列表", required = true, example = "[1, 2, 3]")
    private List<Long> auditRecordIds;
    
    /** 审核状态：1=通过，-1=拒绝 */
    @NotNull(message = "审核结果不能为空")
    @ApiModelProperty(value = "审核结果", notes = "1=通过，-1=拒绝", required = true, example = "1")
    private Integer accept;
    
    /** 审核意见 */
    @ApiModelProperty(value = "审核意见", notes = "审核时的备注说明", example = "批量审核通过")
    private String auditComment;
    
    public List<Long> getAuditRecordIds() {
        return auditRecordIds;
    }
    
    public void setAuditRecordIds(List<Long> auditRecordIds) {
        this.auditRecordIds = auditRecordIds;
    }
    
    public Integer getAccept() {
        return accept;
    }
    
    public void setAccept(Integer accept) {
        this.accept = accept;
    }
    
    public String getAuditComment() {
        return auditComment;
    }
    
    public void setAuditComment(String auditComment) {
        this.auditComment = auditComment;
    }
    
    @Override
    public String toString() {
        return "BatchAuditRequest{" +
                "auditRecordIds=" + auditRecordIds +
                ", accept=" + accept +
                ", auditComment='" + auditComment + '\'' +
                '}';
    }
} 