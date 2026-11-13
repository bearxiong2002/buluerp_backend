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

    @ApiModelProperty(value = "订单内部编号、外部编号或附加编号关键字")
    private String orderCodeKeyword;

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

    public String getOrderCodeKeyword() {
        return orderCodeKeyword;
    }

    public void setOrderCodeKeyword(String orderCodeKeyword) {
        this.orderCodeKeyword = orderCodeKeyword;
    }

    public List<Integer> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<Integer> statusList) {
        this.statusList = statusList;
    }
}
