package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.web.domain.ErpAuditRecord;
import com.ruoyi.web.domain.ErpOrders;
import com.ruoyi.web.enums.AuditTypeEnum;
import com.ruoyi.web.enums.NotificationTypeEnum;
import com.ruoyi.web.mapper.ErpAuditRecordMapper;
import com.ruoyi.web.service.IErpAuditRecordService;
import com.ruoyi.web.service.IErpOrdersService;
import com.ruoyi.web.service.INotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 审核记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
@Service
public class ErpAuditRecordServiceImpl implements IErpAuditRecordService 
{
    private static final Logger log = LoggerFactory.getLogger(ErpAuditRecordServiceImpl.class);

    @Autowired
    private ErpAuditRecordMapper erpAuditRecordMapper;

    @Autowired
    private INotificationService notificationService;

    @Autowired
    private IErpOrdersService ordersService;

    /** 订单状态常量 */
    private static final Integer ORDER_STATUS_PENDING = 0;  // 待审核
    private static final Integer ORDER_STATUS_APPROVED = 1; // 已审核 待设计
    private static final Integer ORDER_STATUS_DESIGNING = 2; // 设计中

    /**
     * 查询审核记录
     * 支持多种查询方式：
     * 1. 根据ID查询单条记录
     * 2. 根据条件查询列表
     * 3. 根据审核类型和对象ID精确查询
     * 4. 查询待审核记录
     * 
     * @param erpAuditRecord 审核记录查询条件
     * @param auditType 审核类型（可选）
     * @param auditId 审核对象ID（可选）
     * @param pendingOnly 是否只查询待审核记录（可选）
     * @param auditor 审核人（可选，用于查询特定审核人的待审核记录）
     * @return 审核记录列表
     */
    @Override
    public List<ErpAuditRecord> selectAuditRecords(ErpAuditRecord erpAuditRecord, 
                                                  Integer auditType, 
                                                  Long auditId, 
                                                  Boolean pendingOnly, 
                                                  String auditor)
    {
        try {
            // 如果同时指定了审核类型和审核对象ID，使用精确查询
            if (auditType != null && auditId != null) {
                return erpAuditRecordMapper.selectByAuditTypeAndId(auditType, auditId);
            }
            
            // 如果只查询待审核记录
            if (Boolean.TRUE.equals(pendingOnly)) {
                if (auditType != null) {
                    return erpAuditRecordMapper.selectPendingAuditRecords(auditType);
                } else {
                    // 查询所有待审核记录
                    LambdaQueryWrapper<ErpAuditRecord> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(ErpAuditRecord::getConfirm, 0); // 未审核状态
                    if (StringUtils.hasText(auditor)) {
                        wrapper.eq(ErpAuditRecord::getAuditor, auditor);
                    }
                    return erpAuditRecordMapper.selectList(wrapper);
                }
            }
            
            // 设置可选查询条件
            if (erpAuditRecord == null) {
                erpAuditRecord = new ErpAuditRecord();
            }
            
            if (auditType != null) {
                erpAuditRecord.setAuditType(auditType);
            }
            
            if (auditId != null) {
                erpAuditRecord.setAuditId(auditId);
            }
            
            if (StringUtils.hasText(auditor)) {
                erpAuditRecord.setAuditor(auditor);
            }
            
            // 使用通用查询方法
            return erpAuditRecordMapper.selectErpAuditRecordList(erpAuditRecord);
            
        } catch (Exception e) {
            log.error("查询审核记录失败", e);
            throw new RuntimeException("查询审核记录失败", e);
        }
    }

