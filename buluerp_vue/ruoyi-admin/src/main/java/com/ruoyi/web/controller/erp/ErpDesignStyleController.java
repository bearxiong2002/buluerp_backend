package com.ruoyi.web.controller.erp;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.web.domain.ErpDesignStyle;
import com.ruoyi.web.service.IErpDesignStyleService;
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
 * 设计造型Controller
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
@RestController
@RequestMapping("/system/style")
public class ErpDesignStyleController extends BaseController
{
    @Autowired
    private IErpDesignStyleService erpDesignStyleService;

    /**
     * 查询设计造型列表
     */
    @PreAuthorize("@ss.hasPermi('system:style:list')")
    @GetMapping("/list")
    public TableDataInfo list(ErpDesignStyle erpDesignStyle)
    {
        startPage();
        List<ErpDesignStyle> list = erpDesignStyleService.selectErpDesignStyleList(erpDesignStyle);
        return getDataTable(list);
    }

    /**
     * 导出设计造型列表
     */
    @PreAuthorize("@ss.hasPermi('system:style:export')")
    @Log(title = "设计造型", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ErpDesignStyle erpDesignStyle)
    {
        List<ErpDesignStyle> list = erpDesignStyleService.selectErpDesignStyleList(erpDesignStyle);
        ExcelUtil<ErpDesignStyle> util = new ExcelUtil<ErpDesignStyle>(ErpDesignStyle.class);
        util.exportExcel(response, list, "设计造型数据");
    }

    /**
     * 获取设计造型详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:style:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(erpDesignStyleService.selectErpDesignStyleById(id));
    }

    /**
     * 新增设计造型
     */
    @PreAuthorize("@ss.hasPermi('system:style:add')")
    @Log(title = "设计造型", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ErpDesignStyle erpDesignStyle)
    {
        return toAjax(erpDesignStyleService.insertErpDesignStyle(erpDesignStyle));
    }

    /**
     * 修改设计造型
     */
    @PreAuthorize("@ss.hasPermi('system:style:edit')")
    @Log(title = "设计造型", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ErpDesignStyle erpDesignStyle)
    {
        return toAjax(erpDesignStyleService.updateErpDesignStyle(erpDesignStyle));
    }

    /**
     * 删除设计造型
     */
    @PreAuthorize("@ss.hasPermi('system:style:remove')")
    @Log(title = "设计造型", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(erpDesignStyleService.deleteErpDesignStyleByIds(ids));
    }
}
