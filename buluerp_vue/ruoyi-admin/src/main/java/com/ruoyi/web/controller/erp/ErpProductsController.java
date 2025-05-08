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
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    @GetMapping("/list")
    public TableDataInfo list(ListProductRequest listProductRequest) {
        startPage();
        List<ErpProducts> list = erpProductsService.selectErpProductsList(listProductRequest);
        return getDataTable(list);
    }

    @ApiOperation(value = "导出产品列表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:products:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, ListProductRequest listProductRequest) {
        List<ErpProducts> list = erpProductsService.selectErpProductsList(listProductRequest);
        ExcelUtil<ErpProducts> util = new ExcelUtil<ErpProducts>(ErpProducts.class);
        util.exportExcel(response, list, "产品数据");
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
    @PostMapping
    public AjaxResult add(@RequestBody AddProductRequest addProductRequest) throws IOException {
        return toAjax(erpProductsService.insertErpProducts(addProductRequest));
    }

    @ApiOperation(value = "修改产品")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:products:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody UpdateProductRequest updateProductRequest) throws IOException {
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