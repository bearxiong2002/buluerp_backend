-- 新增“生产 -> 已完成订单”菜单。
-- 该脚本可重复执行；仅当目标菜单不存在时插入。
INSERT INTO sys_menu (
    menu_name, parent_id, order_num, path, component, query, route_name,
    is_frame, is_cache, menu_type, visible, status, perms, icon,
    create_by, create_time, update_by, update_time, remark
)
SELECT
    '已完成订单', production.menu_id, 2, 'PMProduceArchive', NULL, NULL, '',
    1, 0, 'M', '0', '0', NULL, '#',
    'admin', NOW(), '', NULL, ''
FROM sys_menu production
JOIN sys_menu center ON center.menu_id = production.parent_id
WHERE production.menu_name = '生产'
  AND production.menu_type = 'M'
  AND center.menu_name = '生产管理中心'
  AND NOT EXISTS (
      SELECT 1
      FROM sys_menu existing
      WHERE existing.parent_id = production.menu_id
        AND existing.menu_name = '已完成订单'
  )
ORDER BY production.menu_id
LIMIT 1;