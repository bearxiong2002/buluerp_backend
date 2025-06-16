package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.web.domain.ErpAuditRecord;
import com.ruoyi.web.mapper.ErpAuditRecordMapper;
import com.ruoyi.web.service.IErpAuditRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

    /**
     * 查询审核记录
     * 
     * @param id 审核记录主键
     * @return 审核记录
     */
    @Override
    public ErpAuditRecord selectErpAuditRecordById(Long id)
    {
        return erpAuditRecordMapper.selectById(id);
    }

    /**
     * 查询审核记录列表
     * 
     * @param erpAuditRecord 审核记录
     * @return 审核记录
     */
    @Override
    public List<ErpAuditRecord> selectErpAuditRecordList(ErpAuditRecord erpAuditRecord)
    {
        return erpAuditRecordMapper.selectErpAuditRecordList(erpAuditRecord);
    }

    /**
     * 新增审核记录
     * 
     * @param erpAuditRecord 审核记录
     * @return 结果
     */
    @Override
    public int insertErpAuditRecord(ErpAuditRecord erpAuditRecord)
    {
        erpAuditRecord.setCreateTime(DateUtils.getNowDate());
        return erpAuditRecordMapper.insert(erpAuditRecord);
    }

    /**
     * 修改审核记录
     * 
     * @param erpAuditRecord 审核记录
     * @return 结果
     */
    @Override
    public int updateErpAuditRecord(ErpAuditRecord erpAuditRecord)
    {
        return erpAuditRecordMapper.updateById(erpAuditRecord);
    }

    /**
     * 批量删除审核记录
     * 
     * @param ids 需要删除的审核记录主键
     * @return 结果
     */
    @Override
    public int deleteErpAuditRecordByIds(Long[] ids)
    {
        return erpAuditRecordMapper.deleteBatchIds(Arrays.asList(ids));
    }

    /**
     * 删除审核记录信息
     * 
     * @param id 审核记录主键
     * @return 结果
     */
    @Override
    public int deleteErpAuditRecordById(Long id)
    {
        return erpAuditRecordMapper.deleteById(id);
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
            ErpAuditRecord auditRecord = new ErpAuditRecord();
            auditRecord.setAuditType(auditType);
            auditRecord.setAuditId(auditId);
            auditRecord.setPreStatus(preStatus);
            auditRecord.setToStatus(toStatus);
            auditRecord.setConfirm(0); // 未审核
            auditRecord.setCreateTime(DateUtils.getNowDate());
            
            // 设置创建人信息
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null) {
                auditRecord.setCreateBy(loginUser.getUsername());
            }
            
            erpAuditRecordMapper.insert(auditRecord);
            log.info("创建审核记录成功，审核类型：{}，审核对象ID：{}，记录ID：{}", 
                    auditType, auditId, auditRecord.getId());
            
            return auditRecord;
        } catch (Exception e) {
            log.error("创建审核记录失败，审核类型：{}，审核对象ID：{}", auditType, auditId, e);
            throw new RuntimeException("创建审核记录失败", e);
        }
    }

    /**
     * 审核通过
     * 
     * @param auditRecordId 审核记录ID
     * @param auditor 审核人
     * @param auditComment 审核意见
     * @return 结果
     */
    @Override
    @Transactional
    public int approveAudit(Long auditRecordId, String auditor, String auditComment)
    {
        try {
            ErpAuditRecord auditRecord = new ErpAuditRecord();
            auditRecord.setId(auditRecordId);
            auditRecord.setConfirm(1); // 审核通过
            auditRecord.setAuditor(auditor);
            auditRecord.setCheckTime(new Date());
            auditRecord.setAuditComment(auditComment);
            
            int result = erpAuditRecordMapper.updateById(auditRecord);
            log.info("审核通过成功，审核记录ID：{}，审核人：{}", auditRecordId, auditor);
            
            return result;
        } catch (Exception e) {
            log.error("审核通过失败，审核记录ID：{}，审核人：{}", auditRecordId, auditor, e);
            throw new RuntimeException("审核通过失败", e);
        }
    }

    /**
     * 审核拒绝
     * 
     * @param auditRecordId 审核记录ID
     * @param auditor 审核人
     * @param auditComment 审核意见
     * @return 结果
     */
    @Override
    @Transactional
    public int rejectAudit(Long auditRecordId, String auditor, String auditComment)
    {
        try {
            ErpAuditRecord auditRecord = new ErpAuditRecord();
            auditRecord.setId(auditRecordId);
            auditRecord.setConfirm(-1); // 审核拒绝
            auditRecord.setAuditor(auditor);
            auditRecord.setCheckTime(new Date());
            auditRecord.setAuditComment(auditComment);
            
            int result = erpAuditRecordMapper.updateById(auditRecord);
            log.info("审核拒绝成功，审核记录ID：{}，审核人：{}", auditRecordId, auditor);
            
            return result;
        } catch (Exception e) {
            log.error("审核拒绝失败，审核记录ID：{}，审核人：{}", auditRecordId, auditor, e);
            throw new RuntimeException("审核拒绝失败", e);
        }
    }

    /**
     * 根据审核类型和对象ID查询审核记录
     * 
     * @param auditType 审核类型
     * @param auditId 审核对象ID
     * @return 审核记录列表
     */
    @Override
    public List<ErpAuditRecord> selectByAuditTypeAndId(Integer auditType, Long auditId)
    {
        return erpAuditRecordMapper.selectByAuditTypeAndId(auditType, auditId);
    }

    /**
     * 查询待审核记录列表
     * 
     * @param auditType 审核类型
     * @return 待审核记录列表
     */
    @Override
    public List<ErpAuditRecord> selectPendingAuditRecords(Integer auditType)
    {
        return erpAuditRecordMapper.selectPendingAuditRecords(auditType);
    }

    /**
     * 查询用户的待审核记录数量
     * 
     * @param auditor 审核人
     * @param auditType 审核类型
     * @return 待审核记录数量
     */
    @Override
    public int countPendingAuditsByAuditor(String auditor, Integer auditType)
    {
        return erpAuditRecordMapper.countPendingAuditsByAuditor(auditor, auditType);
    }

    /**
     * 批量审核
     * 
     * @param ids 审核记录ID列表
     * @param confirm 审核状态（1:通过 -1:拒绝）
     * @param auditor 审核人
     * @param auditComment 审核意见
     * @return 结果
     */
    @Override
    @Transactional
    public int batchAudit(List<Long> ids, Integer confirm, String auditor, String auditComment)
    {
        try {
            int result = erpAuditRecordMapper.batchUpdateAuditStatus(ids, confirm, auditor);
            log.info("批量审核完成，审核记录数：{}，审核状态：{}，审核人：{}", 
                    ids.size(), confirm, auditor);
            
            return result;
        } catch (Exception e) {
            log.error("批量审核失败，审核记录数：{}，审核状态：{}，审核人：{}", 
                    ids.size(), confirm, auditor, e);
            throw new RuntimeException("批量审核失败", e);
        }
    }
} 