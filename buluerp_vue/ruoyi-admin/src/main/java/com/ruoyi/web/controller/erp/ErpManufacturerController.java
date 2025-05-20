package com.ruoyi.web.controller.erp;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.web.domain.ErpManufacturer;
import com.ruoyi.web.request.manufacturer.AddManufacturerRequest;
import com.ruoyi.web.request.manufacturer.ListManufacturerRequest;
import com.ruoyi.web.request.manufacturer.UpdateManufacturerRequest;
import com.ruoyi.web.service.IErpManufacturerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


@RestController
@RequestMapping("/system/manufacturer")
@Api(value = "厂家相关")
public class ErpManufacturerController extends BaseController {

    @Autowired
    private IErpManufacturerService erpManufacturerService;

    /**
     * 查询厂家列表
     */
    @ApiOperation(value = "获得厂家列表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:manufacturer:list')")
    @GetMapping("/list")
    public TableDataInfo list(ListManufacturerRequest listManufacturerRequest)
    {
        startPage();
        List<ErpManufacturer> list = erpManufacturerService.selectErpManufacturerList(listManufacturerRequest);
        return getDataTable(list);
    }

    /**
     * 导出【请填写功能名称】列表
     */
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:manufacturer:export')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Long[] ids)
    {
        List<ErpManufacturer> list = erpManufacturerService.selectErpManufacturerListByIds(ids);
        ExcelUtil<ErpManufacturer> util = new ExcelUtil<ErpManufacturer>(ErpManufacturer.class);
        util.exportExcel(response, list, "【请填写功能名称】数据");
    }

    /**
     * 获取厂家详细信息
     */
    @ApiOperation(value = "获得厂家详细信息")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:manufacturer:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(erpManufacturerService.selectErpManufacturerById(id));
    }

    /**
     * 新增厂家
     */
    @ApiOperation(value = "新增厂家")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:manufacturer:add')")
    @Log(title = "新增厂家", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AddManufacturerRequest addManufacturerRequest)
    {
        return toAjax(erpManufacturerService.insertErpManufacturer(addManufacturerRequest));
    }

    /**
     * 修改厂家信息
     */
    @ApiOperation(value = "修改厂家")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:manufacturer:edit')")
    @Log(title = "修改厂家", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody UpdateManufacturerRequest updateManufacturerRequest)
    {
        return toAjax(erpManufacturerService.updateErpManufacturer(updateManufacturerRequest));
    }

    /**
     * 删除厂家
     */
    @ApiOperation(value = "删除厂家")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:manufacturer:remove')")
    @Log(title = "删除厂家", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(erpManufacturerService.deleteErpManufacturerByIds(ids));
    }


}
