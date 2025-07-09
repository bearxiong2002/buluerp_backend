package com.ruoyi.web.controller.erp;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.web.domain.ErpPackagingMaterialInventory;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.web.domain.ErpPackagingMaterialInventoryChange;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
 
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.Validation;

@RestController
@RequestMapping("/system/inventory/packaging-material")
@Api(value = "料包出入库管理")
public class ErpPackagingMaterialInventoryController extends BaseController {

    @Autowired
    private IErpPackagingMaterialInventoryService erpPackagingMaterialInventoryService;

    @ApiOperation(value = "获取料包出入库列表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:packaging-inventory:list')")
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
            @ApiImplicitParam(name = "createTimeFrom", value = "创建时间起始", dataType = "date"),
            @ApiImplicitParam(name = "createTimeTo", value = "创建时间终止", dataType = "date"),
            @ApiImplicitParam(name = "changeDateFrom", value = "变更日期起始", dataType = "date"),
            @ApiImplicitParam(name = "changeDateTo", value = "变更日期结束", dataType = "date")
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
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeTo,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date changeDateFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date changeDateTo) {

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
        List<ErpPackagingMaterialInventoryChange> list = erpPackagingMaterialInventoryService.selectList(request);
        return getDataTable(list);
    }

    @ApiOperation(value = "导出料包出入库数据")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:packaging-inventory:export')")
    @GetMapping("/export")
    @ApiImplicitParam(name = "ids", value = "出入库记录ID列表，多个ID用逗号分隔", dataType = "String", required = true, paramType = "query")
    public void export(HttpServletResponse response, @RequestParam("ids") List<Integer> ids) {
        List<ErpPackagingMaterialInventoryChange> list = erpPackagingMaterialInventoryService.selectListByIds(ids);
        ExcelUtil<ErpPackagingMaterialInventoryChange> util = new ExcelUtil<>(ErpPackagingMaterialInventoryChange.class);
        String fileName = "料包出入库记录_" + new SimpleDateFormat("yyyyMMdd").format(new Date());
        util.exportExcel(response, list, fileName);
    }

    @ApiOperation(value = "导出料包库存数据")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:packaging-inventory:export')")
    @GetMapping("/export-store")
    @ApiImplicitParam(name = "ids", value = "库存记录ID列表，多个ID用逗号分隔", dataType = "String", required = true, paramType = "query")
    public void exportStore(HttpServletResponse response, @RequestParam("ids") List<Long> ids) {
        List<ErpPackagingMaterialInventory> list = erpPackagingMaterialInventoryService.selectStoreByIds(ids);
        ExcelUtil<ErpPackagingMaterialInventory> util = new ExcelUtil<>(ErpPackagingMaterialInventory.class);
        String fileName = "料包库存汇总_" + new SimpleDateFormat("yyyyMMdd").format(new Date());
        util.exportExcel(response, list, fileName);
    }

    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:packaging-inventory:import')")
    @PostMapping("/import")
    @ApiOperation(value = "导入料包出入库")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file) throws IOException {
        ExcelUtil<AddPackagingMaterialRequest> util = new ExcelUtil<>(AddPackagingMaterialRequest.class);
        List<AddPackagingMaterialRequest> requests;
        List<Map<String, Object>> errorList = new ArrayList<>();

        try {
            requests = util.importExcel(file.getInputStream());
        } catch (Exception e) {
            return AjaxResult.error("Excel解析失败: " + e.getMessage());
        }

        int rowNumber = 1; // 数据行号（从标题行下一行开始）
        int successCount = 0;
        
        for (AddPackagingMaterialRequest request : requests) {
            try {
                // 使用校验器触发注解规则
                Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
                Set<ConstraintViolation<AddPackagingMaterialRequest>> violations = validator.validate(request);

                // 收集校验错误
                if (!violations.isEmpty()) {
                    List<String> errors = new ArrayList<>();
                    for (ConstraintViolation<AddPackagingMaterialRequest> violation : violations) {
                        errors.add(violation.getMessage());
                    }
                    
                    Map<String, Object> errorEntry = new HashMap<>();
                    errorEntry.put("row", rowNumber);
                    errorEntry.put("error", String.join("; ", errors));
                    errorEntry.put("data", request.toString());
                    errorList.add(errorEntry);
                }
            } catch (Exception e) {
                Map<String, Object> errorEntry = new HashMap<>();
                errorEntry.put("row", rowNumber);
                errorEntry.put("error", "系统错误: " + e.getMessage());
                errorEntry.put("data", request.toString());
                errorList.add(errorEntry);
            }
            rowNumber++;
        }

        // 构建返回结果
        if (!errorList.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("total", requests.size());
            result.put("success", successCount);
            result.put("failure", errorList.size());
            result.put("errors", errorList);
            return new AjaxResult(HttpStatus.CONFLICT, "导入完成，但有部分错误", result);
        }
        else {
            for(AddPackagingMaterialRequest request:requests){
                // 调用 Service 插入数据
                erpPackagingMaterialInventoryService.insertRecord(request);
                successCount++;
            }
        }
        return AjaxResult.success("导入成功，共导入 " + successCount + " 条数据");
    }

    @ApiOperation(value = "获取料包出入库详情")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:packaging-inventory:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(erpPackagingMaterialInventoryService.getById(id));
    }

    @ApiOperation(value = "新增料包出入库记录")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:packaging-inventory:add')")
    @PostMapping()
    public AjaxResult add(@RequestBody AddPackagingMaterialRequest request) {
        return toAjax(erpPackagingMaterialInventoryService.insertRecord(request));
    }

    @ApiOperation(value = "修改料包出入库记录")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:packaging-inventory:edit')")
    @PutMapping()
    public AjaxResult edit(@RequestBody UpdatePackagingMaterialRequest request) {
        return toAjax(erpPackagingMaterialInventoryService.updateRecord(request));
    }

    @ApiOperation(value = "删除料包出入库记录")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:packaging-inventory:remove')")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable List<Integer> ids) {
        return toAjax(erpPackagingMaterialInventoryService.deleteByIds(ids));
    }

    @ApiOperation(value = "查询料包库存")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:packaging-inventory:store')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productPartNumber", value = "产品货号", dataType = "string"),
            @ApiImplicitParam(name = "packingNumber", value = "分包编号", dataType = "string"),
            @ApiImplicitParam(name = "updateTimeFrom", value = "更新时间起始", dataType = "date"),
            @ApiImplicitParam(name = "updateTimeTo", value = "更新时间终止", dataType = "date")
    })
    @GetMapping("/store")
    public TableDataInfo listPackaging(
            @RequestParam(required = false) String productPartNumber,
            @RequestParam(required = false) String packingNumber,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(required = false) Date updateTimeFrom,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(required = false) Date updateTimeTo) {

        ErpPackagingMaterialInventory query = new ErpPackagingMaterialInventory();
        query.setProductPartNumber(productPartNumber);
        query.setPackingNumber(packingNumber);

        startPage();
        List<ErpPackagingMaterialInventory> list = erpPackagingMaterialInventoryService.ListStore(query, updateTimeFrom, updateTimeTo);
        return getDataTable(list);
    }

    @GetMapping("/template")
    @ApiOperation("下载料包出入库导入模板")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:packaging-inventory:import')")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        // 创建示例数据行（提供合理的示例值）
        List<AddPackagingMaterialRequest> templateData = new ArrayList<>();
        
        // 示例1：入库记录
        AddPackagingMaterialRequest inExample = new AddPackagingMaterialRequest();
        inExample.setOrderCode("ORD-2024-001");           // 订单编号示例
        inExample.setProductPartNumber("PN-CASE-001");    // 产品货号示例
        inExample.setPackagingNumber("PKG-001");          // 分包编号示例
        inExample.setInOutQuantity(100);                  // 入库数量（正数）
        inExample.setStorageLocation("A区-01-01");        // 存储位置示例
        inExample.setEditAction("入库");                  // 操作信息
        inExample.setChangeDate(new Date());              // 变更日期
        inExample.setRemarks("新产品入库");               // 备注信息
        templateData.add(inExample);
        
        // 示例2：出库记录
        AddPackagingMaterialRequest outExample = new AddPackagingMaterialRequest();
        outExample.setOrderCode("ORD-2024-002");          // 订单编号示例
        outExample.setProductPartNumber("PN-CASE-002");   // 产品货号示例
        outExample.setPackagingNumber("PKG-002");         // 分包编号示例
        outExample.setInOutQuantity(-50);                 // 出库数量（负数）
        outExample.setStorageLocation("B区-02-03");       // 存储位置示例
        outExample.setEditAction("出库");                 // 操作信息
        outExample.setChangeDate(new Date());             // 变更日期
        outExample.setRemarks("生产领料");                // 备注信息
        templateData.add(outExample);

        // 创建ExcelUtil实例并导出模板
        ExcelUtil<AddPackagingMaterialRequest> util = new ExcelUtil<>(AddPackagingMaterialRequest.class);
        util.exportExcel(response, templateData, "料包出入库导入模板");
    }
}