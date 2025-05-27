package com.ruoyi.web.controller.erp;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.domain.validation.Save;
import com.ruoyi.common.domain.validation.Update;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.web.domain.ErpPackagingList;
import com.ruoyi.web.service.IErpPackagingListService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    public TableDataInfo list(ErpPackagingList erpPackagingList) {
        startPage();
        return getDataTable(packagingListService.selectErpPackagingListList(erpPackagingList));
    }

    // @PreAuthorize("@ss.hasPermi('system:packaging-list:export')")
    @Anonymous
    @PostMapping("/export")
    @ApiOperation(value = "导出分包列表", notes = "导出分包列表")
    public void export(HttpServletResponse response, Integer[] ids) {
        List<ErpPackagingList> list = packagingListService.selectErpPackagingListListByIds(ids);
        ExcelUtil<ErpPackagingList> util = new ExcelUtil<ErpPackagingList>(ErpPackagingList.class);
        util.exportExcel(response, list, "分包数据");
    }

    // @PreAuthorize("@ss.hasPermi('system:packaging-list:import')")
    @Anonymous
    @PostMapping("/import")
    @ApiOperation(value = "导入分包列表", notes = "导入分包列表")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file) throws IOException {
        List<ErpPackagingList> list = validateExcel(file, ErpPackagingList.class);
        int count = 0;
        for (ErpPackagingList erpPackagingList : list) {
            packagingListService.insertErpPackagingList(erpPackagingList);
            count++;
        }
        return toAjax(count);
    }

    // @PreAuthorize("@ss.hasPermi('system:packaging-list:query')")
    @Anonymous
    @GetMapping(value = "/{id}")
    @ApiOperation(value = "获取分包详细信息", notes = "获取分包详细信息")
    public AjaxResult getInfo(@PathVariable("id") Integer id) {
        return success(requiresNotNull(packagingListService.selectErpPackagingListById(id)));
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
    public AjaxResult remove(@PathVariable Integer[] ids) {
        return toAjax(packagingListService.deleteErpPackagingListByIds(ids));
    }
}
