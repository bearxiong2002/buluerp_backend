package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.web.domain.ErpAuditRecord;
import com.ruoyi.web.domain.ErpOrders;
import com.ruoyi.web.domain.ErpProductionSchedule;
import com.ruoyi.web.domain.ErpPurchaseCollection;
import com.ruoyi.web.domain.ErpPackagingList;
import com.ruoyi.web.enums.AuditTypeEnum;
import com.ruoyi.web.enums.NotificationTypeEnum;
import com.ruoyi.web.mapper.ErpAuditRecordMapper;
import com.ruoyi.web.service.IErpAuditRecordService;
import com.ruoyi.web.service.IErpOrdersService;
import com.ruoyi.web.service.IErpProductionScheduleService;
import com.ruoyi.web.service.IErpPurchaseCollectionService;
import com.ruoyi.web.service.IErpNotificationService;
import com.ruoyi.web.service.IErpPackagingListService;
import com.ruoyi.web.service.IErpProductsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Objects;
import com.ruoyi.web.enums.OrderStatus;
import com.ruoyi.web.domain.ErpOrdersProduct;

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
    private IErpNotificationService notificationService;

    @Autowired
    private IErpOrdersService ordersService;

    @Autowired
    private IErpProductionScheduleService productionScheduleService;

    @Autowired
    private IErpPurchaseCollectionService purchaseCollectionService;

    @Autowired
    private IErpPackagingListService packagingListService;

    @Autowired
    private IErpProductsService productsService;

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
     * @param dateParams 时间范围查询参数
     * @return 审核记录列表
     */
    @Override
    public List<ErpAuditRecord> selectAuditRecords(ErpAuditRecord erpAuditRecord, 
                                                  Integer auditType, 
                                                  Long auditId, 
                                                  Boolean pendingOnly, 
                                                  Map<String, Date> dateParams)
    {
        try {
            // 使用 MyBatis-Plus 的 LambdaQueryWrapper 进行查询
            LambdaQueryWrapper<ErpAuditRecord> wrapper = new LambdaQueryWrapper<>();
            
            // 如果同时指定了审核类型和审核对象ID，使用精确查询
            if (auditType != null && auditId != null) {
                wrapper.eq(ErpAuditRecord::getAuditType, auditType)
                       .eq(ErpAuditRecord::getAuditId, auditId);
                return erpAuditRecordMapper.selectList(wrapper);
            }
            
            // 如果只查询待审核记录
            if (Boolean.TRUE.equals(pendingOnly)) {
                wrapper.eq(ErpAuditRecord::getConfirm, 0); // 未审核状态
                if (auditType != null) {
                    wrapper.eq(ErpAuditRecord::getAuditType, auditType);
                }
                wrapper.orderByDesc(ErpAuditRecord::getCreateTime); // 按创建时间倒序
                return erpAuditRecordMapper.selectList(wrapper);
            }
            
            // 设置可选查询条件
            if (erpAuditRecord != null) {
                if (erpAuditRecord.getId() != null) {
                    wrapper.eq(ErpAuditRecord::getId, erpAuditRecord.getId());
                }
                if (erpAuditRecord.getConfirm() != null) {
                    wrapper.eq(ErpAuditRecord::getConfirm, erpAuditRecord.getConfirm());
                }
                if (erpAuditRecord.getPreStatus() != null) {
                    wrapper.eq(ErpAuditRecord::getPreStatus, erpAuditRecord.getPreStatus());
                }
                if (erpAuditRecord.getToStatus() != null) {
                    wrapper.eq(ErpAuditRecord::getToStatus, erpAuditRecord.getToStatus());
                }
                if (StringUtils.hasText(erpAuditRecord.getAuditor())) {
                    wrapper.eq(ErpAuditRecord::getAuditor, erpAuditRecord.getAuditor());
                }
                if (StringUtils.hasText(erpAuditRecord.getAuditComment())) {
                    wrapper.like(ErpAuditRecord::getAuditComment, erpAuditRecord.getAuditComment());
                }
            }
            
            if (auditType != null) {
                wrapper.eq(ErpAuditRecord::getAuditType, auditType);
            }
            
            if (auditId != null) {
                wrapper.eq(ErpAuditRecord::getAuditId, auditId);
            }

            // 处理时间范围查询
            if (dateParams != null) {
                // 创建时间范围
                if (dateParams.get("createTimeStart") != null) {
                    wrapper.ge(ErpAuditRecord::getCreateTime, dateParams.get("createTimeStart"));
                }
                if (dateParams.get("createTimeEnd") != null) {
                    wrapper.le(ErpAuditRecord::getCreateTime, dateParams.get("createTimeEnd"));
                }
                
                // 审核时间范围
                if (dateParams.get("checkTimeStart") != null) {
                    wrapper.ge(ErpAuditRecord::getCheckTime, dateParams.get("checkTimeStart"));
                }
                if (dateParams.get("checkTimeEnd") != null) {
                    wrapper.le(ErpAuditRecord::getCheckTime, dateParams.get("checkTimeEnd"));
                }
            }
            
            wrapper.orderByDesc(ErpAuditRecord::getCreateTime);
            return erpAuditRecordMapper.selectList(wrapper);
            
        } catch (Exception e) {
            log.error("查询审核记录失败", e);
            throw new RuntimeException("查询审核记录失败", e);
        }
    }

    /**
     * 批量删除审核记录
     * 
     * @param ids 需要删除的审核记录ID
     * @return 结果
     */
    @Override
    public int deleteAuditRecordByIds(List<Integer> ids)
    {
        return erpAuditRecordMapper.deleteBatchIds(ids);
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
            auditRecord.setPreStatus(preStatus == null ? 0 : preStatus);
            auditRecord.setToStatus(toStatus);
            auditRecord.setConfirm(0); // 未审核
            auditRecord.setCreateTime(DateUtils.getNowDate());

            // 更新业务表的审核状态为"审核中"
            updateAuditStatus(auditType, auditId, 1); // 1 = 审核中
            
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
            // 先将该订单之前的通知标记为已读
            notificationService.markNotificationsAsReadByBusiness(order.getId(), "ORDER");
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
            
            // 2. 更新订单状态为审核记录中的目标状态
            ErpOrders order = ordersService.selectErpOrdersById(auditRecord.getAuditId());
            if (order == null) {
                throw new RuntimeException("订单不存在");
            }

            // 在发送新通知前，将旧通知标记为已读
            notificationService.markNotificationsAsReadByBusiness(order.getId(), "ORDER");

            Integer originalStatus = order.getStatus(); // 记录原始状态
            order.setStatus(auditRecord.getToStatus());
            order.setAuditStatus(2); // 审核通过
            ordersService.applyApprovedStatus(order);
            
            // 3. 构建通知模板数据
            Map<String, Object> templateData = buildOrderNotificationData(order);
            templateData.put("auditor", auditor);
            templateData.put("auditComment", auditComment != null ? auditComment : "审核通过");

            Integer toStatus = auditRecord.getToStatus();
            Integer completedStatus = ordersService.getStatusValue(OrderStatus.COMPLETED.getLabel());

            // 最终状态“已完成”审核通过后，不再发送通知
            if (Objects.equals(toStatus, completedStatus)) {
                log.info("订单审核通过流程处理完成，订单ID：{}，审核记录ID：{}。状态已更新为最终态，不发送通知。",
                        order.getId(), auditRecordId);
            } else {
                // ==================== 根据状态变更，动态决定通知对象 START ====================
                String notifyRole = "design-dept"; // 默认通知设计部（适用于初次审核）
                Integer preStatus = auditRecord.getPreStatus();
                
                // 如果 preStatus 不为 null，说明这是一个状态变更审核，而不是初次创建审核
                if (preStatus != null) {
                    Integer materialInInventoryStatus = ordersService.getStatusValue(OrderStatus.MATERIAL_IN_INVENTORY.getLabel());
                    Integer deliveredStatus = ordersService.getStatusValue(OrderStatus.DELIVERED.getLabel());

                    if (Objects.equals(toStatus, materialInInventoryStatus)) {
                        notifyRole = "warehouse"; // 通知仓库开始套料
                    } else if (Objects.equals(toStatus, deliveredStatus)) {
                        notifyRole = "sell-dept"; // 发货相关的，通知回销售部
                    }
                }
                // ==================== 根据状态变更，动态决定通知对象 END ====================

                // 4. 发送通知给对应的角色
                notificationService.sendNotificationToRole(
                    NotificationTypeEnum.ORDER_AUDIT_APPROVED,
                    notifyRole,
                    order.getId(),
                    "ORDER",
                    templateData
                );
                
                log.info("订单审核通过流程处理完成，订单ID：{}，审核记录ID：{}，已通知角色：{}",
                        order.getId(), auditRecordId, notifyRole);
            }
            
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
            
            // 在发送新通知前，将旧通知标记为已读
            notificationService.markNotificationsAsReadByBusiness(order.getId(), "ORDER");
            
            order.setAuditStatus(-1); // 审核被拒绝
            ordersService.updateErpOrders(order); // 单独更新审核状态
            
            // 3. 构建通知模板数据
            Map<String, Object> templateData = buildOrderNotificationData(order);
            templateData.put("auditor", auditor);
            templateData.put("rejectReason", auditComment != null ? auditComment : "审核未通过");
            
            // ==================== 根据审核类型，动态决定通知对象 START ====================
            String notifyRole = "sell-dept"; // 默认通知销售部（适用于初次审核被拒）
            Integer preStatus = auditRecord.getPreStatus();
            
            // 如果 preStatus 不为 null，说明这是一个状态变更审核被拒
            if (preStatus != null) {
                Integer packagedStatus = ordersService.getStatusValue(OrderStatus.PACKAGED.getLabel());
                if (preStatus < packagedStatus) {
                    // 如果是“拉线完成”之前的状态变更被拒（即生产采购阶段），通知PMC协调
                    notifyRole = "pmc-dept";
                } else {
                    // 如果是“拉线完成”之后的状态变更被拒（即发货完成阶段），通知销售部
                    notifyRole = "sell-dept";
                }
            }
            // ==================== 根据审核类型，动态决定通知对象 END ====================

            // 4. 发送通知给对应的角色
            notificationService.sendNotificationToRole(
                NotificationTypeEnum.ORDER_AUDIT_REJECTED,
                notifyRole,
                order.getId(),
                "ORDER",
                templateData
            );
            
            log.info("订单审核拒绝流程处理完成，订单ID：{}，审核记录ID：{}，订单状态保持不变，已通知角色：{}",
                    order.getId(), auditRecordId, notifyRole);
            
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
            // 在创建新的审核记录前，先将该业务之前的所有通知标记为已读
            notificationService.markNotificationsAsReadByBusiness(order.getId(), "ORDER");

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
                "order_auditor", // 状态变更统一由订单审核人处理
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

    // ==================== 布产审核业务方法实现 ====================

    /**
     * 布产计划创建后处理（创建审核记录并发送通知）
     */
    @Override
    @Transactional
    public void handleProductionScheduleCreated(ErpProductionSchedule schedule) {
        try {
            // 先将该生产计划之前的通知标记为已读
            notificationService.markNotificationsAsReadByBusiness(schedule.getId(), "PRODUCTION_SCHEDULE");
            log.info("开始处理布产计划创建审核流程，布产计划ID：{}", schedule.getId());
            
            // 1. 创建审核记录
            ErpAuditRecord auditRecord = createAuditRecord(
                AuditTypeEnum.PRODUCTION_AUDIT.getCode(),
                schedule.getId(),
                0, // 创建状态
                1  // 目标状态：审核通过
            );
            
            // 2. 构建通知模板数据
            Map<String, Object> templateData = buildProductionScheduleNotificationData(schedule);
            
            // 3. 发送通知给布产审核人
            notificationService.sendNotificationToRole(
                NotificationTypeEnum.PRODUCTION_AUDIT_PENDING,
                "production-auditor", // 布产审核人角色标识
                schedule.getId(),
                "PRODUCTION_SCHEDULE",
                templateData
            );
            
            log.info("布产计划创建审核流程处理完成，布产计划ID：{}，审核记录ID：{}", 
                    schedule.getId(), auditRecord.getId());
            
        } catch (Exception e) {
            log.error("处理布产计划创建审核流程失败，布产计划ID：{}", schedule.getId(), e);
            throw new RuntimeException("处理布产计划创建审核流程失败", e);
        }
    }

    /**
     * 布产计划审核通过处理
     */
    @Override
    @Transactional
    public void handleProductionScheduleApproved(Long auditRecordId, String auditor, String auditComment) {
        try {
            log.info("开始处理布产计划审核通过流程，审核记录ID：{}", auditRecordId);
            
            // 1. 获取审核记录信息
            ErpAuditRecord queryRecord = new ErpAuditRecord();
            queryRecord.setId(auditRecordId);
            List<ErpAuditRecord> records = selectAuditRecords(queryRecord, null, null, null, null);
            if (records.isEmpty()) {
                throw new RuntimeException("审核记录不存在");
            }
            ErpAuditRecord auditRecord = records.get(0);
            
            // 2. 更新布产计划状态
            ErpProductionSchedule schedule = productionScheduleService.getById(auditRecord.getAuditId());
            if (schedule == null) {
                throw new RuntimeException("布产计划不存在");
            }
            
            // 在发送新通知前，将旧通知标记为已读
            notificationService.markNotificationsAsReadByBusiness(schedule.getId(), "PRODUCTION_SCHEDULE");

            schedule.setStatus(Long.valueOf(auditRecord.getToStatus()));
            schedule.setAuditStatus(2); // 审核通过
            productionScheduleService.applyApprovedStatus(schedule);

            // ==================== 自动更新父订单状态 START ====================
            try {
                ErpOrders order = ordersService.selectByOrderCode(schedule.getOrderCode());
                if (order != null) {
                    Integer currentStatus = order.getStatus();
                    Integer statusValuePending = ordersService.getStatusValue(OrderStatus.PURCHASE_PRODUCTION_PENDING.getLabel());
                    Integer statusValueInProduction = ordersService.getStatusValue(OrderStatus.IN_PRODUCTION.getLabel());

                    if (Objects.equals(currentStatus, statusValuePending)) {
                        // 如果订单当前是“待计划”状态，则直接进入“布产中”
                        ordersService.updateOrderStatusAutomatic(order.getId(), OrderStatus.IN_PRODUCTION);
                    } else if (Objects.equals(currentStatus, statusValueInProduction)) {
                        // 如果订单当前已在“外购中”，则进入“外购与布产中”
                        ordersService.updateOrderStatusAutomatic(order.getId(), OrderStatus.PURCHASING_IN_PRODUCTION);
                    }
                } else {
                    log.warn("布产计划审核通过后，未找到对应的父订单，订单号: {}", schedule.getOrderCode());
                }
            } catch (Exception e) {
                log.error("布产计划审核通过后，自动更新父订单状态失败，布产计划ID: {}", schedule.getId(), e);
                // 这里不向上抛出异常，确保布产计划的流程能正常完成
            }
            // ==================== 自动更新父订单状态 END ====================
            
            // 3. 构建通知模板数据
            Map<String, Object> templateData = buildProductionScheduleNotificationData(schedule);
            templateData.put("auditor", auditor);
            templateData.put("auditComment", auditComment != null ? auditComment : "审核通过");
            
            // 4. 发送通知给注塑部
            notificationService.sendNotificationToRole(
                NotificationTypeEnum.PRODUCTION_APPROVED_TO_DEPT,
                "injectionmolding-dept", // 注塑部角色标识
                schedule.getId(),
                "PRODUCTION_SCHEDULE",
                templateData
            );
            
            log.info("布产计划审核通过流程处理完成，布产计划ID：{}，审核记录ID：{}", 
                    schedule.getId(), auditRecordId);
            
        } catch (Exception e) {
            log.error("处理布产计划审核通过流程失败，审核记录ID：{}", auditRecordId, e);
            throw new RuntimeException("处理布产计划审核通过流程失败", e);
        }
    }

    /**
     * 布产计划审核拒绝处理
     */
    @Override
    @Transactional
    public void handleProductionScheduleRejected(Long auditRecordId, String auditor, String auditComment) {
        try {
            log.info("开始处理布产计划审核拒绝流程，审核记录ID：{}", auditRecordId);
            
            // 1. 获取审核记录信息
            ErpAuditRecord queryRecord = new ErpAuditRecord();
            queryRecord.setId(auditRecordId);
            List<ErpAuditRecord> records = selectAuditRecords(queryRecord, null, null, null, null);
            if (records.isEmpty()) {
                throw new RuntimeException("审核记录不存在");
            }
            ErpAuditRecord auditRecord = records.get(0);
            
            // 2. 获取布产计划信息（不修改状态，保持原状态）
            ErpProductionSchedule schedule = productionScheduleService.getById(auditRecord.getAuditId());
            if (schedule == null) {
                throw new RuntimeException("布产计划不存在");
            }
            
            // 在发送新通知前，将旧通知标记为已读
            notificationService.markNotificationsAsReadByBusiness(schedule.getId(), "PRODUCTION_SCHEDULE");

            schedule.setAuditStatus(-1); // 审核被拒绝
            productionScheduleService.updateById(schedule); // 单独更新审核状态
            
            // 3. 构建通知模板数据
            Map<String, Object> templateData = buildProductionScheduleNotificationData(schedule);
            templateData.put("auditor", auditor);
            templateData.put("rejectReason", auditComment != null ? auditComment : "审核未通过");
            
            // 4. 发送通知给PMC部门
            notificationService.sendNotificationToRole(
                NotificationTypeEnum.PRODUCTION_REJECTED_TO_PMC,
                "pmc-dept", // PMC部门角色标识
                schedule.getId(),
                "PRODUCTION_SCHEDULE",
                templateData
            );
            
            log.info("布产计划审核拒绝流程处理完成，布产计划ID：{}，审核记录ID：{}", 
                    schedule.getId(), auditRecordId);
            
        } catch (Exception e) {
            log.error("处理布产计划审核拒绝流程失败，审核记录ID：{}", auditRecordId, e);
            throw new RuntimeException("处理布产计划审核拒绝流程失败", e);
        }
    }

    /**
     * 布产计划状态变更审核处理
     */
    @Override
    @Transactional
    public void handleProductionScheduleStatusChange(ErpProductionSchedule schedule, Integer newStatus) {
        try {
            // 在创建新的审核记录前，先将该业务之前的所有通知标记为已读
            notificationService.markNotificationsAsReadByBusiness(schedule.getId(), "PRODUCTION_SCHEDULE");

            log.info("开始处理布产计划状态变更审核流程，布产计划ID：{}，当前状态：{}，目标状态：{}", 
                    schedule.getId(), schedule.getStatus(), newStatus);
            
            // 1. 创建审核记录
            ErpAuditRecord auditRecord = createAuditRecord(
                AuditTypeEnum.PRODUCTION_AUDIT.getCode(),
                schedule.getId(),
                schedule.getStatus() != null ? schedule.getStatus().intValue() : 0,
                newStatus
            );
            
            // 2. 构建通知模板数据
            Map<String, Object> templateData = buildProductionScheduleNotificationData(schedule);
            templateData.put("currentStatus", getProductionStatusDescription(schedule.getStatus()));
            templateData.put("targetStatus", getProductionStatusDescription(Long.valueOf(newStatus)));
            
            // 3. 发送通知给布产审核人
            notificationService.sendNotificationToRole(
                NotificationTypeEnum.PRODUCTION_AUDIT_PENDING,
                "production-auditor", // 布产审核人角色标识
                schedule.getId(),
                "PRODUCTION_SCHEDULE",
                templateData
            );
            
            log.info("布产计划状态变更审核流程处理完成，布产计划ID：{}，审核记录ID：{}", 
                    schedule.getId(), auditRecord.getId());
            
        } catch (Exception e) {
            log.error("处理布产计划状态变更审核流程失败，布产计划ID：{}", schedule.getId(), e);
            throw new RuntimeException("处理布产计划状态变更审核流程失败", e);
        }
    }

    /**
     * 获取布产计划详情
     */
    @Override
    public ErpProductionSchedule getProductionScheduleDetail(Long scheduleId) {
        try {
            log.info("获取布产计划详情，布产计划ID：{}", scheduleId);
            ErpProductionSchedule schedule = productionScheduleService.getById(scheduleId);
            if (schedule == null) {
                log.warn("布产计划不存在，布产计划ID：{}", scheduleId);
            }
            return schedule;
        } catch (Exception e) {
            log.error("获取布产计划详情失败，布产计划ID：{}", scheduleId, e);
            throw new RuntimeException("获取布产计划详情失败", e);
        }
    }

    // ==================== 采购审核业务方法实现 ====================

    /**
     * 采购汇总创建后处理（创建审核记录并发送通知）
     */
    @Override
    @Transactional
    public void handlePurchaseCollectionCreated(ErpPurchaseCollection collection) {
        try {
            // 先将该采购汇总之前的通知标记为已读
            notificationService.markNotificationsAsReadByBusiness(collection.getId(), "PURCHASE_COLLECTION");
            log.info("开始处理采购汇总创建审核流程，采购汇总ID：{}", collection.getId());
            
            // 1. 创建审核记录（使用采购审核类型）
            ErpAuditRecord auditRecord = createAuditRecord(
                AuditTypeEnum.PURCHASE_AUDIT.getCode(),
                collection.getId(),
                0, // 创建状态
                1  // 目标状态：审核通过
            );
            
            // 2. 构建通知模板数据
            Map<String, Object> templateData = buildPurchaseCollectionNotificationData(collection);
            
            // 3. 发送通知给采购审核人
            notificationService.sendNotificationToRole(
                NotificationTypeEnum.PURCHASE_AUDIT_PENDING,
                "purchase-auditor", // 采购审核人角色标识
                collection.getId(),
                "PURCHASE_COLLECTION",
                templateData
            );
            
            log.info("采购汇总创建审核流程处理完成，采购汇总ID：{}，审核记录ID：{}", 
                    collection.getId(), auditRecord.getId());
            
        } catch (Exception e) {
            log.error("处理采购汇总创建审核流程失败，采购汇总ID：{}", collection.getId(), e);
            throw new RuntimeException("处理采购汇总创建审核流程失败", e);
        }
    }

    /**
     * 采购汇总审核通过处理
     */
    @Override
    @Transactional
    public void handlePurchaseCollectionApproved(Long auditRecordId, String auditor, String auditComment) {
        try {
            log.info("开始处理采购汇总审核通过流程，审核记录ID：{}", auditRecordId);
            
            // 1. 获取审核记录信息
            ErpAuditRecord queryRecord = new ErpAuditRecord();
            queryRecord.setId(auditRecordId);
            List<ErpAuditRecord> records = selectAuditRecords(queryRecord, null, null, null, null);
            if (records.isEmpty()) {
                throw new RuntimeException("审核记录不存在");
            }
            ErpAuditRecord auditRecord = records.get(0);
            
            // 2. 更新采购汇总状态
            ErpPurchaseCollection collection = purchaseCollectionService.selectErpPurchaseCollectionById(auditRecord.getAuditId());
            if (collection == null) {
                throw new RuntimeException("采购汇总不存在");
            }
            
            // 在发送新通知前，将旧通知标记为已读
            notificationService.markNotificationsAsReadByBusiness(collection.getId(), "PURCHASE_COLLECTION");

            collection.setStatus(Long.valueOf(auditRecord.getToStatus()));
            collection.setAuditStatus(2); // 审核通过
            purchaseCollectionService.applyApprovedStatus(collection);

            // ==================== 自动更新父订单状态 START ====================
            try {
                ErpOrders order = ordersService.selectByOrderCode(collection.getOrderCode());
                if (order != null) {
                    Integer currentStatus = order.getStatus();
                    Integer statusValuePending = ordersService.getStatusValue(OrderStatus.PURCHASE_PRODUCTION_PENDING.getLabel());
                    Integer statusValueInProduction = ordersService.getStatusValue(OrderStatus.IN_PRODUCTION.getLabel());

                    if (Objects.equals(currentStatus, statusValuePending)) {
                        // 如果订单当前是“待计划”状态，则直接进入“外购中”
                        ordersService.updateOrderStatusAutomatic(order.getId(), OrderStatus.PURCHASING);
                    } else if (Objects.equals(currentStatus, statusValueInProduction)) {
                        // 如果订单当前已在“布产中”，则进入“外购与布产中”
                        ordersService.updateOrderStatusAutomatic(order.getId(), OrderStatus.PURCHASING_IN_PRODUCTION);
                    }
                } else {
                    log.warn("采购计划审核通过后，未找到对应的父订单，订单号: {}", collection.getOrderCode());
                }
            } catch (Exception e) {
                log.error("采购计划审核通过后，自动更新父订单状态失败，采购汇总ID: {}", collection.getId(), e);
                // 这里不向上抛出异常，确保采购计划的流程能正常完成
            }
            // ==================== 自动更新父订单状态 END ====================
            
            // 3. 构建通知模板数据
            Map<String, Object> templateData = buildPurchaseCollectionNotificationData(collection);
            templateData.put("auditor", auditor);
            templateData.put("auditComment", auditComment != null ? auditComment : "审核通过");
            
            // 4. 发送通知给采购部
            notificationService.sendNotificationToRole(
                NotificationTypeEnum.PURCHASE_APPROVED_TO_DEPT,
                "purchase-dept", // 采购部角色标识
                collection.getId(),
                "PURCHASE_COLLECTION",
                templateData
            );
            
            log.info("采购汇总审核通过流程处理完成，采购汇总ID：{}，审核记录ID：{}", 
                    collection.getId(), auditRecordId);
            
        } catch (Exception e) {
            log.error("处理采购汇总审核通过流程失败，审核记录ID：{}", auditRecordId, e);
            throw new RuntimeException("处理采购汇总审核通过流程失败", e);
        }
    }

    /**
     * 采购汇总审核拒绝处理
     */
    @Override
    @Transactional
    public void handlePurchaseCollectionRejected(Long auditRecordId, String auditor, String auditComment) {
        try {
            log.info("开始处理采购汇总审核拒绝流程，审核记录ID：{}", auditRecordId);
            
            // 1. 获取审核记录信息
            ErpAuditRecord queryRecord = new ErpAuditRecord();
            queryRecord.setId(auditRecordId);
            List<ErpAuditRecord> records = selectAuditRecords(queryRecord, null, null, null, null);
            if (records.isEmpty()) {
                throw new RuntimeException("审核记录不存在");
            }
            ErpAuditRecord auditRecord = records.get(0);
            
            // 2. 获取采购汇总信息（不修改状态，保持原状态）
            ErpPurchaseCollection collection = purchaseCollectionService.selectErpPurchaseCollectionById(auditRecord.getAuditId());
            if (collection == null) {
                throw new RuntimeException("采购汇总不存在");
            }
            
            // 在发送新通知前，将旧通知标记为已读
            notificationService.markNotificationsAsReadByBusiness(collection.getId(), "PURCHASE_COLLECTION");
            
            collection.setAuditStatus(-1); // 审核被拒绝
            purchaseCollectionService.updateErpPurchaseCollection(collection); // 单独更新审核状态
            
            // 3. 发送通知给PMC部门
            Map<String, Object> templateData = buildPurchaseCollectionNotificationData(collection);
            templateData.put("auditor", auditor);
            templateData.put("rejectReason", auditComment != null ? auditComment : "审核未通过");
            notificationService.sendNotificationToRole(
                NotificationTypeEnum.PURCHASE_REJECTED_TO_PMC,
                "pmc-dept", // 发送给PMC部门
                collection.getId(),
                "PURCHASE_COLLECTION",
                templateData
            );

            log.info("采购汇总审核拒绝流程处理完成，采购汇总ID：{}，审核记录ID：{}", 
                    collection.getId(), auditRecordId);
            
        } catch (Exception e) {
            log.error("处理采购汇总审核拒绝流程失败，审核记录ID：{}", auditRecordId, e);
            throw new RuntimeException("处理采购汇总审核拒绝流程失败", e);
        }
    }

    /**
     * 采购汇总状态变更审核处理
     */
    @Override
    @Transactional
    public void handlePurchaseCollectionStatusChange(ErpPurchaseCollection collection, Integer newStatus) {
        try {
            // 在创建新的审核记录前，先将该业务之前的所有通知标记为已读
            notificationService.markNotificationsAsReadByBusiness(collection.getId(), "PURCHASE_COLLECTION");

            log.info("开始处理采购汇总状态变更审核流程，采购汇总ID：{}，当前状态：{}，目标状态：{}", 
                    collection.getId(), collection.getStatus(), newStatus);
            
            // 1. 创建审核记录（使用采购审核类型）
            ErpAuditRecord auditRecord = createAuditRecord(
                AuditTypeEnum.PURCHASE_AUDIT.getCode(),
                collection.getId(),
                collection.getStatus() != null ? collection.getStatus().intValue() : 0,
                newStatus
            );
            
            // 2. 构建通知模板数据
            Map<String, Object> templateData = buildPurchaseCollectionNotificationData(collection);
            templateData.put("currentStatus", getPurchaseStatusDescription(collection.getStatus()));
            templateData.put("targetStatus", getPurchaseStatusDescription(Long.valueOf(newStatus)));
            
            // 3. 发送通知给采购审核人
            notificationService.sendNotificationToRole(
                NotificationTypeEnum.PURCHASE_AUDIT_PENDING,
                "purchase-auditor", // 采购审核人角色标识
                collection.getId(),
                "PURCHASE_COLLECTION",
                templateData
            );
            
            log.info("采购汇总状态变更审核流程处理完成，采购汇总ID：{}，审核记录ID：{}", 
                    collection.getId(), auditRecord.getId());
            
        } catch (Exception e) {
            log.error("处理采购汇总状态变更审核流程失败，采购汇总ID：{}", collection.getId(), e);
            throw new RuntimeException("处理采购汇总状态变更审核流程失败", e);
        }
    }

    /**
     * 获取采购汇总详情
     */
    @Override
    public ErpPurchaseCollection getPurchaseCollectionDetail(Long collectionId) {
        try {
            log.info("获取采购汇总详情，采购汇总ID：{}", collectionId);
            ErpPurchaseCollection collection = purchaseCollectionService.selectErpPurchaseCollectionById(collectionId);
            if (collection == null) {
                log.warn("采购汇总不存在，采购汇总ID：{}", collectionId);
            }
            return collection;
        } catch (Exception e) {
            log.error("获取采购汇总详情失败，采购汇总ID：{}", collectionId, e);
            throw new RuntimeException("获取采购汇总详情失败", e);
        }
    }

    // ==================== 辅助方法 ====================

    /**
     * 构建布产计划通知模板数据
     */
    private Map<String, Object> buildProductionScheduleNotificationData(ErpProductionSchedule schedule) {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("scheduleId", schedule.getId());
        templateData.put("orderCode", schedule.getOrderCode());
        templateData.put("productCode", schedule.getProductCode());
        templateData.put("mouldCode", schedule.getMouldNumber());
        templateData.put("productionQuantity", schedule.getProductionQuantity());
        templateData.put("operator", schedule.getOperator());
        
        // 格式化布产时间
        if (schedule.getProductionTime() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            templateData.put("productionTime", sdf.format(schedule.getProductionTime()));
        } else {
            templateData.put("productionTime", "未设置");
        }
        
        return templateData;
    }

    /**
     * 构建采购汇总通知模板数据
     */
    private Map<String, Object> buildPurchaseCollectionNotificationData(ErpPurchaseCollection collection) {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("collectionId", collection.getId());
        templateData.put("orderCode", collection.getOrderCode());
        templateData.put("purchaseCode", collection.getPurchaseCode());
        templateData.put("mouldNumber", collection.getMouldNumber());
        templateData.put("purchaseQuantity", collection.getPurchaseQuantity());
        templateData.put("supplier", collection.getSupplier());
        templateData.put("operator", collection.getOperator());
        
        // 格式化下单时间
        if (collection.getOrderTime() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            templateData.put("orderTime", sdf.format(collection.getOrderTime()));
        } else {
            templateData.put("orderTime", "未设置");
        }
        
        return templateData;
    }

    /**
     * 获取布产状态描述
     */
    private String getProductionStatusDescription(Long status) {
        if (status == null) {
            return "未知";
        }
        switch (status.intValue()) {
            case 0: return "待审核";
            case 1: return "已审核";
            default: return "未知状态(" + status + ")";
        }
    }

    /**
     * 获取采购状态描述
     */
    private String getPurchaseStatusDescription(Long status) {
        if (status == null) {
            return "未知";
        }
        switch (status.intValue()) {
            case 0: return "待审核";
            case 1: return "已审核";
            default: return "未知状态(" + status + ")";
        }
    }

    @Override
    @Transactional
    public void handleAuditableEntityDeleted(Integer auditType, Long auditId) {
        try {
            if (auditType == null || auditId == null) {
                log.warn("处理待审核对象删除事件时，auditType 或 auditId 为空，已跳过。");
                return;
            }

            log.info("开始处理待审核对象删除事件，审核类型：{}，审核对象ID：{}", auditType, auditId);

            // 1. 查找所有相关的待审核记录 (confirm = 0)
            LambdaQueryWrapper<ErpAuditRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ErpAuditRecord::getAuditType, auditType)
                   .eq(ErpAuditRecord::getAuditId, auditId)
                   .eq(ErpAuditRecord::getConfirm, 0); // 仅查找待审核记录

            List<ErpAuditRecord> pendingRecords = erpAuditRecordMapper.selectList(wrapper);

            if (pendingRecords.isEmpty()) {
                log.info("没有找到与对象（类型：{}，ID：{}）相关的待审核记录，无需处理。", auditType, auditId);
                return;
            }

            // 2. 更新这些记录
            for (ErpAuditRecord record : pendingRecords) {
                record.setConfirm(1); // 标记为已处理
                record.setAuditComment("待审核对象已被删除");
                record.setAuditor("system"); // 标记为系统自动处理
                record.setCheckTime(new Date());
                erpAuditRecordMapper.updateById(record);
            }

            log.info("成功处理了 {} 条因对象删除而作废的待审核记录，审核类型：{}，审核对象ID：{}",
                    pendingRecords.size(), auditType, auditId);

        } catch (Exception e) {
            log.error("处理待审核对象删除事件失败，审核类型：{}，审核对象ID：{}", auditType, auditId, e);
            // 不向上抛出异常，以免影响主业务（如订单删除）的事务
        }
    }

    // ==================== 包装清单/分包审核业务方法 ====================

    @Override
    @Transactional
    public void handlePackagingListCreated(ErpPackagingList packagingList) {
        try {
            // 先将该包装清单之前的通知标记为已读
            notificationService.markNotificationsAsReadByBusiness(packagingList.getId(), "PACKAGING");
            log.info("开始处理包装清单创建后流程，ID：{}", packagingList.getId());
            ErpAuditRecord auditRecord = createAuditRecord(
                AuditTypeEnum.SUBCONTRACT_AUDIT.getCode(),
                packagingList.getId(),
                null, // 新创建无前置状态
                1 // 目标状态：审核通过
            );

            // 发送通知给分包审核员
            Map<String, Object> templateData = buildPackagingListNotificationData(packagingList);
            notificationService.sendNotificationToRole(
                NotificationTypeEnum.SUBCONTRACT_AUDIT_PENDING,
                AuditTypeEnum.SUBCONTRACT_AUDIT.getRoleKey(),
                packagingList.getId(),
                "PACKAGING",
                templateData
            );

            log.info("包装清单创建后流程处理完成，ID：{}", packagingList.getId());
        } catch (Exception e) {
            log.error("处理包装清单创建后流程失败，ID：{}", packagingList.getId(), e);
            throw new RuntimeException("处理包装清单创建后流程失败", e);
        }
    }

    @Override
    @Transactional
    public void handlePackagingListApproved(Long auditRecordId, String auditor, String auditComment) {
        try {
            log.info("开始处理包装清单审核通过流程，审核记录ID：{}", auditRecordId);

            // 1. 获取审核和包装清单信息
            ErpAuditRecord auditRecord = erpAuditRecordMapper.selectById(auditRecordId);
            if (auditRecord == null) throw new ServiceException("审核记录不存在");
            ErpPackagingList packagingList = packagingListService.selectErpPackagingListById(auditRecord.getAuditId());
            if (packagingList == null) throw new ServiceException("包装清单不存在");

            // 在发送新通知前，将旧通知标记为已读
            notificationService.markNotificationsAsReadByBusiness(packagingList.getId(), "PACKAGING");

            // 2. 更新包装清单状态为审核记录中的目标状态
            packagingList.setStatus(auditRecord.getToStatus());
            packagingList.setAuditStatus(2); // 审核通过
            packagingListService.applyApprovedStatus(packagingList);

            // ==================== 自动更新父订单状态 START ====================
            try {
                ErpOrders order = ordersService.selectByOrderCode(packagingList.getOrderCode());
                if (order != null) {
                    ordersService.updateOrderStatusAutomatic(order.getId(), OrderStatus.PACKAGED);
                } else {
                    log.warn("包装清单审核通过后，未找到对应的父订单，订单号: {}", packagingList.getOrderCode());
                }
            } catch (Exception e) {
                log.error("包装清单审核通过后，自动更新父订单状态失败，包装清单ID: {}", packagingList.getId(), e);
                // 这里不向上抛出异常，确保包装清单的流程能正常完成
            }
            // ==================== 自动更新父订单状态 END ====================

            // 3. 发送通知给仓库部门
            Map<String, Object> templateData = buildPackagingListNotificationData(packagingList);
            templateData.put("auditor", auditor);
            templateData.put("auditComment", auditComment != null ? auditComment : "审核通过");
            
            // 如果是状态变更审核，添加额外信息
            if (auditRecord.getPreStatus() != null) {
                templateData.put("isStatusChange", true);
                templateData.put("fromStatus", auditRecord.getPreStatus());
                templateData.put("toStatus", auditRecord.getToStatus());
            }

            notificationService.sendNotificationToRole(
                NotificationTypeEnum.SUBCONTRACT_AUDIT_APPROVED,
                "warehouse", // 发送给仓库部门
                packagingList.getId(),
                "PACKAGING",
                templateData
            );

            log.info("包装清单审核通过流程处理完成，ID：{}", packagingList.getId());
        } catch (Exception e) {
            log.error("处理包装清单审核通过流程失败，审核记录ID：{}", auditRecordId, e);
            throw new RuntimeException("处理包装清单审核通过流程失败", e);
        }
    }

    @Override
    @Transactional
    public void handlePackagingListRejected(Long auditRecordId, String auditor, String auditComment) {
        try {
            log.info("开始处理包装清单审核拒绝流程，审核记录ID：{}", auditRecordId);
            
            // 1. 获取审核和包装清单信息
            ErpAuditRecord auditRecord = erpAuditRecordMapper.selectById(auditRecordId);
            if (auditRecord == null) throw new ServiceException("审核记录不存在");
            ErpPackagingList packagingList = packagingListService.selectErpPackagingListById(auditRecord.getAuditId());
            if (packagingList == null) throw new ServiceException("包装清单不存在");

            // 在发送新通知前，将旧通知标记为已读
            notificationService.markNotificationsAsReadByBusiness(packagingList.getId(), "PACKAGING");

            // 2. 发送通知给PMC部门
            packagingList.setAuditStatus(-1); // 审核被拒绝
            packagingListService.updateErpPackagingList(packagingList); // 单独更新审核状态
            Map<String, Object> templateData = buildPackagingListNotificationData(packagingList);
            templateData.put("auditor", auditor);
            templateData.put("rejectReason", auditComment != null ? auditComment : "审核未通过");

            // 如果是状态变更审核，添加额外信息
            if (auditRecord.getPreStatus() != null) {
                templateData.put("isStatusChange", true);
                templateData.put("fromStatus", auditRecord.getPreStatus());
                templateData.put("toStatus", auditRecord.getToStatus());
            }
            
            notificationService.sendNotificationToRole(
                NotificationTypeEnum.SUBCONTRACT_AUDIT_REJECTED,
                "pmc-dept", // 发送给PMC部门
                packagingList.getId(),
                "PACKAGING",
                templateData
            );

            log.info("包装清单审核拒绝流程处理完成，ID：{}", packagingList.getId());
        } catch (Exception e) {
            log.error("处理包装清单审核拒绝流程失败，审核记录ID：{}", auditRecordId, e);
            throw new RuntimeException("处理包装清单审核拒绝流程失败", e);
        }
    }

    @Override
    @Transactional
    public void handlePackagingListStatusChange(ErpPackagingList packagingList, Integer newStatus) {
         try {
            // 在创建新的审核记录前，先将该业务之前的所有通知标记为已读
            notificationService.markNotificationsAsReadByBusiness(packagingList.getId(), "PACKAGING");

            log.info("开始处理包装清单状态变更审核流程，ID：{}，当前状态：{}，目标状态：{}",
                    packagingList.getId(), packagingList.getStatus(), newStatus);
            createAuditRecord(
                AuditTypeEnum.SUBCONTRACT_AUDIT.getCode(),
                packagingList.getId(),
                packagingList.getStatus(),
                newStatus
            );
            
            // 发送通知给分包审核员
            Map<String, Object> templateData = buildPackagingListNotificationData(packagingList);
            templateData.put("isStatusChange", true);
            templateData.put("fromStatus", packagingList.getStatus());
            templateData.put("toStatus", newStatus);
            notificationService.sendNotificationToRole(
                NotificationTypeEnum.SUBCONTRACT_AUDIT_PENDING,
                AuditTypeEnum.SUBCONTRACT_AUDIT.getRoleKey(),
                packagingList.getId(),
                "PACKAGING",
                templateData
            );
        } catch (Exception e) {
            log.error("处理包装清单状态变更审核流程失败，ID：{}", packagingList.getId(), e);
            throw new RuntimeException("处理包装清单状态变更审核流程失败", e);
        }
    }

    @Override
    public ErpPackagingList getPackagingListDetail(Long packagingListId) {
        return packagingListService.selectErpPackagingListById(packagingListId);
    }

    /**
     * 构建包装清单通知所需的数据
     */
    private Map<String, Object> buildPackagingListNotificationData(ErpPackagingList packagingList) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", packagingList.getId());
        data.put("orderCode", packagingList.getOrderCode());
        data.put("creator", packagingList.getOperator());
        data.put("creationTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(packagingList.getCreationTime()));
        return data;
    }

    /**
     * 更新业务表的审核状态
     *
     * @param auditType   审核类型
     * @param businessId  业务ID
     * @param auditStatus 审核状态
     */
    private void updateAuditStatus(Integer auditType, Long businessId, Integer auditStatus) throws IOException {
        if (auditType == null || businessId == null || auditStatus == null) {
            log.warn("updateAuditStatus received null parameters, skipping update.");
            return;
        }
        AuditTypeEnum typeEnum = AuditTypeEnum.getByCode(auditType);
        if (typeEnum == null) {
            log.error("Unknown audit type code: " + auditType);
            return;
        }

        switch (typeEnum) {
            case ORDER_AUDIT:
                ErpOrders order = new ErpOrders();
                order.setId(businessId);
                order.setAuditStatus(auditStatus);
                ordersService.updateErpOrders(order);
                break;
            case PRODUCTION_AUDIT:
                ErpProductionSchedule schedule = new ErpProductionSchedule();
                schedule.setId(businessId);
                schedule.setAuditStatus(auditStatus);
                productionScheduleService.updateById(schedule);
                break;
            case PURCHASE_AUDIT:
                ErpPurchaseCollection collection = new ErpPurchaseCollection();
                collection.setId(businessId);
                collection.setAuditStatus(auditStatus);
                purchaseCollectionService.updateErpPurchaseCollection(collection);
                break;
            case SUBCONTRACT_AUDIT:
                ErpPackagingList packagingList = new ErpPackagingList();
                packagingList.setId(businessId);
                packagingList.setAuditStatus(auditStatus);
                packagingListService.updateErpPackagingList(packagingList);
                break;
            default:
                log.error("无法修改对应记录审核状态：" + typeEnum);
                break;
        }
    }
} 