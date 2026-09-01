-- 将原“物料资料表”菜单拆分为“内部物料”和“外购物料”两个子菜单。
-- 迁移策略：
-- 1. 保留原物料菜单 ID，改为一级父菜单，避免原角色绑定丢失。
-- 2. 新增内部/外购两个子菜单。
-- 3. 原“新增/修改/删除”按钮权限移动到内部物料，并复制一组到外购物料。
-- 4. 原拥有物料资料表权限的角色，同步拥有两个新子菜单权限。

SET @front_menu_id := (
    SELECT menu_id
    FROM sys_menu
    WHERE menu_name = '前端菜单'
    LIMIT 1
);

SET @material_menu_id := (
    SELECT menu_id
    FROM sys_menu
    WHERE path = 'ProMaterial'
       OR menu_name = '物料资料表'
    ORDER BY menu_id
    LIMIT 1
);

UPDATE sys_menu
SET menu_name = '物料资料表',
    parent_id = @front_menu_id,
    order_num = 4,
    path = '/',
    component = NULL,
    query = NULL,
    route_name = NULL,
    menu_type = 'M',
    visible = '0',
    status = '0',
    perms = '',
    icon = '#',
    update_by = 'admin',
    update_time = NOW()
WHERE menu_id = @material_menu_id;

INSERT INTO sys_menu (
    menu_name, parent_id, order_num, path, component, query, route_name,
    is_frame, is_cache, menu_type, visible, status, perms, icon,
    create_by, create_time, update_by, update_time, remark
)
SELECT
    '内部物料', @material_menu_id, 1, 'ProInternalMaterial', NULL, NULL, NULL,
    1, 0, 'M', '0', '0', '', '#',
    'admin', NOW(), '', NULL, '内部物料菜单'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu
    WHERE parent_id = @material_menu_id
      AND path = 'ProInternalMaterial'
);

INSERT INTO sys_menu (
    menu_name, parent_id, order_num, path, component, query, route_name,
    is_frame, is_cache, menu_type, visible, status, perms, icon,
    create_by, create_time, update_by, update_time, remark
)
SELECT
    '外购物料', @material_menu_id, 2, 'ProPurchasedMaterial', NULL, NULL, NULL,
    1, 0, 'M', '0', '0', '', '#',
    'admin', NOW(), '', NULL, '外购物料菜单'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu
    WHERE parent_id = @material_menu_id
      AND path = 'ProPurchasedMaterial'
);

SET @internal_menu_id := (
    SELECT menu_id
    FROM sys_menu
    WHERE parent_id = @material_menu_id
      AND path = 'ProInternalMaterial'
    LIMIT 1
);

SET @purchased_menu_id := (
    SELECT menu_id
    FROM sys_menu
    WHERE parent_id = @material_menu_id
      AND path = 'ProPurchasedMaterial'
    LIMIT 1
);

UPDATE sys_menu
SET parent_id = @internal_menu_id,
    update_by = 'admin',
    update_time = NOW()
WHERE parent_id = @material_menu_id
  AND menu_name IN ('新增', '修改', '删除');

INSERT INTO sys_menu (
    menu_name, parent_id, order_num, path, component, query, route_name,
    is_frame, is_cache, menu_type, visible, status, perms, icon,
    create_by, create_time, update_by, update_time, remark
)
SELECT
    src.menu_name, @purchased_menu_id, src.order_num, src.path, src.component, src.query, src.route_name,
    src.is_frame, src.is_cache, src.menu_type, src.visible, src.status, src.perms, src.icon,
    'admin', NOW(), '', NULL, CONCAT('外购物料', src.menu_name, '权限')
FROM sys_menu src
WHERE src.parent_id = @internal_menu_id
  AND src.menu_name IN ('新增', '修改', '删除')
  AND NOT EXISTS (
      SELECT 1
      FROM sys_menu existing
      WHERE existing.parent_id = @purchased_menu_id
        AND existing.menu_name = src.menu_name
  );

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT src.role_id, @internal_menu_id
FROM sys_role_menu src
WHERE src.menu_id = @material_menu_id
  AND NOT EXISTS (
      SELECT 1
      FROM sys_role_menu existing
      WHERE existing.role_id = src.role_id
        AND existing.menu_id = @internal_menu_id
  );

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT src.role_id, @purchased_menu_id
FROM sys_role_menu src
WHERE src.menu_id = @material_menu_id
  AND NOT EXISTS (
      SELECT 1
      FROM sys_role_menu existing
      WHERE existing.role_id = src.role_id
        AND existing.menu_id = @purchased_menu_id
  );

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, purchased_control.menu_id
FROM sys_role_menu rm
JOIN sys_menu internal_control
  ON internal_control.menu_id = rm.menu_id
JOIN sys_menu purchased_control
  ON purchased_control.parent_id = @purchased_menu_id
 AND purchased_control.menu_name = internal_control.menu_name
WHERE internal_control.parent_id = @internal_menu_id
  AND internal_control.menu_name IN ('新增', '修改', '删除')
  AND NOT EXISTS (
      SELECT 1
      FROM sys_role_menu existing
      WHERE existing.role_id = rm.role_id
        AND existing.menu_id = purchased_control.menu_id
  );
