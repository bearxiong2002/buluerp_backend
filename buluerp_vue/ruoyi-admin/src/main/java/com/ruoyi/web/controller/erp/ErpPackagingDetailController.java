package com.ruoyi.web.controller.erp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.validation.Save;
import com.ruoyi.common.validation.Update;
import com.ruoyi.web.domain.ErpPackagingDetail;
import com.ruoyi.web.service.IErpPackagingDetailService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/system/packaging-detail")
public class ErpPackagingDetailController extends BaseController {
    @Autowired
    private IErpPackagingDetailService erpPackagingDetailService;

    @Anonymous
    @GetMapping("/list")
    @ApiOperation(value = "搜索分包明细列表", notes = "搜索分包明细列表")
    public TableDataInfo list(ErpPackagingDetail erpPackagingDetail) {
        startPage();
        QueryWrapper<ErpPackagingDetail> queryWrapper = new QueryWrapper<>(erpPackagingDetail);
        List<ErpPackagingDetail> list = erpPackagingDetailService.list(queryWrapper);
        return getDataTable(list);
    }

    @Anonymous
    @GetMapping
    @ApiOperation(value = "获取分包明细列表", notes = "获取分包明细列表")
    public AjaxResult get(Long[] ids) {
        return AjaxResult.success(
                erpPackagingDetailService.listByIds(Arrays.asList(ids))
        );
    }

    @Anonymous
    @PostMapping
    @ApiOperation(value = "新增分包明细", notes = "新增分包明细")
    public AjaxResult add(@ModelAttribute @Validated(Save.class) ErpPackagingDetail entity) throws IOException {
        erpPackagingDetailService.check(entity);
        erpPackagingDetailService.uploadImage(entity);
        return toAjax(
                erpPackagingDetailService.saveOrUpdate(entity)
        );
    }

    @Anonymous
    @PutMapping
    @ApiOperation(value = "修改分包明细", notes = "修改分包明细")
    public AjaxResult update(@ModelAttribute @Validated(Update.class) ErpPackagingDetail entity) throws IOException {
        erpPackagingDetailService.checkReferences(entity);
        erpPackagingDetailService.uploadImage(entity);
        return toAjax(
                erpPackagingDetailService.updateById(entity)
        );
    }

    @Anonymous
    @DeleteMapping("/{ids}")
    @ApiOperation(value = "删除分包明细", notes = "删除分包明细")
    public AjaxResult delete(@PathVariable Long[] ids) {
        return toAjax(
                erpPackagingDetailService.removeBatchByIds(Arrays.asList(ids))
        );
    }
}
