package com.ruoyi.web.controller.erp;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.web.domain.ErpMouldHouse;
import com.ruoyi.web.request.mouldhouse.AddMouldHouseRequest;
import com.ruoyi.web.request.mouldhouse.ListMouldHouseRequest;
import com.ruoyi.web.request.mouldhouse.UpdateMouldHouseRequest;
import com.ruoyi.web.service.IErpMouldHouseService;
import com.ruoyi.web.service.IListValidationService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/system/mould-house")
public class ErpMouldHouseController extends BaseController {
    @Autowired
    private IErpMouldHouseService erpMouldHouseService;

    @Autowired
    private IListValidationService listValidationService;

    @Anonymous
    @GetMapping("/list")
    @ApiOperation("搜索模房列表")
    public TableDataInfo list(ListMouldHouseRequest request) {
        startPage();
        List<ErpMouldHouse> mouldHouseList = erpMouldHouseService.search(request);
        return getDataTable(mouldHouseList);
    }

    @Anonymous
    @PostMapping("/export")
    @ApiOperation("导出模房列表")
    public void export(Long[] ids, HttpServletResponse response) {
        List<ErpMouldHouse> mouldHouseList = erpMouldHouseService.listByIds(Arrays.asList(ids));
        ExcelUtil<ErpMouldHouse> util = new ExcelUtil<>(ErpMouldHouse.class);
        util.exportExcel(response, mouldHouseList, "模房列表");
    }

    @Anonymous
    @PostMapping("/import")
    @ApiOperation("导入模房列表")
    public AjaxResult importExcel(@RequestPart MultipartFile file) throws IOException {
        listValidationService.importExcel(
                file,
                AddMouldHouseRequest.class,
                erpMouldHouseService::addChecked
        );
        return success();
    }

    @Anonymous
    @GetMapping("/export/template")
    @ApiOperation("导出模房列表模板")
    public void exportTemplate(HttpServletResponse response) throws InstantiationException, IllegalAccessException {
        listValidationService.exportExample(response, AddMouldHouseRequest.class);
    }

    @Anonymous
    @GetMapping("/{id}")
    @ApiOperation("获取模房详情")
    public AjaxResult get(@PathVariable Long id) {
        return success(erpMouldHouseService.getById(id));
    }

    @Anonymous
    @PostMapping
    @ApiOperation("新增模房")
    public AjaxResult add(@RequestBody @Validated AddMouldHouseRequest request) {
        erpMouldHouseService.addChecked(request);
        return success();
    }

    @Anonymous
    @PutMapping
    @ApiOperation("更新模房")
    public AjaxResult update(@RequestBody @Validated UpdateMouldHouseRequest request) {
        erpMouldHouseService.updateChecked(request);
        return success();
    }

    @Anonymous
    @DeleteMapping
    @ApiOperation("删除模房")
    public AjaxResult delete(@RequestParam Long[] ids) {
        erpMouldHouseService.removeBatchChecked(Arrays.asList(ids));
        return success();
    }
}
