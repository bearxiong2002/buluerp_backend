package com.ruoyi.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.web.domain.ErpOperationLog;
import com.ruoyi.web.log.AutoLogIgnore;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@AutoLogIgnore
public interface ErpOperationLogMapper extends BaseMapper<ErpOperationLog> {
}
