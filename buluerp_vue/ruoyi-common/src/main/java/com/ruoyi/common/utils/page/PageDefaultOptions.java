package com.ruoyi.common.utils.page;

public class PageDefaultOptions {
    private Integer pageNum;
    private Integer pageSize;
    private String orderByColumn;
    private String isAsc;

    public static PageDefaultOptions create() {
        PageDefaultOptions options = new PageDefaultOptions();
        options.setPageNum(1);
        options.setPageSize(10);
        options.setOrderByColumn("createTime");
        options.setIsAsc("desc");
        return options;
    }

    public PageDefaultOptions pageNum(Integer pageNum) {
        this.setPageNum(pageNum);
        return this;
    }

    public PageDefaultOptions pageSize(Integer pageSize) {
        this.setPageSize(pageSize);
        return this;
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

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
