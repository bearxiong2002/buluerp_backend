package com.ruoyi.web.request.material;

import com.ruoyi.common.annotation.Example;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.web.domain.ErpMaterialInfo;
import io.swagger.annotations.ApiModelProperty;

public class AddMaterialInfoRequest extends ErpMaterialInfo {
    @ApiModelProperty("产品编码")
    @Excel(name = "产品编码")
    @Example("ABC123")
    private String productCode;

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
}
