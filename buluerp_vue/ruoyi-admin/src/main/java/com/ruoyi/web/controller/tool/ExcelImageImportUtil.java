package com.ruoyi.web.controller.tool;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.web.exception.ImportException;
import com.ruoyi.web.request.purchase.AddPurchaseOrderRequest;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Excel 图片导入工具类
 */
public class ExcelImageImportUtil {

    private static final int MAX_INVOICE_COLS = 50; // 最大处理50个发票列

    /**
     * 导入发票图片（从指定列开始获取图片）
     */
    public static Map<Integer, List<String>> importInvoiceImages(InputStream is, int invoiceStartCol) throws IOException {
        Map<Integer, List<String>> invoiceImages = new HashMap<>();
        
        try (Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // 获取所有图片
            Map<String, PictureData> allPictures = getAllPictures(workbook);
            // System.out.println("Excel中检测到的嵌入图片数量: " + allPictures.size());
            // allPictures.forEach((key, pictureData) -> 
            //     System.out.println("图片位置: " + key + ", 大小: " + pictureData.getData().length + " bytes"));
            
            // 遍历每一行（跳过标题行）
            int totalRows = sheet.getLastRowNum();
            // System.out.println("Excel总行数: " + (totalRows + 1) + ", 从第" + (invoiceStartCol + 1) + "列开始检测图片");
            
            for (int rowIdx = 1; rowIdx <= totalRows; rowIdx++) {
                Row row = sheet.getRow(rowIdx);
                if (row == null) {
                    // System.out.println("第" + rowIdx + "行为空，跳过");
                    continue;
                }
                
                // System.out.println("处理第" + rowIdx + "行，单元格数量: " + row.getLastCellNum());
                
                // 检查从invoiceStartCol开始的单元格内容
                for (int colIdx = invoiceStartCol; colIdx < row.getLastCellNum() && colIdx < invoiceStartCol + 10; colIdx++) {
                    Cell cell = row.getCell(colIdx);
                    // if (cell != null) {
                    //     System.out.println("第" + rowIdx + "行第" + (colIdx + 1) + "列: 类型=" + cell.getCellType() + 
                    //                      ", 内容=" + (cell.getCellType() == CellType.STRING ? 
                    //                      cell.getStringCellValue().substring(0, Math.min(50, cell.getStringCellValue().length())) + "..." : 
                    //                      cell.toString()));
                    // } else {
                    //     System.out.println("第" + rowIdx + "行第" + (colIdx + 1) + "列: null");
                    // }
                }
                
                List<String> rowImages = getInvoiceImagesForRow(row, rowIdx, invoiceStartCol, allPictures);
                if (!rowImages.isEmpty()) {
                    invoiceImages.put(rowIdx, rowImages);
                    // System.out.println("第" + rowIdx + "行找到" + rowImages.size() + "个图片");
                } else {
                    // System.out.println("第" + rowIdx + "行未找到图片");
                }
            }
        }
        
        return invoiceImages;
    }

