package com.ruoyi.web.controller.erp;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.web.domain.ErpProductInventory;
import com.ruoyi.web.domain.ErpProductInventoryChange;
import com.ruoyi.web.request.Inventory.AddProductInventoryRequest;
import com.ruoyi.web.request.Inventory.ListProductInventoryRequest;
import com.ruoyi.web.request.Inventory.UpdateProductInventoryRequest;
import com.ruoyi.web.service.IErpProductInventoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/system/inventory/product")
@Api(value = "成品出入库管理")
public class ErpProductInventoryController extends BaseController {

    @Autowired
    private IErpProductInventoryService productInventoryService;

    @ApiOperation(value = "获取成品出入库列表")
    //@PreAuthorize("@ss.hasPermi('system:product-inventory:list')")
    @Anonymous
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "记录ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "orderCode", value = "订单编号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "productPartNumber", value = "产品部件号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "operator", value = "操作人", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "inOutQuantity", value = "出入库数量", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "storageLocation", value = "存储位置", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "remarks", value = "备注信息", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "createTimeFrom", value = "创建时间起始", dataType = "datetime"),
            @ApiImplicitParam(name = "createTimeTo", value = "创建时间终止", dataType = "datetime"),
            @ApiImplicitParam(name = "changeDateFrom", value = "变更日期起始", dataType = "date"),
            @ApiImplicitParam(name = "changeDateTo", value = "变更日期结束", dataType = "date")
    })
    @GetMapping("/list")
    public TableDataInfo list(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String orderCode,
            @RequestParam(required = false) String productPartNumber,
            @RequestParam(required = false) String operator,
            @RequestParam(required = false) Integer inOutQuantity,
            @RequestParam(required = false) String storageLocation,
            @RequestParam(required = false) String remarks,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime createTimeFrom,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime createTimeTo,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(required = false) Date changeDateFrom,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(required = false) Date changeDateTo) {

        ListProductInventoryRequest request = new ListProductInventoryRequest();
        request.setId(id);
        request.setOrderCode(orderCode);
        request.setProductPartNumber(productPartNumber);
        request.setOperator(operator);
        request.setInOutQuantity(inOutQuantity);
        request.setStorageLocation(storageLocation);
        request.setRemarks(remarks);
        request.setCreateTimeFrom(createTimeFrom);
        request.setCreateTimeTo(createTimeTo);
        request.setChangeDateFrom(changeDateFrom);
        request.setChangeDateTo(changeDateTo);

        startPage();
        List<ErpProductInventoryChange> list = productInventoryService.selectList(request);
        return getDataTable(list);
    }

    @ApiOperation(value = "导出成品出入库数据")
    //@PreAuthorize("@ss.hasPermi('system:product-inventory:export')")
    @Anonymous
    @PostMapping("/export")
    public void export(HttpServletResponse response, ListProductInventoryRequest request) {
        List<ErpProductInventoryChange> list = productInventoryService.selectList(request);
        ExcelUtil<ErpProductInventoryChange> util = new ExcelUtil<>(ErpProductInventoryChange.class);
        String fileName = "成品库存_" + new SimpleDateFormat("yyyyMMdd").format(new Date());
        util.exportExcel(response, list, fileName);
    }

    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:product-inventory:import')")
    @PostMapping("/import")
    @ApiOperation(value = "导入成品出入库")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file) throws IOException {
        ExcelUtil<AddProductInventoryRequest> util = new ExcelUtil<>(AddProductInventoryRequest.class);
        List<AddProductInventoryRequest> importList = util.importExcel(file.getInputStream());
        int count = 0;
        for (AddProductInventoryRequest req : importList) {
            productInventoryService.insertRecord(req);
            count++;
        }
        return success(count);
    }

    @ApiOperation(value = "获取成品出入库详情")
    //@PreAuthorize("@ss.hasPermi('system:product-inventory:query')")
    @Anonymous
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(productInventoryService.getById(id));
    }

    @ApiOperation(value = "新增成品库存记录")
    //@PreAuthorize("@ss.hasPermi('system:product-inventory:add')")
    @Anonymous
    @PostMapping()
    public AjaxResult add(@RequestBody AddProductInventoryRequest request) {
        return toAjax(productInventoryService.insertRecord(request));
    }

    @ApiOperation(value = "修改成品出入库记录")
    //@PreAuthorize("@ss.hasPermi('system:product-inventory:edit')")
    @Anonymous
    @PutMapping()
    public AjaxResult edit(@RequestBody UpdateProductInventoryRequest request) {
        return toAjax(productInventoryService.updateRecord(request));
    }

    @ApiOperation(value = "删除成品出入库记录")
    //@PreAuthorize("@ss.hasPermi('system:product-inventory:remove')")
    @Anonymous
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable List<Integer> ids) {
        return toAjax(productInventoryService.deleteByIds(ids));
    }

    @ApiOperation(value = "查询成品库存")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:product-inventory:store')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderCode", value = "订单编号", dataType = "string"),
            @ApiImplicitParam(name = "productPartNumber", value = "产品货号", dataType = "string"),
            @ApiImplicitParam(name = "updateTimeFrom", value = "更新时间起始", dataType = "datetime"),
            @ApiImplicitParam(name = "updateTimeTo", value = "更新时间终止", dataType = "datetime")
    })
    @GetMapping("/product/list")
    public TableDataInfo listProduct(
            @RequestParam(required = false) String orderCode,
            @RequestParam(required = false) String productPartNumber,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime updateTimeFrom,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime updateTimeTo) {

        ErpProductInventory query = new ErpProductInventory();
        query.setOrderCode(orderCode);
        query.setProductPartNumber(productPartNumber);

        startPage();
        List<ErpProductInventory> list = productInventoryService.ListStore(query, updateTimeFrom, updateTimeTo);
        return getDataTable(list);
    }
}