    /**
     * 审核处理
     * 支持单个和批量审核
     * 
     * @param auditRecordIds 审核记录ID列表
     * @param confirm 审核决定（1:通过 -1:拒绝）
     * @param auditor 审核人
     * @param auditComment 审核意见
     * @return 处理结果
     */
    @Override
    @Transactional
    public int processAudit(List<Long> auditRecordIds, Integer confirm, String auditor, String auditComment)
    {
        try {
            if (auditRecordIds == null || auditRecordIds.isEmpty()) {
                throw new IllegalArgumentException("审核记录ID列表不能为空");
            }
            
            if (confirm == null || (!confirm.equals(1) && !confirm.equals(-1))) {
                throw new IllegalArgumentException("审核决定参数错误，1=通过，-1=拒绝");
            }
            
            if (!StringUtils.hasText(auditor)) {
                throw new IllegalArgumentException("审核人不能为空");
            }
            
            int totalProcessed = 0;
            Date checkTime = new Date();
            
            // 处理每个审核记录
            for (Long auditRecordId : auditRecordIds) {
                ErpAuditRecord auditRecord = new ErpAuditRecord();
                auditRecord.setId(auditRecordId);
                auditRecord.setConfirm(1); // 无论通过还是拒绝，都标记为已处理
                auditRecord.setAuditor(auditor);
                auditRecord.setCheckTime(checkTime);
                auditRecord.setAuditComment(auditComment);
                
                int result = erpAuditRecordMapper.updateById(auditRecord);
                if (result > 0) {
                    totalProcessed++;
                }
            }
            
            String action = confirm.equals(1) ? "通过" : "拒绝";
            log.info("批量审核{}成功，处理记录数：{}，审核人：{}", action, totalProcessed, auditor);
            
            return totalProcessed;
            
        } catch (Exception e) {
            log.error("审核处理失败，审核记录ID：{}，审核人：{}", auditRecordIds, auditor, e);
            throw new RuntimeException("审核处理失败", e);
        }
    }

    /**
     * 创建审核记录
     * 
     * @param auditType 审核类型
     * @param auditId 审核对象ID
     * @param preStatus 审核前状态
     * @param toStatus 目标状态
     * @return 审核记录
     */
    @Override
    @Transactional
    public ErpAuditRecord createAuditRecord(Integer auditType, Long auditId, Integer preStatus, Integer toStatus)
    {
        try {
            if (auditType == null || auditId == null) {
                throw new IllegalArgumentException("审核类型和审核对象ID不能为空");
            }
            
            ErpAuditRecord auditRecord = new ErpAuditRecord();
            auditRecord.setAuditType(auditType);
            auditRecord.setAuditId(auditId);
            auditRecord.setPreStatus(preStatus);
            auditRecord.setToStatus(toStatus);
            auditRecord.setConfirm(0); // 未审核
            auditRecord.setCreateTime(DateUtils.getNowDate());
            
            erpAuditRecordMapper.insert(auditRecord);
            log.info("创建审核记录成功，审核类型：{}，审核对象ID：{}，记录ID：{}", 
                    auditType, auditId, auditRecord.getId());
            
            return auditRecord;
            
        } catch (Exception e) {
            log.error("创建审核记录失败，审核类型：{}，审核对象ID：{}", auditType, auditId, e);
            throw new RuntimeException("创建审核记录失败", e);
        }
    }

    // ==================== 订单审核业务方法 ====================

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
            ErpAuditRecord auditRecord = createAuditRecord(
                AuditTypeEnum.ORDER_AUDIT.getCode(),
                order.getId(),
                null, // 新创建的订单没有前状态
                ORDER_STATUS_APPROVED // 目标状态：审核通过
            );
            
            // 2. 构建通知模板数据
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
            
            // 1. 获取审核记录信息
            ErpAuditRecord queryRecord = new ErpAuditRecord();
            queryRecord.setId(auditRecordId);
            List<ErpAuditRecord> records = selectAuditRecords(queryRecord, null, null, null, null);
            if (records.isEmpty()) {
                throw new RuntimeException("审核记录不存在");
            }
            ErpAuditRecord auditRecord = records.get(0);
            
            // 2. 更新订单状态为设计中
            ErpOrders order = ordersService.selectErpOrdersById(auditRecord.getAuditId());
            if (order == null) {
                throw new RuntimeException("订单不存在");
            }
            
