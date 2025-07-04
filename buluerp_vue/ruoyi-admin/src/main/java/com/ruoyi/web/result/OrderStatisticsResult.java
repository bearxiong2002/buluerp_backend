package com.ruoyi.web.result;

import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

public class OrderStatisticsResult {
    public static class StatusCouunt {
        private Integer status;
        private Long count;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }

    @ApiModelProperty(value = "已交付订单数量")
    private Long deliveredCount;

    @ApiModelProperty(value = "准时订单数量")
    private Long punctualCount;

    @ApiModelProperty(value = "总订单数量")
    private Long totalCount;

    @ApiModelProperty(value = "各状态订单数量统计")
    private Map<String, Long> statusCount;

    public Map<String, Long> getStatusCount() {
        return statusCount;
    }

    public void setStatusCount(Map<String, Long> statusCount) {
        this.statusCount = statusCount;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getPunctualCount() {
        return punctualCount;
    }

    public void setPunctualCount(Long punctualCount) {
        this.punctualCount = punctualCount;
    }

    public Long getDeliveredCount() {
        return deliveredCount;
    }

    public void setDeliveredCount(Long deliveredCount) {
        this.deliveredCount = deliveredCount;
    }
}
