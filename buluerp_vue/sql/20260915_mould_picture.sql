-- 模具独立图片，历史记录保持 NULL。请在部署新版后端之前执行。
-- 兼容 MySQL 5.7 / 8.0，可重复执行；不改动状态、物料图片或上传文件。
SET @mould_picture_exists = (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'erp_mould' AND COLUMN_NAME = 'picture_url'
);
SET @mould_picture_sql = IF(@mould_picture_exists = 0,
    'ALTER TABLE erp_mould ADD COLUMN picture_url VARCHAR(500) NULL COMMENT ''模具图片'' AFTER mould_number',
    'SELECT ''erp_mould.picture_url already exists'' AS result');
PREPARE mould_picture_statement FROM @mould_picture_sql;
EXECUTE mould_picture_statement;
DEALLOCATE PREPARE mould_picture_statement;
