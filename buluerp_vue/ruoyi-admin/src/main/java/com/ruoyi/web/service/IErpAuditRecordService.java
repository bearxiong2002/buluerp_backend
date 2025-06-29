package com.ruoyi.web.service;

import com.ruoyi.web.domain.ErpAuditRecord;
import com.ruoyi.web.domain.ErpOrders;
import com.ruoyi.web.domain.ErpProductionSchedule;
import com.ruoyi.web.domain.ErpPurchaseCollection;

import java.util.List;

/**
 * 审核记录Service接口
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
public interface IErpAuditRecordService 
{
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
    List<ErpAuditRecord> selectAuditRecords(ErpAuditRecord erpAuditRecord, 
                                           Integer auditType, 
                                           Long auditId, 
                                           Boolean pendingOnly, 
                                           String auditor);

    /**
     * 审核处理
     * 支持单个和批量审核
     * 
     * @param auditRecordIds 审核记录ID列表
     * @param confirm 审核状态（1:通过 -1:拒绝）
     * @param auditor 审核人
     * @param auditComment 审核意见
     * @return 处理结果
     */
    int processAudit(List<Long> auditRecordIds, Integer confirm, String auditor, String auditComment);

    /**
     * 创建审核记录
     * 
     * @param auditType 审核类型
     * @param auditId 审核对象ID
     * @param preStatus 审核前状态
     * @param toStatus 目标状态
     * @return 审核记录
     */
    ErpAuditRecord createAuditRecord(Integer auditType, Long auditId, Integer preStatus, Integer toStatus);

    // ==================== 订单审核业务方法 ====================

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

    /**
     * 订单状态变更审核处理（创建审核记录并发送通知）
     * 
     * @param order 订单信息
     * @param newStatus 新状态
     */
    void handleOrderStatusChange(ErpOrders order, Integer newStatus);

    // ==================== 布产审核业务方法 ====================

    /**
     * 布产计划创建后处理（创建审核记录并发送通知）
     * 
     * @param schedule 布产计划信息
     */
    void handleProductionScheduleCreated(ErpProductionSchedule schedule);

    /**
     * 布产计划审核通过处理（更新状态并发送通知）
     * 
     * @param auditRecordId 审核记录ID
     * @param auditor 审核人
     * @param auditComment 审核意见
     */
    void handleProductionScheduleApproved(Long auditRecordId, String auditor, String auditComment);

    /**
     * 布产计划审核拒绝处理（更新状态并发送通知）
     * 
     * @param auditRecordId 审核记录ID
     * @param auditor 审核人
     * @param auditComment 审核意见
     */
    void handleProductionScheduleRejected(Long auditRecordId, String auditor, String auditComment);

    /**
     * 布产计划状态变更审核处理
     * 
     * @param schedule 布产计划信息
     * @param newStatus 新状态
     */
    void handleProductionScheduleStatusChange(ErpProductionSchedule schedule, Integer newStatus);

    /**
     * 获取布产计划详情
     * 
     * @param scheduleId 布产计划ID
     * @return 布产计划详情
     */
    ErpProductionSchedule getProductionScheduleDetail(Long scheduleId);

    // ==================== 采购审核业务方法 ====================

    /**
     * 采购汇总创建后处理（创建审核记录并发送通知）
     * 
     * @param collection 采购汇总信息
     */
    void handlePurchaseCollectionCreated(ErpPurchaseCollection collection);

    /**
     * 采购汇总审核通过处理（更新状态并发送通知）
     * 
     * @param auditRecordId 审核记录ID
     * @param auditor 审核人
     * @param auditComment 审核意见
     */
    void handlePurchaseCollectionApproved(Long auditRecordId, String auditor, String auditComment);

    /**
     * 采购汇总审核拒绝处理（更新状态并发送通知）
     * 
     * @param auditRecordId 审核记录ID
     * @param auditor 审核人
     * @param auditComment 审核意见
     */
    void handlePurchaseCollectionRejected(Long auditRecordId, String auditor, String auditComment);

    /**
     * 采购汇总状态变更审核处理
     * 
     * @param collection 采购汇总信息
     * @param newStatus 新状态
     */
    void handlePurchaseCollectionStatusChange(ErpPurchaseCollection collection, Integer newStatus);

    /**
     * 获取采购汇总详情
     * 
     * @param collectionId 采购汇总ID
     * @return 采购汇总详情
     */
    ErpPurchaseCollection getPurchaseCollectionDetail(Long collectionId);
} 