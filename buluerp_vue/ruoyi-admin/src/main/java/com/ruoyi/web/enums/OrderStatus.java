package com.ruoyi.web.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public enum OrderStatus {

    // 状态标签，通过订单服务可转换成状态值
    AUDIT_REJECT("审核不通过"),
    CREATED("创建(未审核)"),
    DESIGN_PENDING("待设计"),
    DESIGNED("设计中"),
    PRODUCTION_SCHEDULE_PENDING("待布产"),
    PRODUCTION_SCHEDULING("布产中"),
    IN_PRODUCTION("排产中"),
    PRODUCTION_DONE_PURCHASING("生产完成(待采购完成)"),
    MATERIAL_IN_INVENTORY("已齐料入库(待套料)"),
    MATERIAL_NESTING("套料中"),
    PACKAGING_PENDING("套料完成(待拉线)"),
    PACKAGING("拉线组包中"),
    PACKAGED("拉线完成(分包已入库)"),
    PACKING("包装中"),
    DELIVERED("已发货"),
    COMPLETED("已完成");

    public static final String ORDER_STATUS_DICT_TYPE = "erp_order_status";

    public interface StatusMapper {
        Integer getStatusValue(String label);
        String getStatusLabel(Integer value);
    }

    private final String label;
    OrderStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public Integer getValue(StatusMapper mapper) {
        return mapper.getStatusValue(this.label);
    }

    public static OrderStatus of(String label) {
        for (OrderStatus status : values()) {
            if (status.label.equals(label)) {
                return status;
            }
        }
        throw new IllegalArgumentException("不存在状态标签：" + label);
    }

    public static OrderStatus of(Integer value, StatusMapper mapper) {
        return of(mapper.getStatusLabel(value));
    }

    public interface StatusRule {
        boolean allows(Integer from, Integer to, String role, StatusMapper mapper);
    }

    // 允许连续状态间任意转换
    public static class StatusBlock implements StatusRule {
        private final OrderStatus from;
        private final OrderStatus to;
        private final String[] roles;

        public StatusBlock(OrderStatus from, OrderStatus to, String[] roles) {
            this.from = from;
            this.to = to;
            this.roles = roles;
        }

        public StatusBlock(OrderStatus from, OrderStatus to, String role) {
            this.from = from;
            this.to = to;
            this.roles = role != null? new String[]{role} : null;
        }

        @Override
        public boolean allows(Integer from, Integer to, String role, StatusMapper mapper) {
            if (this.roles != null && !"admin".equals(role)) {
                if (Arrays.stream(this.roles).noneMatch(r -> r.equals(role))) {
                    return false;
                }
            }
            Integer allowFrom = this.from.getValue(mapper);
            Integer allowTo = this.to.getValue(mapper);
            return allowFrom <= from && allowFrom <= to &&
                    allowTo >= from && allowTo >= to;
        }
    }

    // 允许从 from 状态转换到 to 状态
    public static class StatusRoute implements StatusRule {
        private final OrderStatus from;
        private final OrderStatus to;
        private final String[] roles;

        public StatusRoute(OrderStatus from, OrderStatus to, String[] roles) {
            this.from = from;
            this.to = to;
            this.roles = roles;
        }

        public StatusRoute(OrderStatus from, OrderStatus to, String role) {
            this.from = from;
            this.to = to;
            this.roles = role != null ? new String[]{role} : null;
        }

        @Override
        public boolean allows(Integer from, Integer to, String role, StatusMapper mapper) {
            if (this.roles!= null &&!"admin".equals(role)) {
                if (Arrays.stream(this.roles).noneMatch(r -> r.equals(role))) {
                    return false;
                }
            }
            Integer allowFrom = this.from.getValue(mapper);
            Integer allowTo = this.to.getValue(mapper);
            return Objects.equals(allowFrom, from) && Objects.equals(allowTo, to);
        }
    }

    // 配置状态转移规则
    public static final List<StatusRule> STATUS_RULES = Arrays.asList(
            // 审计负责，订单模块不作限制
            new StatusBlock(AUDIT_REJECT, DESIGN_PENDING, (String) null),
            new StatusBlock(DESIGN_PENDING, PRODUCTION_SCHEDULE_PENDING, "design-dept"),
            // ***********************************************************************************
            // ** 以下状态的变更不再由部门手动触发，而是由关联业务（布产、采购）的审核结果自动触发 **
            // ***********************************************************************************
            new StatusRoute(PRODUCTION_SCHEDULING, IN_PRODUCTION, new String[]{ "admin" }),
            new StatusRoute(IN_PRODUCTION, PRODUCTION_DONE_PURCHASING, new String[]{ "admin" }),
            new StatusRoute(IN_PRODUCTION, MATERIAL_IN_INVENTORY, new String[]{ "admin" }),
            new StatusRoute(PRODUCTION_DONE_PURCHASING, MATERIAL_IN_INVENTORY, new String[]{ "admin" }),
            new StatusBlock(MATERIAL_IN_INVENTORY, PACKAGING_PENDING, new String[]{"warehouse"}),
            new StatusBlock(PACKAGING_PENDING, PACKAGED, new String[]{"wirestaying-dept", "admin"}), // 分包审核通过后自动变更
            new StatusBlock(PACKAGED, COMPLETED, new String[]{"sell-dept"})
    );
}
