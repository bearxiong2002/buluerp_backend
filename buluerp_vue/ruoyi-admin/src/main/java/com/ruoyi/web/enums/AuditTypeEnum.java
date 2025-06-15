package com.ruoyi.web.enums;

/**
 * 审核类型枚举
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
public enum AuditTypeEnum {
    
    /** 订单审核 */
    ORDER_AUDIT(1, "订单审核", "order_auditor"),
    
    /** 委外加工单审核 */
    OUTSOURCING_AUDIT(2, "委外加工单审核", "outsourcing_auditor"),
    
    /** 布产审核 */
    PRODUCTION_AUDIT(3, "布产审核", "production_auditor"),
    
    /** 分包审核 */
    SUBCONTRACT_AUDIT(4, "分包审核", "subcontract_auditor");
    
    private final Integer code;
    private final String description;
    private final String roleKey; // 对应的角色标识
    
    AuditTypeEnum(Integer code, String description, String roleKey) {
        this.code = code;
        this.description = description;
        this.roleKey = roleKey;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getRoleKey() {
        return roleKey;
    }
    
    /**
     * 根据code获取枚举
     */
    public static AuditTypeEnum getByCode(Integer code) {
        for (AuditTypeEnum auditType : values()) {
            if (auditType.getCode().equals(code)) {
                return auditType;
            }
        }
        return null;
    }
    
    /**
     * 根据角色标识获取审核类型
     */
    public static AuditTypeEnum getByRoleKey(String roleKey) {
        for (AuditTypeEnum auditType : values()) {
            if (auditType.getRoleKey().equals(roleKey)) {
                return auditType;
            }
        }
        return null;
    }
} 