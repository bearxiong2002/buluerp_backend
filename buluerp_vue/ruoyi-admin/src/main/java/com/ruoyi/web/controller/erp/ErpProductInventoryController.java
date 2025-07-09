package com.ruoyi.web.controller.erp;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.web.domain.ErpProductInventory;
import com.ruoyi.common.constant.HttpStatus;
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
            @RequestParam(required = false) String operator,
            @RequestParam(required = false) Integer inOutQuantity,
            @RequestParam(required = false) String storageLocation,
            @RequestParam(required = false) String remarks,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeTo,
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
    @GetMapping("/export")
    @ApiImplicitParam(name = "ids", value = "出入库记录ID列表，多个ID用逗号分隔", dataType = "String", required = true, paramType = "query")
    public void export(HttpServletResponse response, @RequestParam("ids") List<Integer> ids) {
        List<ErpProductInventoryChange> list = productInventoryService.selectListByIds(ids);
        ExcelUtil<ErpProductInventoryChange> util = new ExcelUtil<>(ErpProductInventoryChange.class);
        String fileName = "成品出入库记录_" + new SimpleDateFormat("yyyyMMdd").format(new Date());
        util.exportExcel(response, list, fileName);
    }

    @ApiOperation(value = "导出成品库存数据")
    //@PreAuthorize("@ss.hasPermi('system:product-inventory:export')")
    @Anonymous
    @GetMapping("/export-store")
    @ApiImplicitParam(name = "ids", value = "库存记录ID列表，多个ID用逗号分隔", dataType = "String", required = true, paramType = "query")
    public void exportStore(HttpServletResponse response, @RequestParam("ids") List<Long> ids) {
        List<ErpProductInventory> list = productInventoryService.selectStoreByIds(ids);
        ExcelUtil<ErpProductInventory> util = new ExcelUtil<>(ErpProductInventory.class);
        String fileName = "成品库存汇总_" + new SimpleDateFormat("yyyyMMdd").format(new Date());
        util.exportExcel(response, list, fileName);
    }

    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:product-inventory:import')")
    @PostMapping("/import")
    @ApiOperation(value = "导入成品出入库")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file) throws IOException {
        ExcelUtil<AddProductInventoryRequest> util = new ExcelUtil<>(AddProductInventoryRequest.class);
        List<AddProductInventoryRequest> requests;
        List<Map<String, Object>> errorList = new ArrayList<>();

        try {
            requests = util.importExcel(file.getInputStream());
        } catch (Exception e) {
            return AjaxResult.error("Excel解析失败: " + e.getMessage());
        }

        int rowNumber = 1; // 数据行号（从标题行下一行开始）
        int successCount = 0;
        
        for (AddProductInventoryRequest request : requests) {
            try {
                // 使用校验器触发注解规则
                Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
                Set<ConstraintViolation<AddProductInventoryRequest>> violations = validator.validate(request);

                // 收集校验错误
                if (!violations.isEmpty()) {
                    List<String> errors = new ArrayList<>();
                    for (ConstraintViolation<AddProductInventoryRequest> violation : violations) {
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
            for (AddProductInventoryRequest request:requests){
                // 调用 Service 插入数据
                productInventoryService.insertRecord(request);
                successCount++;
            }
        }
        return AjaxResult.success("导入成功，共导入 " + successCount + " 条数据");
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
            @ApiImplicitParam(name = "productPartNumber", value = "产品货号", dataType = "string"),
            @ApiImplicitParam(name = "updateTimeFrom", value = "更新时间起始", dataType = "date"),
            @ApiImplicitParam(name = "updateTimeTo", value = "更新时间终止", dataType = "date")
    })
    @GetMapping("/store")
    public TableDataInfo listProduct(
            @RequestParam(required = false) String productPartNumber,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date updateTimeFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date updateTimeTo) {

        ErpProductInventory query = new ErpProductInventory();
        query.setProductPartNumber(productPartNumber);

        startPage();
        List<ErpProductInventory> list = productInventoryService.ListStore(query, updateTimeFrom, updateTimeTo);
        return getDataTable(list);
    }

    @GetMapping("/template")
    @ApiOperation("下载成品出入库导入模板")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:product-inventory:import')")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        // 创建示例数据行（提供合理的示例值）
        List<AddProductInventoryRequest> templateData = new ArrayList<>();
        
        // 示例1：入库记录
        AddProductInventoryRequest inExample = new AddProductInventoryRequest();
        inExample.setOrderCode("ORD-2024-001");       // 订单编号示例
        inExample.setProductPartNumber("PN-PHONE-001"); // 产品货号示例
        inExample.setInOutQuantity(50);               // 入库数量（正数）
        inExample.setStorageLocation("成品区-A-01");   // 存储位置示例
        inExample.setChangeDate(new Date());          // 变更日期
        inExample.setRemarks("完工入库");             // 备注信息
        templateData.add(inExample);
        
        // 示例2：出库记录
        AddProductInventoryRequest outExample = new AddProductInventoryRequest();
        outExample.setOrderCode("ORD-2024-002");      // 订单编号示例
        outExample.setProductPartNumber("PN-PHONE-002"); // 产品货号示例
        outExample.setInOutQuantity(-30);             // 出库数量（负数）
        outExample.setStorageLocation("成品区-B-02");  // 存储位置示例
        outExample.setChangeDate(new Date());         // 变更日期
        outExample.setRemarks("销售出库");            // 备注信息
        templateData.add(outExample);

        // 创建ExcelUtil实例并导出模板
        ExcelUtil<AddProductInventoryRequest> util = new ExcelUtil<>(AddProductInventoryRequest.class);
        util.exportExcel(response, templateData, "成品出入库导入模板");
    }
}