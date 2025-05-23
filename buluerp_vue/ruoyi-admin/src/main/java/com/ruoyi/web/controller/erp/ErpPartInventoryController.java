package com.ruoyi.web.controller.erp;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.web.domain.ErpPartInventoryChange;
import com.ruoyi.web.request.Inventory.AddPartInventoryRequest;
import com.ruoyi.web.request.Inventory.ListPartInventoryRequest;
import com.ruoyi.web.request.Inventory.UpdatePartInventoryRequest;
import com.ruoyi.web.service.IErpPartInventoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/system/inventory/part")
@Api(value = "胶件出入库管理")
public class ErpPartInventoryController extends BaseController {

    @Autowired
    private IErpPartInventoryService partInventoryService;

    @ApiOperation(value = "获取胶件出入库列表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:part-inventory:list')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "记录ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "orderCode", value = "订单编号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "mouldNumber", value = "模具编号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "colorCode", value = "颜色代码", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "operator", value = "操作人", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "inOutQuantity", value = "出入库数量", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "remarks", value = "备注信息", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "createTimeFrom", value = "创建时间起始", dataType = "datetime"),
            @ApiImplicitParam(name = "createTimeTo", value = "创建时间终止", dataType = "datetime"),
            @ApiImplicitParam(name = "changeDateFrom", value = "变更日期起始", dataType = "date"),
            @ApiImplicitParam(name = "changeDateTo", value = "变更日期结束", dataType = "date")
    })
    @GetMapping("/list")
    public TableDataInfo list(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String orderCode,
            @RequestParam(required = false) String mouldNumber,
            @RequestParam(required = false) String colorCode,
            @RequestParam(required = false) String operator,
            @RequestParam(required = false) Integer inOutQuantity,
            @RequestParam(required = false) String remarks,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime createTimeFrom,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime createTimeTo,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(required = false) Date changeDateFrom,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(required = false) Date changeDateTo) {

        ListPartInventoryRequest request = new ListPartInventoryRequest();
        request.setId(id);
        request.setOrderCode(orderCode);
        request.setMouldNumber(mouldNumber);
        request.setColorCode(colorCode);
        request.setOperator(operator);
        request.setInOutQuantity(inOutQuantity);
        request.setRemarks(remarks);
        request.setCreateTimeFrom(createTimeFrom);
        request.setCreateTimeTo(createTimeTo);
        request.setChangeDateFrom(changeDateFrom);
        request.setChangeDateTo(changeDateTo);

        startPage();
        List<ErpPartInventoryChange> list = partInventoryService.selectList(request);
        return getDataTable(list);
    }

    @ApiOperation(value = "导出胶件出入库数据")
    //@PreAuthorize("@ss.hasPermi('system:part-inventory:export')")
    @Anonymous
    @PostMapping("/export")
    public void export(HttpServletResponse response, ListPartInventoryRequest request) {
        List<ErpPartInventoryChange> list = partInventoryService.selectList(request);
        ExcelUtil<ErpPartInventoryChange> util = new ExcelUtil<>(ErpPartInventoryChange.class);
        String fileName = "胶件库存_" + new SimpleDateFormat("yyyyMMdd").format(new Date());
        util.exportExcel(response, list, fileName);
    }

    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:part-inventory:import')")
    @PostMapping("/import")
    @ApiOperation(value = "导入胶件出入库")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file) throws IOException {
        ExcelUtil<AddPartInventoryRequest> util = new ExcelUtil<>(AddPartInventoryRequest.class);
        List<AddPartInventoryRequest> importList = util.importExcel(file.getInputStream());
        int count = 0;
        for (AddPartInventoryRequest req : importList) {
            partInventoryService.insertRecord(req);
            count++;
        }
        return success(count);
    }

    @ApiOperation(value = "获取胶件出入库详情")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:part-inventory:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(partInventoryService.getById(id));
    }

    @ApiOperation(value = "新增胶件出入库记录")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:part-inventory:add')")
    @PostMapping()
    public AjaxResult add(@RequestBody AddPartInventoryRequest request) {
        return toAjax(partInventoryService.insertRecord(request));
    }

    @ApiOperation(value = "修改胶件出入库记录")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:part-inventory:edit')")
    @PutMapping()
    public AjaxResult edit(@RequestBody UpdatePartInventoryRequest request) {
        return toAjax(partInventoryService.updateRecord(request));
    }

    @ApiOperation(value = "删除胶件出入库记录")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:part-inventory:remove')")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable List<Integer> ids) {
        return toAjax(partInventoryService.deleteByIds(ids));
    }
}