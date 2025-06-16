package com.ruoyi.web.service;

import com.ruoyi.web.domain.ErpAuditRecord;

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
     * 
     * @param id 审核记录主键
     * @return 审核记录
     */
    ErpAuditRecord selectErpAuditRecordById(Long id);

    /**
     * 查询审核记录列表
     * 
     * @param erpAuditRecord 审核记录
     * @return 审核记录集合
     */
    List<ErpAuditRecord> selectErpAuditRecordList(ErpAuditRecord erpAuditRecord);

    /**
     * 新增审核记录
     * 
     * @param erpAuditRecord 审核记录
     * @return 结果
     */
    int insertErpAuditRecord(ErpAuditRecord erpAuditRecord);

    /**
     * 修改审核记录
     * 
     * @param erpAuditRecord 审核记录
     * @return 结果
     */
    int updateErpAuditRecord(ErpAuditRecord erpAuditRecord);

    /**
     * 批量删除审核记录
     * 
     * @param ids 需要删除的审核记录主键集合
     * @return 结果
     */
    int deleteErpAuditRecordByIds(Long[] ids);

    /**
     * 删除审核记录信息
     * 
     * @param id 审核记录主键
     * @return 结果
     */
    int deleteErpAuditRecordById(Long id);

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
     * 审核通过
     * 
     * @param auditRecordId 审核记录ID
     * @param auditor 审核人
     * @param auditComment 审核意见
     * @return 结果
     */
    int approveAudit(Long auditRecordId, String auditor, String auditComment);

    /**
     * 审核拒绝
     * 
     * @param auditRecordId 审核记录ID
     * @param auditor 审核人
     * @param auditComment 审核意见
     * @return 结果
     */
    int rejectAudit(Long auditRecordId, String auditor, String auditComment);

    /**
     * 根据审核类型和对象ID查询审核记录
     * 
     * @param auditType 审核类型
     * @param auditId 审核对象ID
     * @return 审核记录列表
     */
    List<ErpAuditRecord> selectByAuditTypeAndId(Integer auditType, Long auditId);

    /**
     * 查询待审核记录列表
     * 
     * @param auditType 审核类型
     * @return 待审核记录列表
     */
    List<ErpAuditRecord> selectPendingAuditRecords(Integer auditType);

    /**
     * 查询用户的待审核记录数量
     * 
     * @param auditor 审核人
     * @param auditType 审核类型
     * @return 待审核记录数量
     */
    int countPendingAuditsByAuditor(String auditor, Integer auditType);

    /**
     * 批量审核
     * 
     * @param ids 审核记录ID列表
     * @param confirm 审核状态（1:通过 -1:拒绝）
     * @param auditor 审核人
     * @param auditComment 审核意见
     * @return 结果
     */
    int batchAudit(List<Long> ids, Integer confirm, String auditor, String auditComment);
} 