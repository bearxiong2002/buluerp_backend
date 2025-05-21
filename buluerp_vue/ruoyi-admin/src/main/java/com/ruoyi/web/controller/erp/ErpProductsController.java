package com.ruoyi.web.controller.erp;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.poi.ExcelUtil;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

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
            @ApiImplicitParam(name = "design_status", value = "设计状态(0未完成/1完成)", dataType = "integer", paramType = "query", allowableValues = "0,1", example = "1")})
    @GetMapping("/list")
    public TableDataInfo list(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String createUsername,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime createTimeFrom,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime createTimeTo,
            @RequestParam(name = "designStatus", required = false) Integer designStatus) {

        ListProductRequest request = new ListProductRequest();
        request.setId(id);
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
    @PostMapping("/export")
    public void export(HttpServletResponse response, ListProductRequest listProductRequest) {
        List<ErpProducts> list = erpProductsService.selectErpProductsList(listProductRequest);
        ExcelUtil<ErpProducts> util = new ExcelUtil<ErpProducts>(ErpProducts.class);
        // 2. 生成动态文件名（示例：产品数据_20231025.xlsx）
        String fileName = "产品数据_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) ;
        util.exportExcel(response, list, fileName);
    }

    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:products:import')")
    @PostMapping("/import")
    @ApiOperation(value = "导入产品")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file) throws IOException {
        ExcelUtil<AddProductRequest> util = new ExcelUtil<AddProductRequest>(AddProductRequest.class);
        List<AddProductRequest> erpProductsList = util.importExcel(file.getInputStream());
        int count = 0;
        for (AddProductRequest addProductRequest : erpProductsList) {
            erpProductsService.insertErpProducts(addProductRequest);
            count++;
        }
        return success(count);
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
}