package com.ruoyi.web.enums;

/**
 * 通知类型枚举
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
public enum NotificationTypeEnum {
    
    /** 订单创建通知 */
    ORDER_CREATED("ORDER_CREATED", "新订单待审核", "order_auditor"),
    
    /** 订单审核通过通知 */
    ORDER_APPROVED("ORDER_APPROVED", "订单审核通过", "design_user"),
    
    /** 订单审核拒绝通知 */
    ORDER_REJECTED("ORDER_REJECTED", "订单审核未通过", "order_user"),
    
    /** 订单状态变更审核通知 */
    ORDER_STATUS_CHANGE("ORDER_STATUS_CHANGE", "订单状态变更待审核", "order_auditor"),
    
    /** 生产排期通知 */
    PRODUCTION_SCHEDULED("PRODUCTION_SCHEDULED", "生产排期通知", "production_manager"),
    
    /** 库存不足通知 */
    INVENTORY_LOW("INVENTORY_LOW", "库存不足预警", "inventory_manager"),
    
    /** 采购审批通知 */
    PURCHASE_APPROVED("PURCHASE_APPROVED", "采购审批通知", "purchase_creator"),
    
    /** 审核待处理通知 */
    AUDIT_PENDING("AUDIT_PENDING", "待审核通知", "auditor"),
    
    /** 审核通过通知 */
    AUDIT_APPROVED("AUDIT_APPROVED", "审核通过通知", "creator"),
    
    /** 审核拒绝通知 */
    AUDIT_REJECTED("AUDIT_REJECTED", "审核拒绝通知", "creator"),
    
    /** 布产审核通过通知 */
    PRODUCTION_AUDIT_APPROVED("PRODUCTION_AUDIT_APPROVED", "布产审核通过", "production_user"),
    
    /** 布产审核拒绝通知 */
    PRODUCTION_AUDIT_REJECTED("PRODUCTION_AUDIT_REJECTED", "布产审核拒绝", "production_user"),
    
    /** 采购审核拒绝通知 */
    PURCHASE_AUDIT_REJECTED("PURCHASE_AUDIT_REJECTED", "采购审核拒绝", "purchase_user"),
    
    /** 布产创建待审核通知 */
    PRODUCTION_AUDIT_PENDING("PRODUCTION_AUDIT_PENDING", "布产计划待审核", "production_auditor"),
    
    /** 采购创建待审核通知 */
    PURCHASE_AUDIT_PENDING("PURCHASE_AUDIT_PENDING", "采购汇总待审核", "purchase_auditor"),
    
    /** 布产审核通过通知注塑部 */
    PRODUCTION_APPROVED_TO_DEPT("PRODUCTION_APPROVED_TO_DEPT", "布产审核通过", "injectionmolding_dept"),
    
    /** 采购审核通过通知采购部 */
    PURCHASE_APPROVED_TO_DEPT("PURCHASE_APPROVED_TO_DEPT", "采购审核通过", "purchase_dept"),
    
    /** 布产审核拒绝通知PMC */
    PRODUCTION_REJECTED_TO_PMC("PRODUCTION_REJECTED_TO_PMC", "布产审核拒绝", "pmc_dept"),
    
    /** 采购审核拒绝通知PMC */
    PURCHASE_REJECTED_TO_PMC("PURCHASE_REJECTED_TO_PMC", "采购审核拒绝", "pmc_dept"),
    
    /** 订单审核通过通知设计部 */
    ORDER_AUDIT_APPROVED("ORDER_AUDIT_APPROVED", "新订单待设计", "design_dept"),
    
    /** 订单审核拒绝通知销售部 */
    ORDER_AUDIT_REJECTED("ORDER_AUDIT_REJECTED", "订单审核未通过", "sell_dept");
    
    private final String code;
    private final String description;
    private final String targetRoleKey; // 目标角色标识
    
    NotificationTypeEnum(String code, String description, String targetRoleKey) {
        this.code = code;
        this.description = description;
        this.targetRoleKey = targetRoleKey;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getTargetRoleKey() {
        return targetRoleKey;
    }
    
    /**
     * 根据code获取枚举
     */
    public static NotificationTypeEnum getByCode(String code) {
        for (NotificationTypeEnum notificationType : values()) {
            if (notificationType.getCode().equals(code)) {
                return notificationType;
            }
        }
        return null;
    }
} 