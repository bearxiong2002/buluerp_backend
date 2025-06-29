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
    // 使用 MyBatis-Plus 提供的基础方法，不需要自定义SQL
} 