package com.ruoyi.web.service.impl;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.web.domain.*;
import com.ruoyi.web.enums.AuditTypeEnum;
import com.ruoyi.web.enums.OrderStatus;
import com.ruoyi.web.util.log.LogUtil;
import com.ruoyi.web.util.log.OperationLog;
import com.ruoyi.web.mapper.ErpOrdersMapper;
import com.ruoyi.web.request.design.ListDesignPatternsRequest;
import com.ruoyi.web.request.order.ListOrderRequest;
import com.ruoyi.web.result.OrderStatisticsResult;
import com.ruoyi.web.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 订单Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
@Service
public class ErpOrdersServiceImpl implements IErpOrdersService
{
    private static final Logger log = LoggerFactory.getLogger(ErpOrdersServiceImpl.class);
    @Autowired
    private ErpOrdersMapper erpOrdersMapper;

    @Autowired
    private IErpCustomersService erpCustomersService;

    @Autowired
    private IErpProductsService erpProductsService;

    @Autowired
    private IErpDesignPatternsService erpDesignPatternsService;

    @Autowired
    private IErpAuditRecordService erpAuditRecordService;

    @Autowired
    private IErpAuditSwitchService erpAuditSwitchService;

    @Autowired
    private ISysUserService sysUserService;

    private ErpOrders fillErpOrders(ErpOrders erpOrders) {
        if (erpOrders == null) {
            return null;
        }
        List<ErpOrdersProduct> products = erpOrdersMapper.selectOrdersProducts(erpOrders.getId());
        for (ErpOrdersProduct erpOrdersProduct : products) {
            List<ErpProducts> erpProducts = erpProductsService.selectErpProductsListByIds(
                    new Long[]{erpOrdersProduct.getProductId()}
            );
            if (erpProducts.isEmpty()) {
                continue;
            }
            erpOrdersProduct.setProduct(erpProducts.get(0));
        }

        ListDesignPatternsRequest request = new ListDesignPatternsRequest();
        request.setOrderId(erpOrders.getId());
        List<ErpDesignPatterns> erpDesignPatterns = erpDesignPatternsService.selectErpDesignPatternsList(request);
        if (!erpDesignPatterns.isEmpty()) {
            ErpDesignPatterns designPatterns = erpDesignPatterns.get(0);
            Long productId = designPatterns.getProductId();
            List<ErpProducts> erpProducts = erpProductsService.selectErpProductsListByIds(new Long[]{productId});
            if (!erpProducts.isEmpty()) {
                erpOrders.setProduct(erpProducts.get(0));
            }
        }

        erpOrders.setProducts(products);
        return erpOrders;
    }

    /**
     * 查询订单
     * 
     * @param id 订单主键
     * @return 订单
     */
    @Override
    public ErpOrders selectErpOrdersById(Long id)
    {
        return fillErpOrders(erpOrdersMapper.selectErpOrdersById(id));
    }

    @Override
    public ErpOrders selectByOrderCode(String orderCode) {
        return fillErpOrders(erpOrdersMapper.selectErpOrdersByInnerId(orderCode));
    }

    /**
     * 查询订单列表
     * 
     * @param request 订单
     * @return 订单
     */
    @Override
    public List<ErpOrders> selectErpOrdersList(ListOrderRequest request) {
        List<ErpOrders> list = erpOrdersMapper.selectErpOrdersList(request);
        for (ErpOrders erpOrders1 : list) {
            fillErpOrders(erpOrders1);
        }
        return list;
    }

    @Override
    public List<ErpOrders> selectErpOrdersListByIds(Long[] ids) {
        return erpOrdersMapper.selectErpOrdersListByIds(ids)
                .stream()
                .map(this::fillErpOrders)
                .collect(Collectors.toList());
    }

