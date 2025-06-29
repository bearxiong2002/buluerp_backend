package com.ruoyi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.web.domain.ErpOperationLog;
import com.ruoyi.web.log.OperationLog;
import com.ruoyi.web.request.log.ListOperationLogRequest;

import java.util.List;

public interface IErpOperationLogService extends IService<ErpOperationLog> {
    List<ErpOperationLog> listOperationLogs(ListOperationLogRequest request);
    int saveOperations(List<OperationLog> operationLogs);
}
