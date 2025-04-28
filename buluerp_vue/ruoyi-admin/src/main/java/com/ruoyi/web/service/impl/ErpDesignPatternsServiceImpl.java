package com.ruoyi.web.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.web.mapper.ErpDesignStyleMapper;
import com.ruoyi.web.request.design.AddDesignPatternsRequest;
import com.ruoyi.web.domain.ErpDesignPatterns;
import com.ruoyi.web.mapper.ErpDesignPatternsMapper;
import com.ruoyi.web.request.design.ListDesignPatternsRequest;
import com.ruoyi.web.request.design.UpdateDesignPatternsRequest;
import com.ruoyi.web.result.DesignPatternsResult;
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
public class ErpDesignPatternsServiceImpl extends ServiceImpl<ErpDesignPatternsMapper,ErpDesignPatterns> implements IErpDesignPatternsService
{
    @Autowired
    private ErpDesignPatternsMapper erpDesignPatternsMapper;

    @Autowired
    private ErpDesignStyleMapper erpDesignStyleMapper;

    /**
     * 查询总表详情
     * 
     * @param id design_pattern_id
     * @return 造型表之和
     */
    @Override
    public DesignPatternsResult selectErpDesignPatternsById(Long id)
    {
        return new DesignPatternsResult(
                id,
                erpDesignStyleMapper.selectMouldNumberSet(id),
                erpDesignStyleMapper.selectLddNumberSet(id),
                erpDesignStyleMapper.selectMouldCategorySet(id),
                erpDesignStyleMapper.selectMouldIdSet(id),
                erpDesignStyleMapper.selectPictureUrlSet(id),
                erpDesignStyleMapper.selectColorSet(id),
                erpDesignStyleMapper.selectProductNameSet(id),
                erpDesignStyleMapper.sumQuantityById(id),
                erpDesignStyleMapper.selectMaterialSet(id)
        );
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param listDesignPatternsRequest 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<ErpDesignPatterns> selectErpDesignPatternsList(ListDesignPatternsRequest listDesignPatternsRequest)
    {
        ErpDesignPatterns erpDesignPatterns=new ErpDesignPatterns(listDesignPatternsRequest.getProductId(), listDesignPatternsRequest.getCreateUserId(), listDesignPatternsRequest.getOrderId(), listDesignPatternsRequest.getConfirm());
        return erpDesignPatternsMapper.selectErpDesignPatternsList(erpDesignPatterns);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param addDesignPatternsRequest 新增设计总表请求
     * @return 结果
     */
    @Override
    public int insertErpDesignPatterns(AddDesignPatternsRequest addDesignPatternsRequest)
    {
        // 获取当前登录用户信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 直接获取用户ID
        Long userId = loginUser.getUserId();

        ErpDesignPatterns erpDesignPatterns=new ErpDesignPatterns(addDesignPatternsRequest.getProductId(),userId, addDesignPatternsRequest.getOrderId());
        erpDesignPatterns.setCreateTime(DateUtils.getNowDate());

        return erpDesignPatternsMapper.insert(erpDesignPatterns);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param updateDesignPatternsRequest 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateErpDesignPatterns(UpdateDesignPatternsRequest updateDesignPatternsRequest)
    {
        ErpDesignPatterns erpDesignPatterns =new ErpDesignPatterns(updateDesignPatternsRequest.getId(),updateDesignPatternsRequest.getProductId(),null, updateDesignPatternsRequest.getOrderId());
        return erpDesignPatternsMapper.updateById(erpDesignPatterns);
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

    public int confirmErpDesignPatternsById(Long id){
        return erpDesignPatternsMapper.confirmErpDesignPatternsById(id);
    }

    public int cancelConfirmById(Long id){
        return erpDesignPatternsMapper.cancelConfirmById(id);
    }

}
