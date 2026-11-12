-- 为模具增加备注字段；可重复执行。
SET @ddl = (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE `erp_mould` ADD COLUMN `remark` TEXT NULL COMMENT ''备注'' AFTER `picture_url`',
    'SELECT ''erp_mould.remark already exists'' AS result'
  )
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'erp_mould'
    AND COLUMN_NAME = 'remark'
);
PREPARE mould_remark_stmt FROM @ddl;
EXECUTE mould_remark_stmt;
DEALLOCATE PREPARE mould_remark_stmt;
