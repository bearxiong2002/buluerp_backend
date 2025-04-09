package com.ruoyi.web.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.web.domain.ErpDesignPatterns;
import com.ruoyi.web.mapper.ErpDesignPatternsMapper;
import com.ruoyi.web.service.IErpDesignPatternsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
@Service
public class ErpDesignPatternsServiceImpl implements IErpDesignPatternsService
{
    @Autowired
    private ErpDesignPatternsMapper erpDesignPatternsMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public ErpDesignPatterns selectErpDesignPatternsById(Long id)
    {
        return erpDesignPatternsMapper.selectErpDesignPatternsById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param erpDesignPatterns 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<ErpDesignPatterns> selectErpDesignPatternsList(ErpDesignPatterns erpDesignPatterns)
    {
        return erpDesignPatternsMapper.selectErpDesignPatternsList(erpDesignPatterns);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param erpDesignPatterns 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertErpDesignPatterns(ErpDesignPatterns erpDesignPatterns)
    {
        erpDesignPatterns.setCreateTime(DateUtils.getNowDate());
        return erpDesignPatternsMapper.insertErpDesignPatterns(erpDesignPatterns);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param erpDesignPatterns 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateErpDesignPatterns(ErpDesignPatterns erpDesignPatterns)
    {
        return erpDesignPatternsMapper.updateErpDesignPatterns(erpDesignPatterns);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteErpDesignPatternsByIds(Long[] ids)
    {
        return erpDesignPatternsMapper.deleteErpDesignPatternsByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteErpDesignPatternsById(Long id)
    {
        return erpDesignPatternsMapper.deleteErpDesignPatternsById(id);
    }
}
