package com.ruoyi.web.request.design;

import com.ruoyi.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

/**
 * 创建设计总表请求
 */

@ApiModel(value = "新增造型表请求类")
public class AddDesignRequest {

    @NotNull(message = "分组编号不能为空")
    @Excel(name = "造型表的分组")
    @ApiModelProperty(dataType = "Long", value = "造型表分组编号", required = true)
    private Long groupId;

    @NotNull(message = "产品id不能为空")
    @Excel(name = "产品id")
    @ApiModelProperty(dataType = "Long", value = "产品id", required = true)
    private Long productId;

    @NotBlank(message = "模具编号不能为空")
    @Excel(name = "模具编号")
    @ApiModelProperty(dataType = "String", value = "模具编号", required = true)
    private String mouldNumber;

    @NotBlank(message = "LDD编号不能为空")
    @Excel(name = "LDD编号")
    @ApiModelProperty(dataType = "String", value = "LDD编号", required = true)
    private String lddNumber;

    @NotBlank(message = "模具类别不能为空")
    @Excel(name = "模具类别")
    @ApiModelProperty(dataType = "String", value = "模具类别", required = true)
    private String mouldCategory;

    @NotBlank(message = "模具ID不能为空")
    @Excel(name = "模具ID")
    @ApiModelProperty(dataType = "String", value = "模具id", required = true)
    private String mouldId;

    @NotBlank(message = "产品名称不能为空")
    @Excel(name = "模具生产的产品名称")
    @ApiModelProperty(dataType = "String", value = "模具生产的产品名称", required = true)
    private String productName;

    @NotNull(message = "模具数量不能为空")
    @Excel(name = "模具数量")
    @ApiModelProperty(dataType = "Long", value = "模具数量", required = true)
    private Long quantity;

    @NotBlank(message = "模具用料不能为空")
    @Excel(name = "模具用料")
    @ApiModelProperty(dataType = "String", value = "模具的材料", required = true)
    private String material;

    // 非必填字段
    @Excel(name = "模具的颜色描述")
    @ApiModelProperty(dataType = "String", value = "模具颜色描述")
    private String color;

    @Excel(name = "模具图片",cellType = Excel.ColumnType.IMAGE)
    private String pictureStr;

    @ApiModelProperty(dataType = "file", value = "模具图片")
    private MultipartFile picture;

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
            String fileName = generateImageName(this.mouldNumber);
            MultipartFile convertedFile = convertBase64ToMultipartFile(pictureStr, fileName);
            if (convertedFile != null) {
                this.picture = convertedFile;
            } else {
                // 转换失败时设置为null
                this.picture = null;
            }
        }
    }

    private String generateImageName(String mouldNumber) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());
        // 使用模具编号作为文件名的一部分（若为空则使用默认值）
        String safeNumber = (mouldNumber != null && !mouldNumber.isEmpty())
                ? mouldNumber.replaceAll("[^a-zA-Z0-9_-]", "")
                : "mould";

        // 缩短文件名长度（防止文件名过长）
        if (safeNumber.length() > 50) {
            safeNumber = safeNumber.substring(0, 50);
        }

        return timestamp + "_" + safeNumber;
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

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getMouldNumber() {
        return mouldNumber;
    }

    public void setMouldNumber(String mouldNumber) {
        this.mouldNumber = mouldNumber;
    }

    public String getLddNumber() {
        return lddNumber;
    }

    public void setLddNumber(String lddNumber) {
        this.lddNumber = lddNumber;
    }

    public String getMouldCategory() {
        return mouldCategory;
    }

    public void setMouldCategory(String mouldCategory) {
        this.mouldCategory = mouldCategory;
    }

    public String getMouldId() {
        return mouldId;
    }

    public void setMouldId(String mouldId) {
        this.mouldId = mouldId;
    }

    public MultipartFile getPicture() {
        return picture;
    }

    public void setPicture(MultipartFile picture) {
        this.picture = picture;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }
}