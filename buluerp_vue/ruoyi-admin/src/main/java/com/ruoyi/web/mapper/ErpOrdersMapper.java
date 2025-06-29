package com.ruoyi.web.mapper;

import com.ruoyi.web.domain.ErpOrders;
import com.ruoyi.web.domain.ErpOrdersProduct;
import com.ruoyi.web.request.order.ListOrderRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;


/**
 * 订单Mapper接口
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
@Mapper
public interface ErpOrdersMapper 
{
    /**
     * 查询订单
     * 
     * @param id 订单主键
     * @return 订单
     */
    public ErpOrders selectErpOrdersById(Long id);

    ErpOrders selectErpOrdersByInnerId(String innerId);

    ErpOrders selectErpOrdersByOuterId(String outerId);

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
     * 修改订单
     * 
     * @param erpOrders 订单
     * @return 结果
     */
    public int updateErpOrders(ErpOrders erpOrders);

    /**
     * 删除订单
     * 
     * @param id 订单主键
     * @return 结果
     */
    public int deleteErpOrdersById(Long id);

    /**
     * 批量删除订单
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteErpOrdersByIds(Long[] ids);

    int insertOrdersProducts(List<ErpOrdersProduct> products);

    int clearOrdersProducts(Long ordersId);

    int clearOrdersProductsByProduct(Long productId);

    List<ErpOrdersProduct> selectOrdersProducts(Long ordersId);
}