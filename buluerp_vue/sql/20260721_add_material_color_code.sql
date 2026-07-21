ALTER TABLE erp_material_info
    ADD COLUMN color_code VARCHAR(255) NULL COMMENT '颜色编号'
    AFTER spare_code;
