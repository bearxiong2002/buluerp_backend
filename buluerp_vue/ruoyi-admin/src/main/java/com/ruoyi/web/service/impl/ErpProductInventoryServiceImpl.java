package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.web.domain.*;
import com.ruoyi.web.mapper.ErpProductInventoryChangeMapper;
import com.ruoyi.web.mapper.ErpProductInventoryMapper;
import com.ruoyi.web.request.Inventory.AddProductInventoryRequest;
import com.ruoyi.web.request.Inventory.ListProductInventoryRequest;
import com.ruoyi.web.request.Inventory.UpdateProductInventoryRequest;
import com.ruoyi.web.service.IErpProductInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.web.service.IErpNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.ArrayList;

@Service
public class ErpProductInventoryServiceImpl extends ServiceImpl<ErpProductInventoryChangeMapper, ErpProductInventoryChange> implements IErpProductInventoryService {

    private static final Logger log = LoggerFactory.getLogger(ErpProductInventoryServiceImpl.class);

    @Autowired
    private ErpProductInventoryChangeMapper erpProductInventoryChangeMapper;

    @Autowired
    private ErpProductInventoryMapper inventoryMapper;

    @Autowired
    private IErpNotificationService notificationService;

    @Override
    public List<ErpProductInventoryChange> selectList(ListProductInventoryRequest request) {
        LambdaQueryWrapper<ErpProductInventoryChange> query = new LambdaQueryWrapper<>();
        query.eq(request.getId() != null, ErpProductInventoryChange::getId, request.getId())
                .eq(StringUtils.isNotBlank(request.getOrderCode()), ErpProductInventoryChange::getOrderCode, request.getOrderCode())
                .like(StringUtils.isNotBlank(request.getOperator()), ErpProductInventoryChange::getOperator, request.getOperator())
                .eq(StringUtils.isNotBlank(request.getProductPartNumber()), ErpProductInventoryChange::getProductPartNumber, request.getProductPartNumber())
                .eq(request.getInOutQuantity() != null, ErpProductInventoryChange::getInOutQuantity, request.getInOutQuantity())
                .like(StringUtils.isNotBlank(request.getStorageLocation()), ErpProductInventoryChange::getStorageLocation, request.getStorageLocation())
                .like(StringUtils.isNotBlank(request.getRemarks()), ErpProductInventoryChange::getRemarks, request.getRemarks())
                .lt(request.getChangeDateTo()!=null, ErpProductInventoryChange::getChangeDate,request.getChangeDateTo())
                .lt(request.getCreateTimeTo()!=null, ErpProductInventoryChange::getCreationTime,request.getCreateTimeTo())
                .gt(request.getChangeDateFrom()!=null, ErpProductInventoryChange::getChangeDate,request.getChangeDateFrom())
                .gt(request.getCreateTimeFrom()!=null, ErpProductInventoryChange::getCreationTime,request.getCreateTimeFrom());
        List<ErpProductInventoryChange> list = erpProductInventoryChangeMapper.selectList(query);
        
        // 为每条记录填充总库存数量
        for (ErpProductInventoryChange item : list) {
            Integer totalQuantity = getCurrentTotalQuantity(item.getProductPartNumber());
            item.setTotalQuantity(totalQuantity);
        }
        
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRecord(AddProductInventoryRequest request) {
        ErpProductInventoryChange entity = new ErpProductInventoryChange();
        entity.setOrderCode(request.getOrderCode());
        entity.setProductPartNumber(request.getProductPartNumber());
        entity.setInOutQuantity(request.getInOutQuantity());
        entity.setStorageLocation(request.getStorageLocation());
        entity.setRemarks(request.getRemarks());
        entity.setCreationTime(LocalDateTime.now());
        entity.setOperator(SecurityUtils.getUsername());
        entity.setChangeDate(request.getChangeDate());
        erpProductInventoryChangeMapper.insert(entity);
        refresh(entity.getId());

        // 如果是出库操作（数量为负），则标记关联的包装清单通知为已读
        if (request.getInOutQuantity() < 0 && StringUtils.isNotBlank(request.getOrderCode())) {
            try {
                Long packagingListId = Long.parseLong(request.getOrderCode());
                // 这里的 "packaging" 是业务类型，需要与审核和通知模块中定义的一致
                notificationService.markNotificationsAsReadByBusiness(packagingListId, "packaging");
                log.info("出库操作：已标记业务类型 'packaging'，业务ID '{}' 的相关通知为已读。", packagingListId);
            } catch (NumberFormatException e) {
                log.error("出库操作标记通知已读失败：无法将 orderCode '{}' 转换为Long。", request.getOrderCode(), e);
            } catch (Exception e) {
                log.error("出库操作标记通知已读时发生未知错误。", e);
            }
        }

        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRecord(UpdateProductInventoryRequest request) {
        ErpProductInventoryChange entity = erpProductInventoryChangeMapper.selectById(request.getId());
        if (entity == null) throw new RuntimeException("记录不存在");

        entity.setOperator(SecurityUtils.getUsername());
        Optional.ofNullable(request.getOrderCode()).filter(StringUtils::isNotBlank).ifPresent(entity::setOrderCode);
        Optional.ofNullable(request.getProductPartNumber()).filter(StringUtils::isNotBlank).ifPresent(entity::setProductPartNumber);
        Optional.ofNullable(request.getInOutQuantity()).ifPresent(entity::setInOutQuantity);
        Optional.ofNullable(request.getStorageLocation()).filter(StringUtils::isNotBlank).ifPresent(entity::setStorageLocation);
        Optional.ofNullable(request.getRemarks()).filter(StringUtils::isNotBlank).ifPresent(entity::setRemarks);
        Optional.ofNullable(request.getChangeDate()).ifPresent(entity::setChangeDate);
        erpProductInventoryChangeMapper.updateById(entity);
        refresh(entity.getId());
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(List<Integer> ids) {
        // 先收集所有要删除记录的关键信息，用于后续刷新库存
        Set<String> refreshKeys = new HashSet<>();
        for(Integer id : ids) {
            ErpProductInventoryChange changeEntity = erpProductInventoryChangeMapper.selectById(id);
            if(changeEntity != null) {
                // 只使用产品货号作为唯一键
                refreshKeys.add(changeEntity.getProductPartNumber());
            }
        }
        
        // 执行批量删除
        int result = erpProductInventoryChangeMapper.deleteBatchIds(ids);
        
        // 基于收集的信息刷新相关库存
        for(String productPartNumber : refreshKeys) {
            refreshByKey(productPartNumber);
        }
        
        return result;
    }

    /**
     * 获取当前总库存数量
     * @param productPartNumber 产品货号
     * @return 当前总库存数量
     */
    private Integer getCurrentTotalQuantity(String productPartNumber) {
        LambdaQueryWrapper<ErpProductInventory> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ErpProductInventory::getProductPartNumber, productPartNumber);
        ErpProductInventory inventory = inventoryMapper.selectOne(wrapper);
        return inventory != null ? inventory.getTotalQuantity() : 0;
    }

    @Override
    public List<ErpProductInventoryChange> selectListByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        List<ErpProductInventoryChange> list = erpProductInventoryChangeMapper.selectBatchIds(ids);
        
        // 为每条记录填充总库存数量
        for (ErpProductInventoryChange item : list) {
            Integer totalQuantity = getCurrentTotalQuantity(item.getProductPartNumber());
            item.setTotalQuantity(totalQuantity);
        }
        
        return list;
    }

    @Override
    public List<ErpProductInventory> selectStoreByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return inventoryMapper.selectBatchIds(ids);
    }

