-- 料型名称允许重复，但同一名称和颜色编号的组合必须唯一。
ALTER TABLE erp_material_type
    DROP INDEX name,
    ADD UNIQUE KEY uk_erp_material_type_name_color_code (name, color_code);
