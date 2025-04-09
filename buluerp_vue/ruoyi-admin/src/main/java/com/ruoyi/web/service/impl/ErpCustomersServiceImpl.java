package com.ruoyi.web.service.impl;

import java.util.Date;
import java.util.List;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.web.domain.ErpCustomers;
import com.ruoyi.web.mapper.ErpCustomersMapper;
import com.ruoyi.web.service.IErpCustomersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
@Service
public class ErpCustomersServiceImpl implements IErpCustomersService
{
    @Autowired
    private ErpCustomersMapper erpCustomersMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public ErpCustomers selectErpCustomersById(Long id)
    {
        ErpCustomers erpCustomers = erpCustomersMapper.selectErpCustomersById(id);
        if (erpCustomers == null)
        {
            throw new ServiceException("客户不存在", 400);
        }
        return erpCustomers;
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param erpCustomers 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<ErpCustomers> selectErpCustomersList(ErpCustomers erpCustomers)
    {
        return erpCustomersMapper.selectErpCustomersList(erpCustomers);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param erpCustomers 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertErpCustomers(ErpCustomers erpCustomers)
    {
        Date now = DateUtils.getNowDate();
        erpCustomers.setCreatTime(now);
        erpCustomers.setUpdateTime(now);
        return erpCustomersMapper.insertErpCustomers(erpCustomers);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param erpCustomers 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateErpCustomers(ErpCustomers erpCustomers)
    {
        erpCustomers.setUpdateTime(DateUtils.getNowDate());
        return erpCustomersMapper.updateErpCustomers(erpCustomers);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteErpCustomersByIds(Long[] ids)
    {
        return erpCustomersMapper.deleteErpCustomersByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteErpCustomersById(Long id)
    {
        return erpCustomersMapper.deleteErpCustomersById(id);
    }
}
