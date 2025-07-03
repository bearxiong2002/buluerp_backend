package com.ruoyi.web.controller.erp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.validation.Save;
import com.ruoyi.common.validation.Update;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.web.domain.ErpProductionArrange;
import com.ruoyi.web.request.arrange.AddProductionArrangeFromScheduleRequest;
import com.ruoyi.web.service.IErpProductionArrangeService;
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
@RequestMapping("/system/production-arrange")
public class ErpProductionArrangeController extends BaseController {
    @Autowired
    private IErpProductionArrangeService erpProductionArrangeService;

    // @PreAuthorize("@ss.hasPermi('system:production-arrange:list')")
    @Anonymous
    @GetMapping("/list")
    @ApiOperation(value = "查询排产列表", notes = "查询排产列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "每页条目数", dataType = "int", defaultValue = "10"),
            @ApiImplicitParam(name = "pageSize", value = "当前页码", dataType = "int", defaultValue = "1")
    })
    public TableDataInfo list(ErpProductionArrange erpProductionArrange) {
        startPage();
        QueryWrapper<ErpProductionArrange> wrapper = new QueryWrapper<>(erpProductionArrange);
        List<ErpProductionArrange> list = erpProductionArrangeService.list(wrapper);
        return getDataTable(list);
    }

    // @PreAuthorize("@ss.hasPermi('system:production-arrange:export')")
    @Anonymous
    @PostMapping("/export")
    @ApiOperation(value = "导出排产列表", notes = "导出排产列表")
    public void export(HttpServletResponse response, Long[] ids) {
        List<ErpProductionArrange> list = erpProductionArrangeService.listByIds(Arrays.asList(ids));
        ExcelUtil<ErpProductionArrange> util = new ExcelUtil<>(ErpProductionArrange.class);
        util.exportExcel(response, list, "排产数据");
    }

    @Anonymous
    @GetMapping("/export/template")
    @ApiOperation(value = "下载排产导入模板", notes = "下载排产导入模板")
    public void exportTemplate(HttpServletResponse response) throws InstantiationException, IllegalAccessException {
        List<ErpProductionArrange> list = Collections.singletonList(BaseEntity.createExample(ErpProductionArrange.class));
        ExcelUtil<ErpProductionArrange> util = createTemplateExcelUtil(ErpProductionArrange.class);
        util.exportExcel(response, list, "排产数据");
    }

    // @PreAuthorize("@ss.hasPermi('system:production-arrange:import')")
    @Anonymous
    @PostMapping("/import")
    @ApiOperation(value = "导入排产列表", notes = "导入排产列表")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file) throws IOException {
        List<ErpProductionArrange> erpProductionSchedule = validateExcel(file, ErpProductionArrange.class);
        return toAjax(
                erpProductionArrangeService.insertErpProductionArrangeList(erpProductionSchedule)
        );
    }

    // @PreAuthorize("@ss.hasPermi('system:production-arrange:add')")
    @Anonymous
    @PostMapping
    @ApiOperation(value = "新增排产", notes = "新增排产")
    public AjaxResult add(@ModelAttribute @Validated({Save.class}) ErpProductionArrange erpProductionArrange) throws IOException {
        return toAjax(erpProductionArrangeService.insertErpProductionArrangeList(
                Collections.singletonList(erpProductionArrange)
        ));
    }

    @Anonymous
    @PostMapping("/from-schedule")
    @ApiOperation(value = "从布产生成排产", notes = "从布产生成排产")
    public AjaxResult addFromSchedule(@ModelAttribute @Validated({ Save.class }) AddProductionArrangeFromScheduleRequest request) throws IOException {
        return toAjax(erpProductionArrangeService.insertFromSchedule(request));
    }

    // @PreAuthorize("@ss.hasPermi('system:production-arrange:edit')")
    @Anonymous
    @PutMapping
    @ApiOperation(value = "修改排产", notes = "修改排产")
    public AjaxResult edit(@ModelAttribute @Validated({Update.class }) ErpProductionArrange erpProductionArrange) throws IOException {
        return toAjax(
                erpProductionArrangeService.updateErpProductionArrange(erpProductionArrange)
        );
    }

    // @PreAuthorize("@ss.hasPermi('system:production-arrange:remove')")
    @Anonymous
    @DeleteMapping("/{ids}")
    @ApiOperation(value = "删除排产", notes = "删除排产")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(
                erpProductionArrangeService.removeBatchByIds(Arrays.asList(ids))
        );
    }
}
