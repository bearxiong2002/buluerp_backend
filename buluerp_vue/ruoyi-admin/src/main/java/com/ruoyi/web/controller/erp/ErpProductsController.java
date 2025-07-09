package com.ruoyi.web.controller.erp;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.web.controller.tool.ExcelImageImportUtil;
import com.ruoyi.web.exception.ImportException;
import com.ruoyi.web.request.product.AddProductRequest;
import com.ruoyi.web.request.product.ListProductRequest;
import com.ruoyi.web.request.product.UpdateProductRequest;
import com.ruoyi.web.domain.ErpProducts;
import com.ruoyi.web.service.IErpProductsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/system/products")
@Api(value = "产品相关")
public class ErpProductsController extends BaseController {

    @Autowired
    private IErpProductsService erpProductsService;

    @ApiOperation(value = "获得产品列表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:products:list')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "产品ID", dataType = "long", paramType = "query", example = "1"),
            @ApiImplicitParam(name = "name", value = "产品名称", dataType = "string", paramType = "query", example = "智能手机"),
            @ApiImplicitParam(name = "createUsername", value = "创建用户名称", dataType = "string", paramType = "query", example = "admin"),
            @ApiImplicitParam(name = "createTimeFrom", value = "创建时间起始", dataType = "string", paramType = "query", format = "date-time", example = "2023-01-01 00:00:00"),
            @ApiImplicitParam(name = "createTimeTo", value = "创建时间终止", dataType = "string", paramType = "query", format = "date-time", example = "2023-12-31 23:59:59"),
            @ApiImplicitParam(name = "designStatus", value = "设计状态(0未完成/1完成)", dataType = "integer", paramType = "query", allowableValues = "0,1", example = "1"),
            @ApiImplicitParam(name = "orderId", value = "订单id", dataType = "integer"),
            @ApiImplicitParam(name = "innerId", value = "内部编号", dataType = "String"),
            @ApiImplicitParam(name = "outerId", value = "外部编号", dataType = "String")})

    @GetMapping("/list")
    public TableDataInfo list(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String innerId,
            @RequestParam(required = false) String outerId,
            @RequestParam(required = false) Integer orderId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String createUsername,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime createTimeFrom,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime createTimeTo,
            @RequestParam(name = "designStatus", required = false) Integer designStatus) {

        ListProductRequest request = new ListProductRequest();
        request.setId(id);
        request.setInnerId(innerId);
        request.setOuterId(outerId);
        request.setOrderId(orderId);
        request.setName(name);
        request.setCreateUsername(createUsername);
        request.setCreateTimeFrom(createTimeFrom);
        request.setCreateTimeTo(createTimeTo);
        request.setDesignStatus(designStatus);

        startPage();
        List<ErpProducts> list = erpProductsService.selectErpProductsList(request);
        return getDataTable(list);
    }

    @ApiOperation(value = "导出产品列表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:products:export')")
    @PostMapping("/export/{ids}")
    public void export(HttpServletResponse response,@PathVariable  List<Integer> ids) {
        List<ErpProducts> list = erpProductsService.selectListByIds(ids);
        ExcelUtil<ErpProducts> util = new ExcelUtil<ErpProducts>(ErpProducts.class);
        String fileName = "产品数据_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) ;
        util.exportExcel(response, list, fileName);
    }

    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:products:import')")
    @PostMapping("/import")
    @ApiOperation(value = "导入产品")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file) throws IOException {
        List<AddProductRequest> requests;
        try {
            ExcelUtil<AddProductRequest> util = new ExcelUtil<>(AddProductRequest.class);
            requests = util.importExcel(file.getInputStream());
        } catch (Exception e) {
            return AjaxResult.error("Excel解析失败: " + e.getMessage());
        }

        // 单独处理图片 - 使用ExcelImageImportUtil来获取DISPIMG格式的图片
        Map<Integer, String> pictureMap = new HashMap<>();
        try {
            // 使用工具类直接获取图片，第4列（索引3）
            Map<Integer, List<String>> productImages = 
                ExcelImageImportUtil.importInvoiceImages(
                    file.getInputStream(), 5); // 图片在第4列（索引3）
            
            // 将图片数据映射到对应的行（转换为从0开始的索引）
            productImages.forEach((rowIndex, images) -> {
                if (!images.isEmpty()) {
                    // 取第一张图片，rowIndex是从1开始的，转换为从0开始
                    pictureMap.put(rowIndex - 1, images.get(0));
                }
            });
        } catch (Exception e) {
            System.err.println("图片处理失败，但不影响其他数据导入: " + e.getMessage());
        }

        List<Map<String, Object>> errorList = new ArrayList<>();
        int rowNumber = 0;
        for (int i = 0; i < requests.size(); i++) {
            AddProductRequest request = requests.get(i);
            request.setRowNumber(rowNumber++); // 记录行号
            try {
                // 1. 设置图片数据
                if (pictureMap.containsKey(i)) {
                    request.setPictureStr(pictureMap.get(i));
                }
                
                // 2. 尝试图片转换
                request.convertPictureStrToMultipartFile();

                // 3. 使用校验器触发注解规则
                Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
                Set<ConstraintViolation<AddProductRequest>> violations = validator.validate(request);

                // 4. 收集校验错误
                if (!violations.isEmpty()) {
                    List<String> errors = new ArrayList<>();
                    for (ConstraintViolation<AddProductRequest> violation : violations) {
                        errors.add(violation.getMessage());
                    }
                    throw new ImportException(rowNumber, String.join("; ", errors), request.toString());
                }

                // 5. 替换中文逗号并去除空格
                String materialStr = request.getMaterialString();
                String normalized = materialStr.replace("，", ",").replaceAll("\\s+", "");
                if (!normalized.matches("^\\d+(,\\d+)*$")) { // 正则表达式校验
                    throw new ImportException(request.getRowNumber(), "物料ID列表格式错误（必须为逗号分隔的整数）", request.toString());
                }
            } catch (ImportException e) {
                Map<String, Object> errorEntry = new HashMap<>();
                errorEntry.put("row", e.getRowNumber());
                errorEntry.put("error",e.getMessage());
                errorEntry.put("data", e.getRawData());
                errorList.add(errorEntry);
            } catch (Exception e) {
                Map<String, Object> errorEntry = new HashMap<>();
                errorEntry.put("row", request.getRowNumber());
                errorEntry.put("error", "系统错误: " + e.getMessage());
                errorEntry.put("data", request.toString());
                errorList.add(errorEntry);
            }
        }

        if (!errorList.isEmpty()) {
            return AjaxResult.error("导入失败", errorList);
        }
        else {
            for (AddProductRequest request:requests){
                // 6. 调用Service层处理
                erpProductsService.processMaterialIds(request);
                erpProductsService.insertErpProducts(request);
            }

        }
        return AjaxResult.success("导入成功",rowNumber);
    }

    @ApiOperation(value = "获得产品详细信息")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:products:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(erpProductsService.getById(id));
    }

    @ApiOperation(value = "新增产品")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:products:add')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult add(@ModelAttribute AddProductRequest addProductRequest) throws IOException {
        return toAjax(erpProductsService.insertErpProducts(addProductRequest));
    }

    @ApiOperation(value = "修改产品")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:products:edit')")
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult edit(@ModelAttribute UpdateProductRequest updateProductRequest) throws IOException {
        return toAjax(erpProductsService.updateErpProducts(updateProductRequest));
    }

    @ApiOperation(value = "删除产品")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:products:remove')")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable List<Integer> ids) {
        return toAjax(erpProductsService.deleteErpProductsByIds(ids));
    }

    @GetMapping("/template")
    @ApiOperation("下载导入模板")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:products:import')") // 权限校验（按需添加）
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        // 1. 创建示例数据（一行合法数据）
        List<AddProductRequest> templateData = new ArrayList<>();
        AddProductRequest example = new AddProductRequest();
        example.setOrderId(123); // 订单ID（整数）
        example.setInnerId("inner123");
        example.setOuterId("outer123");
        example.setName("示例产品"); // 产品名称（非空）
        example.setMaterialString("1,2,3"); // 物料ID列表（逗号分隔整数）
        example.setPictureStr("请在此单元格插入图片"); // 图片字段提示
        templateData.add(example);

        // 2. 使用 ExcelUtil 导出
        ExcelUtil<AddProductRequest> util = new ExcelUtil<>(AddProductRequest.class);
        util.exportExcel(response, templateData, "产品导入模板");
    }
}