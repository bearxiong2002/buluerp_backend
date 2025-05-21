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
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:patterns:list')")
    @ApiOperation(value = "获得设计总表列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "产品编码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "createUserId", value = "创建用户ID", dataType = "integer", paramType = "query"),
            @ApiImplicitParam(name = "orderId", value = "订单ID", dataType = "integer", paramType = "query"),
            @ApiImplicitParam(name = "confirm", value = "PMC确认状态(0未确认/1已确认)", dataType = "integer", paramType = "query")
    })
    @GetMapping("/list")
    public TableDataInfo list(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long createUserId,
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) Long confirm) {

        ListDesignPatternsRequest request = new ListDesignPatternsRequest();
        request.setProductId(productId);
        request.setCreateUserId(createUserId);
        request.setOrderId(orderId);
        request.setConfirm(confirm);

        startPage();
        List<ErpDesignPatterns> list = erpDesignPatternsService.selectErpDesignPatternsList(request);
        return getDataTable(list);
    }

    /**
     * 导出【请填写功能名称】列表
     */
    @ApiOperation(value = "导出设计总表列表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:patterns:export')")
    @Log(title = "导出", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Long[] ids)
    {
        List<ErpDesignPatterns> list = erpDesignPatternsService.selectErpDesignPatternsListByIds(ids);
        ExcelUtil<ErpDesignPatterns> util = new ExcelUtil<ErpDesignPatterns>(ErpDesignPatterns.class);
        util.exportExcel(response, list, "设计总表数据");
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
