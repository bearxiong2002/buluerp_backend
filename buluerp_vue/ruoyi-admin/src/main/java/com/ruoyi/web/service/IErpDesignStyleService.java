package com.ruoyi.web.service;

import com.ruoyi.web.domain.ErpDesignStyle;

import java.util.List;


/**
 * 设计造型Service接口
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
public interface IErpDesignStyleService 
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
     * 批量删除设计造型
     * 
     * @param ids 需要删除的设计造型主键集合
     * @return 结果
     */
    public int deleteErpDesignStyleByIds(Long[] ids);

    /**
     * 删除设计造型信息
     * 
     * @param id 设计造型主键
     * @return 结果
     */
    public int deleteErpDesignStyleById(Long id);
}
