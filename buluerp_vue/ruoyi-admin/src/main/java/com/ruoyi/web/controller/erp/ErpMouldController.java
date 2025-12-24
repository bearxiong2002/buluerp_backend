package com.ruoyi.web.controller.erp;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.web.domain.ErpMould;
import com.ruoyi.web.request.mould.AddMouldRequest;
import com.ruoyi.web.request.mould.ListMouldRequest;
import com.ruoyi.web.request.mould.UpdateMouldRequest;
import com.ruoyi.web.service.IErpMouldService;
import com.ruoyi.web.service.IListValidationService;
import com.ruoyi.web.result.MouldInfoResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/system/mould")
public class ErpMouldController extends BaseController {
    @Autowired
    private IErpMouldService mouldService;

    @Autowired
    private IListValidationService listValidationService;

    @Anonymous
    @ApiOperation(value = "查询模具列表")
    @GetMapping("/list")
    public TableDataInfo list(ListMouldRequest request) {
        startPage();
        List<MouldInfoResult> mouldList = mouldService.list(request);
        return getDataTable(mouldList);
    }

    @Anonymous
    @GetMapping("/{mouldNumber}")
    @ApiOperation(value = "查询模具详情")
    public AjaxResult detail(@PathVariable String mouldNumber) {
        return success(mouldService.getMouldInfo(mouldNumber));
    }

    @Anonymous
    @PostMapping("/export")
    @ApiOperation(value = "导出模具列表")
    public void export(HttpServletResponse response, Long[] ids) {
        List<ErpMould> mouldList = mouldService.listByIds(Arrays.asList(ids));
        ExcelUtil<ErpMould> excelUtil = new ExcelUtil<>(ErpMould.class);
        excelUtil.exportExcel(response, mouldList, "模具列表");
    }

    @Anonymous
    @PostMapping("/import")
    @ApiOperation(value = "导入模具列表")
    public AjaxResult importExcel(@RequestPart MultipartFile file) throws IOException {
        listValidationService.importExcel(file, AddMouldRequest.class, this::add);
        return success();
    }

    @Anonymous
    @GetMapping("/export/template")
    @ApiOperation(value = "导出模具列表模板")
    public void exportTemplate(HttpServletResponse response) throws InstantiationException, IllegalAccessException {
        listValidationService.exportExample(response, ErpMould.class);
    }

    @Anonymous
    @PostMapping
    @ApiOperation(value = "添加模具")
    public AjaxResult add(@RequestBody @Validated AddMouldRequest request) {
        mouldService.add(request);
        return success();
    }

    @Anonymous
    @PutMapping
    @ApiOperation(value = "更新模具信息")
    public AjaxResult update(@RequestBody @Validated UpdateMouldRequest request) {
        mouldService.update(request);
        return success();
    }

    @Anonymous
    @DeleteMapping
    @ApiOperation(value = "删除模具")
    public AjaxResult delete(Long[] ids) {
        mouldService.removeBatch(Arrays.asList(ids));
        return success();
    }
}
