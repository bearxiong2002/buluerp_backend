package com.ruoyi.web.service;

import com.ruoyi.web.domain.ErpCustomers;

import java.util.List;


/**
 * 【请填写功能名称】Service接口
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
public interface IErpCustomersService 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public ErpCustomers selectErpCustomersById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param erpCustomers 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<ErpCustomers> selectErpCustomersList(ErpCustomers erpCustomers);

    List<ErpCustomers> selectErpCustomersListByIds(Long[] ids);

    /**
     * 新增【请填写功能名称】
     * 
     * @param erpCustomers 【请填写功能名称】
     * @return 结果
     */
    public int insertErpCustomers(ErpCustomers erpCustomers);

    /**
     * 修改【请填写功能名称】
     * 
     * @param erpCustomers 【请填写功能名称】
     * @return 结果
     */
    public int updateErpCustomers(ErpCustomers erpCustomers);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】主键集合
     * @return 结果
     */
    public int deleteErpCustomersByIds(Long[] ids);

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteErpCustomersById(Long id);
}
