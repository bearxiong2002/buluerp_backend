package com.ruoyi.web.mapper;

import com.ruoyi.web.domain.ErpDesignSubPattern;

import java.util.List;


/**
 * 模具信息，用于存储模具的基本信息和相关数据Mapper接口
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
public interface ErpDesignSubPatternMapper 
{
    /**
     * 查询模具信息，用于存储模具的基本信息和相关数据
     * 
     * @param id 模具信息，用于存储模具的基本信息和相关数据主键
     * @return 模具信息，用于存储模具的基本信息和相关数据
     */
    public ErpDesignSubPattern selectErpDesignSubPatternById(Long id);

    public List<ErpDesignSubPattern> selectErpDesignSubPatternListByIds(Long[] ids);

    /**
     * 查询模具信息，用于存储模具的基本信息和相关数据列表
     * 
     * @param erpDesignSubPattern 模具信息，用于存储模具的基本信息和相关数据
     * @return 模具信息，用于存储模具的基本信息和相关数据集合
     */
    public List<ErpDesignSubPattern> selectErpDesignSubPatternList(ErpDesignSubPattern erpDesignSubPattern);

    /**
     * 新增模具信息，用于存储模具的基本信息和相关数据
     * 
     * @param erpDesignSubPattern 模具信息，用于存储模具的基本信息和相关数据
     * @return 结果
     */
    public int insertErpDesignSubPattern(ErpDesignSubPattern erpDesignSubPattern);

    /**
     * 修改模具信息，用于存储模具的基本信息和相关数据
     * 
     * @param erpDesignSubPattern 模具信息，用于存储模具的基本信息和相关数据
     * @return 结果
     */
    public int updateErpDesignSubPattern(ErpDesignSubPattern erpDesignSubPattern);

    /**
     * 删除模具信息，用于存储模具的基本信息和相关数据
     * 
     * @param id 模具信息，用于存储模具的基本信息和相关数据主键
     * @return 结果
     */
    public int deleteErpDesignSubPatternById(Long id);

    /**
     * 批量删除模具信息，用于存储模具的基本信息和相关数据
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteErpDesignSubPatternByIds(Long[] ids);
}
