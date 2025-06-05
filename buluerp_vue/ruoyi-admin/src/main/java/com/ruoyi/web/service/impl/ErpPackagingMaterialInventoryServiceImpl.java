package com.ruoyi.web.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.web.domain.ErpPackagingMaterialInventory;
import com.ruoyi.web.domain.ErpPackagingMaterialInventoryChange;
import com.ruoyi.web.domain.ErpPartInventory;
import com.ruoyi.web.domain.ErpPartInventoryChange;
import com.ruoyi.web.mapper.ErpPackagingMaterialInventoryChangeMapper;
import com.ruoyi.web.mapper.ErpPackagingMaterialInventoryMapper;
import com.ruoyi.web.request.Inventory.AddPackagingMaterialRequest;
import com.ruoyi.web.request.Inventory.ListPackagingMaterialRequest;
import com.ruoyi.web.request.Inventory.UpdatePackagingMaterialRequest;
import com.ruoyi.web.service.IErpPackagingMaterialInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ErpPackagingMaterialInventoryServiceImpl extends ServiceImpl<ErpPackagingMaterialInventoryChangeMapper, ErpPackagingMaterialInventoryChange> implements IErpPackagingMaterialInventoryService {

    @Autowired
    private ErpPackagingMaterialInventoryChangeMapper erpPackagingMaterialInventoryChangeMapper;

    @Autowired
    private ErpPackagingMaterialInventoryMapper inventoryMapper;

    @Override
    public List<ErpPackagingMaterialInventoryChange> selectList(ListPackagingMaterialRequest request) {
        LambdaQueryWrapper<ErpPackagingMaterialInventoryChange> query = new LambdaQueryWrapper<>();
        query.eq(request.getId()!=null, ErpPackagingMaterialInventoryChange::getId,request.getId())
                .eq(StringUtils.isNotBlank(request.getOrderCode()), ErpPackagingMaterialInventoryChange::getOrderCode, request.getOrderCode())
                .like(StringUtils.isNotBlank(request.getOperator()), ErpPackagingMaterialInventoryChange::getOperator, request.getOperator())
                .eq(StringUtils.isNotBlank(request.getProductPartNumber()), ErpPackagingMaterialInventoryChange::getProductPartNumber, request.getProductPartNumber())
                .eq(StringUtils.isNotBlank(request.getEditAction()) , ErpPackagingMaterialInventoryChange::getEditAction, request.getEditAction())
                .like(StringUtils.isNotBlank(request.getPackagingNumber()), ErpPackagingMaterialInventoryChange::getPackagingNumber,request.getPackagingNumber())
                .eq(request.getInOutQuantity()!=null, ErpPackagingMaterialInventoryChange::getInOutQuantity,request.getInOutQuantity())
                .like(StringUtils.isNotBlank(request.getStorageLocation()), ErpPackagingMaterialInventoryChange::getStorageLocation,request.getStorageLocation())
                .like(StringUtils.isNotBlank(request.getRemarks()), ErpPackagingMaterialInventoryChange::getRemarks,request.getRemarks())
                .lt(request.getChangeDateTo()!=null, ErpPackagingMaterialInventoryChange::getChangeDate,request.getChangeDateTo())
                .lt(request.getCreateTimeTo()!=null, ErpPackagingMaterialInventoryChange::getCreationTime,request.getCreateTimeTo())
                .gt(request.getChangeDateFrom()!=null, ErpPackagingMaterialInventoryChange::getChangeDate,request.getChangeDateFrom())
                .gt(request.getCreateTimeFrom()!=null, ErpPackagingMaterialInventoryChange::getCreationTime,request.getCreateTimeFrom());
        return erpPackagingMaterialInventoryChangeMapper.selectList(query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRecord(AddPackagingMaterialRequest request) {
        ErpPackagingMaterialInventoryChange erpPackagingMaterialInventoryChange = new ErpPackagingMaterialInventoryChange.Builder()
                .orderCode(request.getOrderCode())
                .creationTime(LocalDateTime.now())
                .operator(SecurityUtils.getUsername())
                .changeDate(request.getChangeDate())
                .editAction(request.getEditAction())
                .productPartNumber(request.getProductPartNumber())
                .packagingNumber(request.getPackagingNumber())
                .inOutQuantity(request.getInOutQuantity())
                .storageLocation(request.getStorageLocation())
                .remarks(request.getRemarks())
                .build();

        erpPackagingMaterialInventoryChangeMapper.insert(erpPackagingMaterialInventoryChange);
        refresh(erpPackagingMaterialInventoryChange.getId());
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRecord(UpdatePackagingMaterialRequest request) {
        ErpPackagingMaterialInventoryChange erpPackagingMaterialInventoryChange = erpPackagingMaterialInventoryChangeMapper.selectById(request.getId());
        if (erpPackagingMaterialInventoryChange == null) {
            throw new RuntimeException("记录不存在");
        }

        // 更新时更改操作人
        erpPackagingMaterialInventoryChange.setOperator(SecurityUtils.getUsername());

        if (StringUtils.isNotBlank(request.getOrderCode())) {
            erpPackagingMaterialInventoryChange.setOrderCode(request.getOrderCode());
        }
        if (StringUtils.isNotBlank(request.getProductPartNumber())) {
            erpPackagingMaterialInventoryChange.setProductPartNumber(request.getProductPartNumber());
        }
        if (StringUtils.isNotBlank(request.getEditAction())) {
            erpPackagingMaterialInventoryChange.setEditAction(request.getEditAction());
        }
        if (request.getInOutQuantity() != null) {
            erpPackagingMaterialInventoryChange.setInOutQuantity(request.getInOutQuantity());
        }
        if (StringUtils.isNotBlank(request.getStorageLocation())) {
            erpPackagingMaterialInventoryChange.setStorageLocation(request.getStorageLocation());
        }
        if (StringUtils.isNotBlank(request.getPackagingNumber())) {
            erpPackagingMaterialInventoryChange.setPackagingNumber(request.getPackagingNumber());
        }
        if (StringUtils.isNotBlank(request.getRemarks())) {
            erpPackagingMaterialInventoryChange.setRemarks(request.getRemarks());
        }
        if (request.getChangeDate()!=null){
            erpPackagingMaterialInventoryChange.setChangeDate(request.getChangeDate());
        }

        erpPackagingMaterialInventoryChangeMapper.updateById(erpPackagingMaterialInventoryChange);
        refresh(erpPackagingMaterialInventoryChange.getId());
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(List<Integer> ids) {
        // 先收集所有要删除记录的关键信息，用于后续刷新库存
        Set<String> refreshKeys = new HashSet<>();
        for(Integer id : ids) {
            ErpPackagingMaterialInventoryChange changeEntity = erpPackagingMaterialInventoryChangeMapper.selectById(id);
            if(changeEntity != null) {
                // 构建唯一键：orderCode + productPartNumber + packagingNumber
                String key = changeEntity.getOrderCode() + "|" + 
                           changeEntity.getProductPartNumber() + "|" + 
                           changeEntity.getPackagingNumber();
                refreshKeys.add(key);
            }
        }
        
        // 执行批量删除
        int result = erpPackagingMaterialInventoryChangeMapper.deleteBatchIds(ids);
        
        // 基于收集的信息刷新相关库存
        for(String key : refreshKeys) {
            String[] parts = key.split("\\|");
            if(parts.length == 3) {
                refreshByKey(parts[0], parts[1], parts[2]); // orderCode, productPartNumber, packagingNumber
            }
        }
        
        return result;
    }

    public List<ErpPackagingMaterialInventory> ListStore(ErpPackagingMaterialInventory erpPackagingMaterialInventory, LocalDateTime updateTimeFrom, LocalDateTime updateTimeTo){
        LambdaQueryWrapper<ErpPackagingMaterialInventory> wrapper= Wrappers.lambdaQuery();
        wrapper.like(StringUtils.isNotBlank(erpPackagingMaterialInventory.getProductPartNumber()),ErpPackagingMaterialInventory::getProductPartNumber,erpPackagingMaterialInventory.getProductPartNumber())
                .like(StringUtils.isNotBlank(erpPackagingMaterialInventory.getOrderCode()),ErpPackagingMaterialInventory::getOrderCode,erpPackagingMaterialInventory.getOrderCode())
                .like(StringUtils.isNotBlank(erpPackagingMaterialInventory.getPackingNumber()),ErpPackagingMaterialInventory::getPackingNumber,erpPackagingMaterialInventory.getPackingNumber())
                .lt(updateTimeTo!=null,ErpPackagingMaterialInventory::getUpdateTime,updateTimeTo)
                .gt(updateTimeFrom!=null,ErpPackagingMaterialInventory::getUpdateTime,updateTimeFrom);
        return inventoryMapper.selectList(wrapper);
    }

    private void refresh(Integer id) throws RuntimeException{
        ErpPackagingMaterialInventoryChange changeEntity = erpPackagingMaterialInventoryChangeMapper.selectById(id);
        String orderCode=changeEntity.getOrderCode();
        String productPartNumber=changeEntity.getProductPartNumber();
        String packingNumber=changeEntity.getPackagingNumber();

        //分别构造查询出入库条件
        LambdaQueryWrapper<ErpPackagingMaterialInventoryChange> inWrapper= Wrappers.lambdaQuery();
        inWrapper.eq(ErpPackagingMaterialInventoryChange::getOrderCode,orderCode)
                .eq(ErpPackagingMaterialInventoryChange::getPackagingNumber,packingNumber)
                .eq(ErpPackagingMaterialInventoryChange::getProductPartNumber,productPartNumber)
                .gt(ErpPackagingMaterialInventoryChange::getInOutQuantity,0);
        LambdaQueryWrapper<ErpPackagingMaterialInventoryChange> outWrapper= Wrappers.lambdaQuery();
        outWrapper.eq(ErpPackagingMaterialInventoryChange::getOrderCode,orderCode)
                .eq(ErpPackagingMaterialInventoryChange::getPackagingNumber,packingNumber)
                .eq(ErpPackagingMaterialInventoryChange::getProductPartNumber,productPartNumber)
                .lt(ErpPackagingMaterialInventoryChange::getInOutQuantity,0);

        ErpPackagingMaterialInventory erpPackagingMaterialInventory=new ErpPackagingMaterialInventory();
        Integer inQuantity = erpPackagingMaterialInventoryChangeMapper.sumQuantity(inWrapper);
        Integer outQuantity = erpPackagingMaterialInventoryChangeMapper.sumQuantity(outWrapper);
        erpPackagingMaterialInventory.setInQuantity(inQuantity != null ? inQuantity : 0);
        erpPackagingMaterialInventory.setOutQuantity(outQuantity != null ? outQuantity : 0);
        erpPackagingMaterialInventory.total();
        erpPackagingMaterialInventory.setOrderCode(changeEntity.getOrderCode());
        erpPackagingMaterialInventory.setPackingNumber(changeEntity.getPackagingNumber());
        erpPackagingMaterialInventory.setProductPartNumber(changeEntity.getProductPartNumber());
        erpPackagingMaterialInventory.setUpdateTime(LocalDateTime.now());
        LambdaQueryWrapper<ErpPackagingMaterialInventory> inventoryWrapper= Wrappers.lambdaQuery();
        inventoryWrapper.eq(ErpPackagingMaterialInventory::getPackingNumber,packingNumber)
                .eq(ErpPackagingMaterialInventory::getProductPartNumber,productPartNumber)
                .eq(ErpPackagingMaterialInventory::getOrderCode,orderCode);
        ErpPackagingMaterialInventory preInventory= inventoryMapper.selectOne(inventoryWrapper);
        if(preInventory==null){
            inventoryMapper.insert(erpPackagingMaterialInventory);
        }
        else {
            erpPackagingMaterialInventory.setId(preInventory.getId());
            inventoryMapper.updateById(erpPackagingMaterialInventory);
        }
    }

    /**
     * 基于关键信息刷新库存（用于删除后的库存更新）
     */
    private void refreshByKey(String orderCode, String productPartNumber, String packingNumber) throws RuntimeException{
        // 分别构造查询出入库条件
        LambdaQueryWrapper<ErpPackagingMaterialInventoryChange> inWrapper = Wrappers.lambdaQuery();
        inWrapper.eq(ErpPackagingMaterialInventoryChange::getOrderCode, orderCode)
                .eq(ErpPackagingMaterialInventoryChange::getPackagingNumber, packingNumber)
                .eq(ErpPackagingMaterialInventoryChange::getProductPartNumber, productPartNumber)
                .gt(ErpPackagingMaterialInventoryChange::getInOutQuantity, 0);
        
        LambdaQueryWrapper<ErpPackagingMaterialInventoryChange> outWrapper = Wrappers.lambdaQuery();
        outWrapper.eq(ErpPackagingMaterialInventoryChange::getOrderCode, orderCode)
                .eq(ErpPackagingMaterialInventoryChange::getPackagingNumber, packingNumber)
                .eq(ErpPackagingMaterialInventoryChange::getProductPartNumber, productPartNumber)
                .lt(ErpPackagingMaterialInventoryChange::getInOutQuantity, 0);

        // 查询现有库存记录
        LambdaQueryWrapper<ErpPackagingMaterialInventory> inventoryWrapper = Wrappers.lambdaQuery();
        inventoryWrapper.eq(ErpPackagingMaterialInventory::getPackingNumber, packingNumber)
                .eq(ErpPackagingMaterialInventory::getProductPartNumber, productPartNumber)
                .eq(ErpPackagingMaterialInventory::getOrderCode, orderCode);
        ErpPackagingMaterialInventory preInventory = inventoryMapper.selectOne(inventoryWrapper);

        // 重新计算库存数量
        Integer inQuantity = erpPackagingMaterialInventoryChangeMapper.sumQuantity(inWrapper);
        Integer outQuantity = erpPackagingMaterialInventoryChangeMapper.sumQuantity(outWrapper);
        
        // 如果没有相关的出入库记录了，删除库存记录
        if ((inQuantity == null || inQuantity == 0) && (outQuantity == null || outQuantity == 0)) {
            if (preInventory != null) {
                inventoryMapper.deleteById(preInventory.getId());
            }
            return;
        }

        // 更新或创建库存记录
        ErpPackagingMaterialInventory erpPackagingMaterialInventory = new ErpPackagingMaterialInventory();
        erpPackagingMaterialInventory.setInQuantity(inQuantity != null ? inQuantity : 0);
        erpPackagingMaterialInventory.setOutQuantity(outQuantity != null ? outQuantity : 0);
        erpPackagingMaterialInventory.total();
        erpPackagingMaterialInventory.setOrderCode(orderCode);
        erpPackagingMaterialInventory.setPackingNumber(packingNumber);
        erpPackagingMaterialInventory.setProductPartNumber(productPartNumber);
        erpPackagingMaterialInventory.setUpdateTime(LocalDateTime.now());

        if (preInventory == null) {
            inventoryMapper.insert(erpPackagingMaterialInventory);
        } else {
            erpPackagingMaterialInventory.setId(preInventory.getId());
            inventoryMapper.updateById(erpPackagingMaterialInventory);
        }
    }

}