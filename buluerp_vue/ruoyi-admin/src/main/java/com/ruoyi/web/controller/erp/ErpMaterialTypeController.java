package com.ruoyi.web.controller.erp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.page.PageDefaultOptions;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.validation.Save;
import com.ruoyi.common.validation.Update;
import com.ruoyi.web.domain.ErpMaterialType;
import com.ruoyi.web.service.IErpMaterialTypeService;
import com.ruoyi.web.service.IListValidationService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/system/material-type")
public class ErpMaterialTypeController extends BaseController {
    @Autowired
    private IErpMaterialTypeService erpMaterialTypeService;

    @Autowired
    private IListValidationService listValidationService;

    @Anonymous
    @GetMapping("/list")
    @ApiOperation(value = "获取物料类型列表", notes = "获取物料类型列表")
    public TableDataInfo list(ErpMaterialType erpMaterialType) {
        startPage(PageDefaultOptions.create().orderByColumn("createTime"));
        LambdaQueryWrapper<ErpMaterialType> queryWrapper = new LambdaQueryWrapper<>(erpMaterialType);
        List<ErpMaterialType> list = erpMaterialTypeService.list(queryWrapper);
        return getDataTable(list);
    }

    @Anonymous
    @GetMapping
    @ApiOperation(value = "根据ID查询物料类型", notes = "根据ID查询物料类型")
    public AjaxResult getInfo(Long[] ids) {
        return success(erpMaterialTypeService.listByIds(Arrays.asList(ids)));
    }

    @Anonymous
    @PostMapping("/export")
    @ApiOperation(value = "导出物料类型", notes = "导出物料类型")
    public void export(HttpServletResponse response, Long[] ids) {
        List<ErpMaterialType> list = erpMaterialTypeService.listByIds(Arrays.asList(ids));
        ExcelUtil<ErpMaterialType> util = new ExcelUtil<>(ErpMaterialType.class);
        util.exportExcel(response, list, "物料类型数据");
    }

    @Anonymous
    @GetMapping("/export/template")
    @ApiOperation(value = "导出物料类型导入模板", notes = "导出物料类型导入模板")
    public void exportTemplate(HttpServletResponse response) throws InstantiationException, IllegalAccessException {
        listValidationService.exportExample(response, ErpMaterialType.class);
    }

    @Anonymous
    @PostMapping("/import")
    @ApiOperation(value = "导入物料类型", notes = "导入物料类型")
    public AjaxResult importData(@RequestPart("file") MultipartFile file) throws Exception {
        listValidationService.importExcel(file, ErpMaterialType.class, erpMaterialTypeService::saveChecked);
        return success();
    }

    @Anonymous
    @PostMapping
    @ApiOperation(value = "新增物料类型", notes = "新增物料类型")
    public AjaxResult add(@RequestBody @Validated(Save.class) ErpMaterialType erpMaterialType) {
        return toAjax(erpMaterialTypeService.saveChecked(erpMaterialType));
    }

    @Anonymous
    @PutMapping
    @ApiOperation(value = "编辑物料类型", notes = "编辑物料类型")
    public AjaxResult edit(@RequestBody @Validated(Update.class) ErpMaterialType erpMaterialType) {
        return toAjax(erpMaterialTypeService.updateChecked(erpMaterialType));
    }

    @Anonymous
    @DeleteMapping
    @ApiOperation(value = "删除物料类型", notes = "删除物料类型")
    public AjaxResult delete(Long[] ids) {
        erpMaterialTypeService.removeBatchChecked(Arrays.asList(ids));
        return success();
    }
}
