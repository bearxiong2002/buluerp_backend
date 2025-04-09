package com.ruoyi.web.mapper;

import com.ruoyi.web.domain.ErpDesignStyle;

import java.util.List;


/**
 * 设计造型Mapper接口
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
public interface ErpDesignStyleMapper 
{
    /**
     * 查询设计造型
     * 
     * @param id 设计造型主键
     * @return 设计造型
     */
    public ErpDesignStyle selectErpDesignStyleById(Long id);

    /**
     * 查询设计造型列表
     * 
     * @param erpDesignStyle 设计造型
     * @return 设计造型集合
     */
    public List<ErpDesignStyle> selectErpDesignStyleList(ErpDesignStyle erpDesignStyle);

    /**
     * 新增设计造型
     * 
     * @param erpDesignStyle 设计造型
     * @return 结果
     */
    public int insertErpDesignStyle(ErpDesignStyle erpDesignStyle);

    /**
     * 修改设计造型
     * 
     * @param erpDesignStyle 设计造型
     * @return 结果
     */
    public int updateErpDesignStyle(ErpDesignStyle erpDesignStyle);

    /**
     * 删除设计造型
     * 
     * @param id 设计造型主键
     * @return 结果
     */
    public int deleteErpDesignStyleById(Long id);

    /**
     * 批量删除设计造型
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteErpDesignStyleByIds(Long[] ids);
}
