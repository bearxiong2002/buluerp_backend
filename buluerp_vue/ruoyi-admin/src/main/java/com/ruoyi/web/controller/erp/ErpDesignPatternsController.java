package com.ruoyi.web.controller.erp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.web.request.design.AddDesignPatternsRequest;
import com.ruoyi.web.domain.ErpDesignPatterns;
import com.ruoyi.web.request.product.AddProductRequest;
import com.ruoyi.web.result.DesignPatternsExportDTO;
import com.ruoyi.web.request.design.ListDesignPatternsRequest;
import com.ruoyi.web.request.design.UpdateDesignPatternsRequest;
import com.ruoyi.web.result.DesignPatternsResult;
import com.ruoyi.web.service.IErpDesignPatternsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;

import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 设计总表 Controller
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
@RestController
@RequestMapping("/system/patterns")
@Api(value = "设计总表相关")
public class ErpDesignPatternsController extends BaseController
{
    @Autowired
    private IErpDesignPatternsService erpDesignPatternsService;

    /**
     * 查询设计总表列表
     */
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:patterns:list')")
    @ApiOperation(value = "获得设计总表列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "产品编码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "createUserId", value = "创建用户ID", dataType = "integer", paramType = "query"),
            @ApiImplicitParam(name = "orderId", value = "订单ID", dataType = "integer", paramType = "query"),
            @ApiImplicitParam(name = "confirm", value = "PMC确认状态(0未确认/1已确认)", dataType = "integer", paramType = "query")
    })
    @GetMapping("/list")
    public TableDataInfo list(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long createUserId,
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) Long confirm) {

        ListDesignPatternsRequest request = new ListDesignPatternsRequest();
        request.setProductId(productId);
        request.setCreateUserId(createUserId);
        request.setOrderId(orderId);
        request.setConfirm(confirm);

        startPage();
        List<ErpDesignPatterns> list = erpDesignPatternsService.selectErpDesignPatternsList(request);
        return getDataTable(list);
    }

    /**
     * 导出【请填写功能名称】列表
     */
    @ApiOperation(value = "导出设计总表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:patterns:export')")
    //@Log(title = "导出", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Long[] ids)
    {
        // 1. 查询原始数据
        ListDesignPatternsRequest request = new ListDesignPatternsRequest();
        List<ErpDesignPatterns> originList = erpDesignPatternsService.selectErpDesignPatternsList(request);

        // 2. 转换为 DTO 列表
        List<DesignPatternsExportDTO> exportList = new ArrayList<>();
        for (ErpDesignPatterns designPatterns : originList) {
            // 获取原始结果对象
            DesignPatternsResult result = erpDesignPatternsService.selectErpDesignPatternsById(designPatterns.getId());
            // 转换为 DTO
            exportList.add(new DesignPatternsExportDTO(result));
        }

        // 3. 使用 ExcelUtil 导出 DTO
        ExcelUtil<DesignPatternsExportDTO> util = new ExcelUtil<>(DesignPatternsExportDTO.class);
        util.exportExcel(response, exportList, "设计总表数据");
    }

    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:patterns:import')")
    @PostMapping("/import")
    @ApiOperation(value = "导入设计总表")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file) throws IOException {
        ExcelUtil<AddDesignPatternsRequest> util = new ExcelUtil<>(AddDesignPatternsRequest.class);
        List<AddDesignPatternsRequest> addDesignPatternsRequestList;
        List<Map<String, Object>> errorList = new ArrayList<>();

        try {
            addDesignPatternsRequestList = util.importExcel(file.getInputStream());
        } catch (Exception e) {
            return AjaxResult.error("Excel解析失败: " + e.getMessage());
        }

        int rowNumber = 1; // Excel数据行号从2开始（标题行+1）
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        for (int i = 0; i < addDesignPatternsRequestList.size(); i++) {
            AddDesignPatternsRequest addDesignPatternsRequest = addDesignPatternsRequestList.get(i);
            Map<String, Object> errorEntry = new HashMap<>();
            boolean hasError = false;

            try {
                // 1. 执行校验
                Set<ConstraintViolation<AddDesignPatternsRequest>> violations = validator.validate(addDesignPatternsRequest);

                // 2. 收集校验错误
                if (!violations.isEmpty()) {
                    List<String> errors = new ArrayList<>();
                    for (ConstraintViolation<AddDesignPatternsRequest> violation : violations) {
                        errors.add(violation.getPropertyPath() + ": " + violation.getMessage());
                    }
                    errorEntry.put("error", String.join("; ", errors));
                    hasError = true;
                }

                // 3. 尝试保存（包括业务逻辑校验）
                if (!hasError) {
                    erpDesignPatternsService.insertErpDesignPatterns(addDesignPatternsRequest);
                }
            } catch (Exception e) {
                // 处理服务层的异常
                String errorMsg = e.getMessage();
                if (e.getCause() != null) {
                    errorMsg += " (" + e.getCause().getMessage() + ")";
                }
                errorEntry.put("error", errorMsg);
                hasError = true;
            }

            // 如果本行有错误，添加到错误列表
            if (hasError) {
                errorEntry.put("row", rowNumber);
                errorEntry.put("data", "订单号: " + addDesignPatternsRequest.getOrderId() + 
                                     ", 产品号: " + addDesignPatternsRequest.getProductId());
                errorList.add(errorEntry);
            }

            rowNumber++;
        }

        // 处理结果
        if (!errorList.isEmpty()) {
            // 计算成功数量
            int successCount = addDesignPatternsRequestList.size() - errorList.size();

            // 构造详细错误报告
            Map<String, Object> result = new HashMap<>();
            result.put("total", addDesignPatternsRequestList.size());
            result.put("success", successCount);
            result.put("failure", errorList.size());
            result.put("errors", errorList);

            return AjaxResult.error("导入完成，但有部分错误", result);
        }

        return AjaxResult.success("导入成功，共导入 " + addDesignPatternsRequestList.size() + " 条数据");
    }

    @GetMapping("/template")
    @ApiOperation("下载设计总表导入模板")
    //@PreAuthorize("@ss.hasPermi('system:patterns:import')")
    @Anonymous
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        // 创建示例数据行（使用合理默认值）
        List<AddDesignPatternsRequest> templateData = new ArrayList<>();
        AddDesignPatternsRequest example = new AddDesignPatternsRequest();

        // 设置必填字段示例值（符合验证规则）
        example.setOrderId(2024001L);       // 示例订单号
        example.setProductId(1001L);        // 示例产品号

        templateData.add(example);

        // 创建ExcelUtil实例
        ExcelUtil<AddDesignPatternsRequest> util = new ExcelUtil<>(AddDesignPatternsRequest.class);

        // 导出模板
        util.exportExcel(response, templateData, "设计总表导入模板");
    }

    /**
     * 获取设计总表详细信息
     */
    @ApiOperation(value = "获得设计总表详细信息 /{id}")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:patterns:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(erpDesignPatternsService.selectErpDesignPatternsById(id));
    }

    /**
     * 新增设计总表
     */
    @ApiOperation(value = "新增设计总表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:patterns:add')")
    //@Log(title = "新增设计总表", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AddDesignPatternsRequest addDesignPatternsRequest)
    {
        return toAjax(erpDesignPatternsService.insertErpDesignPatterns(addDesignPatternsRequest));
    }

    /**
     * 修改设计总表
     */
    @ApiOperation(value = "修改设计总表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:patterns:edit')")
    //@Log(title = "修改设计总表", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody UpdateDesignPatternsRequest updateDesignPatternsRequest)
    {
        return toAjax(erpDesignPatternsService.updateErpDesignPatterns(updateDesignPatternsRequest));
    }

    /**
     * 删除设计总表
     */
    @ApiOperation(value = "删除设计总表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:patterns:remove')")
    //@Log(title = "删除设计总表", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable List<Integer> ids)
    {
        return toAjax(erpDesignPatternsService.deleteErpDesignPatternsByIds(ids));
    }

    /**
     * pmc确认设计总表
     */
    @ApiOperation(value = "pmc确认设计总表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:patterns:confirm')")
    //@Log(title = "pmc确认设计总表", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    public AjaxResult confirm(@PathVariable Long id)
    {
        return toAjax(erpDesignPatternsService.confirmErpDesignPatternsById(id));
    }

    /**
     * pmc确认设计总表
     */
    @ApiOperation(value = "pmc取消确认设计总表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:patterns:confirm')")
    //@Log(title = "pmc取消确认设计总表", businessType = BusinessType.UPDATE)
    @PutMapping("/cancel/{id}")
    public AjaxResult cancel(@PathVariable Long id)
    {
        return toAjax(erpDesignPatternsService.cancelConfirmById(id));
    }
}
