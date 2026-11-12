package com.ruoyi.web.request.mould;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import java.util.List;

public class BatchUpdateMouldStatusRequest {
    @NotBlank(message = "更新范围不能为空")
    @ApiModelProperty(value = "更新范围：selected（勾选记录）或 filtered（查询结果）")
    private String scope;
    private List<Long> ids;
    private ListMouldRequest filters;
    @NotBlank(message = "目标状态不能为空")
    private String status;
    private Long mouldHouseId;

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
    public List<Long> getIds() { return ids; }
    public void setIds(List<Long> ids) { this.ids = ids; }
    public ListMouldRequest getFilters() { return filters; }
    public void setFilters(ListMouldRequest filters) { this.filters = filters; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getMouldHouseId() { return mouldHouseId; }
    public void setMouldHouseId(Long mouldHouseId) { this.mouldHouseId = mouldHouseId; }
}
