package com.ruoyi.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.web.domain.ErpAuditRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 审核记录Mapper接口
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
@Mapper
public interface ErpAuditRecordMapper extends BaseMapper<ErpAuditRecord>
{
    /**
     * 查询审核记录列表
     * 
     * @param erpAuditRecord 审核记录
     * @return 审核记录集合
     */
    List<ErpAuditRecord> selectErpAuditRecordList(ErpAuditRecord erpAuditRecord);

    /**
     * 根据审核类型和审核对象ID查询审核记录
     * 
     * @param auditType 审核类型
     * @param auditId 审核对象ID
     * @return 审核记录列表
     */
    List<ErpAuditRecord> selectByAuditTypeAndId(@Param("auditType") Integer auditType, 
                                               @Param("auditId") Long auditId);

    /**
     * 查询待审核记录列表
     * 
     * @param auditType 审核类型
     * @return 待审核记录列表
     */
    List<ErpAuditRecord> selectPendingAuditRecords(@Param("auditType") Integer auditType);

    /**
     * 查询用户的待审核记录数量
     * 
     * @param auditor 审核人
     * @param auditType 审核类型
     * @return 待审核记录数量
     */
    int countPendingAuditsByAuditor(@Param("auditor") String auditor, 
                                   @Param("auditType") Integer auditType);

    /**
     * 批量更新审核状态
     * 
     * @param ids 审核记录ID列表
     * @param confirm 审核状态
     * @param auditor 审核人
     * @return 更新记录数
     */
    int batchUpdateAuditStatus(@Param("ids") List<Long> ids, 
                              @Param("confirm") Integer confirm, 
                              @Param("auditor") String auditor);
} 