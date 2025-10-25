package com.ruoyi.web.request.mouldhouse;


import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

public class AddMouldHouseRequest {
    @ApiModelProperty(value = "名称", required = true)
    @NotBlank(message = "名称不能为空")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
