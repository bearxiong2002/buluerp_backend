package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ruoyi.common.core.domain.entity.SysRole;
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
import com.ruoyi.web.mapper.ErpPackagingListMapper;
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
import java.util.Set;
import java.util.ArrayList;
import java.util.stream.Collectors;
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
    private ErpPackagingListMapper erpPackagingListMapper;

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
                                                  String auditId, 
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
     * 根据用户角色查询对应的待审核请求
     * 自动识别用户角色并返回相应的待审核列表
     * 
     * @return 用户角色对应的待审核记录列表
     */
    @Override
    public List<ErpAuditRecord> getPendingAuditsByUserRole()
    {
        try {
            // 获取当前用户的角色信息
            Set<String> userRoles = SecurityUtils.getLoginUser().getUser().getRoles()
                .stream()
                .map(SysRole::getRoleKey)
                .collect(Collectors.toSet());
            
            // 检查用户是否为管理员
            if (SecurityUtils.isAdmin(SecurityUtils.getUserId()) || userRoles.contains("admin")) {
                // 管理员可以看到所有类型的待审核记录
                return selectAuditRecords(null, null, null, true, null);
            }
            
            // 根据用户角色查找对应的审核类型
            List<Integer> auditTypes = new ArrayList<>();
            for (String roleKey : userRoles) {
                AuditTypeEnum auditType = AuditTypeEnum.getByRoleKey(roleKey);
                if (auditType != null) {
                    auditTypes.add(auditType.getCode());
                }
            }
            
            if (auditTypes.isEmpty()) {
                // 用户没有审核角色，返回空列表
                return new ArrayList<>();
            }
            
            // 查询用户角色对应的待审核记录
            List<ErpAuditRecord> allPendingRecords = new ArrayList<>();
            
            for (Integer auditType : auditTypes) {
                List<ErpAuditRecord> records = selectAuditRecords(
                    null, auditType, null, true, null);
                allPendingRecords.addAll(records);
            }
            
            return allPendingRecords;
            
        } catch (Exception e) {
            log.error("获取待审核记录失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 通用审核接口
     * 通过审核记录ID自动判断审核类型并进行相应的处理
     * 
     * @param auditRecordId 审核记录ID
     * @param accept 审核结果（1=通过，-1=拒绝）
     * @param auditor 审核人
     * @param auditComment 审核意见
     * @return 处理结果
     */
    @Override
    @Transactional
    public boolean processAuditByRecordId(Long auditRecordId, Integer accept, String auditor, String auditComment)
    {
        try {
            // 1. 根据审核记录ID查询审核记录
            ErpAuditRecord queryRecord = new ErpAuditRecord();
            queryRecord.setId(auditRecordId);
            List<ErpAuditRecord> records = selectAuditRecords(queryRecord, null, null, null, null);
            
            if (records.isEmpty()) {
                log.error("审核记录不存在，审核记录ID：{}", auditRecordId);
                return false;
            }
            
            ErpAuditRecord auditRecord = records.get(0);
            
            // 2. 验证审核记录状态
            if (auditRecord.getConfirm() != null && auditRecord.getConfirm() == 1) {
                log.error("审核记录已处理，审核记录ID：{}", auditRecordId);
                return false;
            }
            
            // 3. 根据审核类型进行相应的处理
            AuditTypeEnum auditTypeEnum = AuditTypeEnum.getByCode(auditRecord.getAuditType());
            if (auditTypeEnum == null) {
                log.error("不支持的审核类型，审核记录ID：{}，审核类型：{}", auditRecordId, auditRecord.getAuditType());
                return false;
            }
            
            // 4. 先处理审核记录状态
            int result = processAudit(Arrays.asList(auditRecordId), accept, auditor, auditComment);
            if (result <= 0) {
                log.error("更新审核记录状态失败，审核记录ID：{}", auditRecordId);
                return false;
            }
            
            // 5. 根据审核类型调用相应的业务处理方法
            switch (auditTypeEnum) {
                case ORDER_AUDIT:
                    if (accept.equals(1)) {
                        handleOrderApproved(auditRecordId, auditor, auditComment);
                    } else {
                        handleOrderRejected(auditRecordId, auditor, auditComment);
                    }
                    break;
                    
                case PRODUCTION_AUDIT:
                    if (accept.equals(1)) {
                        handleProductionScheduleApproved(auditRecordId, auditor, auditComment);
                    } else {
                        handleProductionScheduleRejected(auditRecordId, auditor, auditComment);
                    }
                    break;
                    
                case PURCHASE_AUDIT:
                    if (accept.equals(1)) {
                        handlePurchaseCollectionApproved(auditRecordId, auditor, auditComment);
                    } else {
                        handlePurchaseCollectionRejected(auditRecordId, auditor, auditComment);
                    }
                    break;
                    
                case SUBCONTRACT_AUDIT:
                    // 分包审核只有完成审核，使用订单号作为审核ID
                    if (accept.equals(1)) {
                        handlePackagingListCompleteApproved(auditRecordId, auditor, auditComment);
                    } else {
                        handlePackagingListCompleteRejected(auditRecordId, auditor, auditComment);
                    }
                    break;
                    
                default:
                    log.error("不支持的审核类型：{}", auditTypeEnum);
                    return false;
            }
            
            log.info("通用审核处理成功，审核记录ID：{}，审核类型：{}，审核结果：{}", 
                    auditRecordId, auditTypeEnum.getDescription(), accept);
            return true;
            
        } catch (Exception e) {
            log.error("通用审核处理失败，审核记录ID：{}", auditRecordId, e);
            return false;
        }
    }

    /**
     * 批量通用审核接口
     * 批量处理多个审核记录，自动判断每个记录的审核类型并进行相应的处理
     * 
     * @param auditRecordIds 审核记录ID列表
     * @param accept 审核结果（1=通过，-1=拒绝）
     * @param auditor 审核人
     * @param auditComment 审核意见
     * @return 成功处理的记录数
     */
    @Override
    @Transactional
    public int processBatchAuditByRecordIds(List<Long> auditRecordIds, Integer accept, String auditor, String auditComment)
    {
        if (auditRecordIds == null || auditRecordIds.isEmpty()) {
            log.warn("批量审核记录ID列表为空");
            return 0;
        }
        
        int successCount = 0;
        List<String> failedIds = new ArrayList<>();
        
        for (Long auditRecordId : auditRecordIds) {
            try {
                boolean result = processAuditByRecordId(auditRecordId, accept, auditor, auditComment);
                if (result) {
                    successCount++;
                } else {
                    failedIds.add(auditRecordId.toString());
                }
            } catch (Exception e) {
                log.error("批量审核处理失败，审核记录ID：{}", auditRecordId, e);
                failedIds.add(auditRecordId.toString());
            }
        }
        
        if (!failedIds.isEmpty()) {
            log.warn("批量审核部分失败，失败的审核记录ID：{}", String.join(", ", failedIds));
        }
        
        log.info("批量审核完成，总数：{}，成功：{}，失败：{}", 
                auditRecordIds.size(), successCount, auditRecordIds.size() - successCount);
        
        return successCount;
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
    public ErpAuditRecord createAuditRecord(Integer auditType, String auditId, Integer preStatus, Integer toStatus)
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
            notificationService.markNotificationsAsReadByBusiness(order.getInnerId(), "ORDER");
            log.info("开始处理订单创建后流程，订单ID：{}，订单编号：{}", order.getId(), order.getInnerId());
            
            // 1. 创建审核记录
            ErpAuditRecord auditRecord = createAuditRecord(
                AuditTypeEnum.ORDER_AUDIT.getCode(),
                order.getInnerId(), // 使用订单的InnerID而不是ID
                null, // 新创建的订单没有前状态
                ORDER_STATUS_APPROVED // 目标状态：审核通过
            );
            
            // 2. 构建通知模板数据
            Map<String, Object> templateData = buildOrderNotificationData(order);
            
            // 3. 发送通知给订单审核人
            notificationService.sendNotificationToRole(
                NotificationTypeEnum.ORDER_CREATED,
                "order_auditor", // 订单审核人角色标识
                order.getInnerId(),
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
            // 订单审核记录使用InnerID，需要根据InnerID查找订单
            ErpOrders order = ordersService.selectByOrderCode(auditRecord.getAuditId());
            if (order == null) {
                throw new RuntimeException("订单不存在，InnerID: " + auditRecord.getAuditId());
            }

            // 在发送新通知前，将旧通知标记为已读
            notificationService.markNotificationsAsReadByBusiness(order.getInnerId(), "ORDER");

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

            // 最终状态"已完成"审核通过后，不再发送通知
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
                    order.getInnerId(),
                    "ORDER",
                    templateData
                );
                
                log.info("订单审核通过流程处理完成，订单ID：{}，审核记录ID：{}，已通知角色：{}",
                        order.getId(), auditRecordId, notifyRole);
            }

            // 关闭该订单其他所有待审核的记录
            closeObsoleteAuditRecords(auditRecord);
            
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
            // 订单审核记录使用InnerID，需要根据InnerID查找订单
            ErpOrders order = ordersService.selectByOrderCode(auditRecord.getAuditId());
            if (order == null) {
                throw new RuntimeException("订单不存在，InnerID: " + auditRecord.getAuditId());
            }
            
            // 在发送新通知前，将旧通知标记为已读
            notificationService.markNotificationsAsReadByBusiness(order.getInnerId(), "ORDER");
            
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
                    // 如果是"拉线完成"之前的状态变更被拒（即生产采购阶段），通知PMC协调
                    notifyRole = "pmc-dept";
                } else {
                    // 如果是"拉线完成"之后的状态变更被拒（即发货完成阶段），通知销售部
                    notifyRole = "sell-dept";
                }
            }
            // ==================== 根据审核类型，动态决定通知对象 END ====================

            // 4. 发送通知给对应的角色
            notificationService.sendNotificationToRole(
                NotificationTypeEnum.ORDER_AUDIT_REJECTED,
                notifyRole,
                order.getInnerId(),
                "ORDER",
                templateData
            );
            
            log.info("订单审核拒绝流程处理完成，订单ID：{}，审核记录ID：{}，订单状态保持不变，已通知角色：{}",
                    order.getId(), auditRecordId, notifyRole);

            // 关闭该订单其他所有待审核的记录
            closeObsoleteAuditRecords(auditRecord);
            
        } catch (Exception e) {
            log.error("处理订单审核拒绝流程失败，审核记录ID：{}", auditRecordId, e);
            throw new RuntimeException("处理订单审核拒绝流程失败", e);
        }
    }

    /**
     * 获取订单详情
     * 
     * @param orderInnerId 订单InnerID
     * @return 订单详情
     */
    @Override
    public ErpOrders getOrderDetail(String orderInnerId) {
        try {
            log.info("获取订单详情，订单InnerID：{}", orderInnerId);
            ErpOrders order = ordersService.selectByOrderCode(orderInnerId);
            if (order == null) {
                log.warn("订单不存在，订单InnerID：{}", orderInnerId);
            }
            return order;
        } catch (Exception e) {
            log.error("获取订单详情失败，订单InnerID：{}", orderInnerId, e);
            throw new RuntimeException("获取订单详情失败", e);
        }
    }

    /**
     * 订单状态变更审核处理（创建审核记录并发送通知）
     * 
     * @param updatedOrder 订单信息
     * @param oldStatus 旧状态
     * @param newStatus 新状态
     */
    @Override
    @Transactional
    public void handleOrderStatusChange(ErpOrders updatedOrder, Integer oldStatus, Integer newStatus)
    {
        try {
            // 获取订单当前信息
            ErpOrders currentOrder = ordersService.selectErpOrdersById(updatedOrder.getId());
            
            log.info("订单需要重新审核，重新触发审核流程，订单ID：{}，当前状态：{}，目标状态：{}", 
                    updatedOrder.getId(), oldStatus, newStatus);
            
            // 重新创建审核记录
            ErpAuditRecord auditRecord = createAuditRecord(
                AuditTypeEnum.ORDER_AUDIT.getCode(),
                currentOrder.getInnerId(),
                oldStatus,
                newStatus
            );
            
            // 构建通知模板数据
            Map<String, Object> templateData = buildOrderNotificationData(currentOrder);
            templateData.put("currentStatus", getStatusDescription(oldStatus));
            templateData.put("targetStatus", getStatusDescription(newStatus));
            
            // 发送通知给订单审核人
            notificationService.sendNotificationToRole(
                NotificationTypeEnum.ORDER_STATUS_CHANGE,
                "order_auditor", // 订单审核人角色标识
                currentOrder.getInnerId(),
                "ORDER",
                templateData
            );
            // 标记通知为已读
            notificationService.markNotificationsAsReadByBusiness(currentOrder.getInnerId(), "ORDER");
            log.info("订单重新提交审核流程处理完成，订单ID：{}，审核记录ID：{}", 
                    currentOrder.getId(), auditRecord.getId());
        } catch (Exception e) {
            log.error("处理订单状态变更失败，订单ID：{}", updatedOrder.getId(), e);
            throw new RuntimeException("处理订单状态变更失败", e);
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
            notificationService.markNotificationsAsReadByBusiness(schedule.getOrderCode(), "PRODUCTION_SCHEDULE");
            log.info("开始处理布产计划创建审核流程，布产计划ID：{}", schedule.getId());
            
            // 1. 创建审核记录
            ErpAuditRecord auditRecord = createAuditRecord(
                AuditTypeEnum.PRODUCTION_AUDIT.getCode(),
                String.valueOf(schedule.getId()),
                0, // 创建状态
                1  // 目标状态：审核通过
            );
            
            // 2. 构建通知模板数据
            Map<String, Object> templateData = buildProductionScheduleNotificationData(schedule);
            
            // 3. 发送通知给布产审核人
            notificationService.sendNotificationToRole(
                NotificationTypeEnum.PRODUCTION_AUDIT_PENDING,
                "production-auditor", // 布产审核人角色标识
                schedule.getOrderCode(),
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
            notificationService.markNotificationsAsReadByBusiness(schedule.getOrderCode(), "PRODUCTION_SCHEDULE");

            schedule.setStatus(Long.valueOf(auditRecord.getToStatus()));
            schedule.setAuditStatus(2); // 审核通过
            productionScheduleService.applyApprovedStatus(schedule);

            // ==================== 自动更新父订单状态 START ====================
            try {
                ErpOrders order = ordersService.selectByOrderCode(schedule.getOrderCode());
                if (order != null) {
                    Integer currentStatus = order.getStatus();
                    Integer statusValuePending = ordersService.getStatusValue(OrderStatus.PRODUCTION_SCHEDULE_PENDING.getLabel());
                    Integer statusValueInProduction = ordersService.getStatusValue(OrderStatus.IN_PRODUCTION.getLabel());

                    if (Objects.equals(currentStatus, statusValuePending)) {
                        // 如果订单当前是"待计划"状态，则直接进入"布产中"
                        ordersService.updateOrderStatusAutomatic(order.getId(), OrderStatus.IN_PRODUCTION);
                    } else if (Objects.equals(currentStatus, statusValueInProduction)) {
                        // 如果订单当前已在"排产中"，则进入"生产完成(待采购完成)"
                        ordersService.updateOrderStatusAutomatic(order.getId(), OrderStatus.PRODUCTION_DONE_PURCHASING);
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
                schedule.getOrderCode(),
                "PRODUCTION_SCHEDULE",
                templateData
            );
            
            log.info("布产计划审核通过流程处理完成，布产计划ID：{}，审核记录ID：{}", 
                    schedule.getId(), auditRecordId);

            // 关闭该生产计划其他所有待审核的记录
            closeObsoleteAuditRecords(auditRecord);
            
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
            notificationService.markNotificationsAsReadByBusiness(schedule.getOrderCode(), "PRODUCTION_SCHEDULE");

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
                schedule.getOrderCode(),
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
     * 已弃用
     */
//    @Override
//    @Transactional
//    public void handleProductionScheduleStatusChange(ErpProductionSchedule schedule, Integer newStatus) {
//        try {
//            // 在创建新的审核记录前，先将该业务之前的所有通知标记为已读
//            notificationService.markNotificationsAsReadByBusiness(schedule.getId(), "PRODUCTION_SCHEDULE");
//
//            log.info("开始处理布产计划状态变更审核流程，布产计划ID：{}，当前状态：{}，目标状态：{}",
//                    schedule.getId(), schedule.getStatus(), newStatus);
//
//            // 1. 创建审核记录
//            ErpAuditRecord auditRecord = createAuditRecord(
//                AuditTypeEnum.PRODUCTION_AUDIT.getCode(),
//                schedule.getId(),
//                schedule.getStatus() != null ? schedule.getStatus().intValue() : 0,
//                newStatus
//            );
//
//            // 2. 构建通知模板数据
//            Map<String, Object> templateData = buildProductionScheduleNotificationData(schedule);
//            templateData.put("currentStatus", getProductionStatusDescription(schedule.getStatus()));
//            templateData.put("targetStatus", getProductionStatusDescription(Long.valueOf(newStatus)));
//
//            // 3. 发送通知给布产审核人
//            notificationService.sendNotificationToRole(
//                NotificationTypeEnum.PRODUCTION_AUDIT_PENDING,
//                "production-auditor", // 布产审核人角色标识
//                schedule.getId(),
//                "PRODUCTION_SCHEDULE",
//                templateData
//            );
//
//            log.info("布产计划状态变更审核流程处理完成，布产计划ID：{}，审核记录ID：{}",
//                    schedule.getId(), auditRecord.getId());
//
//        } catch (Exception e) {
//            log.error("处理布产计划状态变更审核流程失败，布产计划ID：{}", schedule.getId(), e);
//            throw new RuntimeException("处理布产计划状态变更审核流程失败", e);
//        }
//    }

    /**
     * 获取布产计划详情
     */
    @Override
    public ErpProductionSchedule getProductionScheduleDetail(String scheduleId) {
        try {
            log.info("获取布产计划详情，布产计划ID：{}", scheduleId);
            // 尝试解析为Long类型，如果失败则按字符串处理
            ErpProductionSchedule schedule = null;
            try {
                Long id = Long.parseLong(scheduleId);
                schedule = productionScheduleService.getById(id);
            } catch (NumberFormatException e) {
                // 如果不是数字，可能需要其他查询方式
                log.warn("布产计划ID不是数字格式：{}", scheduleId);
            }
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
            notificationService.markNotificationsAsReadByBusiness(String.valueOf(collection.getId()), "PURCHASE_COLLECTION");
            log.info("开始处理采购汇总创建审核流程，采购汇总ID：{}", collection.getId());
            
            // 1. 创建审核记录（使用采购审核类型）
            ErpAuditRecord auditRecord = createAuditRecord(
                AuditTypeEnum.PURCHASE_AUDIT.getCode(),
                String.valueOf(collection.getId()), // 使用id转成string而不是purchaseCode
                0, // 创建状态
                1  // 目标状态：审核通过
            );
            
            // 2. 构建通知模板数据
            Map<String, Object> templateData = buildPurchaseCollectionNotificationData(collection);
            
            // 3. 发送通知给采购审核人
            notificationService.sendNotificationToRole(
                NotificationTypeEnum.PURCHASE_AUDIT_PENDING,
                "purchase-auditor", // 采购审核人角色标识
                collection.getOrderCode(),
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
            // 采购审核记录使用id转string，需要根据id查找采购汇总
            Long collectionId = Long.valueOf(auditRecord.getAuditId());
            ErpPurchaseCollection collection = purchaseCollectionService.selectErpPurchaseCollectionById(collectionId);
            if (collection == null) {
                throw new RuntimeException("采购汇总不存在，ID: " + auditRecord.getAuditId());
            }
            
            // 在发送新通知前，将旧通知标记为已读
            notificationService.markNotificationsAsReadByBusiness(String.valueOf(collection.getId()), "PURCHASE_COLLECTION");

            collection.setStatus(Long.valueOf(auditRecord.getToStatus()));
            collection.setAuditStatus(2); // 审核通过
            purchaseCollectionService.applyApprovedStatus(collection);

            // ==================== 自动更新父订单状态 START ====================
            try {
                ErpOrders order = ordersService.selectByOrderCode(collection.getOrderCode());
                if (order != null) {
                    Integer currentStatus = order.getStatus();
                    Integer statusValuePending = ordersService.getStatusValue(OrderStatus.PRODUCTION_SCHEDULE_PENDING.getLabel());
                    Integer statusValueInProduction = ordersService.getStatusValue(OrderStatus.IN_PRODUCTION.getLabel());

                    if (Objects.equals(currentStatus, statusValuePending)) {
                        // 如果订单当前是"待计划"状态，则直接进入"外购中"
                        ordersService.updateOrderStatusAutomatic(order.getId(), OrderStatus.PRODUCTION_PENDING);
                    } else if (Objects.equals(currentStatus, statusValueInProduction)) {
                        // 如果订单当前已在"布产中"，则进入"外购与布产中"
                        ordersService.updateOrderStatusAutomatic(order.getId(), OrderStatus.PRODUCTION_DONE_PURCHASING);
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
                String.valueOf(collection.getId()),
                "PURCHASE_COLLECTION",
                templateData
            );
            
            log.info("采购汇总审核通过流程处理完成，采购汇总ID：{}，审核记录ID：{}", 
                    collection.getId(), auditRecordId);

            // 关闭该采购单其他所有待审核的记录
            closeObsoleteAuditRecords(auditRecord);
            
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
            // 采购审核记录使用id转string，需要根据id查找采购汇总
            Long collectionId = Long.valueOf(auditRecord.getAuditId());
            ErpPurchaseCollection collection = purchaseCollectionService.selectErpPurchaseCollectionById(collectionId);
            if (collection == null) {
                throw new RuntimeException("采购汇总不存在，ID: " + auditRecord.getAuditId());
            }
            
            // 在发送新通知前，将旧通知标记为已读
            notificationService.markNotificationsAsReadByBusiness(String.valueOf(collection.getId()), "PURCHASE_COLLECTION");
            
            collection.setAuditStatus(-1); // 审核被拒绝
            purchaseCollectionService.updateErpPurchaseCollection(collection); // 单独更新审核状态
            
            // 3. 发送通知给PMC部门
            Map<String, Object> templateData = buildPurchaseCollectionNotificationData(collection);
            templateData.put("auditor", auditor);
            templateData.put("rejectReason", auditComment != null ? auditComment : "审核未通过");
            notificationService.sendNotificationToRole(
                NotificationTypeEnum.PURCHASE_REJECTED_TO_PMC,
                "pmc-dept", // 发送给PMC部门
                String.valueOf(collection.getId()),
                "PURCHASE_COLLECTION",
                templateData
            );

            log.info("采购汇总审核拒绝流程处理完成，采购汇总ID：{}，审核记录ID：{}", 
                    collection.getId(), auditRecordId);

            // 关闭该采购单其他所有待审核的记录
            closeObsoleteAuditRecords(auditRecord);
            
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
            notificationService.markNotificationsAsReadByBusiness(String.valueOf(collection.getId()), "PURCHASE_COLLECTION");

            log.info("开始处理采购汇总状态变更审核流程，采购汇总ID：{}，当前状态：{}，目标状态：{}", 
                    collection.getId(), collection.getStatus(), newStatus);
            
            // 1. 创建审核记录（使用采购审核类型）
            ErpAuditRecord auditRecord = createAuditRecord(
                AuditTypeEnum.PURCHASE_AUDIT.getCode(),
                String.valueOf(collection.getId()), // 使用id转成string而不是purchaseCode
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
                String.valueOf(collection.getId()),
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
    public ErpPurchaseCollection getPurchaseCollectionDetail(String collectionId) {
        try {
            // 尝试解析为Long类型，如果失败则按purchaseCode处理
            ErpPurchaseCollection collection = null;
            try {
                Long id = Long.valueOf(collectionId);
                collection = purchaseCollectionService.selectErpPurchaseCollectionById(id);
            } catch (NumberFormatException e) {
                // 如果不是数字，按purchaseCode查询
                collection = purchaseCollectionService.selectErpPurchaseCollectionByPurchaseCode(collectionId);
            }
            
            if (collection == null) {
                log.warn("未找到采购汇总记录，ID或purchaseCode: {}", collectionId);
                return null;
            }
            
            return collection;
            
        } catch (Exception e) {
            log.error("获取采购汇总详情失败，ID或purchaseCode: {}", collectionId, e);
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

    @Override
    @Transactional
    public void closeObsoleteAuditRecords(ErpAuditRecord processedRecord) {
        if (processedRecord == null || processedRecord.getAuditId() == null || processedRecord.getAuditType() == null) {
            return;
        }

        try {
            // 找到除了当前已处理记录之外，所有与该业务对象相关的"待处理"审核
            LambdaUpdateWrapper<ErpAuditRecord> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(ErpAuditRecord::getAuditType, processedRecord.getAuditType())
                   .eq(ErpAuditRecord::getAuditId, processedRecord.getAuditId())
                   .eq(ErpAuditRecord::getConfirm, 0) // 状态为"待处理"
                   .ne(ErpAuditRecord::getId, processedRecord.getId()); // 排除当前记录

            wrapper.set(ErpAuditRecord::getConfirm, 1); // 标记为"已处理"
            wrapper.set(ErpAuditRecord::getAuditor, "system");
            wrapper.set(ErpAuditRecord::getCheckTime, new Date());
            wrapper.set(ErpAuditRecord::getAuditComment, "过时的审核请求");

            int updatedCount = erpAuditRecordMapper.update(null, wrapper);
            if (updatedCount > 0) {
                log.info("成功关闭了 {} 个关于业务 [类型: {}, ID: {}] 的过时审核记录。", 
                        updatedCount, processedRecord.getAuditType(), processedRecord.getAuditId());
            }
        } catch (Exception e) {
            log.error("关闭过时审核记录时发生错误，业务 [类型: {}, ID: {}]", 
                    processedRecord.getAuditType(), processedRecord.getAuditId(), e);
            // 此处不向上抛出异常，以免影响主审核流程
        }
    }

    // ==================== 包装清单/分包审核业务方法 ====================

    @Override
    @Transactional
    public void handlePackagingListCreated(ErpPackagingList packagingList) {
        try {
            // 先将该包装清单之前的通知标记为已读
            notificationService.markNotificationsAsReadByBusiness(packagingList.getOrderCode(), "PACKAGING");
            log.info("开始处理包装清单创建后流程，ID：{}", packagingList.getId());
            ErpAuditRecord auditRecord = createAuditRecord(
                AuditTypeEnum.SUBCONTRACT_AUDIT.getCode(),
                String.valueOf(packagingList.getId()), // 使用ID转String
                null, // 新创建无前置状态
                1 // 目标状态：审核通过
            );

            // 发送通知给分包审核员
            Map<String, Object> templateData = buildPackagingListNotificationData(packagingList);
            notificationService.sendNotificationToRole(
                NotificationTypeEnum.SUBCONTRACT_AUDIT_PENDING,
                AuditTypeEnum.SUBCONTRACT_AUDIT.getRoleKey(),
                packagingList.getOrderCode(),
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
            ErpPackagingList packagingList = packagingListService.selectErpPackagingListById(Long.parseLong(auditRecord.getAuditId()));
            if (packagingList == null) throw new ServiceException("包装清单不存在，ID: " + auditRecord.getAuditId());

            // 在发送新通知前，将旧通知标记为已读
            notificationService.markNotificationsAsReadByBusiness(packagingList.getOrderCode(), "PACKAGING");

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
                packagingList.getOrderCode(),
                "PACKAGING",
                templateData
            );

            log.info("包装清单审核通过流程处理完成，ID：{}", packagingList.getId());

            // 关闭该包装清单其他所有待审核的记录
            closeObsoleteAuditRecords(auditRecord);

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
            ErpPackagingList packagingList = packagingListService.selectErpPackagingListById(Long.parseLong(auditRecord.getAuditId()));
            if (packagingList == null) throw new ServiceException("包装清单不存在，ID: " + auditRecord.getAuditId());

            // 在发送新通知前，将旧通知标记为已读
            notificationService.markNotificationsAsReadByBusiness(packagingList.getOrderCode(), "PACKAGING");

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
                packagingList.getOrderCode(),
                "PACKAGING",
                templateData
            );

            log.info("包装清单审核拒绝流程处理完成，ID：{}", packagingList.getId());

            // 关闭该包装清单其他所有待审核的记录
            closeObsoleteAuditRecords(auditRecord);
            
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
            notificationService.markNotificationsAsReadByBusiness(packagingList.getOrderCode(), "PACKAGING");

            log.info("开始处理包装清单状态变更审核流程，ID：{}，当前状态：{}，目标状态：{}",
                    packagingList.getId(), packagingList.getStatus(), newStatus);
            createAuditRecord(
                AuditTypeEnum.SUBCONTRACT_AUDIT.getCode(),
                String.valueOf(packagingList.getId()), // 使用ID转String
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
                packagingList.getOrderCode(),
                "PACKAGING",
                templateData
            );
        } catch (Exception e) {
            log.error("处理包装清单状态变更审核流程失败，ID：{}", packagingList.getId(), e);
            throw new RuntimeException("处理包装清单状态变更审核流程失败", e);
        }
    }

    @Override
    public ErpPackagingList getPackagingListDetail(String packagingListId) {
        try {
            Long id = Long.parseLong(packagingListId);
            return packagingListService.selectErpPackagingListById(id);
        } catch (NumberFormatException e) {
            log.error("包装清单ID格式错误: " + packagingListId);
            return null;
        }
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
    private void updateAuditStatus(Integer auditType, String businessId, Integer auditStatus) throws IOException {
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
                // 订单审核记录使用InnerID，需要根据InnerID查找订单并更新审核状态
                try {
                    ErpOrders order = ordersService.selectByOrderCode(businessId);
                    if (order != null) {
                        order.setAuditStatus(auditStatus);
                        ordersService.updateErpOrders(order);
                    } else {
                        log.error("订单不存在，InnerID: " + businessId);
                    }
                } catch (Exception e) {
                    log.error("更新订单审核状态失败，InnerID: " + businessId, e);
                }
                break;
            case PRODUCTION_AUDIT:
                // 布产完成审核（使用orderCode），需要更新该订单所有布产记录的审核状态
                try {
                    LambdaQueryWrapper<ErpProductionSchedule> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(ErpProductionSchedule::getOrderCode, businessId);
                    List<ErpProductionSchedule> schedules = productionScheduleService.list(wrapper);
                    
                    for (ErpProductionSchedule schedule : schedules) {
                        schedule.setAuditStatus(auditStatus);
                        productionScheduleService.updateById(schedule);
                    }
                    
                    log.debug("布产完成审核，已更新订单 {} 的所有布产记录审核状态为: {}", businessId, auditStatus);
                } catch (Exception e) {
                    log.error("更新布产计划审核状态失败，orderCode: " + businessId, e);
                }
                break;
            case PURCHASE_AUDIT:
                // 采购审核记录使用id转string，需要根据id查找采购汇总并更新审核状态
                try {
                    Long collectionId = Long.valueOf(businessId);
                    ErpPurchaseCollection collection = purchaseCollectionService.selectErpPurchaseCollectionById(collectionId);
                    if (collection != null) {
                        collection.setAuditStatus(auditStatus);
                        purchaseCollectionService.updateErpPurchaseCollection(collection);
                    } else {
                        log.error("采购汇总不存在，ID: " + businessId);
                    }
                } catch (Exception e) {
                    log.error("更新采购汇总审核状态失败，ID: " + businessId, e);
                }
                break;
            case SUBCONTRACT_AUDIT:
                // 分包完成审核（使用orderCode），需要更新该订单所有分包记录的审核状态
                try {
                    LambdaQueryWrapper<ErpPackagingList> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(ErpPackagingList::getOrderCode, businessId);
                    List<ErpPackagingList> packagingLists = erpPackagingListMapper.selectList(wrapper);
                    
                    for (ErpPackagingList packagingList : packagingLists) {
                        packagingList.setAuditStatus(auditStatus);
                        packagingListService.updateErpPackagingList(packagingList);
                    }
                    
                    log.debug("分包完成审核，已更新订单 {} 的所有分包记录审核状态为: {}", businessId, auditStatus);
                } catch (Exception e) {
                    log.error("更新分包记录审核状态失败，orderCode: " + businessId, e);
                }
                break;
            default:
                log.error("无法修改对应记录审核状态：" + typeEnum);
                break;
        }
    }

    /**
     * 布产完成审核处理
     */
    @Override
    @Transactional
    public void handleProductionScheduleCompleteAudit(String orderCode) {
        try {
            log.info("开始处理布产完成审核流程，订单号：{}", orderCode);
            
            // 1. 创建审核记录
            ErpAuditRecord auditRecord = createAuditRecord(
                AuditTypeEnum.PRODUCTION_AUDIT.getCode(),
                orderCode, // 使用订单ID作为审核ID
                0, // 前状态：待审核
                1  // 目标状态：审核通过
            );
            
            // 2. 构建通知模板数据
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("orderCode", orderCode);
            
            // 3. 发送通知给布产审核人
            notificationService.sendNotificationToRole(
                NotificationTypeEnum.PRODUCTION_COMPLETE_AUDIT_PENDING,
                "production-auditor", // 布产审核人角色标识
                orderCode,
                "PRODUCTION_COMPLETE",
                templateData
            );
            
            log.info("布产完成审核流程处理完成，订单号：{}，审核记录ID：{}", 
                    orderCode, auditRecord.getId());
            
        } catch (Exception e) {
            log.error("处理布产完成审核流程失败，订单号：{}", orderCode, e);
            throw new RuntimeException("处理布产完成审核流程失败", e);
        }
    }
    
    /**
     * 布产完成审核通过处理
     */
    @Override
    @Transactional
    public void handleProductionScheduleCompleteApproved(Long auditRecordId, String auditor, String auditComment) {
        try {
            log.info("开始处理布产完成审核通过流程，审核记录ID：{}", auditRecordId);
            
            // 1. 获取审核记录信息
            ErpAuditRecord queryRecord = new ErpAuditRecord();
            queryRecord.setId(auditRecordId);
            List<ErpAuditRecord> records = selectAuditRecords(queryRecord, null, null, null, null);
            if (records.isEmpty()) {
                throw new RuntimeException("审核记录不存在");
            }
            ErpAuditRecord auditRecord = records.get(0);
            
            // 2 反向查找该订单的所有布产记录，标记为审核通过
            LambdaQueryWrapper<ErpProductionSchedule> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ErpProductionSchedule::getOrderCode, auditRecord.getAuditId());
            List<ErpProductionSchedule> schedules = productionScheduleService.list(wrapper);
            
            for (ErpProductionSchedule schedule : schedules) {
                schedule.setStatus(1L); // 标记为审核通过
                schedule.setAuditStatus(2); // 标记审核状态为通过
                productionScheduleService.applyApprovedStatus(schedule);
            }
            
            // 3. 执行实际的布产完成逻辑
            productionScheduleService.executeMarkAllScheduled(auditRecord.getAuditId());
            
            // 4. 构建通知模板数据
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("orderCode", auditRecord.getAuditId());
            templateData.put("auditor", auditor);
            templateData.put("auditComment", auditComment != null ? auditComment : "审核通过");
            
            // 5. 发送通知给注塑部
            notificationService.sendNotificationToRole(
                NotificationTypeEnum.PRODUCTION_COMPLETE_AUDIT_APPROVED,
                "injectionmolding-dept", // 注塑部角色标识
                auditRecord.getAuditId(),
                "PRODUCTION_COMPLETE",
                templateData
            );
            
            log.info("布产完成审核通过流程处理完成，审核记录ID：{}", auditRecordId);
            
        } catch (Exception e) {
            log.error("处理布产完成审核通过流程失败，审核记录ID：{}", auditRecordId, e);
            throw new RuntimeException("处理布产完成审核通过流程失败", e);
        }
    }
    
    /**
     * 布产完成审核拒绝处理
     */
    @Override
    @Transactional
    public void handleProductionScheduleCompleteRejected(Long auditRecordId, String auditor, String auditComment) {
        try {
            log.info("开始处理布产完成审核拒绝流程，审核记录ID：{}", auditRecordId);
            
            // 1. 获取审核记录信息
            ErpAuditRecord queryRecord = new ErpAuditRecord();
            queryRecord.setId(auditRecordId);
            List<ErpAuditRecord> records = selectAuditRecords(queryRecord, null, null, null, null);
            if (records.isEmpty()) {
                throw new RuntimeException("审核记录不存在");
            }
            ErpAuditRecord auditRecord = records.get(0);
            
            // 2. 构建通知模板数据
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("orderCode", auditRecord.getAuditId());
            templateData.put("auditor", auditor);
            templateData.put("rejectReason", auditComment != null ? auditComment : "审核未通过");
            
            // 3. 发送通知给PMC部门
            notificationService.sendNotificationToRole(
                NotificationTypeEnum.PRODUCTION_COMPLETE_AUDIT_REJECTED,
                "pmc-dept", // PMC部门角色标识
                auditRecord.getAuditId(),
                "PRODUCTION_COMPLETE",
                templateData
            );
            
            log.info("布产完成审核拒绝流程处理完成，审核记录ID：{}", auditRecordId);
            
        } catch (Exception e) {
            log.error("处理布产完成审核拒绝流程失败，审核记录ID：{}", auditRecordId, e);
            throw new RuntimeException("处理布产完成审核拒绝流程失败", e);
        }
    }
    
    /**
     * 分包完成审核处理
     */
    @Override
    @Transactional
    public void handlePackagingListCompleteAudit(String orderCode) {
        try {
            log.info("开始处理分包完成审核流程，订单号：{}", orderCode);
            
            // 1. 创建审核记录
            ErpAuditRecord auditRecord = createAuditRecord(
                AuditTypeEnum.SUBCONTRACT_AUDIT.getCode(),
                orderCode, // 使用订单号作为审核ID
                0, // 前状态：待审核
                1  // 目标状态：审核通过
            );
            
            // 2. 构建通知模板数据
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("orderCode", orderCode);
            
            // 3. 发送通知给分包审核人
            notificationService.sendNotificationToRole(
                NotificationTypeEnum.SUBCONTRACT_COMPLETE_AUDIT_PENDING,
                "subcontract-auditor", // 分包审核人角色标识
                auditRecord.getAuditId(),
                "SUBCONTRACT_COMPLETE",
                templateData
            );
            
            log.info("分包完成审核流程处理完成，订单号：{}，审核记录ID：{}", 
                    orderCode, auditRecord.getId());
            
        } catch (Exception e) {
            log.error("处理分包完成审核流程失败，订单号：{}", orderCode, e);
            throw new RuntimeException("处理分包完成审核流程失败", e);
        }
    }
    
    /**
     * 分包完成审核通过处理
     */
    @Override
    @Transactional
    public void handlePackagingListCompleteApproved(Long auditRecordId, String auditor, String auditComment) {
        try {
            log.info("开始处理分包完成审核通过流程，审核记录ID：{}", auditRecordId);
            
            // 1. 获取审核记录信息
            ErpAuditRecord queryRecord = new ErpAuditRecord();
            queryRecord.setId(auditRecordId);
            List<ErpAuditRecord> records = selectAuditRecords(queryRecord, null, null, null, null);
            if (records.isEmpty()) {
                throw new RuntimeException("审核记录不存在");
            }
            ErpAuditRecord auditRecord = records.get(0);
            
            // 2. 反向查找该订单的所有分包记录，标记为审核通过
            LambdaQueryWrapper<ErpPackagingList> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ErpPackagingList::getOrderCode, auditRecord.getAuditId());
            List<ErpPackagingList> packagingLists = erpPackagingListMapper.selectList(wrapper);
            
            for (ErpPackagingList packagingList : packagingLists) {
                packagingList.setDone(true); // 标记为完成
                packagingList.setAuditStatus(2); // 标记审核状态为通过
                packagingListService.updateErpPackagingList(packagingList);
            }
            
            // 3. 执行实际的分包完成逻辑
            packagingListService.executeMarkPackagingDone(auditRecord.getAuditId());
            
            // 4. 构建通知模板数据
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("orderCode", auditRecord.getAuditId());
            templateData.put("auditor", auditor);
            templateData.put("auditComment", auditComment != null ? auditComment : "审核通过");
            
            // 5. 发送通知给仓库部门
            notificationService.sendNotificationToRole(
                NotificationTypeEnum.SUBCONTRACT_COMPLETE_AUDIT_APPROVED,
                "warehouse", // 仓库部门角色标识
                auditRecord.getAuditId(),
                "SUBCONTRACT_COMPLETE",
                templateData
            );
            
            log.info("分包完成审核通过流程处理完成，审核记录ID：{}", auditRecordId);
            
        } catch (Exception e) {
            log.error("处理分包完成审核通过流程失败，审核记录ID：{}", auditRecordId, e);
            throw new RuntimeException("处理分包完成审核通过流程失败", e);
        }
    }
    
    /**
     * 分包完成审核拒绝处理
     */
    @Override
    @Transactional
    public void handlePackagingListCompleteRejected(Long auditRecordId, String auditor, String auditComment) {
        try {
            log.info("开始处理分包完成审核拒绝流程，审核记录ID：{}", auditRecordId);
            
            // 1. 获取审核记录信息
            ErpAuditRecord queryRecord = new ErpAuditRecord();
            queryRecord.setId(auditRecordId);
            List<ErpAuditRecord> records = selectAuditRecords(queryRecord, null, null, null, null);
            if (records.isEmpty()) {
                throw new RuntimeException("审核记录不存在");
            }
            ErpAuditRecord auditRecord = records.get(0);
            
            // 2. 构建通知模板数据
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("orderCode", auditRecord.getAuditId());
            templateData.put("auditor", auditor);
            templateData.put("rejectReason", auditComment != null ? auditComment : "审核未通过");
            
            // 3. 发送通知给PMC部门
            notificationService.sendNotificationToRole(
                NotificationTypeEnum.SUBCONTRACT_COMPLETE_AUDIT_REJECTED,
                "pmc-dept", // PMC部门角色标识
                auditRecord.getAuditId(),
                "SUBCONTRACT_COMPLETE",
                templateData
            );
            
            log.info("分包完成审核拒绝流程处理完成，审核记录ID：{}", auditRecordId);
            
        } catch (Exception e) {
            log.error("处理分包完成审核拒绝流程失败，审核记录ID：{}", auditRecordId, e);
            throw new RuntimeException("处理分包完成审核拒绝流程失败", e);
        }
    }
} 