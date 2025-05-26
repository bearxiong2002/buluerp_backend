package com.ruoyi.web.service.impl;

import java.util.List;

import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.web.domain.ErpOrders;
import com.ruoyi.web.mapper.ErpCustomersMapper;
import com.ruoyi.web.mapper.ErpOrdersMapper;
import com.ruoyi.web.service.IErpCustomersService;
import com.ruoyi.web.service.IErpOrdersService;
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
    @Autowired
    private ErpOrdersMapper erpOrdersMapper;

    @Autowired
    private IErpCustomersService erpCustomersService;

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
                    erpCustomersService.selectErpCustomersById(erpOrders1.getCustomerId())
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
    @Transactional
    public int insertErpOrders(ErpOrders erpOrders)
    {
        erpOrders.setCreateTime(DateUtils.getNowDate());
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            erpOrders.setOperatorId(loginUser.getUserId());
        }
        if (erpOrders.getCustomer() != null) {
            if (0 == erpCustomersService.insertErpCustomers(erpOrders.getCustomer())) {
                throw new ServiceException("添加客户信息失败");
            }
            erpOrders.setCustomerId(erpOrders.getCustomer().getId());
        }
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
    public int updateErpOrders(ErpOrders erpOrders)
    {
        erpOrders.setUpdateTime(DateUtils.getNowDate());
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            erpOrders.setOperatorId(loginUser.getUserId());
        }
       if (0 == erpOrdersMapper.updateErpOrders(erpOrders)) {
           throw new ServiceException("操作失败");
       }
       if (erpOrders.getCustomer() != null) {
           ErpOrders data = erpOrdersMapper.selectErpOrdersById(erpOrders.getId());
           erpOrders.getCustomer().setId(data.getCustomerId());
           if (0 == erpCustomersService.updateErpCustomers(erpOrders.getCustomer())) {
               throw new ServiceException("更新客户信息失败");
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
