package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.web.domain.ErpAuditSwitch;
import com.ruoyi.web.mapper.ErpAuditSwitchMapper;
import com.ruoyi.web.service.IErpAuditSwitchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 审核开关Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
@Service
public class ErpAuditSwitchServiceImpl extends ServiceImpl<ErpAuditSwitchMapper, ErpAuditSwitch> implements IErpAuditSwitchService
{
    private static final Logger log = LoggerFactory.getLogger(ErpAuditSwitchServiceImpl.class);

    @Autowired
    private ErpAuditSwitchMapper erpAuditSwitchMapper;

    /**
     * 查询审核开关列表
     * 
     * @param erpAuditSwitch 审核开关
     * @return 审核开关
     */
    @Override
    public List<ErpAuditSwitch> selectErpAuditSwitchList(ErpAuditSwitch erpAuditSwitch)
    {
        LambdaQueryWrapper<ErpAuditSwitch> wrapper = new LambdaQueryWrapper<>();
        
        if (erpAuditSwitch.getAuditType() != null) {
            wrapper.eq(ErpAuditSwitch::getAuditType, erpAuditSwitch.getAuditType());
        }
        
        if (erpAuditSwitch.getStatus() != null) {
            wrapper.eq(ErpAuditSwitch::getStatus, erpAuditSwitch.getStatus());
        }
        
        return erpAuditSwitchMapper.selectList(wrapper);
    }

    /**
     * 根据审核类型查询审核开关
     * 
     * @param auditType 审核类型
     * @return 审核开关
     */
    @Override
    public ErpAuditSwitch selectByAuditType(Integer auditType)
    {
        return erpAuditSwitchMapper.selectByAuditType(auditType);
    }

    /**
     * 检查指定审核类型是否启用审核
     * 
     * @param auditType 审核类型
     * @return true=启用审核，false=关闭审核
     */
    @Override
    public boolean isAuditEnabled(Integer auditType)
    {
        try {
            ErpAuditSwitch auditSwitch = selectByAuditType(auditType);
            if (auditSwitch == null) {
                // 如果没有配置记录，默认启用审核
                log.warn("审核类型 {} 未配置审核开关，默认启用审核", auditType);
                return true;
            }
            return Integer.valueOf(1).equals(auditSwitch.getStatus());
        } catch (Exception e) {
            log.error("检查审核开关状态失败，审核类型：{}", auditType, e);
            // 异常情况下默认启用审核，确保安全
            return true;
        }
    }

    /**
     * 设置审核开关状态
     * 
     * @param auditType 审核类型
     * @param status 状态 0=关闭审核，1=开启审核
     * @return 结果
     */
    @Override
    @Transactional
    public boolean setAuditStatus(Integer auditType, Integer status)
    {
        try {
            ErpAuditSwitch existingSwitch = selectByAuditType(auditType);
            
            if (existingSwitch != null) {
                // 更新现有记录
                existingSwitch.setStatus(status);
                return updateById(existingSwitch);
            } else {
                // 创建新记录
                ErpAuditSwitch newSwitch = new ErpAuditSwitch();
                newSwitch.setAuditType(auditType);
                newSwitch.setStatus(status);
                return save(newSwitch);
            }
        } catch (Exception e) {
            log.error("设置审核开关状态失败，审核类型：{}，状态：{}", auditType, status, e);
            return false;
        }
    }
} 