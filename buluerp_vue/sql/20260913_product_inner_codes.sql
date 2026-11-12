-- 执行前备份 erp_products，暂停产品资料写入并检查原编号无空白或首尾空格。
-- 只新增映射表；不修改产品主键、默认编号或历史业务数据。
CREATE TABLE IF NOT EXISTS erp_product_inner_code (
    id BIGINT NOT NULL AUTO_INCREMENT,
    product_id INT NOT NULL,
    inner_code VARCHAR(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_product_inner_code (inner_code),
    KEY idx_product_inner_code_product (product_id, sort_order),
    CONSTRAINT fk_product_inner_code_product FOREIGN KEY (product_id)
        REFERENCES erp_products (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='产品内部编号（含默认编号）';

-- 可重复执行；若编号已属于其他产品，让唯一约束报错，不吞掉冲突。
INSERT INTO erp_product_inner_code (product_id, inner_code, sort_order)
SELECT p.id, p.inner_id, 0 FROM erp_products p
WHERE NOT EXISTS (
    SELECT 1 FROM erp_product_inner_code c
    WHERE c.product_id = p.id AND c.inner_code = p.inner_id
);
