ALTER TABLE erp_orders
    ADD COLUMN purchase_required tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否需要采购：1=需要采购，0=无需采购'
    AFTER all_purchased;

