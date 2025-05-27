package com.ruoyi.web.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.domain.validation.Save;
import com.ruoyi.web.domain.ErpCustomers;
import com.ruoyi.common.domain.ExcelRowErrorInfo;
import com.ruoyi.common.exception.excel.ExcelImportException;
import com.ruoyi.web.mapper.ErpCustomersMapper;
import com.ruoyi.web.service.IErpCustomersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


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

    @Autowired
    private Validator validator;

    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public ErpCustomers selectErpCustomersById(Long id)
    {
        return erpCustomersMapper.selectErpCustomersById(id);
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

    @Override
    public List<ErpCustomers> selectErpCustomersListByIds(Long[] ids) {
        return erpCustomersMapper.selectErpCustomersListByIds(ids);
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
