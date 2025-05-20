package com.ruoyi.web.controller.erp;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.web.domain.ErpDesignStyle;
import com.ruoyi.web.domain.ErpOrders;
import com.ruoyi.web.request.design.AddDesignRequest;
import com.ruoyi.web.request.design.LIstDesignRequest;
import com.ruoyi.web.request.design.UpdateDesignRequest;
import com.ruoyi.web.service.IErpDesignStyleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;

import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 设计造型Controller
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
@RestController
@RequestMapping("/system/style")
@Api(value = "造型表相关")
public class ErpDesignStyleController extends BaseController
{
    @Autowired
    private IErpDesignStyleService erpDesignStyleService;

    /**
     * 查询设计造型列表
     */
    @ApiOperation(value = "获取造型表列表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:style:list')")
    @GetMapping("/list")
    public TableDataInfo list(LIstDesignRequest lIstDesignRequest)
    {
        startPage();
        List<ErpDesignStyle> list = erpDesignStyleService.selectErpDesignStyleList(lIstDesignRequest);
        return getDataTable(list);
    }

    /**
     * 导出设计造型列表
     */
    @ApiOperation(value = "导出造型表列表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:style:export')")
    @Log(title = "设计造型", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Long[] ids)
    {
        List<ErpDesignStyle> list = erpDesignStyleService.selectErpDesignStyleListByIds(ids);
        ExcelUtil<ErpDesignStyle> util = new ExcelUtil<ErpDesignStyle>(ErpDesignStyle.class);
        util.exportExcel(response, list, "设计造型数据");
    }

    /**
     * 获取设计造型详细信息
     */
    @ApiOperation(value = "获取造型表详细信息")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:style:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(erpDesignStyleService.selectErpDesignStyleById(id));
    }

    /**
     * 新增设计造型
     */
    @ApiOperation(value = "新增造型表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:style:add')")
    @Log(title = "新增造型表", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AddDesignRequest addDesignRequest) throws IOException {
        return toAjax(erpDesignStyleService.insertErpDesignStyle(addDesignRequest));
    }

    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:style:import')")
    @PostMapping("/import")
    @ApiOperation(value = "导入造型表")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file) throws IOException {
        ExcelUtil<AddDesignRequest> util = new ExcelUtil<AddDesignRequest>(AddDesignRequest.class);
        List<AddDesignRequest> addDesignRequestList = util.importExcel(file.getInputStream());
        int count = 0;
        for (AddDesignRequest addDesignRequest : addDesignRequestList) {
            erpDesignStyleService.insertErpDesignStyle(addDesignRequest);
            count++;
        }
        return success(count);
    }

    /**
     * 修改设计造型
     */
    @ApiOperation(value = "修改造型表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:style:edit')")
    @Log(title = "修改造型", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody UpdateDesignRequest updateDesignRequest) throws IOException {
        return toAjax(erpDesignStyleService.updateErpDesignStyle(updateDesignRequest));
    }

    /**
     * 删除设计造型
     */
    @ApiOperation(value = "删除造型表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:style:remove')")
    @Log(title = "设计造型", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(erpDesignStyleService.deleteErpDesignStyleByIds(ids));
    }
}
