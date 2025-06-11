package com.ruoyi.web.request.order;

import com.ruoyi.web.domain.ErpOrders;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class ListOrderRequest extends ErpOrders {
    @ApiModelProperty(value = "创建时间起始")
    private Date createTimeFrom;
    @ApiModelProperty(value = "创建时间结束")
    private Date createTimeTo;

    public Date getCreateTimeFrom() {
        return createTimeFrom;
    }

    public void setCreateTimeFrom(Date createTimeFrom) {
        this.createTimeFrom = createTimeFrom;
    }

    public Date getCreateTimeTo() {
        return createTimeTo;
    }

    public void setCreateTimeTo(Date createTimeTo) {
        this.createTimeTo = createTimeTo;
    }
}
