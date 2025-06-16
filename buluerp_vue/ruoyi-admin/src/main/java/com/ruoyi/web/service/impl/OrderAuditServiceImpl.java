package com.ruoyi.web.service.impl;

import com.ruoyi.web.domain.ErpAuditRecord;
import com.ruoyi.web.domain.ErpOrders;
import com.ruoyi.web.enums.AuditTypeEnum;
import com.ruoyi.web.enums.NotificationTypeEnum;
import com.ruoyi.web.service.IErpAuditRecordService;
import com.ruoyi.web.service.IErpOrdersService;
import com.ruoyi.web.service.INotificationService;
import com.ruoyi.web.service.IOrderAuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 订单审核Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
@Service
public class OrderAuditServiceImpl implements IOrderAuditService 
{
    private static final Logger log = LoggerFactory.getLogger(OrderAuditServiceImpl.class);

    @Autowired
    private IErpAuditRecordService auditRecordService;

    @Autowired
    private INotificationService notificationService;

    @Autowired
    private IErpOrdersService ordersService;

    /** 订单状态常量 */
    private static final Integer ORDER_STATUS_PENDING = 0;  // 待审核
    private static final Integer ORDER_STATUS_APPROVED = 1; // 已审核 待设计
    private static final Integer ORDER_STATUS_REJECTED = -1; // 审核拒绝
    private static final Integer ORDER_STATUS_DESIGNING = 2; // 设计中

    /**
     * 订单创建后处理（创建审核记录并发送通知）
     * 
     * @param order 订单信息
     */
    @Override
    @Transactional
    public void handleOrderCreated(ErpOrders order)
    {
        try {
            log.info("开始处理订单创建后流程，订单ID：{}，订单编号：{}", order.getId(), order.getInnerId());
            
            // 1. 创建审核记录
            ErpAuditRecord auditRecord = auditRecordService.createAuditRecord(
                AuditTypeEnum.ORDER_AUDIT.getCode(),
                order.getId(),
                null, // 新创建的订单没有前状态
                ORDER_STATUS_APPROVED // 目标状态：审核通过
            );
            
            // 2. 构建通知模板数据 最后被封装进审核记录的信息
            Map<String, Object> templateData = buildOrderNotificationData(order);
            
            // 3. 发送通知给订单审核人
            notificationService.sendNotificationToRole(
                NotificationTypeEnum.ORDER_CREATED,
                "order_auditor", // 订单审核人角色标识
                order.getId(),
                "ORDER",
                templateData
            );
            
            log.info("订单创建后流程处理完成，订单ID：{}，审核记录ID：{}", order.getId(), auditRecord.getId());
            
        } catch (Exception e) {
            log.error("处理订单创建后流程失败，订单ID：{}", order.getId(), e);
            throw new RuntimeException("处理订单创建后流程失败", e);
        }
    }

    /**
     * 订单审核通过处理（更新订单状态并发送通知）
     * 
     * @param auditRecordId 审核记录ID
     * @param auditor 审核人
     * @param auditComment 审核意见
     */
    @Override
    @Transactional
    public void handleOrderApproved(Long auditRecordId, String auditor, String auditComment)
    {
        try {
            log.info("开始处理订单审核通过流程，审核记录ID：{}", auditRecordId);
            
            // 1. 更新审核记录状态
            auditRecordService.approveAudit(auditRecordId, auditor, auditComment);
            
            // 2. 获取审核记录信息
            ErpAuditRecord auditRecord = auditRecordService.selectErpAuditRecordById(auditRecordId);
            if (auditRecord == null) {
                throw new RuntimeException("审核记录不存在");
            }
            
            // 3. 更新订单状态为设计中
            ErpOrders order = ordersService.selectErpOrdersById(auditRecord.getAuditId());
            if (order == null) {
                throw new RuntimeException("订单不存在");
            }
            
            order.setStatus(ORDER_STATUS_DESIGNING); // 设置为设计中状态
            ordersService.updateErpOrders(order);
            
            // 4. 构建通知模板数据
            Map<String, Object> templateData = buildOrderNotificationData(order);
            
            // 5. 发送通知给设计部用户
            notificationService.sendNotificationToRole(
                NotificationTypeEnum.ORDER_APPROVED,
                "design-dept", // 设计部用户角色标识
                order.getId(),
                "ORDER",
                templateData
            );
            
            log.info("订单审核通过流程处理完成，订单ID：{}，审核记录ID：{}", 
                    order.getId(), auditRecordId);
            
        } catch (Exception e) {
            log.error("处理订单审核通过流程失败，审核记录ID：{}", auditRecordId, e);
            throw new RuntimeException("处理订单审核通过流程失败", e);
        }
    }

