package com.ruoyi.web.service.impl;

import java.util.List;

import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.web.domain.ErpOrders;
import com.ruoyi.web.mapper.ErpCustomersMapper;
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

    @Autowired
    private ErpCustomersMapper erpCustomersMapper;

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
        List<ErpOrders> list = erpOrdersMapper.selectErpOrdersList(erpOrders);
        for (ErpOrders erpOrders1 : list) {
            erpOrders1.setCustomer(
                    erpCustomersMapper.selectErpCustomersById(erpOrders1.getCustomerId())
            );
        }
        return list;
    }

    @Override
    public List<ErpOrders> selectErpOrdersListByIds(Long[] ids) {
        return erpOrdersMapper.selectErpOrdersListByIds(ids);
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
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            erpOrders.setOperatorId(loginUser.getUserId());
        }
        int res = erpOrdersMapper.insertErpOrders(erpOrders);
        if (res <= 0) {
            return 0;
        } else {
            erpOrders.setInnerId(erpOrders.generateInnerId());
            erpOrders.setOuterId(erpOrders.generateOuterId());
            return erpOrdersMapper.updateErpOrders(erpOrders);
        }
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
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            erpOrders.setOperatorId(loginUser.getUserId());
        }
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
