package com.ruoyi.web.service;

import com.ruoyi.web.domain.ErpOrders;

/**
 * 订单审核Service接口
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
public interface IOrderAuditService 
{
    /**
     * 订单创建后处理（创建审核记录并发送通知）
     * 
     * @param order 订单信息
     */
    void handleOrderCreated(ErpOrders order);

    /**
     * 订单审核通过处理（更新订单状态并发送通知）
     * 
     * @param auditRecordId 审核记录ID
     * @param auditor 审核人
     * @param auditComment 审核意见
     */
    void handleOrderApproved(Long auditRecordId, String auditor, String auditComment);

    /**
     * 订单审核拒绝处理（更新订单状态并发送通知）
     * 
     * @param auditRecordId 审核记录ID
     * @param auditor 审核人
     * @param auditComment 审核意见
     */
    void handleOrderRejected(Long auditRecordId, String auditor, String auditComment);

    /**
     * 获取订单详情
     * 
     * @param orderId 订单ID
     * @return 订单详情
     */
    ErpOrders getOrderDetail(Long orderId);
} 