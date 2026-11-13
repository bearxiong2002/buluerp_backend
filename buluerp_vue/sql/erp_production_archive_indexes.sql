-- 已完成订单归档联表查询索引。
-- 首次部署执行一次；执行前请确认目标数据库中不存在同名索引。
CREATE INDEX idx_erp_orders_status ON erp_orders (status);
CREATE INDEX idx_erp_production_schedule_order_code ON erp_production_schedule (order_code);
CREATE INDEX idx_erp_production_schedule_arrange_id ON erp_production_schedule (arrange_id);