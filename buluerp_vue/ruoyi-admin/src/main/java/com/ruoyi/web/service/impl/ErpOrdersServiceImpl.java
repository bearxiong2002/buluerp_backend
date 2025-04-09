package com.ruoyi.web.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.web.domain.ErpOrders;
import com.ruoyi.web.mapper.ErpOrdersMapper;
import com.ruoyi.web.service.IErpOrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * 订单Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
@Service
public class ErpOrdersServiceImpl implements IErpOrdersService
{
    @Autowired
    private ErpOrdersMapper erpOrdersMapper;

    /**
     * 查询订单
     * 
     * @param id 订单主键
     * @return 订单
     */
    @Override
    public ErpOrders selectErpOrdersById(Long id)
    {
        return erpOrdersMapper.selectErpOrdersById(id);
    }

    /**
     * 查询订单列表
     * 
     * @param erpOrders 订单
     * @return 订单
     */
    @Override
    public List<ErpOrders> selectErpOrdersList(ErpOrders erpOrders)
    {
        return erpOrdersMapper.selectErpOrdersList(erpOrders);
    }

    /**
     * 新增订单
     * 
     * @param erpOrders 订单
     * @return 结果
     */
    @Override
    public int insertErpOrders(ErpOrders erpOrders)
    {
        erpOrders.setCreateTime(DateUtils.getNowDate());
        return erpOrdersMapper.insertErpOrders(erpOrders);
    }

    /**
     * 修改订单
     * 
     * @param erpOrders 订单
     * @return 结果
     */
    @Override
    public int updateErpOrders(ErpOrders erpOrders)
    {
        erpOrders.setUpdateTime(DateUtils.getNowDate());
        return erpOrdersMapper.updateErpOrders(erpOrders);
    }

    /**
     * 批量删除订单
     * 
     * @param ids 需要删除的订单主键
     * @return 结果
     */
    @Override
    public int deleteErpOrdersByIds(Long[] ids)
    {
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
