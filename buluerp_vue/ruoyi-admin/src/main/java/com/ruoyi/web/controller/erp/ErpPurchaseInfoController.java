package com.ruoyi.web.controller.erp;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.web.domain.ErpPurchaseInfo;
import com.ruoyi.web.service.IErpPurchaseInfoService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 外购资料，用于存储外购物料的基本信息和相关数据Controller
 * 
 * @author ruoyi
 * @date 2025-04-08
 */
@RestController
@RequestMapping("/system/purchase-info")
@Api(value = "外购资料，用于存储外购物料的基本信息和相关数据相关")
public class ErpPurchaseInfoController extends BaseController
{
    @Autowired
    private IErpPurchaseInfoService erpPurchaseInfoService;

    /**
     * 查询外购资料，用于存储外购物料的基本信息和相关数据列表
     */
    // @PreAuthorize("@ss.hasPermi('system:info:list')")
    @Anonymous
    @GetMapping("/list")
    @ApiOperation(value = "搜索外购资料", notes = "搜索外购资料")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "每页条目数", dataType = "int", defaultValue = "10"),
            @ApiImplicitParam(name = "pageSize", value = "当前页码", dataType = "int", defaultValue = "1")
    })
    public TableDataInfo list(ErpPurchaseInfo erpPurchaseInfo)
    {
        startPage();
        QueryWrapper<ErpPurchaseInfo> queryWrapper = new QueryWrapper<>(erpPurchaseInfo);
        List<ErpPurchaseInfo> list = erpPurchaseInfoService.list(queryWrapper);
        return getDataTable(list);
    }

    /**
     * 导出外购资料，用于存储外购物料的基本信息和相关数据列表
     */
    // @PreAuthorize("@ss.hasPermi('system:info:export')")
    @Anonymous
    @Log(title = "外购资料，用于存储外购物料的基本信息和相关数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation(value = "导出外购资料", notes = "导出外购资料")
    public void export(HttpServletResponse response, Long[] ids)
    {
        List<ErpPurchaseInfo> list = erpPurchaseInfoService.listByIds(Arrays.asList(ids));
        ExcelUtil<ErpPurchaseInfo> util = new ExcelUtil<>(ErpPurchaseInfo.class);
        util.exportExcel(response, list, "外购资料");
    }

    @Anonymous
    @PostMapping("/import")
    @ApiOperation(value = "导入外购资料", notes = "导入外购资料")
    public AjaxResult importData(@RequestPart("file") MultipartFile file) throws Exception
    {
        List<ErpPurchaseInfo> list = validateExcel(file, ErpPurchaseInfo.class);
        return toAjax(erpPurchaseInfoService.insertErpPurchaseInfoList(list));
    }

    @Anonymous
    @PostMapping("/export/template")
    @ApiOperation(value = "下载外购资料模板", notes = "下载外购资料模板")
    public void exportTemplate(HttpServletResponse response) throws InstantiationException, IllegalAccessException {
        List<ErpPurchaseInfo> list = Collections.singletonList(BaseEntity.createExample(ErpPurchaseInfo.class));
        ExcelUtil<ErpPurchaseInfo> util = createTemplateExcelUtil(ErpPurchaseInfo.class);
        util.exportExcel(response, list, "外购资料");
    }

    /**
     * 获取外购资料，用于存储外购物料的基本信息和相关数据详细信息
     */
    // @PreAuthorize("@ss.hasPermi('system:info:query')")
    @Anonymous
    @GetMapping
    @ApiOperation(value = "获取外购资料", notes = "获取外购资料")
    public AjaxResult getInfo(Long[] ids)
    {
        return success(erpPurchaseInfoService.listByIds(Arrays.asList(ids)));
    }

    /**
     * 新增外购资料，用于存储外购物料的基本信息和相关数据
     */
    // @PreAuthorize("@ss.hasPermi('system:info:add')")
    @Anonymous
    @Log(title = "外购资料，用于存储外购物料的基本信息和相关数据", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation(value = "新增外购资料", notes = "新增外购资料")
    public AjaxResult add(@ModelAttribute ErpPurchaseInfo erpPurchaseInfo) throws IOException {
        return toAjax(erpPurchaseInfoService.insertErpPurchaseInfoList(
                Collections.singletonList(erpPurchaseInfo)
        ));
    }

    /**
     * 修改外购资料，用于存储外购物料的基本信息和相关数据
     */
    // @PreAuthorize("@ss.hasPermi('system:info:edit')")
    @Anonymous
    @Log(title = "外购资料，用于存储外购物料的基本信息和相关数据", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation(value = "修改外购资料", notes = "修改外购资料")
    public AjaxResult edit(@ModelAttribute ErpPurchaseInfo erpPurchaseInfo) throws IOException {
        return toAjax(erpPurchaseInfoService.updateErpPurchaseInfoList(
                Collections.singletonList(erpPurchaseInfo)
        ));
    }

    /**
     * 删除外购资料，用于存储外购物料的基本信息和相关数据
     */
    // @PreAuthorize("@ss.hasPermi('system:info:remove')")
    @Anonymous
    @Log(title = "外购资料，用于存储外购物料的基本信息和相关数据", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    @ApiOperation(value = "删除外购资料", notes = "删除外购资料")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(erpPurchaseInfoService.removeBatchByIds(Arrays.asList(ids)));
    }
}
