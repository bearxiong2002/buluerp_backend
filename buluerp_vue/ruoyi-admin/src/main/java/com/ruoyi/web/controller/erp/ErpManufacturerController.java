package com.ruoyi.web.controller.erp;

import com.github.pagehelper.PageHelper;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.web.domain.ErpManufacturer;
import com.ruoyi.web.exception.ImportException;
import com.ruoyi.web.request.manufacturer.AddManufacturerRequest;
import com.ruoyi.web.request.manufacturer.ListManufacturerRequest;
import com.ruoyi.web.request.manufacturer.UpdateManufacturerRequest;
import com.ruoyi.web.request.product.AddProductRequest;
import com.ruoyi.web.service.IErpManufacturerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
@RequestMapping("/system/manufacturer")
@Api(value = "厂家相关")
public class ErpManufacturerController extends BaseController {

    @Autowired
    private IErpManufacturerService erpManufacturerService;

    /**
     * 查询厂家列表
     */
    @ApiOperation(value = "获得厂家列表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:manufacturer:list')")
    @GetMapping("/list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "厂家ID（可选）", dataType = "Long", paramType = "query", example = "1"),
            @ApiImplicitParam(name = "name", value = "厂家名称（可选）", dataType = "String", paramType = "query", example = "华为"),
            @ApiImplicitParam(name = "tel", value = "联系方式（可选）", dataType = "String", paramType = "query", example = "13800138000"),
            @ApiImplicitParam(name = "email", value = "邮箱地址（可选）", dataType = "String", paramType = "query", example = "contact@huawei.com"),
            @ApiImplicitParam(name = "remark", value = "备注", dataType = "string"),
            @ApiImplicitParam(name = "createTimeFrom", value = "创建时间起始", dataType = "date"),
            @ApiImplicitParam(name = "createTimeTo", value = "创建时间终止", dataType = "date"),
            @ApiImplicitParam(name = "pageNum", value = "页码（默认1）", dataType = "Integer", paramType = "query", example = "1"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量（默认10）", dataType = "Integer", paramType = "query", example = "10")
    })
    public TableDataInfo list(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String tel,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String remark,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(required = false) Date createTimeFrom,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(required = false) Date createTimeTo,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        // 手动封装查询条件
        ListManufacturerRequest request = new ListManufacturerRequest();
        request.setId(id);
        request.setName(name);
        request.setTel(tel);
        request.setEmail(email);
        request.setCreateTimeFrom(createTimeFrom);
        request.setCreateTimeTo(createTimeTo);
        request.setRemark(remark);

        // 若依框架分页设置
        //PageHelper.startPage(pageNum, pageSize);
        startPage();
        List<ErpManufacturer> list = erpManufacturerService.selectErpManufacturerList(request);
        return getDataTable(list);
    }

