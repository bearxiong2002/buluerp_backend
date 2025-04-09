package com.ruoyi.web.mapper;

import com.ruoyi.web.domain.ErpDesignPatterns;

import java.util.List;


/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
public interface ErpDesignPatternsMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public ErpDesignPatterns selectErpDesignPatternsById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param erpDesignPatterns 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<ErpDesignPatterns> selectErpDesignPatternsList(ErpDesignPatterns erpDesignPatterns);

    /**
     * 新增【请填写功能名称】
     * 
     * @param erpDesignPatterns 【请填写功能名称】
     * @return 结果
     */
    public int insertErpDesignPatterns(ErpDesignPatterns erpDesignPatterns);

    /**
     * 修改【请填写功能名称】
     * 
     * @param erpDesignPatterns 【请填写功能名称】
     * @return 结果
     */
    public int updateErpDesignPatterns(ErpDesignPatterns erpDesignPatterns);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteErpDesignPatternsById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteErpDesignPatternsByIds(Long[] ids);
}
