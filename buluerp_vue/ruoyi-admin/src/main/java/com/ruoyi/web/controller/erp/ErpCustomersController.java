package com.ruoyi.web.controller.erp;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.utils.page.PageDefaultOptions;
import com.ruoyi.common.validation.Save;
import com.ruoyi.common.validation.Update;
import com.ruoyi.web.domain.ErpCustomers;
import com.ruoyi.web.service.IErpCustomersService;
import com.ruoyi.web.service.IListValidationService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;

import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 【请填写功能名称】Controller
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
@RestController
@RequestMapping("/system/customers")
public class ErpCustomersController extends BaseController
{
    @Autowired
    private IErpCustomersService erpCustomersService;

    @Autowired
    private IListValidationService listValidationService;

    /**
     * 查询客户列表
     */
    // @PreAuthorize("@ss.hasPermi('system:customers:list')")
    @Anonymous
    @GetMapping("/list")
    @ApiOperation(value = "查询客户列表", notes = "查询客户列表")
    public TableDataInfo list(ErpCustomers erpCustomers)
    {
        startPage(PageDefaultOptions.create().orderByColumn("creatTime"));
        List<ErpCustomers> list = erpCustomersService.selectErpCustomersList(erpCustomers);
        return getDataTable(list);
    }

    /**
     * 导出客户列表
     */
    // @PreAuthorize("@ss.hasPermi('system:customers:export')")
    @Anonymous
    @Log(title = "客户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation(value = "导出客户列表", notes = "导出客户列表")
    public void export(HttpServletResponse response, Long[] ids)
    {
        List<ErpCustomers> list = erpCustomersService.selectErpCustomersListByIds(ids);
        ExcelUtil<ErpCustomers> util = new ExcelUtil<>(ErpCustomers.class);
        util.exportExcel(response, list, "客户数据");
    }

    @Anonymous
    @GetMapping("/export/template")
    @ApiOperation(value = "下载客户导入模板", notes = "下载客户导入模板")
    public void exportTemplate(HttpServletResponse response) throws InstantiationException, IllegalAccessException {
        IListValidationService.exportExample(response, ErpCustomers.class);
    }

    // @PreAuthorize("@ss.hasPermi('system:customers:import')")
    @Anonymous
    @Log(title = "客户", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    @ApiOperation(value = "导入客户列表", notes = "导入客户列表")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file) throws IOException {
        listValidationService.importExcel(file, ErpCustomers.class, erpCustomersService::insertErpCustomers);
        return success();
    }

    /**
     * 获取客户详细信息
     */
    // @PreAuthorize("@ss.hasPermi('system:customers:query')")
    @Anonymous
    @GetMapping(value = "/{id}")
    @ApiOperation(value = "获取客户详细信息", notes = "获取客户详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(requiresNotNull(erpCustomersService.selectErpCustomersById(id)));
    }

    /**
     * 新增客户
     */
    // @PreAuthorize("@ss.hasPermi('system:customers:add')")
    @Anonymous
    @Log(title = "客户", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation(value = "新增客户", notes = "新增客户")
    public AjaxResult add(@RequestBody @Validated({Save.class}) ErpCustomers erpCustomers)
    {
        return toAjax(erpCustomersService.insertErpCustomers(erpCustomers));
    }

    /**
     * 修改客户
     */
    // @PreAuthorize("@ss.hasPermi('system:customers:edit')")
    @Anonymous
    @Log(title = "客户", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation(value = "修改客户", notes = "修改客户")
    public AjaxResult edit(@RequestBody @Validated({Update.class}) ErpCustomers erpCustomers)
    {
        return toAjax(erpCustomersService.updateErpCustomers(erpCustomers));
    }

    /**
     * 删除客户
     */
    // @PreAuthorize("@ss.hasPermi('system:customers:remove')")
    @Anonymous
    @Log(title = "客户", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    @ApiOperation(value = "删除客户", notes = "删除客户")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(erpCustomersService.deleteErpCustomersByIds(ids));
    }
}