    /**
     * 导出【请填写功能名称】列表
     */
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:manufacturer:export')")
    @Log(title = "导出厂家列表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation(value = "导出厂家列表")
    public void export(HttpServletResponse response, Long[] ids)
    {
        List<ErpManufacturer> list = erpManufacturerService.selectErpManufacturerListByIds(ids);
        ExcelUtil<ErpManufacturer> util = new ExcelUtil<ErpManufacturer>(ErpManufacturer.class);
        String fileName = "厂家数据_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) ;
        util.exportExcel(response, list, fileName);
    }

    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:manufacturer:import')")
    @PostMapping("/import")
    @ApiOperation(value = "导入厂家")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file) {
        ExcelUtil<AddManufacturerRequest> util = new ExcelUtil<>(AddManufacturerRequest.class);
        List<AddManufacturerRequest> requests;
        List<Map<String, Object>> errorList = new ArrayList<>();

        try {
            requests = util.importExcel(file.getInputStream());
        } catch (Exception e) {
            return new AjaxResult(HttpStatus.CONFLICT,"Excel解析失败: " + e.getMessage());
        }

        if (requests == null) {
            return new AjaxResult(HttpStatus.CONFLICT,"导入数据为空，请填写数据后上传");
        }
        requests.removeAll(Collections.singleton(null));
        if (requests.isEmpty()) {
            return AjaxResult.error("导入数据为空，请填写数据后上传");
        }


        int rowNumber = 1; // 数据行号（从标题行下一行开始）
        for (AddManufacturerRequest request : requests) {
            try {
                // 使用校验器触发注解规则
                Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
                Set<ConstraintViolation<AddManufacturerRequest>> violations = validator.validate(request);

                // 收集校验错误
                if (!violations.isEmpty()) {
                    List<String> errors = new ArrayList<>();
                    for (ConstraintViolation<AddManufacturerRequest> violation : violations) {
                        errors.add(violation.getMessage());
                    }
                    throw new ImportException(rowNumber, String.join("; ", errors), request.toString());
                }

            } catch (ImportException e) {
                // Java 8 兼容的 Map 初始化
                Map<String, Object> errorEntry = new HashMap<>();
                errorEntry.put("row", e.getRowNumber());
                errorEntry.put("error", e.getErrorMsg());
                errorEntry.put("data", e.getRawData());
                errorList.add(errorEntry);
            } catch (Exception e) {
                Map<String, Object> errorEntry = new HashMap<>();
                errorEntry.put("row", rowNumber);
                errorEntry.put("error", "系统错误: " + e.getMessage());
                errorEntry.put("data", request.toString());
                errorList.add(errorEntry);
            }
            rowNumber++;
        }

        if (!errorList.isEmpty()) {
            return new AjaxResult(HttpStatus.CONFLICT,"导入失败", errorList);
        }
        else{
            for (AddManufacturerRequest request : requests)
                erpManufacturerService.insertErpManufacturer(request);
        }
        return AjaxResult.success("导入成功");
    }

    /**
     * 获取厂家详细信息
     */
    @ApiOperation(value = "获得厂家详细信息")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:manufacturer:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(erpManufacturerService.selectErpManufacturerById(id));
    }

    /**
     * 新增厂家
     */
    @ApiOperation(value = "新增厂家")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:manufacturer:add')")
    @Log(title = "新增厂家", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody AddManufacturerRequest addManufacturerRequest)
    {
        return toAjax(erpManufacturerService.insertErpManufacturer(addManufacturerRequest));
    }

    /**
     * 修改厂家信息
     */
    @ApiOperation(value = "修改厂家")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:manufacturer:edit')")
    @Log(title = "修改厂家", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody UpdateManufacturerRequest updateManufacturerRequest)
    {
        return toAjax(erpManufacturerService.updateErpManufacturer(updateManufacturerRequest));
    }

    /**
     * 删除厂家
     */
    @ApiOperation(value = "删除厂家")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:manufacturer:remove')")
    @Log(title = "删除厂家", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable List<Integer> ids)
    {
        return toAjax(erpManufacturerService.deleteErpManufacturerByIds(ids));
    }

    @GetMapping("/template")
    @ApiOperation("下载厂家导入模板")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:manufacturer:import')") // 按需添加权限
    public void downloadManufacturerTemplate(HttpServletResponse response) throws IOException {
        List<AddManufacturerRequest> templateData = new ArrayList<>();
        AddManufacturerRequest example = new AddManufacturerRequest();
        example.setName("示例厂家名称");      // 厂家名称（必填）
        example.setTel("021-12345678");    // 联系方式（可选，但需符合格式）
        example.setEmail("demo@example.com"); // 邮箱（可选，需符合格式）
        example.setRemark("这是示例备注，长度不超过200字符"); // 备注（可选）
        templateData.add(example);

        // 2. 使用 ExcelUtil 导出模板
        ExcelUtil<AddManufacturerRequest> util = new ExcelUtil<>(AddManufacturerRequest.class);
        util.exportExcel(response, templateData, "厂家导入模板");
    }
}
