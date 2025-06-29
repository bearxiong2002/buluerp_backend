package com.ruoyi.web.controller.erp;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.web.domain.ErpOperationLog;
import com.ruoyi.web.log.AutoLogIgnore;
import com.ruoyi.web.request.log.ListOperationLogRequest;
import com.ruoyi.web.service.IErpOperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "operation-log")
@AutoLogIgnore
@Api(tags = "操作日志")
public class ErpOperationLogController extends BaseController {
    @Autowired
    private IErpOperationLogService erpOperationLogService;

    @Anonymous
    @GetMapping(value = "/list")
    @ApiOperation(value = "搜索操作日志列表", notes = "搜索操作日志列表")
    public AjaxResult list(ListOperationLogRequest request) {
        return success(erpOperationLogService.listOperationLogs(request));
    }

    @Anonymous
    @PostMapping("/export")
    @ApiOperation(value = "导出操作日志列表", notes = "导出操作日志列表")
    public void export(HttpServletResponse response, Long[] ids) {
        List<ErpOperationLog> list = erpOperationLogService.listByIds(Arrays.asList(ids));
        ExcelUtil<ErpOperationLog> util = new ExcelUtil<>(ErpOperationLog.class);
        util.exportExcel(response, list, "操作日志数据");
    }

    @Anonymous
    @GetMapping
    @ApiOperation(value = "获取操作日志", notes = "获取操作日志")
    public AjaxResult get(Long[] ids) {
        return success(erpOperationLogService.listByIds(Arrays.asList(ids)));
    }

    @Anonymous
    @DeleteMapping("/{ids}")
    @ApiOperation(value = "删除操作日志", notes = "删除操作日志")
    public AjaxResult delete(@PathVariable Long[] ids) {
        return toAjax(erpOperationLogService.removeBatchByIds(Arrays.asList(ids)));
    }
}