    /**
     * 将Base64字符串转换为MultipartFile
     */
    public static MultipartFile convertBase64ToMultipartFile(String base64Image) {
        if (base64Image == null || base64Image.trim().isEmpty()) {
            return null;
        }
        
        try {
            // 提取Base64数据部分
            String base64Data;
            String mimeType = "image/jpeg"; // 默认类型
            
            if (base64Image.startsWith("data:")) {
                // 格式：data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD...
                String[] parts = base64Image.split(",");
                if (parts.length == 2) {
                    String header = parts[0]; // data:image/jpeg;base64
                    base64Data = parts[1];
                    
                    // 提取MIME类型
                    if (header.contains("image/")) {
                        int start = header.indexOf("image/");
                        int end = header.indexOf(";", start);
                        if (end == -1) end = header.length();
                        mimeType = header.substring(start, end);
                    }
                } else {
                    base64Data = base64Image;
                }
            } else {
                base64Data = base64Image;
            }
            
            // 解码Base64
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);
            
            // 生成文件名
            String fileName = "invoice_" + System.currentTimeMillis() + getFileExtension(mimeType);
            
            return new Base64MultipartFile(imageBytes, fileName, mimeType);
            
        } catch (Exception e) {
            System.err.println("转换Base64图片失败: " + e.getMessage());
            return null;
        }
    }

    /**
     * 根据MIME类型获取文件扩展名
     */
    private static String getFileExtension(String mimeType) {
        switch (mimeType.toLowerCase()) {
            case "image/jpeg":
            case "image/jpg":
                return ".jpg";
            case "image/png":
                return ".png";
            case "image/gif":
                return ".gif";
            case "image/bmp":
                return ".bmp";
            default:
                return ".jpg";
        }
    }

    /**
     * 导入包含发票图片的Excel文件
     */
    public static ImportResult importWithInvoiceImages(InputStream is,
                                                       int invoiceStartCol) throws IOException {
        Workbook workbook = WorkbookFactory.create(is);
        Sheet sheet = workbook.getSheetAt(0);

        // 获取所有图片
        Map<String, PictureData> allPictures = getAllPictures(workbook);

        ImportResult result = new ImportResult();
        int rowCount = Math.max(0, sheet.getLastRowNum()); // 总行数

        for (int rowIdx = 0; rowIdx <= rowCount; rowIdx++) {
            Row row = sheet.getRow(rowIdx);
            if (row == null) continue;

            RowResult rowResult = new RowResult();
            rowResult.rowIndex = rowIdx;

            try {
                // 获取发票图片（只从第1行开始）
                if (rowIdx > 0) {
                    List<String> images = getInvoiceImagesForRow(row, rowIdx, invoiceStartCol, allPictures);
                    rowResult.invoiceImages = images;
                }

                // 解析基本字段
                parseBasicFields(row, rowIdx, rowResult);

                // 如果非标题行（行号大于0）才处理
                if (rowIdx > 0) {
                    result.successRows.add(rowResult);
                }
            } catch (ImportException e) {
                // 处理自定义导入异常
                if (rowIdx > 0) {
                    result.errorRows.add(new ImportErrorResult(
                            e.getRowNumber(),
                            e.getErrorMsg(),
                            e.getRawData()
                    ));
                } else {
                    // System.out.println("标题行错误: " + e.getErrorMsg());
                }
            } catch (Exception e) {
                // 处理其他异常
                if (rowIdx > 0) {
                    String rawData = getRawDataFromRow(row);
                    result.errorRows.add(new ImportErrorResult(
                            rowIdx,
                            "系统错误: " + e.getMessage(),
                            rawData
                    ));
                } else {
                    // System.out.println("标题行系统错误: " + e.getMessage());
                }
            }
        }

        workbook.close();
        return result;
    }

    /**
     * 处理基本字段（包含采购ID类型修复）
     */
    private static void parseBasicFields(Row row, int rowIndex, RowResult rowResult) throws ImportException {
        // 采购ID（第0列）
        Cell idCell = row.getCell(0);
        if (idCell == null && rowIndex > 0) {
            throw new ImportException(rowIndex, "采购计划ID不能为空", getRawDataFromRow(row));
        }

        if (idCell != null) {
            if (idCell.getCellType() == CellType.NUMERIC) {
                double value = idCell.getNumericCellValue();
                // 验证必须是整数
                if (value % 1 != 0) {
                    throw new ImportException(rowIndex, "采购计划ID必须为整数", getRawDataFromRow(row));
                }
                rowResult.purchaseId = (int) value;
            } else if (idCell.getCellType() == CellType.STRING) {
                try {
                    rowResult.purchaseId = Integer.parseInt(idCell.getStringCellValue());
                } catch (NumberFormatException e) {
                    throw new ImportException(rowIndex, "采购计划ID格式错误", getRawDataFromRow(row));
                }
            } else if (rowIndex > 0) { // 非标题行才报错
                throw new ImportException(rowIndex, "采购计划ID格式错误", getRawDataFromRow(row));
            }
        }

        // 金额（第1列）
        Cell amountCell = row.getCell(1);
        if (amountCell != null) {
            if (amountCell.getCellType() == CellType.NUMERIC) {
                rowResult.amount = amountCell.getNumericCellValue();
            } else if (amountCell.getCellType() == CellType.STRING) {
                try {
                    rowResult.amount = Double.parseDouble(amountCell.getStringCellValue());
                } catch (NumberFormatException e) {
                    if (rowIndex > 0) {
                        throw new ImportException(rowIndex, "订单金额格式错误", getRawDataFromRow(row));
                    }
                }
            } else if (rowIndex > 0) {
                throw new ImportException(rowIndex, "订单金额格式错误", getRawDataFromRow(row));
            }
        }

        // 供应商（第2列）
        Cell supplierCell = row.getCell(2);
        if (supplierCell != null && supplierCell.getCellType() == CellType.STRING) {
            rowResult.supplier = supplierCell.getStringCellValue();
        } else if (rowIndex > 0) {
            // 非标题行要求供应商不能为空
            rowResult.supplier = ""; // 或设置为默认值
        }
    }

    /**
     * 获取行中的发票图片
     */
    private static List<String> getInvoiceImagesForRow(Row row,
                                                       int rowIdx,
                                                       int startCol,
                                                       Map<String, PictureData> pictures)
            throws ImportException {

        List<String> images = new ArrayList<>();
        int colIdx = startCol;
        int consecutiveEmptyCells = 0; // 连续空单元格计数

        while (colIdx < startCol + MAX_INVOICE_COLS) {
            Cell cell = row.getCell(colIdx);
            
            if (cell == null) {
                consecutiveEmptyCells++;
                // 如果连续3个空单元格，则停止处理
                if (consecutiveEmptyCells >= 3) {
                    break;
                }
                colIdx++;
                continue;
            }

            try {
                String cellRef = rowIdx + "-" + colIdx;
                PictureData pictureData = pictures.get(cellRef);

                if (pictureData != null) {
                    // 转换图片为Base64
                    String base64 = toBase64(pictureData);
                    images.add(base64);
                    consecutiveEmptyCells = 0; // 重置空单元格计数
                } else if (cell.getCellType() == CellType.FORMULA && isDispImgFormula(cell)) {
                    // 处理 _xlfn.DISPIMG 公式类型的图片
                    String placeholder = createImagePlaceholder(cell, pictures);
                    images.add(placeholder);
                    consecutiveEmptyCells = 0; // 重置空单元格计数
                    // System.out.println("第" + rowIdx + "行第" + (colIdx + 1) + "列检测到DISPIMG图片公式");
                } else if (isBase64Image(cell)) {
                    // 获取Base64文本
                    images.add(cell.getStringCellValue());
                    consecutiveEmptyCells = 0; // 重置空单元格计数
                } else {
                    // 非图片单元格，增加空单元格计数
                    consecutiveEmptyCells++;
                    if (consecutiveEmptyCells >= 3) {
                        break; // 连续3个非图片单元格，停止处理
                    }
                }
            } catch (Exception e) {
                throw new ImportException(rowIdx,
                        String.format("第%d列图片处理失败: %s", colIdx + 1, e.getMessage()),
                        getRawDataFromCell(cell)
                );
            }

            colIdx++;
        }

        return images;
    }

    /**
     * 检查是否为DISPIMG公式
     */
    private static boolean isDispImgFormula(Cell cell) {
        if (cell.getCellType() != CellType.FORMULA) {
            return false;
        }
        
        String formula = cell.getCellFormula();
        return formula != null && formula.contains("_xlfn.DISPIMG");
    }

    /**
     * 为DISPIMG公式创建图片占位符或获取实际图片
     */
    private static String createImagePlaceholder(Cell cell, Map<String, PictureData> pictures) {
        String formula = cell.getCellFormula();
        // 提取图片ID
        String imageId = "unknown";
        if (formula.contains("\"")) {
            int start = formula.indexOf("\"") + 1;
            int end = formula.indexOf("\"", start);
            if (end > start) {
                imageId = formula.substring(start, end);
            }
        }
        
        // 尝试从图片资源中找到对应的图片
        // 通常DISPIMG公式的第一个图片对应resource-0
        for (String key : pictures.keySet()) {
            if (key.startsWith("resource-")) {
                PictureData pictureData = pictures.get(key);
                String base64 = toBase64(pictureData);
                // System.out.println("为DISPIMG公式找到实际图片，图片ID: " + imageId + ", 资源: " + key + ", 大小: " + pictureData.getData().length + " bytes");
                return base64;
            }
        }
        
        // 如果找不到实际图片，创建一个简单的占位符图片（1x1透明PNG的Base64）
        String placeholder = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==";
        
        // System.out.println("为DISPIMG公式创建占位符，图片ID: " + imageId);
        return placeholder;
    }

    /**
     * 获取单元格原始数据
     */
    private static String getRawDataFromCell(Cell cell) {
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

    /**
     * 获取行原始数据
     */
    private static String getRawDataFromRow(Row row) {
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
     * 转换为Base64格式
     */
    private static String toBase64(PictureData pictureData) {
        byte[] data = pictureData.getData();
        String mimeType = pictureData.getMimeType();
        return "data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(data);
    }

    /**
     * 检查单元格是否包含Base64图片
     */
    private static boolean isBase64Image(Cell cell) {
        if (cell.getCellType() != CellType.STRING) {
            return false;
        }

        String value = cell.getStringCellValue();
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        
        value = value.trim();
        
        // 检查标准的Base64图片格式
        if (value.startsWith("data:image/")) {
            // System.out.println("检测到data:image格式的Base64图片，长度: " + value.length());
            return true;
        }
        
        // 检查纯Base64字符串（较长的字符串可能是图片）
        if (value.length() > 100) {
            // 简单的Base64格式检查：只包含Base64字符
            if (value.matches("^[A-Za-z0-9+/]*={0,2}$")) {
                // System.out.println("检测到可能的纯Base64图片，长度: " + value.length());
                return true;
            }
        }
        
        // 检查是否为文件路径
        if (value.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif|bmp)$")) {
            // System.out.println("检测到图片文件路径: " + value);
            // 文件路径暂不处理，但可以记录
            return false;
        }
        
        // System.out.println("单元格内容不是图片格式: " + value.substring(0, Math.min(50, value.length())) + "...");
        return false;
    }

    /**
     * 获取所有图片
     */
    private static Map<String, PictureData> getAllPictures(Workbook workbook) {
        Map<String, PictureData> pictures = new HashMap<>();

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            Drawing<?> drawing = sheet.getDrawingPatriarch();
            if (drawing == null) continue;

            // 修复：使用XSSFDrawing的getShapes()方法
            if (drawing instanceof XSSFDrawing) {
                XSSFDrawing xssfDrawing = (XSSFDrawing) drawing;
                List<XSSFShape> shapes = xssfDrawing.getShapes();
                
                for (XSSFShape shape : shapes) {
                    if (shape instanceof XSSFPicture) {
                        XSSFPicture picture = (XSSFPicture) shape;
                        XSSFClientAnchor anchor = (XSSFClientAnchor) picture.getAnchor();

                        if (anchor != null) {
                            int row = anchor.getRow1();
                            int col = anchor.getCol1();

                            String key = row + "-" + col;
                            pictures.put(key, picture.getPictureData());
                            // System.out.println("找到嵌入图片，位置: 第" + (row + 1) + "行第" + (col + 1) + "列");
                        }
                    }
                }
            }
        }

        // 尝试获取工作簿中的所有图片资源（可能包含DISPIMG引用的图片）
        if (workbook instanceof XSSFWorkbook) {
            XSSFWorkbook xssfWorkbook = (XSSFWorkbook) workbook;
            List<? extends PictureData> allPictureData = xssfWorkbook.getAllPictures();
            // System.out.println("工作簿中的图片资源总数: " + allPictureData.size());
            
            // 将这些图片以序号的形式存储，供DISPIMG公式使用
            for (int idx = 0; idx < allPictureData.size(); idx++) {
                pictures.put("resource-" + idx, allPictureData.get(idx));
                // System.out.println("图片资源" + idx + ": " + allPictureData.get(idx).getData().length + " bytes");
            }
        }

        return pictures;
    }

    /**
     * Base64 MultipartFile实现类
     */
    public static class Base64MultipartFile implements MultipartFile {
        private final byte[] content;
        private final String filename;
        private final String contentType;

        public Base64MultipartFile(byte[] content, String filename, String contentType) {
            this.content = content;
            this.filename = filename;
            this.contentType = contentType;
        }

        @Override
        public String getName() {
            return "file";
        }

        @Override
        public String getOriginalFilename() {
            return filename;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public boolean isEmpty() {
            return content.length == 0;
        }

        @Override
        public long getSize() {
            return content.length;
        }

        @Override
        public byte[] getBytes() throws IOException {
            return content;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(content);
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            try (FileOutputStream fos = new FileOutputStream(dest)) {
                fos.write(content);
            }
        }
    }

    /**
     * 导入结果类
     */
    public static class ImportResult {
        public List<RowResult> successRows = new ArrayList<>();
        public List<ImportErrorResult> errorRows = new ArrayList<>();

        public int getTotalRows() {
            return successRows.size() + errorRows.size();
        }

        public int getSuccessCount() {
            return successRows.size();
        }

        public int getErrorCount() {
            return errorRows.size();
        }
    }

    /**
     * 行结果类
     */
    public static class RowResult {
        public int rowIndex;            // 行索引
        public Integer purchaseId;      // 采购ID（整数）
        public Double amount;           // 金额
        public String supplier;         // 供应商
        public List<String> invoiceImages;  // 发票图片Base64列表

        // 转换为业务对象
        public AddPurchaseOrderRequest toRequest() {
            AddPurchaseOrderRequest request = new AddPurchaseOrderRequest();
            request.setPurchaseId(purchaseId);
            request.setAmount(amount);

            // 添加图片
            if (invoiceImages != null) {
                for (String image : invoiceImages) {
                    request.addInvoiceImage(image);
                }
                request.convertImagesToMultipartFiles();
            }

            return request;
        }
    }

    /**
     * 导入错误结果
     */
    public static class ImportErrorResult {
        private int rowNumber;
        private String errorMsg;
        private String rawData;

        public ImportErrorResult(int rowNumber, String errorMsg, String rawData) {
            this.rowNumber = rowNumber;
            this.errorMsg = errorMsg;
            this.rawData = rawData;
        }

        public int getRowNumber() {
            return rowNumber;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public String getRawData() {
            return rawData;
        }
    }
}