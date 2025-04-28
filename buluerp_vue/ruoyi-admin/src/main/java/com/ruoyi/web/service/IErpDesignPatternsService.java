package com.ruoyi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.web.request.AddDesignPatternsRequest;
import com.ruoyi.web.domain.ErpDesignPatterns;
import com.ruoyi.web.request.ListDesignPatternsRequest;
import com.ruoyi.web.request.UpdateDesignPatternsRequest;
import com.ruoyi.web.result.DesignPatternsResult;

import java.util.List;


/**
 * 【请填写功能名称】Service接口
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
public interface IErpDesignPatternsService extends IService<ErpDesignPatterns>
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public DesignPatternsResult selectErpDesignPatternsById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param listDesignPatternsRequest 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<ErpDesignPatterns> selectErpDesignPatternsList(ListDesignPatternsRequest listDesignPatternsRequest);

    /**
     * 新增【请填写功能名称】
     * 
     * @param addDesignPatternsRequest 【请填写功能名称】
     * @return 结果
     */
    public int insertErpDesignPatterns(AddDesignPatternsRequest addDesignPatternsRequest);

    /**
     * 修改【请填写功能名称】
     * 
     * @param updateDesignPatternsRequest 【请填写功能名称】
     * @return 结果
     */
    public int updateErpDesignPatterns(UpdateDesignPatternsRequest updateDesignPatternsRequest);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】主键集合
     * @return 结果
     */
    public int deleteErpDesignPatternsByIds(Long[] ids);

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteErpDesignPatternsById(Long id);
}