    /**
     * 新增订单
     * 
     * @param erpOrders 订单
     * @return 结果
     */
    @Override
    @Transactional
    public int insertErpOrders(ErpOrders erpOrders)
    {
        erpOrders.setCreateTime(DateUtils.getNowDate());
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            erpOrders.setOperator(loginUser.getUsername());
        }
        if (erpOrders.getCustomerId() == null && erpOrders.getCustomerName() != null) {
            List<ErpCustomers> erpCustomersList = erpCustomersService.selectErpCustomersByName(erpOrders.getCustomerName());
            ErpCustomers erpCustomers;
            if (erpCustomersList == null || erpCustomersList.isEmpty()) {
                erpCustomers = new ErpCustomers();
                erpCustomers.setName(erpOrders.getCustomerName());
                if (0 == erpCustomersService.insertErpCustomers(erpCustomers)) {
                    throw new ServiceException("添加客户信息失败，客户ID无效");
                }
            } else {
                erpCustomers = erpCustomersList.get(0);
            }
            erpOrders.setCustomerId(erpCustomers.getId());
        } else if (erpOrders.getCustomerId() != null) {
            ErpCustomers erpCustomers = erpCustomersService.selectErpCustomersById(erpOrders.getCustomerId());
            if (erpCustomers == null) {
                throw new ServiceException("客户ID无效");
            }
        } else {
            throw new ServiceException("客户ID无效");
        }
        if (erpOrders.getProducts() != null && !erpOrders.getProducts().isEmpty()) {
            erpOrdersMapper.insertOrdersProducts(erpOrders.getProducts());
        }
        // erpOrders.setInnerId(ErpOrders.INNER_ID_PLACEHOLDER);
        // erpOrders.setOuterId(ErpOrders.OUTER_ID_PLACEHOLDER);
        if (erpOrdersMapper.selectErpOrdersByInnerId(erpOrders.getInnerId()) != null) {
            throw new ServiceException("订单内部ID已存在");
        }
        if (erpOrdersMapper.selectErpOrdersByOuterId(erpOrders.getOuterId()) != null) {
            throw new ServiceException("订单外部ID已存在");
        }
        if (0 == erpOrdersMapper.insertErpOrders(erpOrders)) {
            throw new ServiceException("操作失败");
        }

        // ErpOrders erpOrders1 = new ErpOrders();
        // erpOrders1.setId(erpOrders.getId());
        // erpOrders1.setInnerId(erpOrders.generateInnerId());
        // erpOrders1.setOuterId(erpOrders.generateOuterId());
        // if (0 == erpOrdersMapper.updateErpOrders(erpOrders1)) {
        //     throw new ServiceException("无法生成订单ID");
        // }


        // 订单创建成功后，检查审核开关并处理
        try {
            // 检查订单审核开关是否启用
            boolean auditEnabled = erpAuditSwitchService.isAuditEnabled(1); // 1 = 订单审核类型
            
            if (auditEnabled) {
                // 审核开关已启用，触发审核流程
                ErpOrders completeOrder = erpOrdersMapper.selectErpOrdersById(erpOrders.getId());
                erpAuditRecordService.handleOrderCreated(completeOrder);
            }
            // 如果审核开关关闭，订单直接进入待设计状态，不进入审核流程
            else {
                erpOrders.setStatus(1);
                erpOrdersMapper.updateErpOrders(erpOrders);
            }
            
        } catch (Exception e) {
            // 记录日志但不影响订单创建
            log.error("订单创建后处理审核流程失败，订单ID：{}", erpOrders.getId(), e);
        }

