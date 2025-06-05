package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.web.domain.ErpPackagingMaterialInventory;
import com.ruoyi.web.domain.ErpPartInventory;
import com.ruoyi.web.domain.ErpPartInventoryChange;
import com.ruoyi.web.mapper.ErpPartInventoryChangeMapper;
import com.ruoyi.web.mapper.ErpPartInventoryMapper;
import com.ruoyi.web.request.Inventory.AddPartInventoryRequest;
import com.ruoyi.web.request.Inventory.ListPartInventoryRequest;
import com.ruoyi.web.request.Inventory.UpdatePartInventoryRequest;
import com.ruoyi.web.service.IErpPartInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ErpPartInventoryServiceImpl extends ServiceImpl<ErpPartInventoryChangeMapper, ErpPartInventoryChange> implements IErpPartInventoryService {

    @Autowired
    private ErpPartInventoryChangeMapper erpPartInventoryChangeMapper;

    @Autowired
    private ErpPartInventoryMapper inventoryMapper;

    @Override
    public List<ErpPartInventoryChange> selectList(ListPartInventoryRequest request) {
        LambdaQueryWrapper<ErpPartInventoryChange> query = new LambdaQueryWrapper<>();
        query.eq(request.getId() != null, ErpPartInventoryChange::getId, request.getId())
                .eq(StringUtils.isNotBlank(request.getOrderCode()), ErpPartInventoryChange::getOrderCode, request.getOrderCode())
                .eq(StringUtils.isNotBlank(request.getMouldNumber()), ErpPartInventoryChange::getMouldNumber, request.getMouldNumber())
                .eq(StringUtils.isNotBlank(request.getColorCode()), ErpPartInventoryChange::getColorCode, request.getColorCode())
                .like(StringUtils.isNotBlank(request.getOperator()), ErpPartInventoryChange::getOperator, request.getOperator())
                .eq(request.getInOutQuantity() != null, ErpPartInventoryChange::getInOutQuantity, request.getInOutQuantity())
                .like(StringUtils.isNotBlank(request.getRemarks()), ErpPartInventoryChange::getRemarks, request.getRemarks())
                .lt(request.getChangeDateTo()!=null, ErpPartInventoryChange::getChangeDate,request.getChangeDateTo())
                .lt(request.getCreateTimeTo()!=null, ErpPartInventoryChange::getCreationTime,request.getCreateTimeTo())
                .gt(request.getChangeDateFrom()!=null, ErpPartInventoryChange::getChangeDate,request.getChangeDateFrom())
                .gt(request.getCreateTimeFrom()!=null, ErpPartInventoryChange::getCreationTime,request.getCreateTimeFrom());
        return erpPartInventoryChangeMapper.selectList(query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRecord(AddPartInventoryRequest request) {
        ErpPartInventoryChange entity = new ErpPartInventoryChange();
        entity.setOrderCode(request.getOrderCode());
        entity.setMouldNumber(request.getMouldNumber());
        entity.setColorCode(request.getColorCode());
        entity.setInOutQuantity(request.getInOutQuantity());
        entity.setRemarks(request.getRemarks());
        entity.setChangeDate(request.getChangeDate());
        entity.setCreationTime(LocalDateTime.now());
        entity.setOperator(SecurityUtils.getUsername());
        erpPartInventoryChangeMapper.insert(entity);
        refresh(entity.getId());
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRecord(UpdatePartInventoryRequest request) {
        ErpPartInventoryChange entity = erpPartInventoryChangeMapper.selectById(request.getId());
        if (entity == null) throw new RuntimeException("记录不存在");

        Optional.ofNullable(request.getOrderCode()).filter(StringUtils::isNotBlank).ifPresent(entity::setOrderCode);
        Optional.ofNullable(request.getMouldNumber()).filter(StringUtils::isNotBlank).ifPresent(entity::setMouldNumber);
        Optional.ofNullable(request.getColorCode()).filter(StringUtils::isNotBlank).ifPresent(entity::setColorCode);
        Optional.ofNullable(request.getInOutQuantity()).ifPresent(entity::setInOutQuantity);
        Optional.ofNullable(request.getRemarks()).filter(StringUtils::isNotBlank).ifPresent(entity::setRemarks);
        Optional.ofNullable(request.getChangeDate()).ifPresent(entity::setChangeDate);
        entity.setOperator(SecurityUtils.getUsername());
        erpPartInventoryChangeMapper.updateById(entity);
        refresh(entity.getId());

        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(List<Integer> ids) {
        // 先收集所有要删除记录的关键信息，用于后续刷新库存
        Set<String> refreshKeys = new HashSet<>();
        for(Integer id : ids) {
            ErpPartInventoryChange changeEntity = erpPartInventoryChangeMapper.selectById(id);
            if(changeEntity != null) {
                // 构建唯一键：orderCode + mouldNumber
                String key = changeEntity.getOrderCode() + "|" + changeEntity.getMouldNumber();
                refreshKeys.add(key);
            }
        }
        
        // 执行批量删除
        int result = erpPartInventoryChangeMapper.deleteBatchIds(ids);
        
        // 基于收集的信息刷新相关库存
        for(String key : refreshKeys) {
            String[] parts = key.split("\\|");
            if(parts.length == 2) {
                refreshByKey(parts[0], parts[1]); // orderCode, mouldNumber
            }
        }
        
        return result;
    }

    public List<ErpPartInventory> ListStore(ErpPartInventory erpPartInventory,Date updateTimeFrom,Date updateTimeTo){
        LambdaQueryWrapper<ErpPartInventory> wrapper= Wrappers.lambdaQuery();
        wrapper.like(StringUtils.isNotBlank(erpPartInventory.getMouldNumber()), ErpPartInventory::getMouldNumber,erpPartInventory.getMouldNumber())
                .like(StringUtils.isNotBlank(erpPartInventory.getOrderCode()),ErpPartInventory::getOrderCode,erpPartInventory.getOrderCode())
                .lt(updateTimeTo!=null,ErpPartInventory::getUpdateTime,updateTimeTo)
                .gt(updateTimeFrom!=null,ErpPartInventory::getUpdateTime,updateTimeFrom);
        return inventoryMapper.selectList(wrapper);
    }


    private void refresh(Integer id) throws RuntimeException{
        ErpPartInventoryChange changeEntity = erpPartInventoryChangeMapper.selectById(id);
        String orderCode=changeEntity.getOrderCode();
        String mouldNumber=changeEntity.getMouldNumber();
        LambdaQueryWrapper<ErpPartInventoryChange> inWrapper= Wrappers.lambdaQuery();
        inWrapper.eq(ErpPartInventoryChange::getOrderCode,orderCode)
                .eq(ErpPartInventoryChange::getMouldNumber,mouldNumber)
                .gt(ErpPartInventoryChange::getInOutQuantity,0);
        LambdaQueryWrapper<ErpPartInventoryChange> outWrapper= Wrappers.lambdaQuery();
        outWrapper.eq(ErpPartInventoryChange::getOrderCode,orderCode)
                .eq(ErpPartInventoryChange::getMouldNumber,mouldNumber)
                .lt(ErpPartInventoryChange::getInOutQuantity,0);
        ErpPartInventory erpPartInventory=new ErpPartInventory();
        Integer inQuantity = erpPartInventoryChangeMapper.sumQuantity(inWrapper);
        Integer outQuantity = erpPartInventoryChangeMapper.sumQuantity(outWrapper);
        erpPartInventory.setInQuantity(inQuantity != null ? inQuantity : 0);
        erpPartInventory.setOutQuantity(outQuantity != null ? outQuantity : 0);
        erpPartInventory.total();
        erpPartInventory.setOrderCode(changeEntity.getOrderCode());
        erpPartInventory.setMouldNumber(changeEntity.getMouldNumber());
        erpPartInventory.setUpdateTime(LocalDateTime.now());
        
        LambdaQueryWrapper<ErpPartInventory> inventoryWrapper= Wrappers.lambdaQuery();
        inventoryWrapper.eq(ErpPartInventory::getMouldNumber,mouldNumber)
                .eq(ErpPartInventory::getOrderCode,orderCode);
        ErpPartInventory preInventory=inventoryMapper.selectOne(inventoryWrapper);
        
        if(preInventory==null){
            // 新记录：默认安全库存为0，无预警
            erpPartInventory.setSafeQuantity(0);
            erpPartInventory.setWarning(0);
            inventoryMapper.insert(erpPartInventory);
        }
        else {
            // 更新记录：保留原有安全库存值，重新计算预警状态
            erpPartInventory.setId(preInventory.getId());
            erpPartInventory.setSafeQuantity(preInventory.getSafeQuantity());
            
            // 根据总库存和安全库存设置预警状态
            Integer totalQty = erpPartInventory.getTotalQuantity();
            Integer safeQty = preInventory.getSafeQuantity();
            if (totalQty != null && safeQty != null && totalQty < safeQty) {
                erpPartInventory.setWarning(1); // 库存不足预警
            } else {
                erpPartInventory.setWarning(0); // 库存正常
            }
            
            inventoryMapper.updateById(erpPartInventory);
        }
    }

    /**
     * 基于关键信息刷新库存（用于删除后的库存更新）
     */
    private void refreshByKey(String orderCode, String mouldNumber) throws RuntimeException{
        // 分别构造查询出入库条件
        LambdaQueryWrapper<ErpPartInventoryChange> inWrapper = Wrappers.lambdaQuery();
        inWrapper.eq(ErpPartInventoryChange::getOrderCode, orderCode)
                .eq(ErpPartInventoryChange::getMouldNumber, mouldNumber)
                .gt(ErpPartInventoryChange::getInOutQuantity, 0);
        
        LambdaQueryWrapper<ErpPartInventoryChange> outWrapper = Wrappers.lambdaQuery();
        outWrapper.eq(ErpPartInventoryChange::getOrderCode, orderCode)
                .eq(ErpPartInventoryChange::getMouldNumber, mouldNumber)
                .lt(ErpPartInventoryChange::getInOutQuantity, 0);

        // 查询现有库存记录
        LambdaQueryWrapper<ErpPartInventory> inventoryWrapper = Wrappers.lambdaQuery();
        inventoryWrapper.eq(ErpPartInventory::getMouldNumber, mouldNumber)
                .eq(ErpPartInventory::getOrderCode, orderCode);
        ErpPartInventory preInventory = inventoryMapper.selectOne(inventoryWrapper);

        // 重新计算库存数量
        Integer inQuantity = erpPartInventoryChangeMapper.sumQuantity(inWrapper);
        Integer outQuantity = erpPartInventoryChangeMapper.sumQuantity(outWrapper);
        
        // 如果没有相关的出入库记录了，删除库存记录
        if ((inQuantity == null || inQuantity == 0) && (outQuantity == null || outQuantity == 0)) {
            if (preInventory != null) {
                inventoryMapper.deleteById(preInventory.getId());
            }
            return;
        }

        // 更新或创建库存记录
        ErpPartInventory erpPartInventory = new ErpPartInventory();
        erpPartInventory.setInQuantity(inQuantity != null ? inQuantity : 0);
        erpPartInventory.setOutQuantity(outQuantity != null ? outQuantity : 0);
        erpPartInventory.total();
        erpPartInventory.setOrderCode(orderCode);
        erpPartInventory.setMouldNumber(mouldNumber);
        erpPartInventory.setUpdateTime(LocalDateTime.now());

        if (preInventory == null) {
            // 新记录：默认安全库存为0，无预警
            erpPartInventory.setSafeQuantity(0);
            erpPartInventory.setWarning(0);
            inventoryMapper.insert(erpPartInventory);
        } else {
            // 更新记录：保留原有安全库存值，重新计算预警状态
            erpPartInventory.setId(preInventory.getId());
            erpPartInventory.setSafeQuantity(preInventory.getSafeQuantity());
            
            // 根据总库存和安全库存设置预警状态
            Integer totalQty = erpPartInventory.getTotalQuantity();
            Integer safeQty = preInventory.getSafeQuantity();
            if (totalQty != null && safeQty != null && totalQty < safeQty) {
                erpPartInventory.setWarning(1); // 库存不足预警
            } else {
                erpPartInventory.setWarning(0); // 库存正常
            }
            
            inventoryMapper.updateById(erpPartInventory);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSafeQuantity(Long inventoryId, Integer safeQuantity) {
        // 查询库存记录
        ErpPartInventory inventory = inventoryMapper.selectById(inventoryId);
        if (inventory == null) {
            throw new RuntimeException("库存记录不存在");
        }
        
        // 更新安全库存
        inventory.setSafeQuantity(safeQuantity);
        
        // 重新计算预警状态
        Integer totalQty = inventory.getTotalQuantity();
        if (totalQty != null && safeQuantity != null && totalQty < safeQuantity) {
            inventory.setWarning(1); // 库存不足预警
        } else {
            inventory.setWarning(0); // 库存正常
        }
        
        inventory.setUpdateTime(LocalDateTime.now());
        return inventoryMapper.updateById(inventory);
    }
}