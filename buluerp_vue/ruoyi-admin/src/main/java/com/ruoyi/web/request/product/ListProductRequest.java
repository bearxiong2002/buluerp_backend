package com.ruoyi.web.request.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@ApiModel(value = "查询产品列表请求类")
public class ListProductRequest {
    @ApiModelProperty(dataType = "Long",value = "产品id",required = false)
    private Long id;
    @ApiModelProperty(dataType = "String",value = "产品名称",required = false)
    private String name;
    @ApiModelProperty(dataType = "String",value = "创建用户名称",required = false)
    private String createUsername;
    @ApiModelProperty(dataType = "Datetime",value = "创建时间起始",required = false)
    private LocalDateTime timeFrom;
    @ApiModelProperty(dataType = "Datetime",value = "创建时间结束",required = false)
    private LocalDateTime timeTo;
    @ApiModelProperty(dataType = "int",value = "产品设计是否完成 0未完成 1完成",required = false)
    private Integer design_status;

    public Integer getDesign_status() {
        return design_status;
    }

    public void setDesign_status(Integer design_status) {
        this.design_status = design_status;
    }

    public String getCreateUsername() {
        return createUsername;
    }

    public void setCreateUsername(String createUsername) {
        this.createUsername = createUsername;
    }

    public LocalDateTime getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(LocalDateTime timeFrom) {
        this.timeFrom = timeFrom;
    }

    public LocalDateTime getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(LocalDateTime timeTo) {
        this.timeTo = timeTo;
    }

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
