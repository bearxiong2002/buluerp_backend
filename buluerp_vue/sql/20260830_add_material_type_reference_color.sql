ALTER TABLE erp_material_type
    ADD COLUMN reference_color_url VARCHAR(255) NULL COMMENT '参考颜色图片URL'
    AFTER color_code;

