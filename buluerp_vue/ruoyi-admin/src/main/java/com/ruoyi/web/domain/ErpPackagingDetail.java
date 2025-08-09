package com.ruoyi.web.domain;

import com.alibaba.excel.metadata.data.ImageData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.common.annotation.Example;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.validation.Save;
import com.ruoyi.common.validation.Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.nio.file.Paths;
import java.util.Collections;

@ApiModel("分包明细")
public class ErpPackagingDetail {
    @TableId(value = "id", type = IdType.AUTO)
    @TableField(condition = SqlCondition.LIKE)
    @Excel(name = "分包明细ID")
    @ApiModelProperty("主键，唯一标识每条记录 [list|PUT|response]")
    private Integer id;

    @TableField(condition = SqlCondition.LIKE)
    @Excel(name = "模具编号")
    @Example(Example.GEN_UUID)
    @ApiModelProperty("模具编号，用于标识模具 [list|POST|PUT|response]")
    // @NotNull(groups = {Save.class}, message = "模具编号不能为空")
    private String mouldNumber;

    @TableField(condition = SqlCondition.LIKE)
    @Excel(name = "胶件图片", cellType = Excel.ColumnType.IMAGE)
    @Example
    @ApiModelProperty("胶件图片，模具零件的图片链接 [list|POST|PUT|response]")
    private String partImageUrl;

    @TableField(condition = SqlCondition.LIKE)
    @Excel(name = "料别")
    @Example("塑料")
    @ApiModelProperty("料别，材料的类型 [list|POST|PUT|response]")
    private String materialType;

    @TableField(condition = SqlCondition.LIKE)
    @Excel(name = "颜色")
    @Example("RGB")
    @ApiModelProperty(value = "颜色编号，模具零件的颜色编码 [list|POST|PUT|response]")
    private String colorCode;

    @TableField(condition = SqlCondition.LIKE)
    @Excel(name = "规格名称")
    @Example("S1")
    @ApiModelProperty("规格名称，模具零件的规格描述 [list|POST|PUT|response]")
    private String specificationName;

    @TableField("`usage`")
    @Excel(name = "用量")
    @Example("0.5")
    @ApiModelProperty(value = "用量，单位为克，表示单个零件的用料量 [list|POST|PUT|response]", required = true)
    @Range(min = 0, message = "用量不能为负数", groups = {Save.class, Update.class})
    @NotNull(groups = {Save.class}, message = "用量不能为空")
    private Double usage;

    @Excel(name = "单重")
    @Example("0.5")
    @ApiModelProperty("单重，单位为克，表示单个胶件的重量 [list|POST|PUT|response]")
    @Range(min = 0, message = "单重不能为负数", groups = {Save.class, Update.class})
    private Double singleWeight;

    @Excel(name = "套料数量")
    @Example("10")
    @ApiModelProperty(value = "套料数量，每套包含的零件数量 [list|POST|PUT|response]", required = true)
    @Range(min = 0, message = "套料数量不能为负数", groups = {Save.class, Update.class})
    @NotNull(groups = {Save.class}, message = "套料数量不能为空")
    private Integer setQuantity;

    @Excel(name = "重量")
    @Example("0.5")
    @ApiModelProperty(value = "重量，单位为克，表示总重量 [list|POST|PUT|response]", required = true)
    @Range(min = 0, message = "重量不能为负数", groups = {Save.class, Update.class})
    @NotNull(groups = {Save.class}, message = "总重量不能为空")
    private Double totalWeight;

    @TableField(condition = SqlCondition.LIKE)
    @Excel(name = "备注")
    @Example("无")
    @ApiModelProperty("备注，其它需要说明的信息 [list|POST|PUT|response]")
    private String remarks;

    @Excel(name = "分包袋编号")
    @ApiModelProperty("分包袋编号，与分包袋主键关联 [list|POST|PUT|response]")
    @NotNull(message = "分包袋编号不能为空", groups = {Save.class})
    private Long packagingBagId;

    @TableField(exist = false)
    @JsonIgnore
    @ApiModelProperty("胶件图片文件 [POST|PUT]")
    private MultipartFile partImageFile;

    @Excel(name = "物料ID")
    @ApiModelProperty(value = "物料ID，与物料信息主键关联 [list|POST|PUT|response]", required = true)
    @NotNull(message = "物料ID不能为空", groups = {Save.class})
    @Example("1")
    private Long materialId;

    @TableField(exist = false)
    @JsonIgnore
    private WriteCellData<Void> imageData;

    public static String parseActualPath(String url) {
        if (!url.startsWith(Constants.RESOURCE_PREFIX)) {
            throw new IllegalArgumentException("文件URL必须以 " + Constants.RESOURCE_PREFIX + " 开头");
        }

        // 2. 移除资源前缀（保留后续路径部分）
        String relativePath = url.substring(Constants.RESOURCE_PREFIX.length());

        // 3. 拼接实际存储路径
        return Paths.get(
                RuoYiConfig.getProfile(), // 基础路径（如 D:/ruoyi/uploadPath）
                relativePath.split("/")   // 拆分路径部分（如 ["", "2025", "05", "10", "xxx.txt"]）
        ).toString();
    }

    public void loadExcelImage() {
        if (partImageUrl == null || partImageUrl.isEmpty()) {
            return;
        }
        String path = parseActualPath(partImageUrl);
        File file = new File(path);

        if (!file.exists()) {
            return;
        }

        try (FileInputStream inputStream = new FileInputStream(file)) {
            // 使用 ByteArrayOutputStream 手动读取字节（Java 8 兼容）
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(bytes)) != -1) {
                buffer.write(bytes, 0, bytesRead);
            }

            ImageData imageData = new ImageData();
            imageData.setImage(buffer.toByteArray()); // 设置图片字节数组

            WriteCellData<Void> cellData = new WriteCellData<>();
            cellData.setRichTextStringDataValue(null); // 清除文本内容
            cellData.setImageDataList(Collections.singletonList(imageData));

            this.imageData = cellData;
        } catch (IOException e) {
            throw new RuntimeException("读取图片文件失败: " + path, e);
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMouldNumber() {
        return mouldNumber;
    }

    public void setMouldNumber(String mouldNumber) {
        this.mouldNumber = mouldNumber;
    }

    public String getPartImageUrl() {
        return partImageUrl;
    }

    public void setPartImageUrl(String partImageUrl) {
        this.partImageUrl = partImageUrl;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getSpecificationName() {
        return specificationName;
    }

    public void setSpecificationName(String specificationName) {
        this.specificationName = specificationName;
    }

    public Double getUsage() {
        return usage;
    }

    public void setUsage(Double usage) {
        this.usage = usage;
    }

    public Double getSingleWeight() {
        return singleWeight;
    }

    public void setSingleWeight(Double singleWeight) {
        this.singleWeight = singleWeight;
    }

    public Integer getSetQuantity() {
        return setQuantity;
    }

    public void setSetQuantity(Integer setQuantity) {
        this.setQuantity = setQuantity;
    }

    public Double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(Double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Long getPackagingBagId() {
        return packagingBagId;
    }

    public void setPackagingBagId(Long packagingBagId) {
        this.packagingBagId = packagingBagId;
    }

    public MultipartFile getPartImageFile() {
        return partImageFile;
    }

    public void setPartImageFile(MultipartFile partImageFile) {
        this.partImageFile = partImageFile;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public WriteCellData<Void> getImageData() {
        return imageData;
    }

    public void setImageData(WriteCellData<Void> imageData) {
        this.imageData = imageData;
    }
}
