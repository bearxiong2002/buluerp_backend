package com.ruoyi.web.controller.erp;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.web.domain.ErpMould;
import com.ruoyi.web.request.mould.AddMouldRequest;
import com.ruoyi.web.request.mould.BatchUpdateMouldStatusRequest;
import com.ruoyi.web.request.mould.ListMouldRequest;
import com.ruoyi.web.request.mould.UpdateMouldRequest;
import com.ruoyi.web.service.IErpMouldService;
import com.ruoyi.web.service.IListValidationService;
import com.ruoyi.web.service.support.MouldImages;
import com.ruoyi.web.result.MouldInfoResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/system/mould")
public class ErpMouldController extends BaseController {
    @Autowired
    private IErpMouldService mouldService;

    @Autowired
    private IListValidationService listValidationService;

    @InitBinder
    public void bindMouldForm(WebDataBinder binder) {
        // 图片地址由上传/Excel解析生成，表单不能直接指定已有文件地址。
        binder.setDisallowedFields("pictureUrl");
    }

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
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult importExcel(@RequestPart MultipartFile file) throws IOException {
        List<AddMouldRequest> requests = readMouldImport(file);
        listValidationService.checkInsertion(requests, (IListValidationService.InsertConsumer<AddMouldRequest>) mouldService::add);
        return success();
    }

    @Anonymous
    @PostMapping("/import/with-manufacturer")
    @ApiOperation(value = "导入模具列表并自动创建厂商")
    @Transactional(rollbackFor = Exception.class)
    /**
     * 导入模具 Excel，并在厂商不存在时自动创建厂商。
     * 该接口先调用通用 Excel 服务解析和校验文件，再调用模具服务批量写入，不依赖远程接口。
     */
    public AjaxResult importExcelWithManufacturer(@RequestPart MultipartFile file) throws IOException {
        // 先一次性解析并校验 Excel，再交给服务层批量查询和写入，避免逐行访问数据库导致大文件导入超时。
        List<AddMouldRequest> requests = readMouldImport(file);
        mouldService.importWithManufacturer(requests);
        return success();
    }

    private List<AddMouldRequest> readMouldImport(MultipartFile file) throws IOException {
        ExcelUtil<AddMouldRequest> util = new ExcelUtil<AddMouldRequest>(AddMouldRequest.class) {
            @Override
            protected String saveImportImage(byte[] data) throws IOException {
                String url = super.saveImportImage(data);
                MouldImages.deleteAfterRollback(url);
                return url;
            }
        };
        try (InputStream input = file.getInputStream()) {
            List<AddMouldRequest> requests = util.importExcel(input);
            listValidationService.checkValidation(requests);
            return requests;
        }
    }

    @Anonymous
    @GetMapping("/export/template")
    @ApiOperation(value = "导出模具列表模板")
    public void exportTemplate(HttpServletResponse response) throws InstantiationException, IllegalAccessException {
        listValidationService.exportExample(response, AddMouldRequest.class);
    }

    @Anonymous
    @PostMapping
    @ApiOperation(value = "添加模具")
    public AjaxResult add(@RequestBody @Validated AddMouldRequest request) {
        mouldService.add(request);
        return success();
    }

    @Anonymous
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value = "添加模具并上传图片")
    public AjaxResult addMultipart(@Validated @ModelAttribute AddMouldRequest request) {
        return add(request);
    }

    @Anonymous
    @PostMapping("/with-manufacturer")
    @ApiOperation(value = "添加模具并自动创建厂商")
    public AjaxResult addWithManufacturer(@RequestBody @Validated AddMouldRequest request) {
        mouldService.addWithManufacturer(request);
        return success();
    }

    @Anonymous
    @PostMapping(value = "/with-manufacturer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value = "添加模具、自动创建厂商并上传图片")
    public AjaxResult addWithManufacturerMultipart(@Validated @ModelAttribute AddMouldRequest request) {
        return addWithManufacturer(request);
    }

    @Anonymous
    @PutMapping
    @ApiOperation(value = "更新模具信息")
    public AjaxResult update(@RequestBody @Validated UpdateMouldRequest request) {
        mouldService.update(request);
        return success();
    }

    @Anonymous
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value = "更新模具及图片")
    public AjaxResult updateMultipart(@Validated @ModelAttribute UpdateMouldRequest request) {
        return update(request);
    }

    @PutMapping("/batch-status")
    @ApiOperation(value = "批量更新模具状态")
    public AjaxResult batchUpdateStatus(@RequestBody @Validated BatchUpdateMouldStatusRequest request) {
        int updatedCount = mouldService.batchUpdateStatus(request);
        return AjaxResult.success("成功更新 " + updatedCount + " 条模具", updatedCount);
    }

    @Anonymous
    @DeleteMapping
    @ApiOperation(value = "删除模具")
    public AjaxResult delete(Long[] ids) {
        mouldService.removeBatch(Arrays.asList(ids));
        return success();
    }
}
