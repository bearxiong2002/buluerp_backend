package com.ruoyi.web.controller.erp;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.validation.Save;
import com.ruoyi.common.validation.Update;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.web.domain.ErpCustomers;
import com.ruoyi.web.domain.ErpMaterialInfo;
import com.ruoyi.web.service.IErpMaterialInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
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

    @Anonymous
    @GetMapping("/export/template")
    @ApiOperation(value = "下载物料导入模板", notes = "下载物料导入模板")
    public void exportTemplate(HttpServletResponse response) throws InstantiationException, IllegalAccessException {
        List<ErpMaterialInfo> list = Collections.singletonList(BaseEntity.createExample(ErpMaterialInfo.class));
        ExcelUtil<ErpMaterialInfo> util = new ExcelUtil<>(ErpMaterialInfo.class);
        util.exportExcel(response, list, "物料数据");
    }

    // @PreAuthorize("@ss.hasPermi('system:material-info:import')")
    @Log(title = "物料资料", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    @ApiOperation(value = "导入物料资料列表", notes = "导入物料资料列表")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file) throws IOException {
        List<ErpMaterialInfo> list = validateExcel(file, ErpMaterialInfo.class);
        return toAjax(erpMaterialInfoService.insertErpMaterialInfos(list));
    }

    // @PreAuthorize("@ss.hasPermi('system:material-info:add')")
    @Anonymous
    @PostMapping
    @ApiOperation(value = "新增物料信息", notes = "新增物料信息")
    public AjaxResult add(@ModelAttribute @Validated({Save.class}) ErpMaterialInfo erpMaterialInfo) throws IOException {
        return toAjax(erpMaterialInfoService.insertErpMaterialInfo(erpMaterialInfo));
    }

    // @PreAuthorize("@ss.hasPermi('system:material-info:edit')")
    @Anonymous
    @PutMapping
    @ApiOperation(value = "修改物料信息", notes = "修改物料信息")
    public AjaxResult edit(@ModelAttribute @Validated({ Update.class }) ErpMaterialInfo erpMaterialInfo) throws IOException {
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
