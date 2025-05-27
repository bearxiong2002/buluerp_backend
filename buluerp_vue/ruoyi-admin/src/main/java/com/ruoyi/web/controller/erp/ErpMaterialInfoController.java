package com.ruoyi.web.controller.erp;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.web.domain.ErpCustomers;
import com.ruoyi.web.domain.ErpMaterialInfo;
import com.ruoyi.web.domain.ErpOrders;
import com.ruoyi.web.service.IErpMaterialInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/system/material-info")
@Api(value = "物料信息相关")
public class ErpMaterialInfoController extends BaseController {
    @Autowired
    private IErpMaterialInfoService erpMaterialInfoService;

    // @PreAuthorize("@ss.hasPermi('system:material-info:list')")
    @Anonymous
    @GetMapping("/list")
    @ApiOperation(value = "获取物料信息列表", notes = "获取物料信息列表")
    public TableDataInfo list(ErpMaterialInfo erpMaterialInfo) {
        startPage();
        List<ErpMaterialInfo> tableDataInfos = erpMaterialInfoService
                .selectErpMaterialInfoList(erpMaterialInfo);
        return getDataTable(tableDataInfos);
    }

    /**
     * 导出物料列表
     */
    // @PreAuthorize("@ss.hasPermi('system:material-info:export')")
    @Log(title = "物料资料", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation(value = "导出物料资料列表", notes = "导出物料资料列表")
    public void export(HttpServletResponse response, Long[] ids)
    {
        List<ErpMaterialInfo> list = erpMaterialInfoService.selectErpMaterialInfoListByIds(ids);
        ExcelUtil<ErpMaterialInfo> util = new ExcelUtil<>(ErpMaterialInfo.class);
        util.exportExcel(response, list, "物料资料数据");
    }

    // @PreAuthorize("@ss.hasPermi('system:material-info:import')")
    @Log(title = "物料资料", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    @ApiOperation(value = "导入物料资料列表", notes = "导入物料资料列表")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file) throws IOException {
        ExcelUtil<ErpMaterialInfo> util = new ExcelUtil<>(ErpMaterialInfo.class);
        List<ErpMaterialInfo> list = util.importExcel(file.getInputStream());
        return toAjax(erpMaterialInfoService.insertErpMaterialInfos(list));
    }

    // @PreAuthorize("@ss.hasPermi('system:material-info:add')")
    @Anonymous
    @PostMapping
    @ApiOperation(value = "新增物料信息", notes = "新增物料信息")
    public AjaxResult add(@ModelAttribute ErpMaterialInfo erpMaterialInfo) throws IOException {
        return toAjax(erpMaterialInfoService.insertErpMaterialInfo(erpMaterialInfo));
    }

    // @PreAuthorize("@ss.hasPermi('system:material-info:edit')")
    @Anonymous
    @PutMapping
    @ApiOperation(value = "修改物料信息", notes = "修改物料信息")
    public AjaxResult edit(@RequestBody ErpMaterialInfo erpMaterialInfo) throws IOException {
        return toAjax(erpMaterialInfoService.updateErpMaterialInfo(erpMaterialInfo));
    }

    // @PreAuthorize("@ss.hasPermi('system:material-info:remove')")
    @Anonymous
    @DeleteMapping("/{ids}")
    @ApiOperation(value = "删除物料信息", notes = "删除物料信息")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(erpMaterialInfoService.deleteErpMaterialInfoByIds(ids));
    }
}
