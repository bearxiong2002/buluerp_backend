package com.ruoyi.web.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.excel.ListValidationException;
import com.ruoyi.common.exception.excel.ListRowErrorInfo;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.web.domain.ErpOrders;
import com.ruoyi.web.domain.ErpPackagingBag;
import com.ruoyi.web.domain.ErpPackagingDetail;
import com.ruoyi.web.domain.ErpPackagingList;
import com.ruoyi.web.enums.AuditTypeEnum;
import com.ruoyi.web.enums.OrderStatus;
import com.ruoyi.web.mapper.ErpPackagingListMapper;
import com.ruoyi.web.service.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ErpPackagingListServiceImpl implements IErpPackagingListService {
    @Autowired
    private ErpPackagingListMapper erpPackagingListMapper;

    @Autowired
    private IErpOrdersService erpOrdersService;

    @Autowired
    private IErpProductsService erpProductsService;

    @Autowired
    private IErpPackagingBagService erpPackagingBagService;

    @Autowired
    private IListValidationService listValidationService;

    @Autowired
    private IErpAuditRecordService auditRecordService;

    @Autowired
    private IErpAuditSwitchService auditSwitchService;

    public static InputStream getListTemplate() {
        return ErpPackagingListServiceImpl.class.getClassLoader().getResourceAsStream(LIST_TEMPLATE);
    }

    public void checkReferences(ErpPackagingList erpPackagingList) {
        if (erpPackagingList.getOrderCode() != null) {
            ErpOrders erpOrders = erpOrdersService.selectByOrderCode(erpPackagingList.getOrderCode());
            if (erpOrders == null) {
                throw new ServiceException("订单不存在");
            } else {
                erpPackagingList.setProductId(erpOrders.getProductId());
            }
        }
        if (erpPackagingList.getProductId() != null) {
            if (erpProductsService.getById(erpPackagingList.getProductId()) == null) {
                throw new ServiceException("产品不存在");
            }
        }
    }

    public void checkUnique(ErpPackagingList erpPackagingList) {
        if (erpPackagingList.getOrderCode()!= null) {
            LambdaQueryWrapper<ErpPackagingList> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ErpPackagingList::getOrderCode, erpPackagingList.getOrderCode());
            ErpPackagingList original = erpPackagingListMapper.selectOne(queryWrapper);
            if (original != null && !Objects.equals(erpPackagingList.getId(), original.getId())) {
                throw new ServiceException("订单已存在分包" + original.getId());
            }
        }
    }

    public void check(ErpPackagingList erpPackagingList) {
        checkReferences(erpPackagingList);
        checkUnique(erpPackagingList);
    }

    public ErpPackagingList fill(ErpPackagingList entity) {
        if (entity == null) {
            return null;
        }
        List<ErpPackagingBag> erpPackagingBags = erpPackagingBagService.listByPackagingList(entity.getId());
        entity.setBagList(erpPackagingBags);
        return entity;
    }

    public List<ErpPackagingList> fill(List<ErpPackagingList> entities) {
        for (ErpPackagingList entity : entities) {
            fill(entity);
        }
        return entities;
    }

    @Override
    public void markPackagingDone(Long id) {
        ErpPackagingList erpPackagingList = erpPackagingListMapper.selectErpPackagingListById(id);
        if (erpPackagingList == null) {
            throw new ServiceException("分包不存在");
        }
        
        // 检查是否启用分包审核
        if (auditSwitchService.isAuditEnabled(AuditTypeEnum.SUBCONTRACT_AUDIT.getCode())) {
            // 发起分包完成审核（使用订单号）
            auditRecordService.handlePackagingListCompleteAudit(erpPackagingList.getOrderCode());
        } else {
            // 如果未启用审核，直接完成
            executeMarkPackagingDone(erpPackagingList.getOrderCode());
        }
    }
    
    /**
     * 执行标记分包完成的具体逻辑
     */
    @Override
    public void executeMarkPackagingDone(String orderCode) {
        // 查找该订单的所有分包记录
        LambdaQueryWrapper<ErpPackagingList> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ErpPackagingList::getOrderCode, orderCode);
        List<ErpPackagingList> packagingLists = erpPackagingListMapper.selectList(wrapper);
        
        for (ErpPackagingList packagingList : packagingLists) {
            if (!packagingList.getDone()) {
                packagingList.setDone(true);
                erpPackagingListMapper.updateById(packagingList);
            }
        }
        
        // 更新订单状态
        erpOrdersService.updateOrderStatusAutomatic(orderCode, OrderStatus.PACKAGED);
    }

    @Override
    public ErpPackagingList selectErpPackagingListById(Long id) {
        return fill(erpPackagingListMapper.selectErpPackagingListById(id));
    }

    @Override
    public List<ErpPackagingList> selectErpPackagingListList(ErpPackagingList erpPackagingList) {
        return fill(erpPackagingListMapper.selectErpPackagingListList(erpPackagingList));
    }

    @Override
    public List<ErpPackagingList> selectErpPackagingListListByIds(Long[] ids) {
        return fill(erpPackagingListMapper.selectErpPackagingListListByIds(ids));
    }

    @Override
    @Transactional
    public int insertErpPackagingList(ErpPackagingList erpPackagingList) {
        erpPackagingList.setCreationTime(DateUtils.getNowDate());
        erpPackagingList.setOperator(SecurityUtils.getUsername());
        check(erpPackagingList);
        int result = erpPackagingListMapper.insertErpPackagingList(erpPackagingList);
        if (result <= 0) {
            throw new ServiceException("新增分包表失败");
        }
        ErpOrders order = erpOrdersService.selectByOrderCode(erpPackagingList.getOrderCode());
        if (order == null) {
            throw new ServiceException("订单不存在");
        }
        erpPackagingList.setProductId(order.getProductId());

        erpOrdersService.updateOrderStatusAutomatic(
                erpPackagingList.getOrderCode(),
                OrderStatus.PACKAGING
        );
        boolean done = erpPackagingList.getDone() != null && erpPackagingList.getDone();
        erpPackagingList.setDone(null);

        // 移除创建时的审核触发，改为在markPackagingDone时触发

        if (done) {
            markPackagingDone(erpPackagingList.getId());
        }

        return result;
    }

    @Override
    public int updateErpPackagingList(ErpPackagingList erpPackagingList) {
        erpPackagingList.setOperator(SecurityUtils.getUsername());
        check(erpPackagingList);

        // 检查状态变更是否需要触发审核
        ErpPackagingList oldPackagingList = erpPackagingListMapper.selectErpPackagingListById(erpPackagingList.getId());
        if (erpPackagingList.getStatus() != null &&
            !erpPackagingList.getStatus().equals(oldPackagingList.getStatus()) &&
            auditSwitchService.isAuditEnabled(AuditTypeEnum.SUBCONTRACT_AUDIT.getCode())) {
            
            auditRecordService.handlePackagingListStatusChange(erpPackagingList, erpPackagingList.getStatus());
            // 状态变更进入审核流程，本次不直接更新状态
            erpPackagingList.setStatus(oldPackagingList.getStatus());
        }

        boolean done = erpPackagingList.getDone() != null && erpPackagingList.getDone();
        erpPackagingList.setDone(null);
        int result = erpPackagingListMapper.updateErpPackagingList(erpPackagingList);
        if (done) {
            markPackagingDone(erpPackagingList.getId());
        }
        return result;
    }

    @Override
    @Transactional
    public int deleteErpPackagingListByIds(Long[] ids) {
        for (Long id : ids) {
            // 删除前处理关联的待审核记录
            auditRecordService.handleAuditableEntityDeleted(
                AuditTypeEnum.SUBCONTRACT_AUDIT.getCode(),
                id
            );
        }
        erpPackagingBagService.deleteCascadeByListIds(ids);
        return erpPackagingListMapper.deleteErpPackagingListByIds(ids);
    }

    @Override
    public void exportExcel(OutputStream outputStream, ErpPackagingList erpPackagingList) throws IOException {
        if (erpPackagingList == null) {
            throw new ServiceException("分包不存在");
        }

        Workbook template = WorkbookFactory.create(getListTemplate());
        if (erpPackagingList.getBagList().isEmpty()) {
            template.removeSheetAt(1);
        } else if (erpPackagingList.getBagList().size() > 1) {
            Sheet src = template.getSheetAt(1);
            for (int i = 2; i <= erpPackagingList.getBagList().size(); i++) {
                Sheet dest = template.createSheet("分包袋" + i);
                copySheetContent(src, dest, template);
            }
        }

        // 把复制好的 workbook 暂存到 ByteArrayOutputStream
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        template.write(bos);
        template.close();

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

        try (ExcelWriter writer = EasyExcel.write(outputStream)
                .withTemplate(bis)
                .build()) {
            WriteSheet listSheet = EasyExcel.writerSheet(0, "分包表")
                    .build();
            writer.fill(ErpPackagingList.ExcelData.fromEntity(erpPackagingList), listSheet);

            int sheetNo = 1;
            for (ErpPackagingBag erpPackagingBag : erpPackagingList.getBagList()) {
                WriteSheet bagSheet = EasyExcel.writerSheet(sheetNo, "分包袋" + sheetNo)
                        .build();
                writer.fill(erpPackagingBag, bagSheet);
                for (ErpPackagingDetail erpPackagingDetail : erpPackagingBag.getDetails()) {
                    erpPackagingDetail.loadExcelImage();
                }
                writer.fill(new FillWrapper("detail", erpPackagingBag.getDetails()), bagSheet);
                sheetNo++;
            }

            writer.finish();
        }
    }

    /**
     * 将源Sheet的内容复制到目标Sheet
     * @param sourceSheet 源Sheet
     * @param targetSheet 目标Sheet
     * @param workbook 工作簿对象
     */
    public static void copySheetContent(Sheet sourceSheet, Sheet targetSheet, Workbook workbook) {
        // 复制所有行
        for (int i = 0; i <= sourceSheet.getLastRowNum(); i++) {
            Row sourceRow = sourceSheet.getRow(i);
            if (sourceRow != null) {
                Row targetRow = targetSheet.createRow(i);
                // 复制行高
                targetRow.setHeight(sourceRow.getHeight());

                // 复制所有单元格
                for (int j = 0; j <= sourceRow.getLastCellNum(); j++) {
                    Cell sourceCell = sourceRow.getCell(j);
                    if (sourceCell != null) {
                        Cell targetCell = targetRow.createCell(j);
                        // 复制单元格内容
                        copyCellContent(sourceCell, targetCell);
                        // 复制单元格样式
                        copyCellStyle(sourceCell, targetCell, workbook);
                    }
                }
            }
        }

        // 复制合并区域
        for (int i = 0; i < sourceSheet.getNumMergedRegions(); i++) {
            CellRangeAddress mergedRegion = sourceSheet.getMergedRegion(i);
            targetSheet.addMergedRegion(mergedRegion.copy());
        }

        // 复制列宽
        for (int i = 0; i < 256; i++) {
            if (sourceSheet.getColumnWidth(i) > 0) {
                targetSheet.setColumnWidth(i, sourceSheet.getColumnWidth(i));
            }
        }
    }

    /**
     * 复制单元格内容
     */
    private static void copyCellContent(Cell sourceCell, Cell targetCell) {
        switch (sourceCell.getCellType()) {
            case STRING:
                targetCell.setCellValue(sourceCell.getStringCellValue());
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(sourceCell)) {
                    targetCell.setCellValue(sourceCell.getDateCellValue());
                } else {
                    targetCell.setCellValue(sourceCell.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                targetCell.setCellValue(sourceCell.getBooleanCellValue());
                break;
            case FORMULA:
                targetCell.setCellFormula(sourceCell.getCellFormula());
                break;
            case BLANK:
                // 保持空白
                break;
            case ERROR:
                targetCell.setCellErrorValue(sourceCell.getErrorCellValue());
                break;
            default:
                break;
        }
    }

    /**
     * 复制单元格样式
     */
    private static void copyCellStyle(Cell sourceCell, Cell targetCell, Workbook workbook) {
        // 由于POI限制，不能直接复制CellStyle，需要创建新的
        CellStyle newStyle = workbook.createCellStyle();
        newStyle.cloneStyleFrom(sourceCell.getCellStyle());
        targetCell.setCellStyle(newStyle);
    }

    @Override
    public void exportExcel(HttpServletResponse response, ErpPackagingList erpPackagingList) throws IOException {
        if (erpPackagingList == null) {
            throw new ServiceException("分包不存在");
        }
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = "分包表" + erpPackagingList.getId();
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        exportExcel(response.getOutputStream(), erpPackagingList);
    }

    @Override
    public ErpPackagingList importExcel(InputStream inputStream, List<String> basSheetNameReceiver) throws Exception {
        // 读取大表
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet mainSheet = workbook.getSheet("分包表");
        ErpPackagingList erpPackagingList = ExcelUtil
                .extractEntityFromSheet(mainSheet, ErpPackagingList.class);

        // 读取小包表
        erpPackagingList.setBagList(new ArrayList<>());
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            String bagSheetName = workbook.getSheetName(i);
            if (!bagSheetName.startsWith("分包袋")) {
                continue;
            }
            basSheetNameReceiver.add(bagSheetName);
            // TODO: 读取小包
            Sheet bagSheet = workbook.getSheet(bagSheetName);
            ErpPackagingBag bag = ExcelUtil
                   .extractEntityFromSheet(
                           bagSheet,
                           ErpPackagingBag.class,
                           bagSheet.getFirstRowNum(),
                           bagSheet.getFirstRowNum() + BAG_TEMPLATE_HEADER_ROW - 1
                   );
            bag.setPackagingListId(1L); // 为了通过验证，但不是实际的分包ID
            erpPackagingList.getBagList().add(bag);
            // TODO: 读取详情
            ExcelUtil<ErpPackagingDetail> excelUtil = new ExcelUtil<>(ErpPackagingDetail.class);
            List<ErpPackagingDetail> details = excelUtil.importExcel(bagSheetName, workbook, BAG_TEMPLATE_HEADER_ROW);
            for (ErpPackagingDetail detail : details) {
                detail.setPackagingBagId(1L); // 为了通过验证，但不是实际的小包ID
            }
            bag.setDetails(details);
        }
        return erpPackagingList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertCascade(ErpPackagingList erpPackagingList, List<String> basSheetNames) {
        erpPackagingList.setCreationTime(DateUtils.getNowDate());
        erpPackagingList.setOperator(SecurityUtils.getUsername());
        List<ListRowErrorInfo> errorInfos = listValidationService.collectInsertionErrors(
                Collections.singletonList(erpPackagingList),
                this::insertErpPackagingList
        )
                .stream()
                .peek((e) -> e.setSheetName("分包表"))
                .collect(Collectors.toList());
        if (!errorInfos.isEmpty()) {
            // 由于分包大表插入失败，无法设置小包表的分包ID，因此不再继续验证，直接抛出异常
            throw new ListValidationException(errorInfos);
        }
        int i = 0;
        for (ErpPackagingBag bag : erpPackagingList.getBagList()) {
            try {
                bag.setPackagingListId(erpPackagingList.getId());
                erpPackagingBagService.insertCascade(bag);
            } catch (ListValidationException e) {
                for (ListRowErrorInfo ei : e.getErrorInfoList()) {
                    ei.setSheetName(basSheetNames.get(i));
                    errorInfos.add(ei);
                }
            }
            i++;
        }
        if (!errorInfos.isEmpty()) {
            throw new ListValidationException(errorInfos);
        }
    }

    @Override
    public void applyApprovedStatus(ErpPackagingList packagingList) {
        // 直接更新数据库，不包含其他业务逻辑
        erpPackagingListMapper.updateErpPackagingList(packagingList);
    }

    @Override
    public List<ErpPackagingList> selectErpPackagingListByOrderCode(String orderCode){
        LambdaQueryWrapper<ErpPackagingList> wrapper= Wrappers.lambdaQuery();
        wrapper.eq(ErpPackagingList::getOrderCode,orderCode);
        return erpPackagingListMapper.selectList(wrapper);
    }
}
