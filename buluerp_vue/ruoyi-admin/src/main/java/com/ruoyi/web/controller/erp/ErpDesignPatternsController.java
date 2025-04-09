package com.ruoyi.web.controller.erp;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.web.domain.ErpDesignPatterns;
import com.ruoyi.web.service.IErpDesignPatternsService;
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
@RequestMapping("/system/patterns")
public class ErpDesignPatternsController extends BaseController
{
    @Autowired
    private IErpDesignPatternsService erpDesignPatternsService;

    /**
     * 查询【请填写功能名称】列表
     */
    @PreAuthorize("@ss.hasPermi('system:patterns:list')")
    @GetMapping("/list")
    public TableDataInfo list(ErpDesignPatterns erpDesignPatterns)
    {
        startPage();
        List<ErpDesignPatterns> list = erpDesignPatternsService.selectErpDesignPatternsList(erpDesignPatterns);
        return getDataTable(list);
    }

    /**
     * 导出【请填写功能名称】列表
     */
    @PreAuthorize("@ss.hasPermi('system:patterns:export')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ErpDesignPatterns erpDesignPatterns)
    {
        List<ErpDesignPatterns> list = erpDesignPatternsService.selectErpDesignPatternsList(erpDesignPatterns);
        ExcelUtil<ErpDesignPatterns> util = new ExcelUtil<ErpDesignPatterns>(ErpDesignPatterns.class);
        util.exportExcel(response, list, "【请填写功能名称】数据");
    }

    /**
     * 获取【请填写功能名称】详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:patterns:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(erpDesignPatternsService.selectErpDesignPatternsById(id));
    }

    /**
     * 新增【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('system:patterns:add')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ErpDesignPatterns erpDesignPatterns)
    {
        return toAjax(erpDesignPatternsService.insertErpDesignPatterns(erpDesignPatterns));
    }

    /**
     * 修改【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('system:patterns:edit')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ErpDesignPatterns erpDesignPatterns)
    {
        return toAjax(erpDesignPatternsService.updateErpDesignPatterns(erpDesignPatterns));
    }

    /**
     * 删除【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('system:patterns:remove')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(erpDesignPatternsService.deleteErpDesignPatternsByIds(ids));
    }
}
