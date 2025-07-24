package com.ruoyi.web.service;

import com.ruoyi.web.domain.ErpOrders;
import com.ruoyi.web.enums.OrderStatus;
import com.ruoyi.web.request.order.ListOrderRequest;
import com.ruoyi.web.result.OrderStatisticsResult;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


/**
 * 订单Service接口
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
public interface IErpOrdersService extends OrderStatus.StatusMapper
{
    /**
     * 查询订单
     * 
     * @param id 订单主键
     * @return 订单
     */
    public ErpOrders selectErpOrdersById(Long id);

    ErpOrders selectByOrderCode(String orderCode);

    List<ErpOrders> selectByInnerIds(List<String> innerIds);

    /**
     * 查询订单列表
     * 
     * @param request 订单
     * @return 订单集合
     */
    public List<ErpOrders> selectErpOrdersList(ListOrderRequest request);

    List<ErpOrders> selectErpOrdersListByIds(Long[] ids);

    /**
     * 新增订单
     * 
     * @param erpOrders 订单
     * @return 结果
     */
    public int insertErpOrders(ErpOrders erpOrders);

    /**
     * 检查订单状态是否符合转移条件
     *
     * @param fromStatus 原状态
     * @param toStatus 目标状态
     * @param operator 操作人。如果设置为 LogUtil.OPERATOR_SYSTEM 则表示系统操作（admin 状态变更权限）
     * @return true 如果状态转移是允许的，否则返回 false
     */
    boolean isOrderStatusTransitionAllowed(Integer fromStatus, Integer toStatus, String operator);

    /**
     * 无审计地更新订单状态
     *
     * @param orderCode 订单内部编号
     * @param status 新的状态
     * @param operator 操作人。如果设置为 LogUtil.OPERATOR_SYSTEM 则表示系统操作（admin 状态变更权限）
     * @throws com.ruoyi.common.exception.ServiceException 如果无权限进行状态变更或部分参数无效
     */
    void updateOrderStatus(String orderCode, Integer status, String operator);

    void updateOrderStatus(Long id, Integer status, String operator);

    void updateOrderStatusAutomatic(Long id, OrderStatus status);

    void updateOrderStatusAutomatic(String orderCode, OrderStatus status);

    void updateOrderStatusAutomatic(Long id, Function<OrderStatus, OrderStatus> transformer);

    void updateOrderStatusAutomatic(String orderCode, Function<OrderStatus, OrderStatus> transformer);

    /**
     * 修改订单
     * 
     * @param erpOrders 订单
     * @return 结果
     */
    public int updateErpOrders(ErpOrders erpOrders);

    void updateOrderAllScheduled(Long orderId, boolean allScheduled);
    void updateOrderAllPurchased(Long orderId, boolean allPurchased);

    /**
     * 批量删除订单
     * 
     * @param ids 需要删除的订单主键集合
     * @return 结果
     */
    public int deleteErpOrdersByIds(Long[] ids);

    /**
     * 删除订单信息
     * 
     * @param id 订单主键
     * @return 结果
     */
    public int deleteErpOrdersById(Long id);

    /**
     * 在审核通过后，直接应用新的状态，不触发额外的审核流程
     * @param erpOrders 包含新状态的订单对象
     */
    void applyApprovedStatus(ErpOrders erpOrders);

    OrderStatisticsResult getOrderStatistics(Date startTime, Date endTime);

    @Override
    Integer getStatusValue(String label);

    @Override
    String getStatusLabel(Integer status);

    Map<String, Integer> getStatusMap();
}