    public List<ErpProductInventory> ListStore(ErpProductInventory erpProductInventory, Date updateTimeFrom, Date updateTimeTo){
        LambdaQueryWrapper<ErpProductInventory> wrapper= Wrappers.lambdaQuery();
        wrapper.like(StringUtils.isNotBlank(erpProductInventory.getProductPartNumber()), ErpProductInventory::getProductPartNumber,erpProductInventory.getProductPartNumber())
                .le(updateTimeTo!=null,ErpProductInventory::getUpdateTime,updateTimeTo)
                .ge(updateTimeFrom!=null,ErpProductInventory::getUpdateTime,updateTimeFrom);
        return inventoryMapper.selectList(wrapper);
    }

    private void refresh(Integer id) throws RuntimeException{
        ErpProductInventoryChange changeEntity = erpProductInventoryChangeMapper.selectById(id);
        String productPartNumber=changeEntity.getProductPartNumber();

        //分别构造查询出入库条件
        LambdaQueryWrapper<ErpProductInventoryChange> inWrapper= Wrappers.lambdaQuery();
        inWrapper.eq(ErpProductInventoryChange::getProductPartNumber,productPartNumber)
                .gt(ErpProductInventoryChange::getInOutQuantity,0);
        LambdaQueryWrapper<ErpProductInventoryChange> outWrapper= Wrappers.lambdaQuery();
        outWrapper.eq(ErpProductInventoryChange::getProductPartNumber,productPartNumber)
                .lt(ErpProductInventoryChange::getInOutQuantity,0);

        ErpProductInventory erpProductInventory=new ErpProductInventory();
        Integer inQuantity = erpProductInventoryChangeMapper.sumQuantity(inWrapper);
        Integer outQuantity = erpProductInventoryChangeMapper.sumQuantity(outWrapper);
        erpProductInventory.setInQuantity(inQuantity != null ? inQuantity : 0);
        erpProductInventory.setOutQuantity(outQuantity != null ? outQuantity : 0);
        erpProductInventory.total();
        erpProductInventory.setProductPartNumber(changeEntity.getProductPartNumber());
        erpProductInventory.setUpdateTime(LocalDateTime.now());
        LambdaQueryWrapper<ErpProductInventory> inventoryWrapper= Wrappers.lambdaQuery();
        inventoryWrapper.eq(ErpProductInventory::getProductPartNumber,productPartNumber);
        ErpProductInventory preInventory= inventoryMapper.selectOne(inventoryWrapper);
        if(preInventory==null){
            inventoryMapper.insert(erpProductInventory);
        }
        else {
            erpProductInventory.setId(preInventory.getId());
            inventoryMapper.updateById(erpProductInventory);
        }
    }

