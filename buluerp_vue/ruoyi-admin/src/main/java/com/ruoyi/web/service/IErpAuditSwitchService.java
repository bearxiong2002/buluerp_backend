package com.ruoyi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.web.domain.ErpAuditSwitch;

import java.util.List;

/**
 * 审核开关Service接口
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
public interface IErpAuditSwitchService extends IService<ErpAuditSwitch>
{
    /**
     * 查询审核开关列表
     * 
     * @param erpAuditSwitch 审核开关
     * @return 审核开关集合
     */
    List<ErpAuditSwitch> selectErpAuditSwitchList(ErpAuditSwitch erpAuditSwitch);

    /**
     * 根据审核类型查询审核开关
     * 
     * @param auditType 审核类型
     * @return 审核开关
     */
    ErpAuditSwitch selectByAuditType(Integer auditType);

    /**
     * 检查指定审核类型是否启用审核
     * 
     * @param auditType 审核类型
     * @return true=启用审核，false=关闭审核
     */
    boolean isAuditEnabled(Integer auditType);

    /**
     * 设置审核开关状态
     * 
     * @param auditType 审核类型
     * @param status 状态 0=关闭审核，1=开启审核
     * @return 结果
     */
    boolean setAuditStatus(Integer auditType, Integer status);
} 