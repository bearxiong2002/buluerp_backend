package com.ruoyi.web.request.mouldhouse;

import io.swagger.annotations.ApiModelProperty;

public class ListMouldHouseRequest {
    @ApiModelProperty(value = "模房ID")
    private Long id;

    @ApiModelProperty(value = "模房名称")
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
