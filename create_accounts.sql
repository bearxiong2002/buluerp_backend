-- ============================================================
-- 创建工厂ERP系统用户账号
-- 说明：请先在数据库管理工具中连接到 buluerp 数据库后执行
-- 使用前请确认 sys_user 表的自增值，避免ID冲突
-- ============================================================

-- 检查当前最大用户ID，避免冲突
SELECT CONCAT('当前最大用户ID: ', IFNULL(MAX(user_id), 0)) AS 提示 FROM sys_user;

-- ============================================================
-- 1. 创建正式员工账号（8人）
-- ============================================================
INSERT INTO sys_user (user_id, dept_id, user_name, nick_name, password, sex, status, create_by, create_time, remark)
VALUES
(1000, 100, 'zhenkai', '镇楷', '$2b$10$HRLy7Hh7qvCvYIg3OlKY0e.WkhzwEu2LjSCsmNY5gLj4gNOAE8rBS', '0', '0', 'admin', NOW(), '销售负责人'),
(1001, 100, 'jiahuan', '佳焕', '$2b$10$HRLy7Hh7qvCvYIg3OlKY0eieWh30YhPiRRB8U1UJs9hSb82DTG.q6', '0', '0', 'admin', NOW(), '厂长'),
(1002, 100, 'yifu', '熠富', '$2b$10$HRLy7Hh7qvCvYIg3OlKY0ecI.F.Z2LKTtLOMPnS4StAr97utwGgTi', '0', '0', 'admin', NOW(), '注塑部负责人'),
(1003, 100, 'kaitao', '凯涛', '$2b$10$HRLy7Hh7qvCvYIg3OlKY0eXz1HweGlh4brrqCfaW.ar1RXwp7dBMS', '0', '0', 'admin', NOW(), '工程部负责人'),
(1004, 100, 'jiexu', '杰旭', '$2b$10$HRLy7Hh7qvCvYIg3OlKY0emoxDBxEaO.2YSx2SFwxufVZDQj0bMn.', '0', '0', 'admin', NOW(), '仓库负责人'),
(1005, 100, 'wangda', '王达', '$2b$10$HRLy7Hh7qvCvYIg3OlKY0enoFJjl1hoS4BNkm4mYTOVcm0h0LeMei', '0', '0', 'admin', NOW(), '拉线负责人'),
(1006, 100, 'ruide', '锐得', '$2b$10$HRLy7Hh7qvCvYIg3OlKY0ebfQhK1eYalppxutMP8TGqorE.CDZe4S', '0', '0', 'admin', NOW(), 'PMC'),
(1007, 100, 'qiajia', '洽佳', '$2b$10$HRLy7Hh7qvCvYIg3OlKY0evdm1leVP72.Awa/H18JEeub4EYaSMO2', '0', '0', 'admin', NOW(), '外购负责人');

-- ============================================================
-- 2. 创建BOSS账号（3个，超级管理员权限）
-- ============================================================
INSERT INTO sys_user (user_id, dept_id, user_name, nick_name, password, sex, status, create_by, create_time, remark)
VALUES
(1008, 100, 'boss001', '老板一', '$2b$10$HRLy7Hh7qvCvYIg3OlKY0efIv1QG72b3UvIlj1e337pZhevn0y1n2', '0', '0', 'admin', NOW(), 'BOSS账号1'),
(1009, 100, 'boss002', '老板二', '$2b$10$HRLy7Hh7qvCvYIg3OlKY0eWXr63jCnZxtbq6YSUHfyBDXHrVXxFU6', '0', '0', 'admin', NOW(), 'BOSS账号2'),
(1010, 100, 'boss003', '老板三', '$2b$10$HRLy7Hh7qvCvYIg3OlKY0enH0RTIIvFlhObvx6nrEbLh2J9b3jEO2', '0', '0', 'admin', NOW(), 'BOSS账号3');

-- ============================================================
-- 3. 创建备用账号（3个）
-- ============================================================
INSERT INTO sys_user (user_id, dept_id, user_name, nick_name, password, sex, status, create_by, create_time, remark)
VALUES
(1011, 100, 'beiyong01', '备用一', '$2b$10$HRLy7Hh7qvCvYIg3OlKY0eJ4R12/fjv1ueMMNuvJYYbP10T4uRtfy', '0', '0', 'admin', NOW(), '备用账号1'),
(1012, 100, 'beiyong02', '备用二', '$2b$10$HRLy7Hh7qvCvYIg3OlKY0e6gZ/iKf6x0R0yHD8yabYcc3WDqjU3E.', '0', '0', 'admin', NOW(), '备用账号2'),
(1013, 100, 'beiyong03', '备用三', '$2b$10$HRLy7Hh7qvCvYIg3OlKY0eNiLmr8hdtzhE8FXSPCvgzCZ76qGP7u6', '0', '0', 'admin', NOW(), '备用账号3');

-- ============================================================
-- 4. 分配角色
--    正式员工 -> 普通角色(role_id=2)
--    BOSS账号 -> 超级管理员(role_id=1)
--    备用账号 -> 普通角色(role_id=2)
-- ============================================================
INSERT INTO sys_user_role (user_id, role_id) VALUES
-- 正式员工 - 普通角色
(1000, 2), (1001, 2), (1002, 2), (1003, 2),
(1004, 2), (1005, 2), (1006, 2), (1007, 2),
-- BOSS账号 - 超级管理员
(1008, 1), (1009, 1), (1010, 1),
-- 备用账号 - 普通角色
(1011, 2), (1012, 2), (1013, 2);

-- ============================================================
-- 5. 分配岗位
--    BOSS账号 -> 董事长(post_id=1)
--    其余 -> 普通员工(post_id=4)
-- ============================================================
INSERT INTO sys_user_post (user_id, post_id) VALUES
-- BOSS - 董事长
(1008, 1), (1009, 1), (1010, 1),
-- 其余 - 普通员工
(1000, 4), (1001, 4), (1002, 4), (1003, 4),
(1004, 4), (1005, 4), (1006, 4), (1007, 4),
(1011, 4), (1012, 4), (1013, 4);

-- ============================================================
-- 查看创建结果
-- ============================================================
SELECT u.user_id, u.user_name, u.nick_name, u.remark, r.role_name AS 角色
FROM sys_user u
LEFT JOIN sys_user_role ur ON u.user_id = ur.user_id
LEFT JOIN sys_role r ON ur.role_id = r.role_id
WHERE u.user_id >= 1000
ORDER BY u.user_id;
