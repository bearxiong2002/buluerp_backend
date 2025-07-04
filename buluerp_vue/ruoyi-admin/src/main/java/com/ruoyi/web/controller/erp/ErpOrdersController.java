package com.ruoyi.web.controller.erp;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.validation.Save;
import com.ruoyi.common.validation.Update;
import com.ruoyi.web.domain.ErpOrders;
import com.ruoyi.web.request.order.ListOrderRequest;
import com.ruoyi.web.service.IErpAuditRecordService;
import com.ruoyi.web.service.IErpOrdersService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;

import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 订单Controller
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
@RestController
@RequestMapping("/system/orders")
public class ErpOrdersController extends BaseController
{
    @Autowired
    private IErpOrdersService erpOrdersService;

    @Autowired
    private IErpAuditRecordService erpAuditRecordService;

    /**
     * 查询订单列表
     */
    // @PreAuthorize("@ss.hasPermi('system:orders:list')")
    @Anonymous
    @GetMapping("/list")
    @ApiOperation(value = "查询订单列表", notes = "查询订单列表")
    public TableDataInfo list(
            ListOrderRequest request
    ) {
        startPage();
        List<ErpOrders> list = erpOrdersService.selectErpOrdersList(request);
        return getDataTable(list);
    }

    /**
     * 导出订单列表
     */
    // @PreAuthorize("@ss.hasPermi('system:orders:export')")
    @Anonymous
    @Log(title = "订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation(value = "导出订单列表", notes = "导出订单列表")
    public void export(HttpServletResponse response, Long[] ids)
    {
        List<ErpOrders> list = erpOrdersService.selectErpOrdersListByIds(ids);
        ExcelUtil<ErpOrders> util = new ExcelUtil<ErpOrders>(ErpOrders.class);
        util.exportExcel(response, list, "订单数据");
    }

    @Anonymous
    @GetMapping("/export/template")
    @ApiOperation(value = "下载订单导入模板", notes = "下载订单导入模板")
    public void exportTemplate(HttpServletResponse response) throws InstantiationException, IllegalAccessException {
        List<ErpOrders> list = Collections.singletonList(BaseEntity.createExample(ErpOrders.class));
        ExcelUtil<ErpOrders> util = createTemplateExcelUtil(ErpOrders.class);
        util.exportExcel(response, list, "订单数据");
    }

    /**
     * 导出订单列表
     */
    // @PreAuthorize("@ss.hasPermi('system:orders:import')") // 需要登录后获取 operatorId
    @Anonymous
    @Log(title = "订单", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    @ApiOperation(value = "导入订单列表", notes = "导入订单列表")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file) throws IOException {
        List<ErpOrders> erpOrders = validateExcel(file, ErpOrders.class);
        int count = 0;
        for (ErpOrders erpOrders1 : erpOrders) {
            erpOrdersService.insertErpOrders(erpOrders1);
            count++;
        }
        return success(count);
    }

    /**
     * 获取订单详细信息
     */
    // @PreAuthorize("@ss.hasPermi('system:orders:query')")
    @Anonymous
    @GetMapping(value = "/{id}")
    @ApiOperation(value = "获取订单详细信息", notes = "获取订单详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(requiresNotNull(erpOrdersService.selectErpOrdersById(id)));
    }

    /**
     * 新增订单
     */
    // @PreAuthorize("@ss.hasPermi('system:orders:add')") // 需要登录后获取 operatorId
    @Anonymous
    @Log(title = "订单", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation(value = "新增订单", notes = "新增订单")
    public AjaxResult add(@RequestBody @Validated({Save.class}) ErpOrders erpOrders)
    {
        return toAjax(erpOrdersService.insertErpOrders(erpOrders));
    }

    /**
     * 修改订单
     */
    // @PreAuthorize("@ss.hasPermi('system:orders:edit')") // 需要登录后获取 operatorId
    @Anonymous
    @Log(title = "订单", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation(value = "修改订单", notes = "修改订单")
    public AjaxResult edit(@RequestBody @Validated({ Update.class }) ErpOrders erpOrders) {
        return toAjax(erpOrdersService.updateErpOrders(erpOrders));
    }

    /**
     * 删除订单
     */
    // @PreAuthorize("@ss.hasPermi('system:orders:remove')")
    @Anonymous
    @Log(title = "订单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    @ApiOperation(value = "删除订单", notes = "删除订单")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(erpOrdersService.deleteErpOrdersByIds(ids));
    }

    @Anonymous
    @GetMapping("/statistics")
    @ApiOperation(value = "获取订单统计信息", notes = "获取订单统计信息")
    public AjaxResult getStatistics(
            @RequestParam(value = "startTime", required = false)
            @ApiParam(value = "开始日期", example = "2024-01-01 00:00:00")
            Date startTime,
            @RequestParam(value = "endTime", required = false)
            @ApiParam(value = "结束日期", example = "2024-12-31 23:59:59")
            Date endTime
    ) {
        return success(erpOrdersService.getOrderStatistics(startTime, endTime));
    }
}
