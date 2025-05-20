package com.ruoyi.web.controller.erp;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import com.ruoyi.system.domain.ErpPurchaseInfo;
import com.ruoyi.system.service.IErpPurchaseInfoService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 外购资料，用于存储外购物料的基本信息和相关数据Controller
 * 
 * @author ruoyi
 * @date 2025-04-08
 */
@RestController
@RequestMapping("/system/info")
public class ErpPurchaseInfoController extends BaseController
{
    @Autowired
    private IErpPurchaseInfoService erpPurchaseInfoService;

    /**
     * 查询外购资料，用于存储外购物料的基本信息和相关数据列表
     */
    @PreAuthorize("@ss.hasPermi('system:info:list')")
    @GetMapping("/list")
    public TableDataInfo list(ErpPurchaseInfo erpPurchaseInfo)
    {
        startPage();
        List<ErpPurchaseInfo> list = erpPurchaseInfoService.selectErpPurchaseInfoList(erpPurchaseInfo);
        return getDataTable(list);
    }

    /**
     * 导出外购资料，用于存储外购物料的基本信息和相关数据列表
     */
    @PreAuthorize("@ss.hasPermi('system:info:export')")
    @Log(title = "外购资料，用于存储外购物料的基本信息和相关数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Integer[] ids)
    {
        List<ErpPurchaseInfo> list = erpPurchaseInfoService.selectErpPurchaseInfoListByIds(ids);
        ExcelUtil<ErpPurchaseInfo> util = new ExcelUtil<ErpPurchaseInfo>(ErpPurchaseInfo.class);
        util.exportExcel(response, list, "外购资料，用于存储外购物料的基本信息和相关数据数据");
    }

    /**
     * 获取外购资料，用于存储外购物料的基本信息和相关数据详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:info:query')")
    @GetMapping(value = "/{purchaseCode}")
    public AjaxResult getInfo(@PathVariable("purchaseCode") String purchaseCode)
    {
        return success(erpPurchaseInfoService.selectErpPurchaseInfoByPurchaseCode(purchaseCode));
    }

    /**
     * 新增外购资料，用于存储外购物料的基本信息和相关数据
     */
    @PreAuthorize("@ss.hasPermi('system:info:add')")
    @Log(title = "外购资料，用于存储外购物料的基本信息和相关数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ErpPurchaseInfo erpPurchaseInfo)
    {
        return toAjax(erpPurchaseInfoService.insertErpPurchaseInfo(erpPurchaseInfo));
    }

    /**
     * 修改外购资料，用于存储外购物料的基本信息和相关数据
     */
    @PreAuthorize("@ss.hasPermi('system:info:edit')")
    @Log(title = "外购资料，用于存储外购物料的基本信息和相关数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ErpPurchaseInfo erpPurchaseInfo)
    {
        return toAjax(erpPurchaseInfoService.updateErpPurchaseInfo(erpPurchaseInfo));
    }

    /**
     * 删除外购资料，用于存储外购物料的基本信息和相关数据
     */
    @PreAuthorize("@ss.hasPermi('system:info:remove')")
    @Log(title = "外购资料，用于存储外购物料的基本信息和相关数据", businessType = BusinessType.DELETE)
	@DeleteMapping("/{purchaseCodes}")
    public AjaxResult remove(@PathVariable String[] purchaseCodes)
    {
        return toAjax(erpPurchaseInfoService.deleteErpPurchaseInfoByPurchaseCodes(purchaseCodes));
    }
}
