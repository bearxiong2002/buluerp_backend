package com.ruoyi.web.controller.erp;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.web.domain.ErpPackagingList;
import com.ruoyi.web.service.IErpPackagingListService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/packaging-list")
public class ErpPackagingListController extends BaseController {
    @Autowired
    private IErpPackagingListService packagingListService;

    // @PreAuthorize("@ss.hasPermi('system:packaging-list:list')")
    @Anonymous
    @GetMapping("/list")
    @ApiOperation(value = "查询分包列表", notes = "查询分包列表")
    public TableDataInfo list(ErpPackagingList erpPackagingList) {
        startPage();
        return getDataTable(packagingListService.selectErpPackagingListList(erpPackagingList));
    }

    // @PreAuthorize("@ss.hasPermi('system:packaging-list:export')")
}
