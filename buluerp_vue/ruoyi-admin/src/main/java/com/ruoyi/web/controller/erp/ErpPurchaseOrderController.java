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
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
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
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "采购单ID", dataType = "integer", paramType = "query", example = "1"),
            @ApiImplicitParam(name = "purchaseId", value = "采购计划ID", dataType = "integer", paramType = "query", example = "100"),
            @ApiImplicitParam(name = "amount", value = "金额", dataType = "double"),
            @ApiImplicitParam(name = "createTimeFrom", value = "创建时间起始", dataType = "string", paramType = "query", format = "date-time", example = "2023-01-01 00:00:00"),
            @ApiImplicitParam(name = "createTimeTo", value = "创建时间终止", dataType = "string", paramType = "query", format = "date-time", example = "2023-12-31 23:59:59"),
            @ApiImplicitParam(name = "createUser", value = "创建用户", dataType = "string", paramType = "query", example = "admin")
    })
    @GetMapping("/list")
    public TableDataInfo list(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) Integer purchaseId,
            @RequestParam(required = false) String createUser,
            @RequestParam(required = false) Double amount,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime createTimeFrom,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime createTimeTo) {

        // 严格使用您实体类的字段封装请求
        ListPurchaseOrderRequest request = new ListPurchaseOrderRequest();
        request.setId(id);
        request.setAmount(amount);
        request.setPurchaseId(purchaseId);
        request.setCreateUser(createUser);
        request.setCreateTimeFrom(createTimeFrom);
        request.setCreateTimeTo(createTimeTo);

        // 分页查询
        startPage();
        List<PurchaseOrderResult> list = erpPurchaseOrderService.selectErpPurchaseOrderList(request);

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
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult add(@ModelAttribute AddPurchaseOrderRequest addPurchaseOrderRequest) throws IOException {
        return toAjax(erpPurchaseOrderService.insertErpPurchaseOrder(addPurchaseOrderRequest));
    }

    @ApiOperation(value = "修改采购订单")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:purchaseOrder:edit')")
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "采购单ID", dataType = "integer",required = true),
            @ApiImplicitParam(name = "purchaseId", value = "采购计划ID", dataType = "integer"),
            @ApiImplicitParam(name = "amount", value = "金额", dataType = "double"),
            @ApiImplicitParam(name = "invoice", value = "发票文件", dataType = "MultipartFile")
    })
    public AjaxResult edit(
            @RequestParam(name = "id", required = true) Integer id,
            @RequestParam(name = "purchaseId", required = false) Integer purchaseId,
            @RequestParam(name = "amount", required = false) Double amount,
            @RequestParam(value = "invoice", required = false) MultipartFile[] invoices) throws IOException {
        UpdatePurchaseOrderRequest request = new UpdatePurchaseOrderRequest();
        request.setId(id);
        if(purchaseId!=null)request.setPurchaseId(purchaseId);
        if(amount!=null)request.setAmount(amount);
        if(invoices!=null)request.setInvoice(invoices);
        return toAjax(erpPurchaseOrderService.updateErpPurchaseOrder(request));
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
    @DeleteMapping("/invoice/{ids}")
    public AjaxResult removeInvoice(@PathVariable List<Integer> ids) {
        return toAjax(erpPurchaseOrderService.removeInvoice(ids));
    }
}