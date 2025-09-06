package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.web.domain.ErpAuditSwitch;
import com.ruoyi.web.enums.AuditTypeEnum;
import com.ruoyi.web.mapper.ErpAuditSwitchMapper;
import com.ruoyi.web.service.IErpAuditSwitchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    /**
     * 根据用户角色查询审核开关列表
     * boss和admin角色可以查看所有审核开关，其他审核人只能查看自己对应的审核开关
     * 
     * @param erpAuditSwitch 查询条件
     * @return 过滤后的审核开关集合
     */
    @Override
    public List<ErpAuditSwitch> selectErpAuditSwitchListWithPermission(ErpAuditSwitch erpAuditSwitch) {
        try {
            // 获取当前用户的角色信息
            Set<String> userRoles = SecurityUtils.getLoginUser().getUser().getRoles()
                    .stream()
                    .map(role -> role.getRoleKey())
                    .collect(Collectors.toSet());


            // 检查用户是否为boss或admin角色
            boolean isBossOrAdmin = userRoles.contains("boss") || 
                                  userRoles.contains("admin") || 
                                  userRoles.contains("SUPER_ADMIN") ||
                                  SecurityUtils.isAdmin(SecurityUtils.getUserId());

            if (isBossOrAdmin) {
                // boss和admin角色可以查看所有审核开关
                return selectErpAuditSwitchList(erpAuditSwitch);
            }

            // 其他角色只能查看自己对应的审核开关
            List<Integer> allowedAuditTypes = new ArrayList<>();
            for (String roleKey : userRoles) {
                AuditTypeEnum auditType = AuditTypeEnum.getByRoleKey(roleKey);
                if (auditType != null) {
                    allowedAuditTypes.add(auditType.getCode());
                }
            }

            if (allowedAuditTypes.isEmpty()) {
                // 用户没有审核角色，返回空列表
                return new ArrayList<>();
            }

            // 如果查询条件中指定了审核类型，检查是否在允许范围内
            if (erpAuditSwitch.getAuditType() != null) {
                if (!allowedAuditTypes.contains(erpAuditSwitch.getAuditType())) {
                    // 用户没有权限查看该审核类型，返回空列表
                    return new ArrayList<>();
                }
                // 有权限，直接查询
                return selectErpAuditSwitchList(erpAuditSwitch);
            }

            // 查询用户有权限的所有审核类型的开关
            LambdaQueryWrapper<ErpAuditSwitch> wrapper= Wrappers.lambdaQuery();
            wrapper.in(ErpAuditSwitch::getAuditType,allowedAuditTypes);
            return erpAuditSwitchMapper.selectList(wrapper);

        } catch (Exception e) {
            log.error("根据权限查询审核开关列表失败", e);
            // 异常情况下返回空列表，确保安全
            return new ArrayList<>();
        }
    }
} 