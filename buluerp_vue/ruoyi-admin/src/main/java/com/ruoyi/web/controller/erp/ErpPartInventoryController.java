package com.ruoyi.web.controller.erp;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.web.domain.ErpPartInventory;
import com.ruoyi.web.domain.ErpPartInventoryChange;
import com.ruoyi.web.request.Inventory.AddPartInventoryRequest;
import com.ruoyi.web.request.Inventory.ListPartInventoryRequest;
import com.ruoyi.web.request.Inventory.UpdatePartInventoryRequest;
import com.ruoyi.web.service.IErpPartInventoryService;
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
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.Validation;

@RestController
@RequestMapping("/system/inventory/part")
@Api(value = "胶件出入库管理")
public class ErpPartInventoryController extends BaseController {

    @Autowired
    private IErpPartInventoryService partInventoryService;

    @ApiOperation(value = "获取胶件出入库列表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:part-inventory:list')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "记录ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "orderCode", value = "订单编号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "mouldNumber", value = "模具编号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "colorCode", value = "颜色代码", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "operator", value = "操作人", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "inOutQuantity", value = "出入库数量", dataType = "Integer", paramType = "query"),
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
            @RequestParam(required = false) String mouldNumber,
            @RequestParam(required = false) String colorCode,
            @RequestParam(required = false) String operator,
            @RequestParam(required = false) Integer inOutQuantity,
            @RequestParam(required = false) String remarks,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime createTimeFrom,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime createTimeTo,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(required = false) Date changeDateFrom,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(required = false) Date changeDateTo) {

        ListPartInventoryRequest request = new ListPartInventoryRequest();
        request.setId(id);
        request.setOrderCode(orderCode);
        request.setMouldNumber(mouldNumber);
        request.setColorCode(colorCode);
        request.setOperator(operator);
        request.setInOutQuantity(inOutQuantity);
        request.setRemarks(remarks);
        request.setCreateTimeFrom(createTimeFrom);
        request.setCreateTimeTo(createTimeTo);
        request.setChangeDateFrom(changeDateFrom);
        request.setChangeDateTo(changeDateTo);

        startPage();
        List<ErpPartInventoryChange> list = partInventoryService.selectList(request);
        return getDataTable(list);
    }

    @ApiOperation(value = "导出胶件出入库数据")
    //@PreAuthorize("@ss.hasPermi('system:part-inventory:export')")
    @Anonymous
    @PostMapping("/export")
    public void export(HttpServletResponse response, ListPartInventoryRequest request) {
        List<ErpPartInventoryChange> list = partInventoryService.selectList(request);
        ExcelUtil<ErpPartInventoryChange> util = new ExcelUtil<>(ErpPartInventoryChange.class);
        String fileName = "胶件库存_" + new SimpleDateFormat("yyyyMMdd").format(new Date());
        util.exportExcel(response, list, fileName);
    }

    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:part-inventory:import')")
    @PostMapping("/import")
    @ApiOperation(value = "导入胶件出入库")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file) throws IOException {
        ExcelUtil<AddPartInventoryRequest> util = new ExcelUtil<>(AddPartInventoryRequest.class);
        List<AddPartInventoryRequest> requests;
        List<Map<String, Object>> errorList = new ArrayList<>();

        try {
            requests = util.importExcel(file.getInputStream());
        } catch (Exception e) {
            return AjaxResult.error("Excel解析失败: " + e.getMessage());
        }

        int rowNumber = 1; // 数据行号（从标题行下一行开始）
        int successCount = 0;
        
        for (AddPartInventoryRequest request : requests) {
            try {
                // 使用校验器触发注解规则
                Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
                Set<ConstraintViolation<AddPartInventoryRequest>> violations = validator.validate(request);

                // 收集校验错误
                if (!violations.isEmpty()) {
                    List<String> errors = new ArrayList<>();
                    for (ConstraintViolation<AddPartInventoryRequest> violation : violations) {
                        errors.add(violation.getMessage());
                    }
                    
                    Map<String, Object> errorEntry = new HashMap<>();
                    errorEntry.put("row", rowNumber);
                    errorEntry.put("error", String.join("; ", errors));
                    errorEntry.put("data", request.toString());
                    errorList.add(errorEntry);
                } else {
                    // 调用 Service 插入数据
                    partInventoryService.insertRecord(request);
                    successCount++;
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
            return AjaxResult.error("导入完成，但有部分错误", result);
        }
        
        return AjaxResult.success("导入成功，共导入 " + successCount + " 条数据");
    }

    @ApiOperation(value = "获取胶件出入库详情")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:part-inventory:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(partInventoryService.getById(id));
    }

    @ApiOperation(value = "新增胶件出入库记录")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:part-inventory:add')")
    @PostMapping()
    public AjaxResult add(@RequestBody AddPartInventoryRequest request) {
        return toAjax(partInventoryService.insertRecord(request));
    }

    @ApiOperation(value = "修改胶件出入库记录")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:part-inventory:edit')")
    @PutMapping()
    public AjaxResult edit(@RequestBody UpdatePartInventoryRequest request) {
        return toAjax(partInventoryService.updateRecord(request));
    }

    @ApiOperation(value = "删除胶件出入库记录")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:part-inventory:remove')")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable List<Integer> ids) {
        return toAjax(partInventoryService.deleteByIds(ids));
    }

    @ApiOperation(value = "查询胶件库存")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:part-inventory:store')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderCode", value = "订单编号", dataType = "String"),
            @ApiImplicitParam(name = "mouldNumber", value = "模具编号", dataType = "String"),
            @ApiImplicitParam(name = "updateTimeFrom", value = "更新时间起始", dataType = "datetime"),
            @ApiImplicitParam(name = "updateTimeTo", value = "更新时间终止", dataType = "datetime")
    })
    @GetMapping("/store")
    public TableDataInfo listStore(
            @RequestParam(required = false) String orderCode,
            @RequestParam(required = false) String mouldNumber,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime updateTimeFrom,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime updateTimeTo) {

        ErpPartInventory query = new ErpPartInventory();
        query.setOrderCode(orderCode);
        query.setMouldNumber(mouldNumber);

        startPage();
        List<ErpPartInventory> list = partInventoryService.ListStore(query, updateTimeFrom, updateTimeTo);
        return getDataTable(list);
    }

    @GetMapping("/template")
    @ApiOperation("下载胶件出入库导入模板")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:part-inventory:import')")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        // 创建示例数据行（提供合理的示例值）
        List<AddPartInventoryRequest> templateData = new ArrayList<>();
        
        // 示例1：入库记录
        AddPartInventoryRequest inExample = new AddPartInventoryRequest();
        inExample.setOrderCode("ORD-2024-001");       // 订单编号示例
        inExample.setMouldNumber("MD-CASE-001");      // 模具编号示例
        inExample.setColorCode("RED-001");            // 颜色代码示例
        inExample.setInOutQuantity(200);              // 入库数量（正数）
        inExample.setChangeDate(new Date());          // 变更日期
        inExample.setRemarks("新模具入库");           // 备注信息
        templateData.add(inExample);
        
        // 示例2：出库记录
        AddPartInventoryRequest outExample = new AddPartInventoryRequest();
        outExample.setOrderCode("ORD-2024-002");      // 订单编号示例
        outExample.setMouldNumber("MD-CASE-002");     // 模具编号示例
        outExample.setColorCode("BLUE-002");          // 颜色代码示例
        outExample.setInOutQuantity(-80);             // 出库数量（负数）
        outExample.setChangeDate(new Date());         // 变更日期
        outExample.setRemarks("生产消耗");            // 备注信息
        templateData.add(outExample);

        // 创建ExcelUtil实例并导出模板
        ExcelUtil<AddPartInventoryRequest> util = new ExcelUtil<>(AddPartInventoryRequest.class);
        util.exportExcel(response, templateData, "胶件出入库导入模板");
    }
    
    @ApiOperation(value = "修改胶件库存安全阈值")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:part-inventory:edit')")
    @PutMapping("/safe-quantity/{inventoryId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "inventoryId", value = "库存编号", dataType = "int"),
            @ApiImplicitParam(name = "safeQuantity", value = "新安全库存", dataType = "int"),
    })
    public AjaxResult updateSafeQuantity(
            @PathVariable("inventoryId") Long inventoryId,
            @RequestParam("safeQuantity") Integer safeQuantity) {
        
        if (safeQuantity < 0) {
            return AjaxResult.error("安全库存阈值不能为负数");
        }
        
        int result = partInventoryService.updateSafeQuantity(inventoryId, safeQuantity);
        return toAjax(result);
    }
}