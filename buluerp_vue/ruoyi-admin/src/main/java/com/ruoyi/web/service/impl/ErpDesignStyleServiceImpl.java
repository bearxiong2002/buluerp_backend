package com.ruoyi.web.service.impl;

import java.util.List;

import com.ruoyi.web.domain.ErpDesignStyle;
import com.ruoyi.web.mapper.ErpDesignStyleMapper;
import com.ruoyi.web.service.IErpDesignStyleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * 设计造型Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
@Service
public class ErpDesignStyleServiceImpl implements IErpDesignStyleService
{
    @Autowired
    private ErpDesignStyleMapper erpDesignStyleMapper;

    /**
     * 查询设计造型
     * 
     * @param id 设计造型主键
     * @return 设计造型
     */
    @Override
    public ErpDesignStyle selectErpDesignStyleById(Long id)
    {
        return erpDesignStyleMapper.selectErpDesignStyleById(id);
    }

    /**
     * 查询设计造型列表
     * 
     * @param erpDesignStyle 设计造型
     * @return 设计造型
     */
    @Override
    public List<ErpDesignStyle> selectErpDesignStyleList(ErpDesignStyle erpDesignStyle)
    {
        return erpDesignStyleMapper.selectErpDesignStyleList(erpDesignStyle);
    }

    /**
     * 新增设计造型
     * 
     * @param erpDesignStyle 设计造型
     * @return 结果
     */
    @Override
    public int insertErpDesignStyle(ErpDesignStyle erpDesignStyle)
    {
        return erpDesignStyleMapper.insertErpDesignStyle(erpDesignStyle);
    }

    /**
     * 修改设计造型
     * 
     * @param erpDesignStyle 设计造型
     * @return 结果
     */
    @Override
    public int updateErpDesignStyle(ErpDesignStyle erpDesignStyle)
    {
        return erpDesignStyleMapper.updateErpDesignStyle(erpDesignStyle);
    }

    /**
     * 批量删除设计造型
     * 
     * @param ids 需要删除的设计造型主键
     * @return 结果
     */
    @Override
    public int deleteErpDesignStyleByIds(Long[] ids)
    {
        return erpDesignStyleMapper.deleteErpDesignStyleByIds(ids);
    }

    /**
     * 删除设计造型信息
     * 
     * @param id 设计造型主键
     * @return 结果
     */
    @Override
    public int deleteErpDesignStyleById(Long id)
    {
        return erpDesignStyleMapper.deleteErpDesignStyleById(id);
    }
}
