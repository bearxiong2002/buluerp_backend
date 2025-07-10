package com.ruoyi.web.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.web.domain.ErpOrders;
import com.ruoyi.web.domain.ErpOrdersProduct;
import com.ruoyi.web.enums.OrderStatus;
import com.ruoyi.web.mapper.ErpDesignStyleMapper;
import com.ruoyi.web.mapper.ErpOrdersMapper;
import com.ruoyi.web.mapper.ErpProductsMapper;
import com.ruoyi.web.request.design.AddDesignPatternsRequest;
import com.ruoyi.web.domain.ErpDesignPatterns;
import com.ruoyi.web.mapper.ErpDesignPatternsMapper;
import com.ruoyi.web.request.design.ListDesignPatternsRequest;
import com.ruoyi.web.request.design.UpdateDesignPatternsRequest;
import com.ruoyi.web.result.DesignPatternsResult;
import com.ruoyi.web.service.IErpDesignPatternsService;
import com.ruoyi.web.service.IErpOrdersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.web.annotation.MarkNotificationsAsRead;


/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
@Service
public class ErpDesignPatternsServiceImpl extends ServiceImpl<ErpDesignPatternsMapper,ErpDesignPatterns> implements IErpDesignPatternsService
{
    private static final Logger log = LoggerFactory.getLogger(ErpDesignPatternsServiceImpl.class);

    @Autowired
    private ErpDesignPatternsMapper erpDesignPatternsMapper;

    @Autowired
    private ErpDesignStyleMapper erpDesignStyleMapper;

    @Autowired
    private ErpProductsMapper erpProductsMapper;

    @Autowired
    private ErpOrdersMapper erpOrdersMapper;

    @Autowired
    private IErpOrdersService erpOrdersService;

