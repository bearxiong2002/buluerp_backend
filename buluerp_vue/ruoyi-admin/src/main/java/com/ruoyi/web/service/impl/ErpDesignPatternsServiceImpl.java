package com.ruoyi.web.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.web.mapper.ErpDesignStyleMapper;
import com.ruoyi.web.mapper.ErpProductsMapper;
import com.ruoyi.web.request.design.AddDesignPatternsRequest;
import com.ruoyi.web.domain.ErpDesignPatterns;
import com.ruoyi.web.mapper.ErpDesignPatternsMapper;
import com.ruoyi.web.request.design.ListDesignPatternsRequest;
import com.ruoyi.web.request.design.UpdateDesignPatternsRequest;
import com.ruoyi.web.result.DesignPatternsResult;
import com.ruoyi.web.service.IErpDesignPatternsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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

    @Autowired
    private ErpProductsMapper erpProductsMapper;

    /**
     * 查询总表详情
     * 
     * @param id design_pattern_id
     * @return 造型表之和
     */
    @Override
    public DesignPatternsResult selectErpDesignPatternsById(Long id)
    {
        ErpDesignPatterns erpDesignPatterns=erpDesignPatternsMapper.selectById(id);
        Long productId=erpDesignPatterns.getProductId();
        return new DesignPatternsResult(
                id,
                erpDesignStyleMapper.selectMouldNumberSet(productId),
                erpDesignStyleMapper.selectLddNumberSet(productId),
                erpDesignStyleMapper.selectMouldCategorySet(productId),
                erpDesignStyleMapper.selectMouldIdSet(productId),
                erpDesignStyleMapper.selectPictureUrlSet(productId),
                erpDesignStyleMapper.selectColorSet(productId),
                erpDesignStyleMapper.selectProductNameSet(productId),
                erpDesignStyleMapper.sumQuantityById(productId),
                erpDesignStyleMapper.selectMaterialSet(productId),
                erpDesignPatterns.getConfirm()
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
        LambdaQueryWrapper<ErpDesignPatterns> wrapper= Wrappers.lambdaQuery();
        wrapper.eq(listDesignPatternsRequest.getConfirm()!=null,ErpDesignPatterns::getConfirm,listDesignPatternsRequest.getConfirm())
                .eq(listDesignPatternsRequest.getProductId()!=null,ErpDesignPatterns::getProductId,listDesignPatternsRequest.getProductId())
                .eq(listDesignPatternsRequest.getCreateUserId()!=null,ErpDesignPatterns::getCreateUserId,listDesignPatternsRequest.getCreateUserId())
                .eq(listDesignPatternsRequest.getOrderId()!=null,ErpDesignPatterns::getOrderId,listDesignPatternsRequest.getOrderId());
        return erpDesignPatternsMapper.selectList(wrapper);
    }

    @Override
    public List<ErpDesignPatterns> selectErpDesignPatternsListByIds(Long[] ids) {
        return erpDesignPatternsMapper.selectErpDesignPatternsListByIds(ids);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param addDesignPatternsRequest 新增设计总表请求
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertErpDesignPatterns(AddDesignPatternsRequest addDesignPatternsRequest)
    {
        // 获取当前登录用户信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 直接获取用户ID
        Long userId = loginUser.getUserId();

        ErpDesignPatterns erpDesignPatterns=new ErpDesignPatterns(addDesignPatternsRequest.getProductId(),userId, addDesignPatternsRequest.getOrderId());
        erpDesignPatterns.setCreateTime(LocalDateTime.now());

        return erpDesignPatternsMapper.insert(erpDesignPatterns);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param updateDesignPatternsRequest 【请填写功能名称】
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateErpDesignPatterns(UpdateDesignPatternsRequest updateDesignPatternsRequest)
    {
        ErpDesignPatterns erpDesignPatterns =new ErpDesignPatterns();
        erpDesignPatterns.setId(updateDesignPatternsRequest.getId());
        erpDesignPatterns.setOrderId(updateDesignPatternsRequest.getOrderId());
        erpDesignPatterns.setProductId(updateDesignPatternsRequest.getProductId());
        return erpDesignPatternsMapper.updateById(erpDesignPatterns);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteErpDesignPatternsByIds(List<Integer> ids)
    {
        return erpDesignPatternsMapper.deleteBatchIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteErpDesignPatternsById(Long id)
    {
        return erpDesignPatternsMapper.deleteErpDesignPatternsById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public int confirmErpDesignPatternsById(Long id){
        Long proId= erpDesignPatternsMapper.selectById(id).getProductId();
        erpProductsMapper.updateStatusById(proId,1L);
        return erpDesignPatternsMapper.confirmErpDesignPatternsById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public int cancelConfirmById(Long id){
        Long proId= erpDesignPatternsMapper.selectById(id).getProductId();
        erpProductsMapper.updateStatusById(proId,0L);
        return erpDesignPatternsMapper.cancelConfirmById(id);
    }

}
