package com.ruoyi.web.controller.erp;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.web.domain.ErpOrders;
import com.ruoyi.web.service.IErpOrdersService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;

import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

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

    /**
     * 查询订单列表
     */
    @PreAuthorize("@ss.hasPermi('system:orders:list')")
    @GetMapping("/list")
    public TableDataInfo list(ErpOrders erpOrders)
    {
        startPage();
        List<ErpOrders> list = erpOrdersService.selectErpOrdersList(erpOrders);
        return getDataTable(list);
    }

    /**
     * 导出订单列表
     */
    @PreAuthorize("@ss.hasPermi('system:orders:export')")
    @Log(title = "订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ErpOrders erpOrders)
    {
        List<ErpOrders> list = erpOrdersService.selectErpOrdersList(erpOrders);
        ExcelUtil<ErpOrders> util = new ExcelUtil<ErpOrders>(ErpOrders.class);
        util.exportExcel(response, list, "订单数据");
    }

    /**
     * 获取订单详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:orders:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(erpOrdersService.selectErpOrdersById(id));
    }

    /**
     * 新增订单
     */
    @PreAuthorize("@ss.hasPermi('system:orders:add')")
    @Log(title = "订单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ErpOrders erpOrders)
    {
        return toAjax(erpOrdersService.insertErpOrders(erpOrders));
    }

    /**
     * 修改订单
     */
    @PreAuthorize("@ss.hasPermi('system:orders:edit')")
    @Log(title = "订单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ErpOrders erpOrders)
    {
        return toAjax(erpOrdersService.updateErpOrders(erpOrders));
    }

    /**
     * 删除订单
     */
    @PreAuthorize("@ss.hasPermi('system:orders:remove')")
    @Log(title = "订单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(erpOrdersService.deleteErpOrdersByIds(ids));
    }
}
