package com.ruoyi.web.request.product;

import com.ruoyi.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@ApiModel(value = "新增产品请求类")
public class AddProductRequest {

    @Excel(name = "内部编号")
    @ApiModelProperty(dataType = "String",value = "内部编号",required = true)
    private String innerId;
    @Excel(name = "外部编号")
    @ApiModelProperty(dataType = "String",value = "外部编号",required = true)
    private String outerId;
    @Excel(name ="产品名")
    @ApiModelProperty(dataType = "Long",value = "产品名称",required = true)
    @NotBlank
    private String name;
    @ApiModelProperty(dataType = "File",value = "产品图片",required = true)
    private MultipartFile picture;
    @Excel(name ="物料id列表")
    @NotBlank
    private String materialString;
    @Excel(name = "产品图片",cellType = Excel.ColumnType.IMAGE)
    private String pictureStr;
    @ApiModelProperty(dataType = "List<Integer>",value = "物料id列表",required = false)
    private List<Integer> materialIds;

    private Integer rowNumber;

    public String getInnerId() {
        return innerId;
    }

    public void setInnerId(String innerId) {
        this.innerId = innerId;
    }

    public String getOuterId() {
        return outerId;
    }

    public void setOuterId(String outerId) {
        this.outerId = outerId;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getMaterialString() {
        return materialString;
    }

    public void setMaterialString(String materialString) {
        this.materialString = materialString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MultipartFile getPicture() {
        return picture;
    }

    public void setPicture(MultipartFile picture) {
        this.picture = picture;
    }

    public void convertPictureStrToMultipartFile() {
        // 检查pictureStr是否为空或者是Excel中的DISPIMG函数格式
        if (pictureStr == null || pictureStr.trim().isEmpty()) {
            return;
        }
        
        // 如果是Excel中的DISPIMG格式，跳过转换
        if (pictureStr.startsWith("=DISPIMG(") || pictureStr.contains("DISPIMG(")) {
            // 设置picture为null，表示没有图片
            this.picture = null;
            return;
        }
        
        // 只处理Base64格式的图片
        if (pictureStr.startsWith("data:image/")) {
            String fileName = generateImageName(this.name);
            MultipartFile convertedFile = convertBase64ToMultipartFile(pictureStr, fileName);
            if (convertedFile != null) {
                this.picture = convertedFile;
            } else {
                // 转换失败时设置为null
                this.picture = null;
            }
        }
    }

    private String generateImageName(String productName) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());
        // 使用产品名称作为文件名的一部分（若为空则使用默认值）
        String safeName = (productName != null && !productName.isEmpty())
                ? productName.replaceAll("[^a-zA-Z0-9_-]", "")
                : "product";

        // 缩短文件名长度（防止文件名过长）
        if (safeName.length() > 50) {
            safeName = safeName.substring(0, 50);
        }

        return timestamp + "_" + safeName;
    }

    /**
     * Base64字符串转MultipartFile
     */
    private MultipartFile convertBase64ToMultipartFile(String base64, String fileName) {
        try {
            // 提取MIME类型和Base64数据
            String[] parts = base64.split(",");
            if (parts.length != 2) {
                System.err.println("Base64格式不正确，无法转换图片: " + base64.substring(0, Math.min(50, base64.length())));
                return null;
            }
            
            String metaData = parts[0];
            String data = parts[1];

            String mimeType = metaData.split(";")[0].split(":")[1];
            String extension = mimeType.split("/")[1];

            // 解码Base64数据
            byte[] bytes = Base64.getDecoder().decode(data);

            // 创建临时文件（实际使用ByteArrayResource避免创建临时文件）
            return new MultipartFile() {
                @Override
                public String getName() {
                    return fileName;
                }

                @Override
                public String getOriginalFilename() {
                    return fileName + "." + extension;
                }

                @Override
                public String getContentType() {
                    return mimeType;
                }

                @Override
                public boolean isEmpty() {
                    return bytes.length == 0;
                }

                @Override
                public long getSize() {
                    return bytes.length;
                }

                @Override
                public byte[] getBytes() throws IOException {
                    return bytes;
                }

                @Override
                public InputStream getInputStream() throws IOException {
                    return new ByteArrayInputStream(bytes);
                }

                @Override
                public void transferTo(File dest) throws IOException, IllegalStateException {
                    new FileOutputStream(dest).write(bytes);
                }
            };
        } catch (Exception e) {
            System.err.println("图片格式转换失败: " + e.getMessage());
            return null;
        }
    }

    public String getPictureStr() {
        return pictureStr;
    }

    public void setPictureStr(String pictureStr) {
        this.pictureStr = pictureStr;
    }

    public List<Integer> getMaterialIds() {
        return materialIds;
    }

    public void setMaterialIds(List<Integer> materialIds) {
        this.materialIds = materialIds;
    }

}
