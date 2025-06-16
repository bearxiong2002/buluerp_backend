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
import com.ruoyi.web.service.IErpOrdersService;
import com.ruoyi.web.service.IOrderAuditService;
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
    private IOrderAuditService orderAuditService;

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

    /**
     * 重新提交订单审核
     */
    // @PreAuthorize("@ss.hasPermi('system:orders:add')") // 需要登录后获取 operatorId
    @Anonymous
    @Log(title = "重新提交订单审核", businessType = BusinessType.UPDATE)
    @PostMapping("/resubmit/{id}")
    @ApiOperation(value = "重新提交订单审核", notes = "用于状态为-1（审核拒绝）的订单重新提交审核")
    public AjaxResult resubmitOrder(@ApiParam("订单ID") @PathVariable("id") Long id)
    {
        try {
            // 1. 获取订单信息
            ErpOrders order = erpOrdersService.selectErpOrdersById(id);
            if (order == null) {
                return error("订单不存在");
            }
            
            // 2. 检查订单状态是否为审核拒绝(-1)
            if (!Integer.valueOf(-1).equals(order.getStatus())) {
                return error("只有审核拒绝状态的订单才能重新提交审核");
            }
            
            // 3. 更新订单状态为待审核(0)
            order.setStatus(0);
            order.setUpdateTime(new Date());
            int updateResult = erpOrdersService.updateErpOrders(order);
            
            if (updateResult <= 0) {
                return error("更新订单状态失败");
            }
            
            // 4. 重新创建审核记录并发送通知
            orderAuditService.handleOrderCreated(order);
            
            return success("订单重新提交审核成功");
            
        } catch (Exception e) {
            logger.error("重新提交订单审核失败，订单ID：{}", id, e);
            return error("重新提交订单审核失败：" + e.getMessage());
        }
    }
}
