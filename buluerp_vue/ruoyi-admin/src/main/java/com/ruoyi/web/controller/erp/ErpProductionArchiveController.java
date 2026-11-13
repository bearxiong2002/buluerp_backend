package com.ruoyi.web.controller.erp;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.page.PageDefaultOptions;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.web.domain.ErpProductionArrange;
import com.ruoyi.web.request.order.ListOrderRequest;
import com.ruoyi.web.request.productionschedule.ListProductionScheduleRequest;
import com.ruoyi.web.result.ProductionArchiveOrderResult;
import com.ruoyi.web.result.ProductionScheduleResult;
import com.ruoyi.web.service.IErpProductionArchiveService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 已完成订单只读归档接口。
 */
@RestController
@RequestMapping("/system/production-archive")
public class ErpProductionArchiveController extends BaseController {
    @Autowired
    private IErpProductionArchiveService erpProductionArchiveService;

    @Anonymous
    @GetMapping("/orders")
    @ApiOperation(value = "查询已完成订单归档列表")
    public TableDataInfo listOrders(ListOrderRequest request) {
        startPage(PageDefaultOptions.create().orderByColumn("createTime"));
        List<ProductionArchiveOrderResult> list = erpProductionArchiveService.listArchiveOrders(request);
        return getDataTable(list);
    }

    @Anonymous
    @GetMapping("/{orderCode}/schedules")
    @ApiOperation(value = "查询已完成订单关联布产记录")
    public TableDataInfo listSchedules(
            @PathVariable String orderCode,
            ListProductionScheduleRequest request
    ) {
        String defaultOrderCode = erpProductionArchiveService.requireCompletedOrderCode(orderCode);
        startPage(PageDefaultOptions.create().orderByColumn("creationTime"));
        List<ProductionScheduleResult> list =
                erpProductionArchiveService.listSchedulesByOrderCode(defaultOrderCode, request);
        return getDataTable(list);
    }

    @Anonymous
    @GetMapping("/{orderCode}/arranges")
    @ApiOperation(value = "查询已完成订单关联排产记录")
    public TableDataInfo listArranges(
            @PathVariable String orderCode,
            ErpProductionArrange filter
    ) {
        String defaultOrderCode = erpProductionArchiveService.requireCompletedOrderCode(orderCode);
        startPage(PageDefaultOptions.create().orderByColumn("creationTime"));
        List<ErpProductionArrange> list =
                erpProductionArchiveService.listArrangesByOrderCode(defaultOrderCode, filter);
        return getDataTable(list);
    }

    @Anonymous
    @PostMapping("/export")
    @ApiOperation(value = "导出已完成订单归档数据")
    public void export(HttpServletResponse response, Long[] ids) {
        List<ProductionArchiveOrderResult> list = erpProductionArchiveService.listArchiveOrdersByIds(ids);
        ExcelUtil<ProductionArchiveOrderResult> util = new ExcelUtil<>(ProductionArchiveOrderResult.class);
        util.exportExcel(response, list, "已完成订单归档数据");
    }
}