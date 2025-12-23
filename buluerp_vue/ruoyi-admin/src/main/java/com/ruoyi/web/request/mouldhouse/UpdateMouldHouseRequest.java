package com.ruoyi.web.request.mouldhouse;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class UpdateMouldHouseRequest {
    @ApiModelProperty(value = "模房ID", required = true)
    @NotNull(message = "ID不能为空")
    @Range(min = 1, message = "ID值无效")
    private Long id;

    @ApiModelProperty(value = "名称", required = true)
    @NotBlank(message = "名称不能为空")
    @Length(max = 50, message = "名称长度不能超过50个字符")
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
