package com.ruoyi.web.request.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

@ApiModel(value = "查询产品列表请求类")
public class ListProductRequest {
    @ApiModelProperty(dataType = "Long",value = "需要修改的产品id(可选)",required = true)
    private Long id;
    @ApiModelProperty(dataType = "Long",value = "产品名称",required = false)
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
