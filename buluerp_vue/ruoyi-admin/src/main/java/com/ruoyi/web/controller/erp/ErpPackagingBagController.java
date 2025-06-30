package com.ruoyi.web.controller.erp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.validation.Save;
import com.ruoyi.common.validation.Update;
import com.ruoyi.web.domain.ErpPackagingBag;
import com.ruoyi.web.service.IErpPackagingBagService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "/system/packaging-bag")
public class ErpPackagingBagController extends BaseController {
    @Autowired
    private IErpPackagingBagService erpPackagingBagService;

    @Anonymous
    @GetMapping("/list")
    @ApiOperation(value = "搜索分包袋列表", notes = "搜索分包袋列表")
    public TableDataInfo list(ErpPackagingBag erpPackagingBag) {
        startPage();
        QueryWrapper<ErpPackagingBag> queryWrapper = new QueryWrapper<>(erpPackagingBag);
        List<ErpPackagingBag> bags = erpPackagingBagService.list(queryWrapper);
        return getDataTable(erpPackagingBagService.fill(bags));
    }

    @Anonymous
    @GetMapping
    @ApiOperation(value = "获取分包袋", notes = "获取分包袋")
    public AjaxResult get(Long[] ids) {
        return AjaxResult.success(
                erpPackagingBagService.fill(erpPackagingBagService.listByIds(Arrays.asList(ids)))
        );
    }

    @Anonymous
    @PostMapping
    @ApiOperation(value = "新增分包袋", notes = "新增分包袋")
    public AjaxResult add(@RequestBody @Validated({ Save.class }) ErpPackagingBag erpPackagingBag) {
        erpPackagingBagService.check(erpPackagingBag);
        return toAjax(erpPackagingBagService.save(erpPackagingBag));
    }

    @Anonymous
    @PutMapping
    @ApiOperation(value = "更新分包袋", notes = "更新分包袋")
    public AjaxResult update(@RequestBody @Validated({ Update.class }) ErpPackagingBag erpPackagingBag) {
        erpPackagingBagService.check(erpPackagingBag);
        return toAjax(erpPackagingBagService.updateById(erpPackagingBag));
    }

    @Anonymous
    @DeleteMapping("/{ids}")
    @ApiOperation(value = "删除分包袋", notes = "删除分包袋")
    public AjaxResult delete(@PathVariable Long[] ids) {
        return success(erpPackagingBagService.deleteCascade(ids));
    }
}
