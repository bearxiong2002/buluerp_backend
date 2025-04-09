package com.ruoyi.web.controller.erp;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.web.domain.ErpCustomers;
import com.ruoyi.web.service.IErpCustomersService;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;

import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

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

    /**
     * 查询【请填写功能名称】列表
     */
    @PreAuthorize("@ss.hasPermi('system:customers:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询客户列表", notes = "查询客户列表")
    public TableDataInfo list(ErpCustomers erpCustomers)
    {
        startPage();
        List<ErpCustomers> list = erpCustomersService.selectErpCustomersList(erpCustomers);
        return getDataTable(list);
    }

    /**
     * 导出【请填写功能名称】列表
     */
    @PreAuthorize("@ss.hasPermi('system:customers:export')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ErpCustomers erpCustomers)
    {
        List<ErpCustomers> list = erpCustomersService.selectErpCustomersList(erpCustomers);
        ExcelUtil<ErpCustomers> util = new ExcelUtil<ErpCustomers>(ErpCustomers.class);
        util.exportExcel(response, list, "【请填写功能名称】数据");
    }

    /**
     * 获取【请填写功能名称】详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:customers:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(erpCustomersService.selectErpCustomersById(id));
    }

    /**
     * 新增【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('system:customers:add')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ErpCustomers erpCustomers)
    {
        return toAjax(erpCustomersService.insertErpCustomers(erpCustomers));
    }

    /**
     * 修改【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('system:customers:edit')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ErpCustomers erpCustomers)
    {
        return toAjax(erpCustomersService.updateErpCustomers(erpCustomers));
    }

    /**
     * 删除【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('system:customers:remove')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(erpCustomersService.deleteErpCustomersByIds(ids));
    }
}
