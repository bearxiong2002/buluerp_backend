package com.ruoyi.web.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.web.domain.*;
import com.ruoyi.web.mapper.ErpOrdersMapper;
import com.ruoyi.web.request.design.AddDesignPatternsRequest;
import com.ruoyi.web.request.order.ListOrderRequest;
import com.ruoyi.web.service.IErpCustomersService;
import com.ruoyi.web.service.IErpDesignPatternsService;
import com.ruoyi.web.service.IErpOrdersService;
import com.ruoyi.web.service.IErpProductsService;
import com.ruoyi.web.service.IOrderAuditService;
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
    private IOrderAuditService orderAuditService;

    private ErpOrders fillErpOrders(ErpOrders erpOrders) {
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
            ErpCustomers erpCustomers = erpCustomersService.selectErpCustomersByName(erpOrders.getCustomerName());
            if (erpCustomers == null) {
                erpCustomers = new ErpCustomers();
                erpCustomers.setName(erpOrders.getCustomerName());
                if (0 == erpCustomersService.insertErpCustomers(erpCustomers)) {
                    throw new ServiceException("添加客户信息失败，客户ID无效");
                }
            }
            erpOrders.setCustomerId(erpCustomers.getId());
        }
        if (erpOrders.getProducts() != null && !erpOrders.getProducts().isEmpty()) {
            erpOrdersMapper.insertOrdersProducts(erpOrders.getProducts());
        }
        erpOrders.setInnerId(ErpOrders.INNER_ID_PLACEHOLDER);
        erpOrders.setOuterId(ErpOrders.OUTER_ID_PLACEHOLDER);
        if (0 == erpOrdersMapper.insertErpOrders(erpOrders)) {
            throw new ServiceException("操作失败");
        }

        ErpOrders erpOrders1 = new ErpOrders();
        erpOrders1.setId(erpOrders.getId());
        erpOrders1.setInnerId(erpOrders.generateInnerId());
        erpOrders1.setOuterId(erpOrders.generateOuterId());
        if (0 == erpOrdersMapper.updateErpOrders(erpOrders1)) {
            throw new ServiceException("无法生成订单ID");
        }

        // 订单创建成功后，触发审核流程
        try {
            // 获取完整的订单信息（包含生成的订单编号）
            ErpOrders completeOrder = erpOrdersMapper.selectErpOrdersById(erpOrders.getId());
            
            // 触发订单审核流程
            orderAuditService.handleOrderCreated(completeOrder);
        } catch (Exception e) {
            // 记录日志但不影响订单创建
            log.error("订单创建后处理审核流程失败，订单ID：{}", erpOrders.getId(), e);
        }

        return 1;
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
        erpOrders.setUpdateTime(DateUtils.getNowDate());
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            erpOrders.setOperator(loginUser.getUsername());
        }
        if (0 == erpOrdersMapper.updateErpOrders(erpOrders)) {
            throw new ServiceException("操作失败");
        }
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
            erpOrdersMapper.clearOrdersProducts(id);
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
}
