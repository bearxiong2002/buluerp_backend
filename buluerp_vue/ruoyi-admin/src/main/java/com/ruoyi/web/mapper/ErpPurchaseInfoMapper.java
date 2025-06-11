package com.ruoyi.web.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.web.domain.ErpPurchaseInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 外购资料，用于存储外购物料的基本信息和相关数据Mapper接口
 * 
 * @author ruoyi
 * @date 2025-04-08
 */
@Mapper
public interface ErpPurchaseInfoMapper extends BaseMapper<ErpPurchaseInfo>
{
    /**
     * 查询外购资料，用于存储外购物料的基本信息和相关数据
     * 
     * @param id 外购资料，用于存储外购物料的基本信息和相关数据主键
     * @return 外购资料，用于存储外购物料的基本信息和相关数据
     */
    public ErpPurchaseInfo selectErpPurchaseInfoById(Long id);

    /**
     * 查询外购资料，用于存储外购物料的基本信息和相关数据列表
     * 
     * @param erpPurchaseInfo 外购资料，用于存储外购物料的基本信息和相关数据
     * @return 外购资料，用于存储外购物料的基本信息和相关数据集合
     */
    public List<ErpPurchaseInfo> selectErpPurchaseInfoList(ErpPurchaseInfo erpPurchaseInfo);

    List<ErpPurchaseInfo> selectErpPurchaseInfoListByIds(Long[] ids);

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
     * 删除外购资料，用于存储外购物料的基本信息和相关数据
     * 
     * @param id 外购资料，用于存储外购物料的基本信息和相关数据主键
     * @return 结果
     */
    public int deleteErpPurchaseInfoById(Long id);

    /**
     * 批量删除外购资料，用于存储外购物料的基本信息和相关数据
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteErpPurchaseInfoByIds(Long[] ids);
}
