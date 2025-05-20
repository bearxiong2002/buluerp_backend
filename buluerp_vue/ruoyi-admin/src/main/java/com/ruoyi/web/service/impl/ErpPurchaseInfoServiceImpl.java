package com.ruoyi.system.service.impl;

import java.util.List;

import com.ruoyi.web.mapper.ErpPurchaseInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.system.domain.ErpPurchaseInfo;
import com.ruoyi.system.service.IErpPurchaseInfoService;

/**
 * 外购资料，用于存储外购物料的基本信息和相关数据Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-04-08
 */
@Service
public class ErpPurchaseInfoServiceImpl implements IErpPurchaseInfoService 
{
    @Autowired
    private ErpPurchaseInfoMapper erpPurchaseInfoMapper;

    /**
     * 查询外购资料，用于存储外购物料的基本信息和相关数据
     * 
     * @param purchaseCode 外购资料，用于存储外购物料的基本信息和相关数据主键
     * @return 外购资料，用于存储外购物料的基本信息和相关数据
     */
    @Override
    public ErpPurchaseInfo selectErpPurchaseInfoByPurchaseCode(String purchaseCode)
    {
        return erpPurchaseInfoMapper.selectErpPurchaseInfoByPurchaseCode(purchaseCode);
    }

    /**
     * 查询外购资料，用于存储外购物料的基本信息和相关数据列表
     * 
     * @param erpPurchaseInfo 外购资料，用于存储外购物料的基本信息和相关数据
     * @return 外购资料，用于存储外购物料的基本信息和相关数据
     */
    @Override
    public List<ErpPurchaseInfo> selectErpPurchaseInfoList(ErpPurchaseInfo erpPurchaseInfo)
    {
        return erpPurchaseInfoMapper.selectErpPurchaseInfoList(erpPurchaseInfo);
    }

    @Override
    public List<ErpPurchaseInfo> selectErpPurchaseInfoListByIds(Integer[] ids) {
        return erpPurchaseInfoMapper.selectErpPurchaseInfoListByIds(ids);
    }

    /**
     * 新增外购资料，用于存储外购物料的基本信息和相关数据
     * 
     * @param erpPurchaseInfo 外购资料，用于存储外购物料的基本信息和相关数据
     * @return 结果
     */
    @Override
    public int insertErpPurchaseInfo(ErpPurchaseInfo erpPurchaseInfo)
    {
        return erpPurchaseInfoMapper.insertErpPurchaseInfo(erpPurchaseInfo);
    }

    /**
     * 修改外购资料，用于存储外购物料的基本信息和相关数据
     * 
     * @param erpPurchaseInfo 外购资料，用于存储外购物料的基本信息和相关数据
     * @return 结果
     */
    @Override
    public int updateErpPurchaseInfo(ErpPurchaseInfo erpPurchaseInfo)
    {
        return erpPurchaseInfoMapper.updateErpPurchaseInfo(erpPurchaseInfo);
    }

    /**
     * 批量删除外购资料，用于存储外购物料的基本信息和相关数据
     * 
     * @param purchaseCodes 需要删除的外购资料，用于存储外购物料的基本信息和相关数据主键
     * @return 结果
     */
    @Override
    public int deleteErpPurchaseInfoByPurchaseCodes(String[] purchaseCodes)
    {
        return erpPurchaseInfoMapper.deleteErpPurchaseInfoByPurchaseCodes(purchaseCodes);
    }

    /**
     * 删除外购资料，用于存储外购物料的基本信息和相关数据信息
     * 
     * @param purchaseCode 外购资料，用于存储外购物料的基本信息和相关数据主键
     * @return 结果
     */
    @Override
    public int deleteErpPurchaseInfoByPurchaseCode(String purchaseCode)
    {
        return erpPurchaseInfoMapper.deleteErpPurchaseInfoByPurchaseCode(purchaseCode);
    }
}
