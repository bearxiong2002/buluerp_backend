package com.ruoyi.web.controller.erp;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.page.PageDefaultOptions;
import com.ruoyi.common.validation.Save;
import com.ruoyi.common.validation.Update;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.web.domain.ErpPackagingList;
import com.ruoyi.web.service.IErpPackagingListService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/system/packaging-list")
public class ErpPackagingListController extends BaseController {
    @Autowired
    private IErpPackagingListService packagingListService;

    // @PreAuthorize("@ss.hasPermi('system:packaging-list:list')")
    @Anonymous
    @GetMapping("/list")
    @ApiOperation(value = "查询分包列表", notes = "查询分包列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "int", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示条数", dataType = "int", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "orderByColumn", value = "排序字段", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "isAsc", value = "排序方式", dataType = "String", paramType = "query", defaultValue = "asc")
    })
    public TableDataInfo list(ErpPackagingList erpPackagingList) {
        startPage(PageDefaultOptions.create().orderByColumn("creationTime"));
        return getDataTable(packagingListService.selectErpPackagingListList(erpPackagingList));
    }

    // @PreAuthorize("@ss.hasPermi('system:packaging-list:export')")
    @Anonymous
    @PostMapping("/export")
    @ApiOperation(value = "导出分包列表", notes = "导出分包列表")
    public void export(HttpServletResponse response, Long id) throws IOException {
        ErpPackagingList erpPackagingList = packagingListService.selectErpPackagingListById(id);
        packagingListService.exportExcel(response, erpPackagingList);
    }

    @Anonymous
    @GetMapping("/export/template")
    @ApiOperation(value = "下载分包导入模板", notes = "下载分包导入模板")
    public void exportTemplate(HttpServletResponse response) throws IOException {
        ErpPackagingList example = ErpPackagingList.createExample();
        packagingListService.exportExcel(response, example);
    }

    // @PreAuthorize("@ss.hasPermi('system:packaging-list:import')")
    @Anonymous
    @PostMapping("/import")
    @ApiOperation(value = "导入分包列表", notes = "导入分包列表")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file) throws Exception {
        List<String> bagSheetNames = new ArrayList<>();
        ErpPackagingList erpPackagingList = packagingListService.importExcel(file.getInputStream(), bagSheetNames);
        packagingListService.insertCascade(erpPackagingList, bagSheetNames);
        return success();
    }

    // @PreAuthorize("@ss.hasPermi('system:packaging-list:query')")
    @Anonymous
    @GetMapping
    @ApiOperation(value = "获取分包详细信息", notes = "获取分包详细信息")
    public AjaxResult getInfo(Long[] ids) {
        return success(requiresNotNull(packagingListService.selectErpPackagingListListByIds(ids)));
    }

    // @PreAuthorize("@ss.hasPermi('system:packaging-list:add')")
    @Anonymous
    @PostMapping
    @ApiOperation(value = "新增分包", notes = "新增分包")
    public AjaxResult add(@RequestBody @Validated({Save.class}) ErpPackagingList erpPackagingList) {
        return toAjax(packagingListService.insertErpPackagingList(erpPackagingList));
    }

    // @PreAuthorize("@ss.hasPermi('system:packaging-list:edit')")
    @Anonymous
    @PutMapping
    @ApiOperation(value = "修改分包", notes = "修改分包")
    public AjaxResult edit(@RequestBody @Validated({ Update.class }) ErpPackagingList erpPackagingList) {
        return toAjax(packagingListService.updateErpPackagingList(erpPackagingList));
    }

    // @PreAuthorize("@ss.hasPermi('system:packaging-list:remove')")
    @Anonymous
    @DeleteMapping("/{ids}")
    @ApiOperation(value = "删除分包", notes = "删除分包")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(packagingListService.deleteErpPackagingListByIds(ids));
    }
}