    /**
     * 基于关键信息刷新库存（用于删除后的库存更新）
     */
    private void refreshByKey(String productPartNumber) throws RuntimeException{
        // 分别构造查询出入库条件
        LambdaQueryWrapper<ErpProductInventoryChange> inWrapper = Wrappers.lambdaQuery();
        inWrapper.eq(ErpProductInventoryChange::getProductPartNumber, productPartNumber)
                .gt(ErpProductInventoryChange::getInOutQuantity, 0);
        
        LambdaQueryWrapper<ErpProductInventoryChange> outWrapper = Wrappers.lambdaQuery();
        outWrapper.eq(ErpProductInventoryChange::getProductPartNumber, productPartNumber)
                .lt(ErpProductInventoryChange::getInOutQuantity, 0);

        // 查询现有库存记录
        LambdaQueryWrapper<ErpProductInventory> inventoryWrapper = Wrappers.lambdaQuery();
        inventoryWrapper.eq(ErpProductInventory::getProductPartNumber, productPartNumber);
        ErpProductInventory preInventory = inventoryMapper.selectOne(inventoryWrapper);

        // 重新计算库存数量
        Integer inQuantity = erpProductInventoryChangeMapper.sumQuantity(inWrapper);
        Integer outQuantity = erpProductInventoryChangeMapper.sumQuantity(outWrapper);
        
        // 如果没有相关的出入库记录了，删除库存记录
        if ((inQuantity == null || inQuantity == 0) && (outQuantity == null || outQuantity == 0)) {
            if (preInventory != null) {
                inventoryMapper.deleteById(preInventory.getId());
            }
            return;
        }

        // 更新或创建库存记录
        ErpProductInventory erpProductInventory = new ErpProductInventory();
        erpProductInventory.setInQuantity(inQuantity != null ? inQuantity : 0);
        erpProductInventory.setOutQuantity(outQuantity != null ? outQuantity : 0);
        erpProductInventory.total();
        erpProductInventory.setProductPartNumber(productPartNumber);
        erpProductInventory.setUpdateTime(LocalDateTime.now());

        if (preInventory == null) {
            inventoryMapper.insert(erpProductInventory);
        } else {
            erpProductInventory.setId(preInventory.getId());
            inventoryMapper.updateById(erpProductInventory);
        }
    }
}