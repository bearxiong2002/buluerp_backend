package com.ruoyi.common.utils.page;

public class PageDefaultOptions {
    private String orderByColumn;
    private String isAsc;

    public static PageDefaultOptions create() {
        PageDefaultOptions options = new PageDefaultOptions();
        options.setOrderByColumn("createTime");
        options.setIsAsc("asc");
        return options;
    }

    public PageDefaultOptions orderByColumn(String orderByColumn) {
        this.setOrderByColumn(orderByColumn);
        return this;
    }

    public PageDefaultOptions isAsc(String isAsc) {
        this.setIsAsc(isAsc);
        return this;
    }

    public String getOrderByColumn() {
        return orderByColumn;
    }

    public void setOrderByColumn(String orderByColumn) {
        this.orderByColumn = orderByColumn;
    }

    public String getIsAsc() {
        return isAsc;
    }

    public void setIsAsc(String isAsc) {
        this.isAsc = isAsc;
    }
}
