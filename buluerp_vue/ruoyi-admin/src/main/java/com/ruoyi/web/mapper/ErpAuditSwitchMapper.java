package com.ruoyi.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.web.domain.ErpAuditSwitch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 审核开关Mapper接口
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
@Mapper
public interface ErpAuditSwitchMapper extends BaseMapper<ErpAuditSwitch>
{
    /**
     * 根据审核类型查询审核开关
     * 
     * @param auditType 审核类型
     * @return 审核开关
     */
    @Select("SELECT * FROM erp_audit_switch WHERE audit_type = #{auditType} LIMIT 1")
    ErpAuditSwitch selectByAuditType(Integer auditType);
} 