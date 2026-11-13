package com.ruoyi.web.result;

/**
 * 已完成订单关联的生产记录数量。
 */
public class ProductionArchiveRecordCount {
    private String orderCode;
    private Long scheduleCount;
    private Long arrangeCount;

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Long getScheduleCount() {
        return scheduleCount;
    }

    public void setScheduleCount(Long scheduleCount) {
        this.scheduleCount = scheduleCount;
    }

    public Long getArrangeCount() {
        return arrangeCount;
    }

    public void setArrangeCount(Long arrangeCount) {
        this.arrangeCount = arrangeCount;
    }
}