            order.setStatus(ORDER_STATUS_DESIGNING); // 设置为设计中状态
            ordersService.updateErpOrders(order);
            
            // 3. 构建通知模板数据
            Map<String, Object> templateData = buildOrderNotificationData(order);
            
            // 4. 发送通知给设计部用户
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
     * 订单审核拒绝处理（发送通知，订单状态保持不变）
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
            
            // 1. 获取审核记录信息
            ErpAuditRecord queryRecord = new ErpAuditRecord();
            queryRecord.setId(auditRecordId);
            List<ErpAuditRecord> records = selectAuditRecords(queryRecord, null, null, null, null);
            if (records.isEmpty()) {
                throw new RuntimeException("审核记录不存在");
            }
            ErpAuditRecord auditRecord = records.get(0);
            
            // 2. 获取订单信息（不修改订单状态，保持原状态）
            ErpOrders order = ordersService.selectErpOrdersById(auditRecord.getAuditId());
            if (order == null) {
                throw new RuntimeException("订单不存在");
            }
            
            // 3. 构建通知模板数据
            Map<String, Object> templateData = buildOrderNotificationData(order);
            templateData.put("rejectReason", auditComment != null ? auditComment : "审核未通过");
            
            // 4. 发送通知给订单部用户
            notificationService.sendNotificationToRole(
                NotificationTypeEnum.ORDER_REJECTED,
                "order_user", // 订单部用户角色标识
                order.getId(),
                "ORDER",
                templateData
            );
            
            log.info("订单审核拒绝流程处理完成，订单ID：{}，审核记录ID：{}，订单状态保持不变", 
                    order.getId(), auditRecordId);
            
        } catch (Exception e) {
            log.error("处理订单审核拒绝流程失败，审核记录ID：{}", auditRecordId, e);
            throw new RuntimeException("处理订单审核拒绝流程失败", e);
        }
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

    /**
     * 订单状态变更审核处理（创建审核记录并发送通知）
     * 
     * @param order 订单信息
     * @param newStatus 新状态
     */
    @Override
    @Transactional
    public void handleOrderStatusChange(ErpOrders order, Integer newStatus)
    {
        try {
            log.info("开始处理订单状态变更审核流程，订单ID：{}，当前状态：{}，目标状态：{}", 
                    order.getId(), order.getStatus(), newStatus);
            
            // 1. 创建审核记录
            ErpAuditRecord auditRecord = createAuditRecord(
                AuditTypeEnum.ORDER_AUDIT.getCode(),
                order.getId(),
                order.getStatus(), // 当前状态作为前状态
                newStatus // 目标状态
            );
            
            // 2. 构建通知模板数据
            Map<String, Object> templateData = buildOrderNotificationData(order);
            templateData.put("currentStatus", getStatusDescription(order.getStatus()));
            templateData.put("targetStatus", getStatusDescription(newStatus));
            
            // 3. 发送通知给订单审核人
            notificationService.sendNotificationToRole(
                NotificationTypeEnum.ORDER_STATUS_CHANGE,
                "order_auditor", // 订单审核人角色标识
                order.getId(),
                "ORDER",
                templateData
            );
            
            log.info("订单状态变更审核流程处理完成，订单ID：{}，审核记录ID：{}", 
                    order.getId(), auditRecord.getId());
            
        } catch (Exception e) {
            log.error("处理订单状态变更审核流程失败，订单ID：{}", order.getId(), e);
            throw new RuntimeException("处理订单状态变更审核流程失败", e);
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
     * 获取状态描述
     * 
     * @param status 状态值
     * @return 状态描述
     */
    private String getStatusDescription(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case -1: return "审核不通过";
            case 0: return "创建(未审核)";
            case 1: return "待设计";
            case 2: return "已设计";
            case 3: return "已发货";
            case 4: return "已完成";
            default: return "未知状态(" + status + ")";
        }
    }
} 