package com.ruoyi.web.controller.erp;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.web.domain.ErpDesignSubPattern;
import com.ruoyi.web.service.IErpDesignSubPatternService;
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
 * 模具信息，用于存储模具的基本信息和相关数据Controller
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
@RestController
@RequestMapping("/system/pattern")
public class ErpDesignSubPatternController extends BaseController
{
    @Autowired
    private IErpDesignSubPatternService erpDesignSubPatternService;

    /**
     * 查询模具信息，用于存储模具的基本信息和相关数据列表
     */
    @PreAuthorize("@ss.hasPermi('system:pattern:list')")
    @GetMapping("/list")
    public TableDataInfo list(ErpDesignSubPattern erpDesignSubPattern)
    {
        startPage();
        List<ErpDesignSubPattern> list = erpDesignSubPatternService.selectErpDesignSubPatternList(erpDesignSubPattern);
        return getDataTable(list);
    }

    /**
     * 导出模具信息，用于存储模具的基本信息和相关数据列表
     */
    @PreAuthorize("@ss.hasPermi('system:pattern:export')")
    @Log(title = "模具信息，用于存储模具的基本信息和相关数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ErpDesignSubPattern erpDesignSubPattern)
    {
        List<ErpDesignSubPattern> list = erpDesignSubPatternService.selectErpDesignSubPatternList(erpDesignSubPattern);
        ExcelUtil<ErpDesignSubPattern> util = new ExcelUtil<ErpDesignSubPattern>(ErpDesignSubPattern.class);
        util.exportExcel(response, list, "模具信息，用于存储模具的基本信息和相关数据数据");
    }

    /**
     * 获取模具信息，用于存储模具的基本信息和相关数据详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:pattern:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(erpDesignSubPatternService.selectErpDesignSubPatternById(id));
    }

    /**
     * 新增模具信息，用于存储模具的基本信息和相关数据
     */
    @PreAuthorize("@ss.hasPermi('system:pattern:add')")
    @Log(title = "模具信息，用于存储模具的基本信息和相关数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ErpDesignSubPattern erpDesignSubPattern)
    {
        return toAjax(erpDesignSubPatternService.insertErpDesignSubPattern(erpDesignSubPattern));
    }

    /**
     * 修改模具信息，用于存储模具的基本信息和相关数据
     */
    @PreAuthorize("@ss.hasPermi('system:pattern:edit')")
    @Log(title = "模具信息，用于存储模具的基本信息和相关数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ErpDesignSubPattern erpDesignSubPattern)
    {
        return toAjax(erpDesignSubPatternService.updateErpDesignSubPattern(erpDesignSubPattern));
    }

    /**
     * 删除模具信息，用于存储模具的基本信息和相关数据
     */
    @PreAuthorize("@ss.hasPermi('system:pattern:remove')")
    @Log(title = "模具信息，用于存储模具的基本信息和相关数据", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(erpDesignSubPatternService.deleteErpDesignSubPatternByIds(ids));
    }
}
