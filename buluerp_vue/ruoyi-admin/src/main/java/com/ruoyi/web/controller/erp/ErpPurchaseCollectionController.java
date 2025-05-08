package com.ruoyi.web.controller.erp;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.web.domain.ErpPurchaseCollection;
import com.ruoyi.web.mapper.ErpPurchaseCollectionMapper;
import com.ruoyi.web.service.IErpPurchaseCollectionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/system/purchase-collection")
@Api(value = "采购计划相关")
public class ErpPurchaseCollectionController extends BaseController {
    @Autowired
    private IErpPurchaseCollectionService erpPurchaseCollectionService;

    // @PreAuthorize("@ss.hasPermi('system:purchase-collection:list')")
    @Anonymous
    @GetMapping("/list")
    @ApiOperation(value = "获取采购计划列表", notes = "获取采购计划列表")
    public TableDataInfo list(ErpPurchaseCollection erpPurchaseCollection) {
        startPage();
        List<ErpPurchaseCollection> tableDataInfos = erpPurchaseCollectionService
                .selectErpPurchaseCollectionList(erpPurchaseCollection);
        return getDataTable(tableDataInfos);
    }

    // @PreAuthorize("@ss.hasPermi('system:purchase-collection:export')")
    @Anonymous
    @PostMapping("/export")
    @ApiOperation(value = "导出采购计划列表", notes = "导出采购计划列表")
    public void export(HttpServletResponse response, ErpPurchaseCollection erpPurchaseCollection) {
        List<ErpPurchaseCollection> list = erpPurchaseCollectionService.selectErpPurchaseCollectionList(erpPurchaseCollection);
        ExcelUtil<ErpPurchaseCollection> util = new ExcelUtil<>(ErpPurchaseCollection.class);
        util.exportExcel(response, list, "采购计划数据");
    }

    // @PreAuthorize("@ss.hasPermi('system:purchase-collection:import')")
    @Anonymous
    @PostMapping("/import")
    @ApiOperation(value = "导入采购计划列表", notes = "导入采购计划列表")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file) throws IOException {
        ExcelUtil<ErpPurchaseCollection> excelUtil = new ExcelUtil<>(ErpPurchaseCollection.class);
        List<ErpPurchaseCollection> list = excelUtil.importExcel(file.getInputStream());
        return toAjax(erpPurchaseCollectionService.insertErpPurchaseCollections(list));
    }

    // @PreAuthorize("@ss.hasPermi('system:purchase-collection:add')")
    @Anonymous
    @PostMapping
    @ApiOperation(value = "新增采购计划", notes = "新增采购计划")
    public AjaxResult add(@RequestBody ErpPurchaseCollection erpPurchaseCollection) {
        return toAjax(erpPurchaseCollectionService.insertErpPurchaseCollection(erpPurchaseCollection));
    }

    // @PreAuthorize("@ss.hasPermi('system:purchase-collection:edit')")
    @Anonymous
    @PutMapping
    @ApiOperation(value = "修改采购计划", notes = "修改采购计划")
    public AjaxResult edit(@RequestBody ErpPurchaseCollection erpPurchaseCollection) {
        return toAjax(erpPurchaseCollectionService.updateErpPurchaseCollection(erpPurchaseCollection));
    }

    // @PreAuthorize("@ss.hasPermi('system:purchase-collection:remove')")
    @Anonymous
    @DeleteMapping("/{ids}")
    @ApiOperation(value = "删除采购计划", notes = "删除采购计划")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(erpPurchaseCollectionService.deleteErpPurchaseCollectionByIds(ids));
    }
}
