package com.ruoyi.web.controller.erp;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.web.request.purchase.AddPurchaseOrderRequest;
import com.ruoyi.web.request.purchase.ListPurchaseOrderRequest;
import com.ruoyi.web.request.purchase.UpdatePurchaseOrderRequest;
import com.ruoyi.web.result.PurchaseOrderResult;
import com.ruoyi.web.service.IErpPurchaseOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/system/purchase/order")
@Api(value = "采购订单相关")
public class ErpPurchaseOrderController extends BaseController {

    @Autowired
    private IErpPurchaseOrderService erpPurchaseOrderService;

    @ApiOperation(value = "获得采购订单列表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:purchaseOrder:list')")
    @GetMapping("/list")
    public TableDataInfo list(ListPurchaseOrderRequest listPurchaseOrderRequest) {
        startPage();
        List<PurchaseOrderResult> list = erpPurchaseOrderService.selectErpPurchaseOrderList(listPurchaseOrderRequest);
        return getDataTable(list);
    }

//    @ApiOperation(value = "导出采购订单列表")
//    @Anonymous
//    //@PreAuthorize("@ss.hasPermi('system:purchaseOrder:export')")
//    @PostMapping("/export")
//    public void export(HttpServletResponse response, ListPurchaseOrderRequest listPurchaseOrderRequest) {
//        List<PurchaseOrderRequest> list = erpPurchaseOrderService.list();
//        ExcelUtil<ErpPurchaseOrder> util = new ExcelUtil<ErpPurchaseOrder>(ErpPurchaseOrder.class);
//        util.exportExcel(response, list, "采购订单数据");
//    }

    @ApiOperation(value = "获得采购订单详细信息")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:purchaseOrder:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id) {
        return success(erpPurchaseOrderService.getById(id));
    }

    @ApiOperation(value = "新增采购订单")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:purchaseOrder:add')")
    @PostMapping
    public AjaxResult add(@RequestBody AddPurchaseOrderRequest addPurchaseOrderRequest) throws IOException {
        return toAjax(erpPurchaseOrderService.insertErpPurchaseOrder(addPurchaseOrderRequest));
    }

    @ApiOperation(value = "修改采购订单")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:purchaseOrder:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody UpdatePurchaseOrderRequest updatePurchaseOrderRequest) throws IOException {
        return toAjax(erpPurchaseOrderService.updateErpPurchaseOrder(updatePurchaseOrderRequest));
    }

    @ApiOperation(value = "删除采购订单")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:purchaseOrder:remove')")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable List<Integer> ids) {
        return toAjax(erpPurchaseOrderService.deleteErpPurchaseOrderByIds(ids));
    }

    @ApiOperation(value = "删除采购订单发票")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:purchaseOrder:remove')")
    @DeleteMapping("/{invoiceUrl}")
    public AjaxResult removeInvoice(@PathVariable String invoiceUrl) {
        return toAjax(erpPurchaseOrderService.removeInvoice(invoiceUrl));
    }
}