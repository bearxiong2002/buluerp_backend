package com.ruoyi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.web.domain.ErpOperationLog;
import com.ruoyi.web.log.OperationLog;

import java.util.List;

public interface IErpOperationLogService extends IService<ErpOperationLog> {
    int saveOperations(List<OperationLog> operationLogs);
}