    /**
     * 查询总表详情
     * 
     * @param productId productId
     * @return 造型表之和
     */
    @Override
    public DesignPatternsResult selectErpDesignPatternsById(Long productId)
    {
        return new DesignPatternsResult(
                productId,
                erpDesignStyleMapper.selectMouldNumberSet(productId),
                erpDesignStyleMapper.selectLddNumberSet(productId),
                erpDesignStyleMapper.selectMouldCategorySet(productId),
                erpDesignStyleMapper.selectMouldIdSet(productId),
                erpDesignStyleMapper.selectPictureUrlSet(productId),
                erpDesignStyleMapper.selectColorSet(productId),
                erpDesignStyleMapper.selectProductNameSet(productId),
                erpDesignStyleMapper.sumQuantityById(productId),
                erpDesignStyleMapper.selectMaterialSet(productId),
                erpDesignStyleMapper.selectConfirm(productId)
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
                .like(listDesignPatternsRequest.getProductId()!=null,ErpDesignPatterns::getProductId,listDesignPatternsRequest.getProductId())
                .orderByDesc(ErpDesignPatterns::getCreateTime)
                .eq(listDesignPatternsRequest.getCreateUserId()!=null,ErpDesignPatterns::getCreateUserId,listDesignPatternsRequest.getCreateUserId())
                .eq(listDesignPatternsRequest.getOrderId()!=null,ErpDesignPatterns::getOrderId,listDesignPatternsRequest.getOrderId())
                .like(listDesignPatternsRequest.getId()!=null,ErpDesignPatterns::getId,listDesignPatternsRequest.getId());
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
    @MarkNotificationsAsRead(businessType = "ORDER", businessIdsExpression = "T(java.util.Collections).singletonList(#addDesignPatternsRequest.orderId)")
    public int insertErpDesignPatterns(AddDesignPatternsRequest addDesignPatternsRequest)
    {
        // 获取当前登录用户信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 直接获取用户ID
        Long userId = loginUser.getUserId();

        ErpDesignPatterns erpDesignPatterns=new ErpDesignPatterns(addDesignPatternsRequest.getProductId(),userId, addDesignPatternsRequest.getOrderId());
        erpDesignPatterns.setCreateTime(LocalDateTime.now());

        int result = erpDesignPatternsMapper.insert(erpDesignPatterns);
        
        if (result > 0) {
            // 首先，将订单状态更新为“设计中”，表示设计工作已开始
            erpOrdersService.updateOrderStatusAutomatic(
                    erpDesignPatterns.getOrderId(),
                    OrderStatus.DESIGNED
            );

            // 然后，立刻检查该订单是否已满足所有产品均设计的条件
            checkAndUpdateRelatedOrders(erpDesignPatterns.getProductId());
        }

        return result;
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

    //弃用方法
    @Transactional(rollbackFor = Exception.class)
    public int confirmErpDesignPatternsById(Long id){
        ErpDesignPatterns erpDesignPatterns=erpDesignPatternsMapper.selectById(id);
        Long proId= erpDesignPatterns.getProductId();
        erpProductsMapper.updateStatusById(proId,1L);
        ErpOrders erpOrders=new ErpOrders();
        erpOrders.setStatus(2);
        erpOrders.setId(erpDesignPatterns.getOrderId());
        erpOrdersMapper.updateErpOrders(erpOrders);
        return erpDesignPatternsMapper.confirmErpDesignPatternsById(id);
    }


    //弃用方法
    @Transactional(rollbackFor = Exception.class)
    public int cancelConfirmById(Long id){
        ErpDesignPatterns erpDesignPatterns=erpDesignPatternsMapper.selectById(id);
        Long proId= erpDesignPatterns.getProductId();
        ErpOrders erpOrders=new ErpOrders();
        erpOrders.setStatus(1);
        erpOrders.setId(erpDesignPatterns.getOrderId());
        erpOrdersMapper.updateErpOrders(erpOrders);
        erpProductsMapper.updateStatusById(proId,0L);
        return erpDesignPatternsMapper.cancelConfirmById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public int confirmProduct(Long proId){
        int result = erpProductsMapper.updateStatusById(proId,1L);
        if (result > 0) {
            checkAndUpdateRelatedOrders(proId);
        }
        return result;
    }

    private void checkAndUpdateRelatedOrders(Long productId) {
        log.info("产品 {} 设计状态被确认，开始检查关联订单...", productId);
        
        // 1. 找出所有包含该产品且处于“设计中”状态的订单
        Integer designingStatusValue = erpOrdersService.getStatusValue(OrderStatus.DESIGNED.getLabel());
        List<ErpOrders> relatedOrders = erpDesignPatternsMapper.findOrdersByProductIdAndStatus(productId, designingStatusValue);

        for (ErpOrders order : relatedOrders) {
            try {
                // 2. 重新获取订单的完整信息，包括其所有产品
                ErpOrders fullOrder = erpOrdersService.selectErpOrdersById(order.getId());
                if (fullOrder == null || fullOrder.getProducts().isEmpty()) {
                    continue;
                }

                // 3. 检查该订单的所有产品是否都已设计完成
                boolean allProductsDesigned = fullOrder.getProducts().stream()
                        .map(ErpOrdersProduct::getProduct)
                        .allMatch(p -> p != null && Objects.equals(p.getDesignStatus(), 1));

                if (allProductsDesigned) {
                    log.info("订单 {} 的所有产品均已设计完成，自动更新状态至“待计划”。", fullOrder.getInnerId());
                    // 4. 如果是，则自动更新订单状态
                    erpOrdersService.updateOrderStatusAutomatic(fullOrder.getId(), OrderStatus.PURCHASE_PRODUCTION_PENDING);
                }
            } catch (Exception e) {
                log.error("自动更新订单 {} 状态失败，产品ID: {}", order.getInnerId(), productId, e);
                // 单个订单更新失败不影响其他订单
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public int cancelConfirmProductById(Long proId){
        return erpProductsMapper.updateStatusById(proId,0L);
    }
}
