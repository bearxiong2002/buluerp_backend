package com.ruoyi.web.controller.erp;

import java.io.IOException;
import java.util.*;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.web.controller.tool.ExcelImageImportUtil;
import com.ruoyi.web.domain.ErpDesignStyle;
import com.ruoyi.web.request.design.AddDesignRequest;
import com.ruoyi.web.request.design.ListDesignRequest;
import com.ruoyi.web.request.design.UpdateDesignRequest;
import com.ruoyi.web.service.IErpDesignStyleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;

import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 设计造型Controller
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
@RestController
@RequestMapping("/system/style")
@Api(value = "造型表相关")
public class ErpDesignStyleController extends BaseController
{
    @Autowired
    private IErpDesignStyleService erpDesignStyleService;

    /**
     * 查询设计造型列表
     */
    @ApiOperation(value = "获取造型表列表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:style:list')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "造型表ID(可选)", dataType = "integer", paramType = "query", example = "1"),
            @ApiImplicitParam(name = "groupId", value = "分组编号", dataType = "integer", paramType = "query", example = "100"),
            @ApiImplicitParam(name = "designPatternId", value = "主设计编号", dataType = "integer", paramType = "query", required = true, example = "2023")
    })
    @GetMapping("/list")
    public TableDataInfo list(
            @RequestParam(name = "id", required = false) Long id,
            @RequestParam(name = "groupId", required = false) Long groupId,
            @RequestParam(name = "designPatternId") @NotNull(message = "主设计编号不能为空") Long designPatternId) {

        ListDesignRequest request = new ListDesignRequest();
        request.setId(id);
        request.setGroupId(groupId);
        request.setDesignPatternId(designPatternId);

        startPage();
        List<ErpDesignStyle> list = erpDesignStyleService.selectErpDesignStyleList(request);
        return getDataTable(list);
    }

    /**
     * 导出设计造型列表
     */
    @ApiOperation(value = "导出造型表列表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:style:export')")
    @Log(title = "设计造型", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ListDesignRequest listDesignRequest)
    {
        List<ErpDesignStyle> list = erpDesignStyleService.selectErpDesignStyleList(listDesignRequest);
        ExcelUtil<ErpDesignStyle> util = new ExcelUtil<ErpDesignStyle>(ErpDesignStyle.class);
        util.exportExcel(response, list, "设计造型数据");
    }

    /**
     * 获取设计造型详细信息
     */
    @ApiOperation(value = "获取造型表详细信息")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:style:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(erpDesignStyleService.selectErpDesignStyleById(id));
    }

    /**
     * 新增设计造型
     */
    @ApiOperation(value = "新增造型表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:style:add')")
    @Log(title = "新增造型表", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@ModelAttribute AddDesignRequest addDesignRequest) throws IOException {
        return toAjax(erpDesignStyleService.insertErpDesignStyle(addDesignRequest));
    }

    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:style:import')")
    @PostMapping("/import")
    @ApiOperation(value = "导入造型表")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file) throws IOException {
        ExcelUtil<AddDesignRequest> util = new ExcelUtil<>(AddDesignRequest.class);
        List<AddDesignRequest> addDesignRequestList;
        List<Map<String, Object>> errorList = new ArrayList<>();

        try {
            addDesignRequestList = util.importExcel(file.getInputStream());
        } catch (Exception e) {
            return AjaxResult.error("Excel解析失败: " + e.getMessage());
        }

        // 单独处理图片 - 使用ExcelImageImportUtil来获取DISPIMG格式的图片
        Map<Integer, String> pictureMap = new HashMap<>();
        try {
            // 使用工具类直接获取图片
            Map<Integer, List<String>> invoiceImages = 
                ExcelImageImportUtil.importInvoiceImages(
                    file.getInputStream(), 9); // 图片在第10列（索引9）
            
            // 将图片数据映射到对应的行（转换为从0开始的索引）
            invoiceImages.forEach((rowIndex, images) -> {
                if (!images.isEmpty()) {
                    // 取第一张图片，rowIndex是从1开始的，转换为从0开始
                    pictureMap.put(rowIndex - 1, images.get(0));
                }
            });
        } catch (Exception e) {
            System.err.println("图片处理失败，但不影响其他数据导入: " + e.getMessage());
        }

        int rowNumber = 1; // Excel数据行号从2开始（标题行+1）
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        for (int i = 0; i < addDesignRequestList.size(); i++) {
            AddDesignRequest addDesignRequest = addDesignRequestList.get(i);
            Map<String, Object> errorEntry = new HashMap<>();
            boolean hasError = false;

            try {
                // 1. 设置图片数据
                if (pictureMap.containsKey(i)) {
                    addDesignRequest.setPictureStr(pictureMap.get(i));
                }
                
                // 2. 尝试图片转换
                addDesignRequest.convertPictureStrToMultipartFile();

                // 3. 执行校验
                Set<ConstraintViolation<AddDesignRequest>> violations = validator.validate(addDesignRequest);

                // 4. 收集校验错误
                if (!violations.isEmpty()) {
                    List<String> errors = new ArrayList<>();
                    for (ConstraintViolation<AddDesignRequest> violation : violations) {
                        errors.add(violation.getPropertyPath() + ": " + violation.getMessage());
                    }
                    errorEntry.put("error", String.join("; ", errors));
                    hasError = true;
                }

                // 5. 尝试保存（包括业务逻辑校验）
                if (!hasError) {
                    erpDesignStyleService.insertErpDesignStyle(addDesignRequest);
                }
            } catch (Exception e) {
                // 处理转换和服务层的异常
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
                errorEntry.put("data", addDesignRequest.toString());
                errorList.add(errorEntry);
            }

            rowNumber++;
        }

        // 处理结果
        if (!errorList.isEmpty()) {
            // 计算成功数量
            int successCount = addDesignRequestList.size() - errorList.size();

            // 构造详细错误报告
            Map<String, Object> result = new HashMap<>();
            result.put("total", addDesignRequestList.size());
            result.put("success", successCount);
            result.put("failure", errorList.size());
            result.put("errors", errorList);

            return AjaxResult.error("导入完成，但有部分错误", result);
        }

        return AjaxResult.success("导入成功，共导入 " + addDesignRequestList.size() + " 条数据");
    }

    /**
     * 修改设计造型
     */
    @ApiOperation(value = "修改造型表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:style:edit')")
    @Log(title = "修改造型", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody UpdateDesignRequest updateDesignRequest) throws IOException {
        return toAjax(erpDesignStyleService.updateErpDesignStyle(updateDesignRequest));
    }

    /**
     * 删除设计造型
     */
    @ApiOperation(value = "删除造型表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:style:remove')")
    @Log(title = "设计造型", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable List<Integer> ids)
    {
        return toAjax(erpDesignStyleService.deleteErpDesignStyleByIds(ids));
    }

    @GetMapping("/template")
    @ApiOperation("下载造型表导入模板")
    //@PreAuthorize("@ss.hasPermi('system:style:import')")
    @Anonymous
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        // 创建示例数据行（使用合理默认值）
        List<AddDesignRequest> templateData = new ArrayList<>();
        AddDesignRequest example = new AddDesignRequest();

        // 设置必填字段示例值（符合验证规则）
        example.setGroupId(1001L);       // 示例分组编号
        example.setDesignPatternId(2001L);// 示例设计编号
        example.setMouldNumber("MD-2023-001"); // 模具编号示例
        example.setLddNumber("LDD-00123");    // LDD编号示例
        example.setMouldCategory("注塑模具");  // 模具类别示例
        example.setMouldId("MID-001");      // 模具ID示例
        example.setProductName("智能手机外壳"); // 产品名称示例
        example.setQuantity(2L);           // 数量示例
        example.setMaterial("PMMA塑料");     // 材料示例

        // 设置非必填字段示例值
        example.setColor("透明");            // 颜色描述示例

        // 图片字段设置 - 添加清晰的说明文本
        example.setPictureStr("请在此单元格插入图片");

        templateData.add(example);

        // 创建ExcelUtil实例
        ExcelUtil<AddDesignRequest> util = new ExcelUtil<>(AddDesignRequest.class);

        // 导出模板（添加水印效果）
        util.exportExcel(response, templateData, "造型表导入模板");
    }
}
