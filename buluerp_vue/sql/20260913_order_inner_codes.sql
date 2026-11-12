-- 执行前备份 erp_orders，暂停订单资料写入并检查原编号无空白或首尾空格。
-- 只新增映射表；不修改订单主键、默认编号或历史业务数据。
CREATE TABLE IF NOT EXISTS erp_order_inner_code (
    id BIGINT NOT NULL AUTO_INCREMENT,
    order_id INT NOT NULL,
    inner_code VARCHAR(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_inner_code (inner_code),
    KEY idx_order_inner_code_order (order_id, sort_order),
    CONSTRAINT fk_order_inner_code_order FOREIGN KEY (order_id)
        REFERENCES erp_orders (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='订单内部编号（含默认编号）';

ALTER TABLE erp_order_inner_code COMMENT = '订单内部编号（含默认编号）';

-- 可重复执行；若编号已属于其他订单，让唯一约束报错，不吞掉冲突。
INSERT INTO erp_order_inner_code (order_id, inner_code, sort_order)
SELECT p.id, p.inner_id, 0 FROM erp_orders p
WHERE NOT EXISTS (
    SELECT 1 FROM erp_order_inner_code c
    WHERE c.order_id = p.id AND c.inner_code = p.inner_id
);
