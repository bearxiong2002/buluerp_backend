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
    PURCHASE_APPROVED("PURCHASE_APPROVED", "采购审批通知", "purchase_creator");
    
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