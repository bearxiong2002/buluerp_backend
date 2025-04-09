package com.ruoyi.web.service.impl;

import java.util.List;

import com.ruoyi.web.domain.ErpDesignSubPattern;
import com.ruoyi.web.mapper.ErpDesignSubPatternMapper;
import com.ruoyi.web.service.IErpDesignSubPatternService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * 模具信息，用于存储模具的基本信息和相关数据Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
@Service
public class ErpDesignSubPatternServiceImpl implements IErpDesignSubPatternService
{
    @Autowired
    private ErpDesignSubPatternMapper erpDesignSubPatternMapper;

    /**
     * 查询模具信息，用于存储模具的基本信息和相关数据
     * 
     * @param id 模具信息，用于存储模具的基本信息和相关数据主键
     * @return 模具信息，用于存储模具的基本信息和相关数据
     */
    @Override
    public ErpDesignSubPattern selectErpDesignSubPatternById(Long id)
    {
        return erpDesignSubPatternMapper.selectErpDesignSubPatternById(id);
    }

    /**
     * 查询模具信息，用于存储模具的基本信息和相关数据列表
     * 
     * @param erpDesignSubPattern 模具信息，用于存储模具的基本信息和相关数据
     * @return 模具信息，用于存储模具的基本信息和相关数据
     */
    @Override
    public List<ErpDesignSubPattern> selectErpDesignSubPatternList(ErpDesignSubPattern erpDesignSubPattern)
    {
        return erpDesignSubPatternMapper.selectErpDesignSubPatternList(erpDesignSubPattern);
    }

    /**
     * 新增模具信息，用于存储模具的基本信息和相关数据
     * 
     * @param erpDesignSubPattern 模具信息，用于存储模具的基本信息和相关数据
     * @return 结果
     */
    @Override
    public int insertErpDesignSubPattern(ErpDesignSubPattern erpDesignSubPattern)
    {
        return erpDesignSubPatternMapper.insertErpDesignSubPattern(erpDesignSubPattern);
    }

    /**
     * 修改模具信息，用于存储模具的基本信息和相关数据
     * 
     * @param erpDesignSubPattern 模具信息，用于存储模具的基本信息和相关数据
     * @return 结果
     */
    @Override
    public int updateErpDesignSubPattern(ErpDesignSubPattern erpDesignSubPattern)
    {
        return erpDesignSubPatternMapper.updateErpDesignSubPattern(erpDesignSubPattern);
    }

    /**
     * 批量删除模具信息，用于存储模具的基本信息和相关数据
     * 
     * @param ids 需要删除的模具信息，用于存储模具的基本信息和相关数据主键
     * @return 结果
     */
    @Override
    public int deleteErpDesignSubPatternByIds(Long[] ids)
    {
        return erpDesignSubPatternMapper.deleteErpDesignSubPatternByIds(ids);
    }

    /**
     * 删除模具信息，用于存储模具的基本信息和相关数据信息
     * 
     * @param id 模具信息，用于存储模具的基本信息和相关数据主键
     * @return 结果
     */
    @Override
    public int deleteErpDesignSubPatternById(Long id)
    {
        return erpDesignSubPatternMapper.deleteErpDesignSubPatternById(id);
    }
}
