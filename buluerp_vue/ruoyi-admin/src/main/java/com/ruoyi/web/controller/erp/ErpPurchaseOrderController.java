package com.ruoyi.web.controller.erp;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.web.controller.tool.ExcelImageImportUtil;
import com.ruoyi.web.domain.ErpMaterialInfo;
import com.ruoyi.web.domain.ErpPurchaseOrder;
import com.ruoyi.web.exception.ImportException;
import com.ruoyi.web.request.product.AddProductRequest;
import com.ruoyi.web.request.purchase.AddPurchaseOrderRequest;
import com.ruoyi.web.request.purchase.ListPurchaseOrderRequest;
import com.ruoyi.web.request.purchase.UpdatePurchaseOrderRequest;
import com.ruoyi.web.result.PurchaseOrderResult;
import com.ruoyi.web.service.IErpPurchaseOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system/purchase/order")
@Api(value = "采购订单相关")
public class ErpPurchaseOrderController extends BaseController {

    @Autowired
    private IErpPurchaseOrderService erpPurchaseOrderService;

    @ApiOperation(value = "获得采购订单列表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:purchaseOrder:list')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "采购单ID", dataType = "integer", paramType = "query", example = "1"),
            @ApiImplicitParam(name = "purchaseId", value = "采购计划ID", dataType = "integer", paramType = "query", example = "100"),
            @ApiImplicitParam(name = "amount", value = "金额", dataType = "double"),
            @ApiImplicitParam(name = "createTimeFrom", value = "创建时间起始", dataType = "string", paramType = "query", format = "date-time", example = "2023-01-01 00:00:00"),
            @ApiImplicitParam(name = "createTimeTo", value = "创建时间终止", dataType = "string", paramType = "query", format = "date-time", example = "2023-12-31 23:59:59"),
            @ApiImplicitParam(name = "createUser", value = "创建用户", dataType = "string", paramType = "query", example = "admin")
    })
    @GetMapping("/list")
    public TableDataInfo list(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) Integer purchaseId,
            @RequestParam(required = false) String createUser,
            @RequestParam(required = false) Double amount,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime createTimeFrom,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime createTimeTo) {

        ListPurchaseOrderRequest request = new ListPurchaseOrderRequest();
        request.setId(id);
        request.setAmount(amount);
        request.setPurchaseId(purchaseId);
        request.setCreateUser(createUser);
        request.setCreateTimeFrom(createTimeFrom);
        request.setCreateTimeTo(createTimeTo);

        // 分页查询
        startPage();
        List<PurchaseOrderResult> list = erpPurchaseOrderService.selectErpPurchaseOrderList(request);

        return getDataTable(list);
    }

    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:purchaseOrder:import')")
    @PostMapping("/import")
    @ApiOperation("导入采购订单")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file) throws IOException {
        final int INVOICE_START_COL = 2; // 发票列从第4列开始（0-based index）

        List<AddPurchaseOrderRequest> importRequests = new ArrayList<>();
        int totalRows = 0;
        int successfulRows = 0;
        List<Map<String, Object>> errorList = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {
            // 1. 创建Excel工作簿
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            // 2. 获取所有图片数据
            Map<Integer, List<String>> invoiceImages = null;
            try {
                invoiceImages = ExcelImageImportUtil.importInvoiceImages(new ByteArrayInputStream(file.getBytes()), INVOICE_START_COL);
                // System.out.println("图片导入结果: " + (invoiceImages != null ? invoiceImages.size() : "null") + " 行包含图片");
                // if (invoiceImages != null) {
                //     invoiceImages.forEach((rowIdx, images) -> 
                //         System.out.println("第" + rowIdx + "行包含" + images.size() + "个发票图片"));
                // }
            } catch (Exception e) {
                // 图片处理失败但不中断整个导入
                // System.out.println("发票图片处理异常: " + e.getMessage());
                e.printStackTrace(); // 保留完整堆栈跟踪用于调试
            }

            // 3. 处理每一行
            for (int rowIdx = 1; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
                Row row = sheet.getRow(rowIdx);
                if (row == null) continue;
                
                // 检查是否为空行（跳过空行）
                if (isEmptyRow(row)) {
                    // System.out.println("跳过空行: " + rowIdx);
                    continue;
                }

                totalRows++;
                AddPurchaseOrderRequest request = new AddPurchaseOrderRequest();

                try {
                    // 4. 解析基本字段
                    parseBaseFields(row, request, rowIdx);

                    // 5. 添加发票图片
                    if (invoiceImages != null && invoiceImages.containsKey(rowIdx)) {
                        List<String> rowImages = invoiceImages.get(rowIdx);
                        if (rowImages != null) {
                            rowImages.forEach(request::addInvoiceImage);
                        }
                    }

                    // 6. 转换图片为MultipartFile数组
                    request.convertImagesToMultipartFiles();
                    // System.out.println("第" + rowIdx + "行处理完成，发票数量: " + (request.getInvoice() != null ? request.getInvoice().length : "null"));

                    // 7. 调用服务保存订单
                    erpPurchaseOrderService.insertErpPurchaseOrder(request);

                    successfulRows++;
                    // System.out.println("第" + rowIdx + "行数据保存成功");
                } catch (Exception e) {
                    errorList.add(createErrorEntry(rowIdx, e, request));
                }
            }
        } catch (Exception e) {
            return AjaxResult.error("Excel解析失败: " + e.getMessage());
        }

        // 8. 返回导入结果
        return buildImportResult(totalRows, successfulRows, errorList);
    }

    /**
     * 解析基本字段
     */
    private void parseBaseFields(Row row, AddPurchaseOrderRequest request, int rowIdx) throws ImportException {
        // 采购ID（第0列）
        Cell idCell = row.getCell(0);
        if (idCell == null) {
            throw new ImportException(rowIdx, "采购计划ID不能为空", getRawDataFromRow(row));
        }

        if (idCell.getCellType() == CellType.NUMERIC) {
            double value = idCell.getNumericCellValue();
            // 验证必须是整数
            if (value % 1 != 0) {
                throw new ImportException(rowIdx, "采购计划ID必须为整数", getRawDataFromRow(row));
            }
            request.setPurchaseId((int) value);
        } else if (idCell.getCellType() == CellType.STRING) {
            try {
                request.setPurchaseId(Integer.parseInt(idCell.getStringCellValue()));
            } catch (NumberFormatException e) {
                throw new ImportException(rowIdx, "采购计划ID格式错误", getRawDataFromRow(row));
            }
        } else {
            throw new ImportException(rowIdx, "采购计划ID格式错误", getRawDataFromRow(row));
        }

        // 金额（第1列）
        Cell amountCell = row.getCell(1);
        if (amountCell == null) {
            throw new ImportException(rowIdx, "订单金额不能为空", getRawDataFromRow(row));
        }

        if (amountCell.getCellType() == CellType.NUMERIC) {
            request.setAmount(amountCell.getNumericCellValue());
        } else if (amountCell.getCellType() == CellType.STRING) {
            try {
                request.setAmount(Double.parseDouble(amountCell.getStringCellValue()));
            } catch (NumberFormatException e) {
                throw new ImportException(rowIdx, "订单金额格式错误", getRawDataFromRow(row));
            }
        } else {
            throw new ImportException(rowIdx, "订单金额格式错误", getRawDataFromRow(row));
        }
    }

    /**
     * 创建错误条目
     */
    private Map<String, Object> createErrorEntry(int rowIdx, Exception e, AddPurchaseOrderRequest request) {
        Map<String, Object> errorEntry = new HashMap<>();
        errorEntry.put("row", rowIdx);
        
        if (e instanceof ImportException) {
            ImportException importEx = (ImportException) e;
            errorEntry.put("error", importEx.getErrorMsg());
            errorEntry.put("data", importEx.getRawData());
        } else {
            errorEntry.put("error", "系统错误: " + e.getMessage());
            errorEntry.put("data", request.toString());
        }
        
        return errorEntry;
    }

    /**
     * 构建导入结果
     */
    private AjaxResult buildImportResult(int totalRows, int successfulRows, List<Map<String, Object>> errorList) {
        if (!errorList.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("totalRows", totalRows);
            result.put("successRows", successfulRows);
            result.put("errorRows", errorList.size());
            result.put("errors", errorList);
            
            return AjaxResult.error("导入完成，但存在错误", result);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalRows", totalRows);
        result.put("successRows", successfulRows);
        result.put("errorRows", 0);
        
        return AjaxResult.success("导入成功", result);
    }

    /**
     * 获取行原始数据
     */
    private String getRawDataFromRow(Row row) {
        if (row == null) return "";

        List<String> values = new ArrayList<>();
        int lastCellNum = row.getLastCellNum();
        if (lastCellNum <= 0) {
            return "";
        }
        
        for (int i = 0; i < lastCellNum; i++) {
            Cell cell = row.getCell(i);
            if (cell != null) {
                values.add(getRawDataFromCell(cell));
            } else {
                values.add("");
            }
        }
        return String.join(",", values);
    }

    /**
     * 获取单元格原始数据
     */
    private String getRawDataFromCell(Cell cell) {
        if (cell == null) return "";

        try {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    return String.valueOf(cell.getNumericCellValue());
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                default:
                    return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    @ApiOperation(value = "导出采购订单列表")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:purchaseOrder:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, ListPurchaseOrderRequest listPurchaseOrderRequest) {
        List<ErpPurchaseOrder> list = erpPurchaseOrderService.list();
        ExcelUtil<ErpPurchaseOrder> util = new ExcelUtil<ErpPurchaseOrder>(ErpPurchaseOrder.class);
        util.exportExcel(response, list, "采购订单数据");
    }

    @ApiOperation(value = "获得采购订单详细信息")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:purchaseOrder:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id) {
        return success(erpPurchaseOrderService.getById(id));
    }

    @ApiOperation(value = "新增采购订单")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:purchaseOrder:add')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult add(@ModelAttribute AddPurchaseOrderRequest addPurchaseOrderRequest) throws IOException {
        return toAjax(erpPurchaseOrderService.insertErpPurchaseOrder(addPurchaseOrderRequest));
    }

    @ApiOperation(value = "修改采购订单")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:purchaseOrder:edit')")
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "采购单ID", dataType = "integer",required = true),
            @ApiImplicitParam(name = "purchaseId", value = "采购计划ID", dataType = "integer"),
            @ApiImplicitParam(name = "amount", value = "金额", dataType = "double"),
            @ApiImplicitParam(name = "invoice", value = "发票文件", dataType = "MultipartFile")
    })
    public AjaxResult edit(@ModelAttribute UpdatePurchaseOrderRequest updatePurchaseOrderRequest) throws IOException {
        return toAjax(erpPurchaseOrderService.updateErpPurchaseOrder(updatePurchaseOrderRequest));
    }

    @ApiOperation(value = "删除采购订单")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:purchaseOrder:remove')")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable List<Integer> ids) {
        return toAjax(erpPurchaseOrderService.deleteErpPurchaseOrderByIds(ids));
    }

    @ApiOperation(value = "删除采购订单发票")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:purchaseOrder:remove')")
    @DeleteMapping("/invoice/{ids}")
    public AjaxResult removeInvoice(@PathVariable List<Integer> ids) {
        return toAjax(erpPurchaseOrderService.removeInvoice(ids));
    }

    @GetMapping("/template")
    @ApiOperation("下载导入模板")
    @Anonymous
    //@PreAuthorize("@ss.hasPermi('system:purchaseOrder:import')")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        // 1. 创建示例数据（一行合法数据）
        List<AddPurchaseOrderRequest> templateData = new ArrayList<>();
        AddPurchaseOrderRequest example = new AddPurchaseOrderRequest();
        example.setPurchaseId(123); // 采购计划ID（整数）
        example.setAmount(10000.00); // 订单金额（浮点数）
        templateData.add(example);

        // 2. 使用 ExcelUtil 导出
        ExcelUtil<AddPurchaseOrderRequest> util = new ExcelUtil<>(AddPurchaseOrderRequest.class);
        util.exportExcel(response, templateData, "采购订单导入模板");
    }

    /**
     * 检查是否为空行
     */
    private boolean isEmptyRow(Row row) {
        if (row == null) return true;
        
        // 检查前3列（采购ID、金额、供应商）是否都为空
        for (int i = 0; i < 3; i++) {
            Cell cell = row.getCell(i);
            if (cell != null && !isCellEmpty(cell)) {
                return false; // 只要有一个单元格不为空，就不是空行
            }
        }
        return true; // 前3列都为空，认为是空行
    }
    
    /**
     * 检查单元格是否为空
     */
    private boolean isCellEmpty(Cell cell) {
        if (cell == null) return true;
        
        switch (cell.getCellType()) {
            case STRING:
                String stringValue = cell.getStringCellValue();
                return stringValue == null || stringValue.trim().isEmpty();
            case NUMERIC:
                return false; // 数字单元格不为空
            case BOOLEAN:
                return false; // 布尔单元格不为空
            case FORMULA:
                return false; // 公式单元格不为空（可能是图片）
            case BLANK:
            case _NONE:
            default:
                return true;
        }
    }
}