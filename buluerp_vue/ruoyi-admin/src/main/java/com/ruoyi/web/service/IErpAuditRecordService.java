package com.ruoyi.web.service;

import com.ruoyi.web.domain.ErpAuditRecord;
import com.ruoyi.web.domain.ErpOrders;
import com.ruoyi.web.domain.ErpProductionSchedule;
import com.ruoyi.web.domain.ErpPurchaseCollection;
import com.ruoyi.web.domain.ErpPackagingList;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
     * 5. 支持时间范围查询（创建时间和审核时间）
     * 
     * @param erpAuditRecord 审核记录查询条件
     * @param auditType 审核类型（可选）
     * @param auditId 审核对象ID（可选）
     * @param pendingOnly 是否只查询待审核记录（可选）
     * @param dateParams 时间范围参数，支持 createTimeStart, createTimeEnd, checkTimeStart, checkTimeEnd
     * @return 审核记录列表
     */
    List<ErpAuditRecord> selectAuditRecords(ErpAuditRecord erpAuditRecord, 
                                           Integer auditType, 
                                           Long auditId, 
                                           Boolean pendingOnly, 
                                           Map<String, Date> dateParams);

    /**
     * 根据用户角色查询对应的待审核请求
     * 自动识别用户角色并返回相应的待审核列表
     * 
     * @return 用户角色对应的待审核记录列表
     */
    List<ErpAuditRecord> getPendingAuditsByUserRole();

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
    boolean processAuditByRecordId(Long auditRecordId, Integer accept, String auditor, String auditComment);

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
    int processBatchAuditByRecordIds(List<Long> auditRecordIds, Integer accept, String auditor, String auditComment);

    /**
     * 批量删除审核记录
     * 
     * @param ids 需要删除的审核记录ID列表
     * @return 删除的记录数
     */
    int deleteAuditRecordByIds(List<Integer> ids);

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

    /**
     * 关闭指定业务对象的过时审核记录
     *
     * @param processedRecord 已处理的审核记录，用于获取业务ID和类型
     */
    void closeObsoleteAuditRecords(ErpAuditRecord processedRecord);

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
     * @param updatedOrder 包含最新修改数据的订单信息
     * @param oldStatus 变更前的状态
     * @param newStatus 目标新状态
     */
    void handleOrderStatusChange(ErpOrders updatedOrder, Integer oldStatus, Integer newStatus);

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

    /**
     * 处理待审核对象被删除的逻辑
     * 将相关的待审核记录标记为已处理
     *
     * @param auditType 审核类型
     * @param auditId   被删除的审核对象ID
     */
    void handleAuditableEntityDeleted(Integer auditType, Long auditId);

    // ==================== 包装清单/分包审核业务方法 ====================

    /**
     * 包装清单创建后处理（创建审核记录并发送通知）
     *
     * @param packagingList 包装清单信息
     */
    void handlePackagingListCreated(ErpPackagingList packagingList);

    /**
     * 包装清单审核通过处理
     *
     * @param auditRecordId 审核记录ID
     * @param auditor       审核人
     * @param auditComment  审核意见
     */
    void handlePackagingListApproved(Long auditRecordId, String auditor, String auditComment);

    /**
     * 包装清单审核拒绝处理
     *
     * @param auditRecordId 审核记录ID
     * @param auditor       审核人
     * @param auditComment  审核意见
     */
    void handlePackagingListRejected(Long auditRecordId, String auditor, String auditComment);

    /**
     * 包装清单状态变更审核处理
     *
     * @param packagingList 包装清单信息
     * @param newStatus     新状态
     */
    void handlePackagingListStatusChange(ErpPackagingList packagingList, Integer newStatus);

    /**
     * 获取包装清单详情
     *
     * @param packagingListId 包装清单ID
     * @return 包装清单详情
     */
    ErpPackagingList getPackagingListDetail(Long packagingListId);
} 