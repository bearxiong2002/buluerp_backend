package com.ruoyi.web.service.impl;

import java.util.List;

import com.ruoyi.web.domain.ErpDesignStyle;
import com.ruoyi.web.mapper.ErpDesignStyleMapper;
import com.ruoyi.web.request.AddDesignRequest;
import com.ruoyi.web.request.LIstDesignRequest;
import com.ruoyi.web.request.UpdateDesignRequest;
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
     * @param lIstDesignRequest 设计造型
     * @return 设计造型
     */
    @Override
    public List<ErpDesignStyle> selectErpDesignStyleList(LIstDesignRequest lIstDesignRequest)
    {
        ErpDesignStyle erpDesignStyle=new ErpDesignStyle(lIstDesignRequest.getDesignPatternId(), lIstDesignRequest.getGroupId());
        return erpDesignStyleMapper.selectErpDesignStyleList(erpDesignStyle);
    }

    /**
     * 新增设计造型
     * 
     * @param addDesignRequest 造型表新增请求
     * @return 结果
     */
    @Override
    public int insertErpDesignStyle(AddDesignRequest addDesignRequest)
    {
        ErpDesignStyle erpDesignStyle=new ErpDesignStyle(addDesignRequest.getDesignPatternId(),addDesignRequest.getGroupId(), addDesignRequest.getMouldNumber(), addDesignRequest.getLddNumber(), addDesignRequest.getMouldCategory(), addDesignRequest.getMouldId(), addDesignRequest.getPictureUrl(), addDesignRequest.getColor(), addDesignRequest.getProductName(), addDesignRequest.getQuantity(), addDesignRequest.getMaterial());

        erpDesignStyleMapper.insertErpDesignStyle(erpDesignStyle);

        return erpDesignStyleMapper.insertErpDesignStyle(erpDesignStyle);
    }

    /**
     * 修改设计造型
     * 
     * @param updateDesignRequest 设计造型
     * @return 结果
     */
    @Override
    public int updateErpDesignStyle(UpdateDesignRequest updateDesignRequest)
    {
        ErpDesignStyle erpDesignStyle=new ErpDesignStyle(updateDesignRequest.getDesignPatternId(), updateDesignRequest.getGroupId(), updateDesignRequest.getMouldNumber(), updateDesignRequest.getLddNumber(), updateDesignRequest.getMouldCategory(), updateDesignRequest.getMouldId(), updateDesignRequest.getPictureUrl(), updateDesignRequest.getColor(), updateDesignRequest.getProductName(), updateDesignRequest.getQuantity(), updateDesignRequest.getMaterial());
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
