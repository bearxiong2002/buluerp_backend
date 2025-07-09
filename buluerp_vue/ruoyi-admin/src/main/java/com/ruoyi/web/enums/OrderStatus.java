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
    PURCHASE_PRODUCTION_PENDING("待定制外购与布产计划"),
    PURCHASING("外购中"),
    IN_PRODUCTION("布产中"),
    PURCHASING_IN_PRODUCTION("外购与布产中"),
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
            this.roles = new String[]{role};
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
            return allowFrom <= from && allowTo >= to;
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
            this.roles = new String[]{role};
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
            return allowFrom <= from && allowTo >= to;
        }
    }

    // 配置状态转移规则
    public static final List<StatusRule> STATUS_RULES = Arrays.asList(
            // 审计负责，订单模块不作限制
            new StatusBlock(AUDIT_REJECT, DESIGN_PENDING, (String) null),
            new StatusBlock(DESIGN_PENDING, PURCHASE_PRODUCTION_PENDING, "design-dept"),
            new StatusRoute(PURCHASE_PRODUCTION_PENDING, PURCHASING, new String[]{"purchase-dept", "purchase-auditor"}),
            new StatusRoute(IN_PRODUCTION, PURCHASING_IN_PRODUCTION, new String[]{"purchase-dept", "purchase-auditor"}),
            new StatusRoute(PURCHASE_PRODUCTION_PENDING, IN_PRODUCTION, new String[]{"injectionmolding-dept", "production-auditor"}),
            new StatusRoute(PURCHASING, PURCHASING_IN_PRODUCTION, new String[]{"injectionmolding-dept", "production-auditor"}),
            new StatusRoute(PURCHASING_IN_PRODUCTION, MATERIAL_IN_INVENTORY, new String[]{"purchase-dept", "purchase-auditor", "injectionmolding-dept", "production-auditor"}),
            new StatusBlock(MATERIAL_IN_INVENTORY, PACKAGING_PENDING, new String[]{"warehouse"}),
            new StatusBlock(PACKAGING_PENDING, PACKAGED, new String[]{"wirestaying-dept"}),
            new StatusBlock(PACKAGED, COMPLETED, new String[]{"sell-dept"})
    );
}
