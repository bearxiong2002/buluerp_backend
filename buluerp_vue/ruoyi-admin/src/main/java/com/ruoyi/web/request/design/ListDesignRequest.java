package com.ruoyi.web.request.design;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "获取造型表列表请求类")
public class ListDesignRequest {

    @ApiModelProperty(dataType = "Long",value = "造型表id(可选)",required = false)
    private Long id;

    @ApiModelProperty(dataType = "Long",value = "造型表分组编号",required = false)
    private Long GroupId;

    /** 主设计编号 */
    @ApiModelProperty(dataType = "Long",value = "主设计编号",required = true)
    private Long designPatternId;

//    /** 模具编号，用于唯一标识模具 */
//    @ApiModelProperty(dataType = "String",value = "模具编号",required = true)
//    private String mouldNumber;
//
//    /** LDD编号，与模具相关的编号 */
//    @ApiModelProperty(dataType = "String",value = "LDD编号",required = true)
//    private String lddNumber;
//
//    /** 模具类别，如注塑模具、冲压模具等 */
//    @ApiModelProperty(dataType = "String",value = "模具类别",required = true)
//    private String mouldCategory;
//
//    /** 模具ID，用于内部标识模具 */
//    @ApiModelProperty(dataType = "String",value = "模具id",required = true)
//    private String mouldId;
//
//    /** 模具图片的URL链接，用于存储模具外观图片 */
//    @ApiModelProperty(dataType = "String",value = "模具图片的URL链接，用于存储模具外观图片 ")
//    private String pictureUrl;
//
//    /** 模具的颜色描述 */
//    @ApiModelProperty(dataType = "String",value = "模具颜色描述")
//    private String color;
//
//    /** 模具生产的产品名称 */
//    @ApiModelProperty(dataType = "String",value = "模具生产的产品名称",required = true)
//    private String productName;
//
//    /** 模具的数量 */
//    @ApiModelProperty(dataType = "Long",value = "模具数量",required = true)
//    private Long quantity;
//
//    /** 模具的用料，如钢材、铝合金等 */
//    @ApiModelProperty(dataType = "String",value = "模具的材料",required = true)
//    private String material;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGroupId() {
        return GroupId;
    }

    public void setGroupId(Long groupId) {
        GroupId = groupId;
    }

    public Long getDesignPatternId() {
        return designPatternId;
    }

    public void setDesignPatternId(Long designPatternId) {
        this.designPatternId = designPatternId;
    }
}
