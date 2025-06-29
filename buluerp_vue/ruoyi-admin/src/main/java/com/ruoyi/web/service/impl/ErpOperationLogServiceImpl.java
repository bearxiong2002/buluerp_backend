package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.web.domain.ErpOperationLog;
import com.ruoyi.web.log.LogUtil;
import com.ruoyi.web.log.OperationLog;
import com.ruoyi.web.mapper.ErpOperationLogMapper;
import com.ruoyi.web.request.log.ListOperationLogRequest;
import com.ruoyi.web.service.IErpOperationLogService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ErpOperationLogServiceImpl
        extends ServiceImpl<ErpOperationLogMapper, ErpOperationLog>
        implements IErpOperationLogService {
    @Override
    public List<ErpOperationLog> listOperationLogs(ListOperationLogRequest request) {
        return getBaseMapper().listOperationLogs(request);
    }

    @Override
    public int saveOperations(List<OperationLog> operationLogs) {
        int count = 0;
        for (OperationLog operationLog : operationLogs) {
            ErpOperationLog log = ErpOperationLog.fromOperationLog(operationLog);
            if (this.save(log)) {
                count++;
            }
        }
        return count;
    }
}