        return 1;
    }

    @Override
    public boolean isOrderStatusTransitionAllowed(Integer fromStatus, Integer toStatus, String operator) {
        List<String> roles;
        if (LogUtil.OPERATOR_SYSTEM.equals(operator)) {
            roles = Collections.singletonList("admin");
        } else {
            SysUser sysUser = sysUserService.selectUserByUserName(operator);
            if (sysUser == null) {
                throw new IllegalArgumentException("用户不存在。");
            }
            roles = sysUser.getRoles()
                    .stream()
                    .map(SysRole::getRoleKey)
                    .collect(Collectors.toList());
        }
        for (OrderStatus.StatusRule rule : OrderStatus.STATUS_RULES) {
            boolean allows = roles.stream()
                    .anyMatch(role -> rule.allows(fromStatus, toStatus, role, this));
            if (allows) {
                return true;
            }
        }
        return false;
    }

    public void updateOrderStatus(String orderCode, Integer status, String operator) {
        ErpOrders oldOrder = erpOrdersMapper.selectErpOrdersByInnerId(orderCode);
        if (oldOrder == null) {
            throw new ServiceException("订单ID不存在");
        }
        if (Objects.equals(oldOrder.getStatus(), status)) {
            return;
        }

        String oldStatus = getStatusLabel(oldOrder.getStatus());
        String newStatus = getStatusLabel(status);

        if (oldStatus == null || newStatus == null) {
            throw new ServiceException("订单状态无效");
        }

        if (!isOrderStatusTransitionAllowed(getStatusValue(oldStatus), getStatusValue(newStatus), operator)) {
            throw new ServiceException("当前不允许将订单" + orderCode + "的状态从" + oldStatus + "变更为" + newStatus);
        }

        List<OperationLog> cachedLog = LogUtil.getOperationLog();
        LogUtil.clearOperationLog();

        try {
            LogUtil.addOperationLog(
                    OperationLog.builder()
                            .operator(operator)
                            .operationTime(DateUtils.getNowDate())
                            .operationType("订单状态变更")
                            .recordId("订单" + orderCode)
                            .details("将订单" + orderCode + "的状态从" + oldStatus + "变更为" + newStatus)
                            .build()
            );

            ErpOrders order = new ErpOrders();
            order.setId(oldOrder.getId());
            order.setStatus(status);
            if (0 == erpOrdersMapper.updateErpOrders(order)) {
                throw new ServiceException("更新订单状态失败");
            }

            LogUtil.commitOperationLogs();
        } finally {
            LogUtil.setOperationLog(cachedLog);
        }
    }

    @Override
    public void updateOrderStatus(Long id, Integer status, String operator) {
        ErpOrders erpOrders = erpOrdersMapper.selectErpOrdersById(id);
        if (erpOrders == null) {
            throw new ServiceException("订单编号不存在");
        }
        updateOrderStatus(erpOrders.getInnerId(), status, operator);
    }

    @Override
    public void updateOrderStatusAutomatic(Long id, OrderStatus status) {
        updateOrderStatus(id, status.getValue(this), LogUtil.OPERATOR_SYSTEM);
    }

    @Override
    public void updateOrderStatusAutomatic(String orderCode, OrderStatus status) {
        updateOrderStatus(orderCode, status.getValue(this), LogUtil.OPERATOR_SYSTEM);
    }

    @Override
    public void updateOrderStatusAutomatic(Long id, Function<OrderStatus, OrderStatus> transformer) {
        ErpOrders erpOrders = erpOrdersMapper.selectErpOrdersById(id);
        if (erpOrders == null) {
            throw new ServiceException("订单ID不存在");
        }
        OrderStatus targetStatus = transformer.apply(OrderStatus.of(erpOrders.getStatus(), this));
        updateOrderStatusAutomatic(id, targetStatus);
    }

    @Override
    public void updateOrderStatusAutomatic(String orderCode, Function<OrderStatus, OrderStatus> transformer) {
        ErpOrders erpOrders = erpOrdersMapper.selectErpOrdersByInnerId(orderCode);
        if (erpOrders == null) {
            throw new ServiceException("订单编号不存在");
        }
        OrderStatus targetStatus = transformer.apply(OrderStatus.of(erpOrders.getStatus(), this));
        updateOrderStatusAutomatic(orderCode, targetStatus);
    }

    /**
     * 修改订单
     * 
     * @param erpOrders 订单
     * @return 结果
     */
    @Override
    @Transactional
    public int updateErpOrders(ErpOrders erpOrders) {
        // 获取原始订单信息，检查是否涉及状态修改
        ErpOrders originalOrder = erpOrdersMapper.selectErpOrdersById(erpOrders.getId());
        if (originalOrder == null) {
            throw new ServiceException("订单不存在");
        }
        
        boolean hasStatusChange = false;
        Integer newStatus = null;
        
        // 检查是否涉及状态修改
        if (erpOrders.getStatus() != null && !erpOrders.getStatus().equals(originalOrder.getStatus())) {
            hasStatusChange = true;
            newStatus = erpOrders.getStatus();
        }
        

        erpOrders.setUpdateTime(DateUtils.getNowDate());
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            erpOrders.setOperator(loginUser.getUsername());
        }
        
        // 如果涉及状态修改，先更新其他字段，但暂时不更新状态
        if (hasStatusChange) {
            //创建一个副本，暂时不包含状态修改
            ErpOrders ordersWithoutStatus = new ErpOrders();
            ordersWithoutStatus.setId(erpOrders.getId());
            ordersWithoutStatus.setUpdateTime(erpOrders.getUpdateTime());
            ordersWithoutStatus.setOperator(erpOrders.getOperator());
            ordersWithoutStatus.setQuantity(erpOrders.getQuantity());
            ordersWithoutStatus.setDeliveryDeadline(erpOrders.getDeliveryDeadline());
            ordersWithoutStatus.setDeliveryTime(erpOrders.getDeliveryTime());
            ordersWithoutStatus.setCustomerId(erpOrders.getCustomerId());
            ordersWithoutStatus.setProductId(erpOrders.getProductId());
            ordersWithoutStatus.setProductionId(erpOrders.getProductionId());
            ordersWithoutStatus.setPurchaseId(erpOrders.getPurchaseId());
            ordersWithoutStatus.setSubcontractId(erpOrders.getSubcontractId());
            ordersWithoutStatus.setRemark(erpOrders.getRemark());
            ordersWithoutStatus.setInnerId(erpOrders.getInnerId());
            ordersWithoutStatus.setOuterId(erpOrders.getOuterId());
            // 不设置status字段
            
            if (0 == erpOrdersMapper.updateErpOrders(ordersWithoutStatus)) {
                throw new ServiceException("更新订单信息失败");
            }
        } else {
            // 原先逻辑
            if (0 == erpOrdersMapper.updateErpOrders(erpOrders)) {
                throw new ServiceException("操作失败");
            }
        }
        //原先逻辑
        if (erpOrders.getCustomerName() != null) {
           ErpOrders data = erpOrdersMapper.selectErpOrdersById(erpOrders.getId());
           ErpCustomers erpCustomers = new ErpCustomers();
           erpCustomers.setId(data.getCustomerId());
           erpCustomers.setName(erpOrders.getCustomerName());
           if (0 == erpCustomersService.updateErpCustomers(erpCustomers)) {
               throw new ServiceException("更新客户信息失败，客户ID无效");
           }
       }
        if (erpOrders.getProducts() != null) {
            erpOrdersMapper.clearOrdersProducts(erpOrders.getId());
            if (!erpOrders.getProducts().isEmpty()) {
                for (ErpOrdersProduct erpOrdersProduct : erpOrders.getProducts()) {
                    erpOrdersProduct.setOrdersId(erpOrders.getId());
                }
                erpOrdersMapper.insertOrdersProducts(erpOrders.getProducts());
            }
        }

        // TODO: 将状态修改逻辑移到审核流程中
        if (erpOrders.getStatus() != null) {
            updateOrderStatus(erpOrders.getId(), erpOrders.getStatus(), SecurityUtils.getUsername());
        }

                 // 如果涉及状态修改，检查审核开关并处理
         if (hasStatusChange) {
             try {
                 // 检查订单审核开关是否启用
                 boolean auditEnabled = erpAuditSwitchService.isAuditEnabled(1); // 1 = 订单审核类型
                 
                 if (auditEnabled) {
                     // 审核开关已启用，创建审核记录并发送通知
                     ErpOrders updatedOrder = fillErpOrders(erpOrdersMapper.selectErpOrdersById(erpOrders.getId()));
                     erpAuditRecordService.handleOrderStatusChange(updatedOrder, newStatus);
                 } else {
                     // 审核开关已关闭，直接更新状态
                     ErpOrders statusUpdate = new ErpOrders();
                     statusUpdate.setId(erpOrders.getId());
                     statusUpdate.setStatus(newStatus);
                     statusUpdate.setUpdateTime(DateUtils.getNowDate());
                     
                     if (0 == erpOrdersMapper.updateErpOrders(statusUpdate)) {
                         throw new ServiceException("更新订单状态失败");
                     }
                 }
                 
             } catch (Exception e) {
                 throw new ServiceException("处理订单状态变更失败：" + e.getMessage());
             }
         }

       return 1;
    }

    /**
     * 批量删除订单
     * 
     * @param ids 需要删除的订单主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteErpOrdersByIds(Long[] ids)
    {
        for (Long id : ids) {
            // 在删除订单之前，处理相关的待审核记录
            try {
                erpAuditRecordService.handleAuditableEntityDeleted(
                    AuditTypeEnum.ORDER_AUDIT.getCode(),
                    id
                );
            } catch (Exception e) {
                // 记录日志，但不要影响主删除流程
                log.error("删除订单前，处理其待审核记录失败，订单ID：{}", id, e);
            }
        }
        return erpOrdersMapper.deleteErpOrdersByIds(ids);
    }

    /**
     * 删除订单信息
     * 
     * @param id 订单主键
     * @return 结果
     */
    @Override
    public int deleteErpOrdersById(Long id)
    {
        return erpOrdersMapper.deleteErpOrdersById(id);
    }

    @Override
    public void applyApprovedStatus(ErpOrders erpOrders) {
        // 直接更新数据库，不包含其他业务逻辑
        erpOrdersMapper.updateErpOrders(erpOrders);
    }

    @Override
    public OrderStatisticsResult getOrderStatistics(Date startTime, Date endTime) {
        OrderStatisticsResult result = new OrderStatisticsResult();
        result.setTotalCount(erpOrdersMapper.getOrderCount(null));
        result.setDeliveredCount(erpOrdersMapper.getDeliveredOrderCount(null));
        result.setPunctualCount(erpOrdersMapper.getPunctualOrderCount(null));
        if (result.getDeliveredCount() != 0) {
            result.setPunctualRate(result.getPunctualCount() * 100 / (double) result.getDeliveredCount());

            Date today = DateUtils.getNowDate();
            Date yesterday = DateUtils.addDays(today, -1);
            Date lastWeek = DateUtils.addDays(today, -7);
            long punctualToday = erpOrdersMapper.getPunctualOrderCount(today);
            long deliveredToday = erpOrdersMapper.getDeliveredOrderCount(today);
            long punctualYesterday = erpOrdersMapper.getPunctualOrderCount(yesterday);
            long deliveredYesterday = erpOrdersMapper.getDeliveredOrderCount(yesterday);
            long punctualLastWeek = erpOrdersMapper.getPunctualOrderCount(lastWeek);
            long deliveredLastWeek = erpOrdersMapper.getDeliveredOrderCount(lastWeek);

            if (deliveredToday != 0) {
                if (deliveredYesterday != 0) {
                    result.setPunctualRateDayOnDay(
                            punctualToday * deliveredYesterday * 100 / (double) (punctualYesterday * deliveredToday)
                    );
                }
                if (punctualLastWeek != 0) {
                    result.setPunctualRateWeekOnWeek(
                            punctualToday * deliveredLastWeek * 100 / (double) (punctualLastWeek * deliveredToday)
                    );
                }
            }
        }


        result.setStatusCount(
                erpOrdersMapper.getOrderStatusCount(startTime, endTime).stream()
                        .collect(Collectors.toMap(
                                statusCount -> getStatusLabel(statusCount.getStatus()),
                                OrderStatisticsResult.StatusCouunt::getCount
                        ))
        );
        return result;
    }

    @Override
    public Integer getStatusValue(String label) {
        String dictValue = DictUtils.getDictValue(OrderStatus.ORDER_STATUS_DICT_TYPE, label);
        if (!StringUtils.isNumeric(dictValue.startsWith("-") ? dictValue.substring(1) : dictValue)) {
            throw new IllegalArgumentException("订单状态无效：" + label);
        }
        return Integer.valueOf(dictValue);
    }

    @Override
    public String getStatusLabel(Integer status) {
        return DictUtils.getDictLabel(OrderStatus.ORDER_STATUS_DICT_TYPE, String.valueOf(status));
    }

    @Override
    public Map<String, Integer> getStatusMap() {
        String dictLabels = DictUtils.getDictLabels(OrderStatus.ORDER_STATUS_DICT_TYPE);
        return Arrays.stream(dictLabels.split(DictUtils.SEPARATOR))
               .collect(Collectors.toMap(
                       label -> label,
                       this::getStatusValue
               ));
    }
}
