package com.ruoyi.web.controller.erp;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.web.domain.ErpPackagingMaterialInventory;
import com.ruoyi.web.request.Inventory.AddPackagingMaterialRequest;
import com.ruoyi.web.request.Inventory.ListPackagingMaterialRequest;
import com.ruoyi.web.request.Inventory.UpdatePackagingMaterialRequest;
import com.ruoyi.web.service.IErpPackagingMaterialInventoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/system/inventory/packaging-material")
@Api(value = "分包库存管理")
public class ErpPackagingMaterialInventoryController extends BaseController {

    @Autowired
    private IErpPackagingMaterialInventoryService erpPackagingMaterialInventoryService;

    @ApiOperation(value = "获取分包库存列表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:packaging:list')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "记录ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "orderCode", value = "订单编号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "productPartNumber", value = "产品部件号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "packagingNumber", value = "包装编号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "operator", value = "操作人", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "inOutQuantity", value = "出入库数量", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "storageLocation", value = "存储位置", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "remarks", value = "备注信息", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "editAction", value = "操作信息", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "createTimeFrom", value = "创建时间起始", dataType = "datetime", example = "2023-01-01 00:00:00"),
            @ApiImplicitParam(name = "createTimeTo", value = "创建时间终止", dataType = "datetime", example = "2023-12-31 23:59:59"),
            @ApiImplicitParam(name = "changeDateFrom", value = "变更日期起始", dataType = "date", example = "2023-10-25 00:00:00"),
            @ApiImplicitParam(name = "changeDateTo", value = "变更日期结束", dataType = "date",example = "2023-10-25 23:59:59")
    })
    @GetMapping("/list")
    public TableDataInfo list(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String orderCode,
            @RequestParam(required = false) String productPartNumber,
            @RequestParam(required = false) String packagingNumber,
            @RequestParam(required = false) String operator,
            @RequestParam(required = false) Integer inOutQuantity,
            @RequestParam(required = false) String storageLocation,
            @RequestParam(required = false) String remarks,
            @RequestParam(required = false) String editAction,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime createTimeFrom,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime createTimeTo,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) Date changeDateFrom,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) Date changeDateTo) {

        ListPackagingMaterialRequest request = new ListPackagingMaterialRequest();
        request.setId(id);
        request.setOrderCode(orderCode);
        request.setProductPartNumber(productPartNumber);
        request.setPackagingNumber(packagingNumber);
        request.setOperator(operator);
        request.setInOutQuantity(inOutQuantity);
        request.setStorageLocation(storageLocation);
        request.setRemarks(remarks);
        request.setEditAction(editAction);
        request.setCreateTimeFrom(createTimeFrom);
        request.setCreateTimeTo(createTimeTo);
        request.setChangeDateFrom(changeDateFrom);  // 新增参数
        request.setChangeDateTo(changeDateTo);      // 新增参数

        startPage();
        List<ErpPackagingMaterialInventory> list = erpPackagingMaterialInventoryService.selectList(request);
        return getDataTable(list);
    }

    @ApiOperation(value = "导出分包库存数据")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:packaging:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, ListPackagingMaterialRequest request) {
        List<ErpPackagingMaterialInventory> list = erpPackagingMaterialInventoryService.selectList(request);
        ExcelUtil<ErpPackagingMaterialInventory> util = new ExcelUtil<>(ErpPackagingMaterialInventory.class);
        String fileName = "包材库存_" + new SimpleDateFormat("yyyyMMdd").format(new Date());
        util.exportExcel(response, list, fileName);
    }

    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:packaging:import')")
    @PostMapping("/import")
    @ApiOperation(value = "导入分包库存")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file) throws IOException {
        ExcelUtil<AddPackagingMaterialRequest> util = new ExcelUtil<>(AddPackagingMaterialRequest.class);
        List<AddPackagingMaterialRequest> importList = util.importExcel(file.getInputStream());
        int count = 0;
        for (AddPackagingMaterialRequest req : importList) {
            erpPackagingMaterialInventoryService.insertRecord(req);
            count++;
        }
        return success(count);
    }

    @ApiOperation(value = "获取分包库存详情")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:packaging:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(erpPackagingMaterialInventoryService.getById(id));
    }

    @ApiOperation(value = "新增分包库存记录")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:packaging:add')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult add(@ModelAttribute AddPackagingMaterialRequest request) {
        return toAjax(erpPackagingMaterialInventoryService.insertRecord(request));
    }

    @ApiOperation(value = "修改分包库存记录")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:packaging:edit')")
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult edit(@ModelAttribute UpdatePackagingMaterialRequest request) {
        return toAjax(erpPackagingMaterialInventoryService.updateRecord(request));
    }

    @ApiOperation(value = "删除分包库存记录")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:packaging:remove')")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable List<Integer> ids) {
        return toAjax(erpPackagingMaterialInventoryService.deleteByIds(ids));
    }
}