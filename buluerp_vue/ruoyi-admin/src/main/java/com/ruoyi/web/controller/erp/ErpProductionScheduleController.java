package com.ruoyi.web.controller.erp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.page.PageDefaultOptions;
import com.ruoyi.common.validation.Save;
import com.ruoyi.common.validation.Update;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.web.domain.ErpProductionSchedule;
import com.ruoyi.web.service.IErpProductionScheduleService;
import com.ruoyi.web.service.IListValidationService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/system/products-schedule")
public class ErpProductionScheduleController extends BaseController {
    @Autowired
    private IErpProductionScheduleService erpProductionScheduleService;

    @Autowired
    private IListValidationService listValidationService;

    // @PreAuthorize("@ss.hasPermi('system:products-schedule:list')")
    @Anonymous
    @GetMapping("/list")
    @ApiOperation(value = "查询布产列表", notes = "查询布产列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "每页条目数", dataType = "int", defaultValue = "10"),
            @ApiImplicitParam(name = "pageSize", value = "当前页码", dataType = "int", defaultValue = "1")
    })
    public TableDataInfo list(ErpProductionSchedule erpProductionSchedule) {
        startPage(PageDefaultOptions.create().orderByColumn("creationTime"));
        QueryWrapper<ErpProductionSchedule> wrapper = new QueryWrapper<>(erpProductionSchedule);
        List<ErpProductionSchedule> list = erpProductionScheduleService.list(wrapper);
        for (ErpProductionSchedule item : list) {
            item.setMaterialIds(
                erpProductionScheduleService
                    .getProductionScheduleMaterialIds(item.getId())
            );
        }
        return getDataTable(list);
    }

    // @PreAuthorize("@ss.hasPermi('system:products-schedule:export')")
    @Anonymous
    @PostMapping("/export")
    @ApiOperation(value = "导出布产列表", notes = "导出布产列表")
    public void export(HttpServletResponse response, Long[] ids) {
        List<ErpProductionSchedule> list = erpProductionScheduleService.listByIds(Arrays.asList(ids));
        ExcelUtil<ErpProductionSchedule> util = new ExcelUtil<>(ErpProductionSchedule.class);
        util.exportExcel(response, list, "布产数据");
    }

    @Anonymous
    @GetMapping("/export/template")
    @ApiOperation(value = "下载布产导入模板", notes = "下载布产导入模板")
    public void exportTemplate(HttpServletResponse response) throws InstantiationException, IllegalAccessException {
        IListValidationService.exportExample(response, ErpProductionSchedule.class);
    }

    // @PreAuthorize("@ss.hasPermi('system:products-schedule:import')")
    @Anonymous
    @PostMapping("/import")
    @ApiOperation(value = "导入布产列表", notes = "导入布产列表")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file) throws IOException {
        listValidationService.importExcel(file, ErpProductionSchedule.class, erpProductionScheduleService::insertErpProductionSchedule);
        return success();
    }

    // @PreAuthorize("@ss.hasPermi('system:products-schedule:add')")
    @Anonymous
    @PostMapping
    @ApiOperation(value = "新增布产", notes = "新增布产")
    public AjaxResult add(@ModelAttribute @Validated({Save.class}) ErpProductionSchedule erpProductionSchedule) throws IOException {
        return toAjax(
                erpProductionScheduleService
                        .insertErpProductionSchedule(erpProductionSchedule)
        );
    }

    // @PreAuthorize("@ss.hasPermi('system:products-schedule:edit')")
    @Anonymous
    @PutMapping
    @ApiOperation(value = "修改布产", notes = "修改布产")
    public AjaxResult edit(@ModelAttribute @Validated({ Update.class }) ErpProductionSchedule erpProductionSchedule) throws IOException {
        return toAjax(
                erpProductionScheduleService
                        .updateErpProductionSchedule(erpProductionSchedule)
        );
    }

    // @PreAuthorize("@ss.hasPermi('system:products-schedule:remove')")
    @Anonymous
    @DeleteMapping("/{ids}")
    @ApiOperation(value = "删除布产", notes = "删除布产")
    public AjaxResult remove(@PathVariable List<Long> ids) {
        return toAjax(
                erpProductionScheduleService
                        .removeErpProductionScheduleList(ids)
        );
    }
}
