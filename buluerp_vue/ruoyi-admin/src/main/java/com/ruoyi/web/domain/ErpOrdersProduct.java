package com.ruoyi.web.domain;

public class ErpOrdersProduct {
    private Long id;
    private Long ordersId;
    private Long productId;
    private Long quantity;

    private ErpProducts product;

    public Long getOrdersId() {
        return ordersId;
    }

    public void setOrdersId(Long ordersId) {
        this.ordersId = ordersId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public ErpProducts getProduct() {
        return product;
    }

    public void setProduct(ErpProducts product) {
        this.product = product;
    }
}
