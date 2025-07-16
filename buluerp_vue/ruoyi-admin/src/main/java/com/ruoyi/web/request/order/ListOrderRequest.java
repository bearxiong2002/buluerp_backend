package com.ruoyi.web.request.order;

import com.ruoyi.web.domain.ErpOrders;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

public class ListOrderRequest extends ErpOrders {
    @ApiModelProperty(value = "创建时间起始")
    private Date createTimeFrom;
    @ApiModelProperty(value = "创建时间结束")
    private Date createTimeTo;

    @ApiModelProperty(value = "是否通过审核，true - 已通过，false - 未通过，null - 忽略是否通过")
    private Boolean approved;

    @ApiModelProperty(value = "状态集合")
    private List<Integer> statusList;

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

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public List<Integer> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<Integer> statusList) {
        this.statusList = statusList;
    }
}
