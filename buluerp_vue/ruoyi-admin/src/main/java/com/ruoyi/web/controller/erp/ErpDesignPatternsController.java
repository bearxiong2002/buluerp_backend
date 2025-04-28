package com.ruoyi.web.controller.erp;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.web.request.design.AddDesignPatternsRequest;
import com.ruoyi.web.domain.ErpDesignPatterns;
import com.ruoyi.web.request.design.ListDesignPatternsRequest;
import com.ruoyi.web.request.design.UpdateDesignPatternsRequest;
import com.ruoyi.web.service.IErpDesignPatternsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
 * 设计总表 Controller
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
@RestController
@RequestMapping("/system/patterns")
@Api(value = "设计总表相关")
public class ErpDesignPatternsController extends BaseController
{
    @Autowired
    private IErpDesignPatternsService erpDesignPatternsService;

    /**
     * 查询设计总表列表
     */
    @ApiOperation(value = "获得设计总表列表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:patterns:list')")
    @GetMapping("/list")
    public TableDataInfo list(ListDesignPatternsRequest listDesignPatternsRequest)
    {
        startPage();
        List<ErpDesignPatterns> list = erpDesignPatternsService.selectErpDesignPatternsList(listDesignPatternsRequest);
        return getDataTable(list);
    }

    /**
     * 导出【请填写功能名称】列表
     */
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:patterns:export')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ListDesignPatternsRequest listDesignPatternsRequest)
    {
        List<ErpDesignPatterns> list = erpDesignPatternsService.selectErpDesignPatternsList(listDesignPatternsRequest);
        ExcelUtil<ErpDesignPatterns> util = new ExcelUtil<ErpDesignPatterns>(ErpDesignPatterns.class);
        util.exportExcel(response, list, "【请填写功能名称】数据");
    }

    /**
     * 获取设计总表详细信息
     */
    @ApiOperation(value = "获得设计总表详细信息 /{id}")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:patterns:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(erpDesignPatternsService.selectErpDesignPatternsById(id));
    }

    /**
     * 新增设计总表
     */
    @ApiOperation(value = "新增设计总表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:patterns:add')")
    @Log(title = "新增设计总表", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AddDesignPatternsRequest addDesignPatternsRequest)
    {
        return toAjax(erpDesignPatternsService.insertErpDesignPatterns(addDesignPatternsRequest));
    }

    /**
     * 修改设计总表
     */
    @ApiOperation(value = "修改设计总表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:patterns:edit')")
    @Log(title = "修改设计总表", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody UpdateDesignPatternsRequest updateDesignPatternsRequest)
    {
        return toAjax(erpDesignPatternsService.updateErpDesignPatterns(updateDesignPatternsRequest));
    }

    /**
     * 删除设计总表
     */
    @ApiOperation(value = "删除设计总表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:patterns:remove')")
    @Log(title = "删除设计总表", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(erpDesignPatternsService.deleteErpDesignPatternsByIds(ids));
    }

    /**
     * pmc确认设计总表
     */
    @ApiOperation(value = "pmc确认设计总表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:patterns:confirm')")
    @Log(title = "pmc确认设计总表", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    public AjaxResult confirm(@PathVariable Long id)
    {
        return toAjax(erpDesignPatternsService.confirmErpDesignPatternsById(id));
    }

    /**
     * pmc确认设计总表
     */
    @ApiOperation(value = "pmc取消确认设计总表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:patterns:confirm')")
    @Log(title = "pmc取消确认设计总表", businessType = BusinessType.UPDATE)
    @PutMapping("/cancel/{id}")
    public AjaxResult cancel(@PathVariable Long id)
    {
        return toAjax(erpDesignPatternsService.cancelConfirmById(id));
    }
}
