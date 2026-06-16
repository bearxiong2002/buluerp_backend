# SQL 与 Mapper 表覆盖检查

> 检查时间：2026-05-30  
> 检查范围：`buluerp_vue/sql`、后端 `*Mapper.xml`、`*Mapper.java`、实体 `@TableName`

## 结论

当前本地目录内 SQL 文件 **不能覆盖项目 Mapper 涉及的全部表**。

- `buluerp_vue/sql` 目录下只有 2 个 SQL 文件：`ry_20240629.sql`、`quartz.sql`
- SQL 文件共创建 30 张表
- Mapper XML、Mapper Java 注解 SQL、实体 `@TableName` 共涉及 50 张表
- 能被 SQL 覆盖的表：19 张
- 未被 SQL 覆盖的表：31 张
- 未覆盖表全部为 `erp_*` 定制业务表

因此，当前 SQL 只能支撑若依基础系统、代码生成器和定时任务基础表；ERP 业务接口访问订单、产品、设计、采购、库存、包装等模块时，仍会因为缺少业务表而失败。

## 本地 SQL 文件

| SQL 文件 | 作用判断 |
| --- | --- |
| `buluerp_vue/sql/ry_20240629.sql` | 若依系统管理、代码生成器基础表 |
| `buluerp_vue/sql/quartz.sql` | Quartz 定时任务表 |

## SQL 中创建的表

### 若依系统与代码生成器表

- `gen_table`
- `gen_table_column`
- `sys_config`
- `sys_dept`
- `sys_dict_data`
- `sys_dict_type`
- `sys_job`
- `sys_job_log`
- `sys_logininfor`
- `sys_menu`
- `sys_notice`
- `sys_oper_log`
- `sys_post`
- `sys_role`
- `sys_role_dept`
- `sys_role_menu`
- `sys_user`
- `sys_user_post`
- `sys_user_role`

### Quartz 表

- `qrtz_blob_triggers`
- `qrtz_calendars`
- `qrtz_cron_triggers`
- `qrtz_fired_triggers`
- `qrtz_job_details`
- `qrtz_locks`
- `qrtz_paused_trigger_grps`
- `qrtz_scheduler_state`
- `qrtz_simple_triggers`
- `qrtz_simprop_triggers`
- `qrtz_triggers`

## Mapper 已覆盖的表

以下表在 Mapper 或框架模块中会被访问，并且已经存在于本地 SQL 文件中：

- `gen_table`
- `gen_table_column`
- `sys_config`
- `sys_dept`
- `sys_dict_data`
- `sys_dict_type`
- `sys_job`
- `sys_job_log`
- `sys_logininfor`
- `sys_menu`
- `sys_notice`
- `sys_oper_log`
- `sys_post`
- `sys_role`
- `sys_role_dept`
- `sys_role_menu`
- `sys_user`
- `sys_user_post`
- `sys_user_role`

## Mapper 未覆盖的表

以下表在 Mapper XML、Mapper Java 注解 SQL 或实体 `@TableName` 中出现，但没有出现在 `buluerp_vue/sql` 的建表语句中。

| 缺失表 | 主要来源 |
| --- | --- |
| `erp_audit_record` | `ErpAuditRecord.java` |
| `erp_audit_switch` | `ErpAuditSwitch.java`、`ErpAuditSwitchMapper.java` |
| `erp_customers` | `ErpCustomersMapper.xml`、`ErpOrdersMapper.xml` |
| `erp_design_patterns` | `ErpDesignPatternsMapper.xml`、`ErpManufacturerMapper.xml`、`ErpOrdersMapper.xml` |
| `erp_design_style` | `ErpDesignStyleMapper.xml` |
| `erp_design_sub_pattern` | `ErpDesignSubPatternMapper.xml` |
| `erp_manufacturer` | `ErpManufacturerMapper.xml`、`ErpMouldMapper.xml` |
| `erp_material_info` | `ErpMaterialInfoMapper.xml`、`ErpProductionScheduleMapper.xml`、`ErpPurchaseCollectionMapper.xml` |
| `erp_material_type` | `ErpProductionScheduleMapper.xml`、`ErpPurchaseCollectionMapper.xml` |
| `erp_mould` | `ErpMaterialInfoMapper.xml`、`ErpMouldMapper.xml`、`ErpProductionScheduleMapper.xml` |
| `erp_notification` | `ErpNotification.java` |
| `erp_operation_log` | `ErpOperationLogMapper.xml` |
| `erp_orders` | `ErpDesignPatternsMapper.xml`、`ErpOrdersMapper.xml` |
| `erp_orders_product` | `ErpOrdersMapper.xml` |
| `erp_packaging_bag` | `ErpPackagingDetailMapper.xml` |
| `erp_packaging_detail` | `ErpPackagingDetailMapper.xml` |
| `erp_packaging_list` | `ErpOrdersMapper.xml`、`ErpPackagingListMapper.xml` |
| `erp_packaging_material_inventory` | `ErpPackagingMaterialInventory.java` |
| `erp_packaging_material_inventory_change` | `ErpPackagingMaterialInventoryChange.java`、`ErpPackagingMaterialInventoryChangeMapper.java` |
| `erp_part_inventory` | `ErpPartInventory.java` |
| `erp_part_inventory_change` | `ErpPartInventoryChange.java`、`ErpPartInventoryChangeMapper.java` |
| `erp_product_inventory` | `ErpProductInventory.java` |
| `erp_product_inventory_change` | `ErpProductInventoryChange.java`、`ErpProductInventoryChangeMapper.java` |
| `erp_product_material` | `ErpProductsMapper.xml` |
| `erp_production_arrange` | `ErpProductionArrange.java` |
| `erp_production_schedule` | `ErpProductionSchedule.java`、`ErpOrdersMapper.xml`、`ErpProductionScheduleMapper.xml` |
| `erp_products` | `ErpDesignStyleMapper.xml`、`ErpPackagingListMapper.xml`、`ErpProductsMapper.xml` |
| `erp_purchase_collection` | `ErpOrdersMapper.xml`、`ErpPurchaseCollectionMapper.xml` |
| `erp_purchase_info` | `ErpPurchaseInfo.java`、`ErpMaterialInfoMapper.xml`、`ErpPurchaseCollectionMapper.xml`、`ErpPurchaseInfoMapper.xml` |
| `erp_purchase_order` | `ErpPurchaseOrderMapper.xml` |
| `erp_purchase_order_invoice` | `ErpPurchaseOrderMapper.xml` |

## 额外说明

- `qrtz_*` 表虽然没有被项目 Mapper 直接引用，但它们由 Quartz 框架运行时使用，因此出现在 SQL 中是合理的。
- `information_schema.columns`、`information_schema.tables` 属于 MySQL 元数据表，用于代码生成器查询数据库结构，不属于项目业务库需要建表的对象。
- 检查时已排除 XML 标签名导致的误判，例如 `<update id="...">` 不会被当作 `id` 表。

## 建议

1. 继续尝试拿到远程库可用导出，这是恢复 ERP 业务最稳妥的方式。
2. 如果远程库无法访问，需要从 Mapper 与实体反推 31 张 `erp_*` 表结构，但这只能恢复结构，无法恢复菜单、权限、字典和业务种子数据。
3. 本地已经导入的 SQL 可以保留，但它只能保证若依基础系统部分可运行，不能说明 ERP 模块数据库已完整初始化。
