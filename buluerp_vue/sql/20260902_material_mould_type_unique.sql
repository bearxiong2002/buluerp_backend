-- 同一套模具允许关联多种料别，但同一“模具编号 + 料别”只能有一条物料资料。
-- 执行前应先确认现有数据不存在重复组合。
ALTER TABLE erp_material_info
    ADD UNIQUE KEY uk_material_mould_type (mould_number, material_type);
