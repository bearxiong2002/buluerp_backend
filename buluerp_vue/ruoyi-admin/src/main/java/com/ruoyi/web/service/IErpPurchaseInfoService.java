package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.ErpPurchaseInfo;

/**
 * 外购资料，用于存储外购物料的基本信息和相关数据Service接口
 * 
 * @author ruoyi
 * @date 2025-04-08
 */
public interface IErpPurchaseInfoService 
{
    /**
     * 查询外购资料，用于存储外购物料的基本信息和相关数据
     * 
     * @param purchaseCode 外购资料，用于存储外购物料的基本信息和相关数据主键
     * @return 外购资料，用于存储外购物料的基本信息和相关数据
     */
    public ErpPurchaseInfo selectErpPurchaseInfoByPurchaseCode(String purchaseCode);

    /**
     * 查询外购资料，用于存储外购物料的基本信息和相关数据列表
     * 
     * @param erpPurchaseInfo 外购资料，用于存储外购物料的基本信息和相关数据
     * @return 外购资料，用于存储外购物料的基本信息和相关数据集合
     */
    public List<ErpPurchaseInfo> selectErpPurchaseInfoList(ErpPurchaseInfo erpPurchaseInfo);

    /**
     * 新增外购资料，用于存储外购物料的基本信息和相关数据
     * 
     * @param erpPurchaseInfo 外购资料，用于存储外购物料的基本信息和相关数据
     * @return 结果
     */
    public int insertErpPurchaseInfo(ErpPurchaseInfo erpPurchaseInfo);

    /**
     * 修改外购资料，用于存储外购物料的基本信息和相关数据
     * 
     * @param erpPurchaseInfo 外购资料，用于存储外购物料的基本信息和相关数据
     * @return 结果
     */
    public int updateErpPurchaseInfo(ErpPurchaseInfo erpPurchaseInfo);

    /**
     * 批量删除外购资料，用于存储外购物料的基本信息和相关数据
     * 
     * @param purchaseCodes 需要删除的外购资料，用于存储外购物料的基本信息和相关数据主键集合
     * @return 结果
     */
    public int deleteErpPurchaseInfoByPurchaseCodes(String[] purchaseCodes);

    /**
     * 删除外购资料，用于存储外购物料的基本信息和相关数据信息
     * 
     * @param purchaseCode 外购资料，用于存储外购物料的基本信息和相关数据主键
     * @return 结果
     */
    public int deleteErpPurchaseInfoByPurchaseCode(String purchaseCode);
}
