package com.ruoyi.web.service.impl;

import java.io.IOException;
import java.util.List;

import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.web.domain.ErpDesignStyle;
import com.ruoyi.web.mapper.ErpDesignStyleMapper;
import com.ruoyi.web.request.design.AddDesignRequest;
import com.ruoyi.web.request.design.LIstDesignRequest;
import com.ruoyi.web.request.design.UpdateDesignRequest;
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
        ErpDesignStyle erpDesignStyle=new ErpDesignStyle(lIstDesignRequest.getId(), lIstDesignRequest.getDesignPatternId(), lIstDesignRequest.getGroupId());
        return erpDesignStyleMapper.selectErpDesignStyleList(erpDesignStyle);
    }

    @Override
    public List<ErpDesignStyle> selectErpDesignStyleListByIds(Long[] ids) {
        return erpDesignStyleMapper.selectErpDesignStyleListByIds(ids);
    }

    /**
     * 新增设计造型
     * 
     * @param addDesignRequest 造型表新增请求
     * @return 结果
     */
    @Override
    public int insertErpDesignStyle(AddDesignRequest addDesignRequest) throws IOException {

        String url= FileUploadUtils.upload(addDesignRequest.getPicture());

        ErpDesignStyle erpDesignStyle=new ErpDesignStyle(addDesignRequest.getDesignPatternId(),addDesignRequest.getGroupId(), addDesignRequest.getMouldNumber(), addDesignRequest.getLddNumber(), addDesignRequest.getMouldCategory(), addDesignRequest.getMouldId(), url, addDesignRequest.getColor(), addDesignRequest.getProductName(), addDesignRequest.getQuantity(), addDesignRequest.getMaterial());

        return erpDesignStyleMapper.insertErpDesignStyle(erpDesignStyle);
    }

    /**
     * 修改设计造型
     * 
     * @param updateDesignRequest 设计造型
     * @return 结果
     */
    @Override
    public int updateErpDesignStyle(UpdateDesignRequest updateDesignRequest) throws IOException {
        String url= FileUploadUtils.upload(updateDesignRequest.getPicture());
        ErpDesignStyle erpDesignStyle=new ErpDesignStyle(updateDesignRequest.getId(), updateDesignRequest.getDesignPatternId(), updateDesignRequest.getGroupId(), updateDesignRequest.getMouldNumber(), updateDesignRequest.getLddNumber(), updateDesignRequest.getMouldCategory(), updateDesignRequest.getMouldId(), url, updateDesignRequest.getColor(), updateDesignRequest.getProductName(), updateDesignRequest.getQuantity(), updateDesignRequest.getMaterial());
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