    /**
     * 订单审核拒绝处理（更新订单状态并发送通知）
     * 
     * @param auditRecordId 审核记录ID
     * @param auditor 审核人
     * @param auditComment 审核意见
     */
    @Override
    @Transactional
    public void handleOrderRejected(Long auditRecordId, String auditor, String auditComment)
    {
        try {
            log.info("开始处理订单审核拒绝流程，审核记录ID：{}", auditRecordId);
            
            // 1. 更新审核记录状态
            auditRecordService.rejectAudit(auditRecordId, auditor, auditComment);
            
            // 2. 获取审核记录信息
            ErpAuditRecord auditRecord = auditRecordService.selectErpAuditRecordById(auditRecordId);
            if (auditRecord == null) {
                throw new RuntimeException("审核记录不存在");
            }
            
            // 3. 更新订单状态为审核拒绝
            ErpOrders order = ordersService.selectErpOrdersById(auditRecord.getAuditId());
            if (order == null) {
                throw new RuntimeException("订单不存在");
            }
            
            order.setStatus(ORDER_STATUS_REJECTED); // 设置为审核拒绝状态
            ordersService.updateErpOrders(order);
            
            // 4. 构建通知模板数据
            Map<String, Object> templateData = buildOrderNotificationData(order);
            templateData.put("rejectReason", auditComment != null ? auditComment : "审核未通过");
            
            // 5. 发送通知给订单部用户
            notificationService.sendNotificationToRole(
                NotificationTypeEnum.ORDER_REJECTED,
                "order_user", // 订单部用户角色标识
                order.getId(),
                "ORDER",
                templateData
            );
            
            log.info("订单审核拒绝流程处理完成，订单ID：{}，审核记录ID：{}", 
                    order.getId(), auditRecordId);
            
        } catch (Exception e) {
            log.error("处理订单审核拒绝流程失败，审核记录ID：{}", auditRecordId, e);
            throw new RuntimeException("处理订单审核拒绝流程失败", e);
        }
    }

    /**
     * 构建订单通知模板数据
     * 
     * @param order 订单信息
     * @return 模板数据
     */
    private Map<String, Object> buildOrderNotificationData(ErpOrders order) {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("orderCode", order.getInnerId());
        templateData.put("customerName", order.getCustomerName());
        templateData.put("quantity", order.getQuantity());
        
        // 格式化交货期限
        if (order.getDeliveryDeadline() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            templateData.put("deliveryDeadline", sdf.format(order.getDeliveryDeadline()));
        } else {
            templateData.put("deliveryDeadline", "未设置");
        }
        
        return templateData;
    }

    /**
     * 获取订单详情
     * 
     * @param orderId 订单ID
     * @return 订单详情
     */
    @Override
    public ErpOrders getOrderDetail(Long orderId) {
        try {
            log.info("获取订单详情，订单ID：{}", orderId);
            ErpOrders order = ordersService.selectErpOrdersById(orderId);
            if (order == null) {
                log.warn("订单不存在，订单ID：{}", orderId);
            }
            return order;
        } catch (Exception e) {
            log.error("获取订单详情失败，订单ID：{}", orderId, e);
            throw new RuntimeException("获取订单详情失败", e);
        }
    }
} 