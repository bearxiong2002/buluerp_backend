package com.ruoyi.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.web.domain.ErpOperationLog;
import com.ruoyi.web.annotation.AutoLogIgnore;
import com.ruoyi.web.request.log.ListOperationLogRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@AutoLogIgnore
public interface ErpOperationLogMapper extends BaseMapper<ErpOperationLog> {
    List<ErpOperationLog> listOperationLogs(ListOperationLogRequest request);
}
