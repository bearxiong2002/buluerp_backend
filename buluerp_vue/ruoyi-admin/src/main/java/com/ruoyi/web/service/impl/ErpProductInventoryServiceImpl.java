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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ErpProductInventoryServiceImpl extends ServiceImpl<ErpProductInventoryChangeMapper, ErpProductInventoryChange> implements IErpProductInventoryService {

    @Autowired
    private ErpProductInventoryChangeMapper erpProductInventoryChangeMapper;

    @Autowired
    private ErpProductInventoryMapper inventoryMapper;

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
        return erpProductInventoryChangeMapper.selectList(query);
    }

    @Override
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
        return 1;
    }

    @Override
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
    public int deleteByIds(List<Integer> ids) {
        for(Integer id:ids){
            refresh(id);
        }
        return erpProductInventoryChangeMapper.deleteBatchIds(ids);
    }

    public List<ErpProductInventory> ListStore(ErpProductInventory erpProductInventory, LocalDateTime updateTimeFrom, LocalDateTime updateTimeTo){
        LambdaQueryWrapper<ErpProductInventory> wrapper= Wrappers.lambdaQuery();
        wrapper.like(StringUtils.isNotBlank(erpProductInventory.getProductPartNumber()), ErpProductInventory::getProductPartNumber,erpProductInventory.getProductPartNumber())
                .like(StringUtils.isNotBlank(erpProductInventory.getOrderCode()),ErpProductInventory::getOrderCode,erpProductInventory.getOrderCode())
                .lt(updateTimeTo!=null,ErpProductInventory::getUpdateTime,updateTimeTo)
                .gt(updateTimeFrom!=null,ErpProductInventory::getUpdateTime,updateTimeFrom);
        return inventoryMapper.selectList(wrapper);
    }

    private void refresh(Integer id) throws RuntimeException{
        ErpProductInventoryChange changeEntity = erpProductInventoryChangeMapper.selectById(id);
        String orderCode=changeEntity.getOrderCode();
        String productPartNumber=changeEntity.getProductPartNumber();

        //分别构造查询出入库条件
        LambdaQueryWrapper<ErpProductInventoryChange> inWrapper= Wrappers.lambdaQuery();
        inWrapper.eq(ErpProductInventoryChange::getOrderCode,orderCode)
                .eq(ErpProductInventoryChange::getProductPartNumber,productPartNumber)
                .gt(ErpProductInventoryChange::getInOutQuantity,0);
        LambdaQueryWrapper<ErpProductInventoryChange> outWrapper= Wrappers.lambdaQuery();
        outWrapper.eq(ErpProductInventoryChange::getOrderCode,orderCode)
                .eq(ErpProductInventoryChange::getProductPartNumber,productPartNumber)
                .lt(ErpProductInventoryChange::getInOutQuantity,0);

        ErpProductInventory erpProductInventory=new ErpProductInventory();
        erpProductInventory.setInQuantity(erpProductInventoryChangeMapper.sumQuantity(inWrapper));
        erpProductInventory.setOutQuantity(erpProductInventoryChangeMapper.sumQuantity(outWrapper));
        erpProductInventory.total();
        erpProductInventory.setOrderCode(changeEntity.getOrderCode());
        erpProductInventory.setProductPartNumber(changeEntity.getProductPartNumber());
        erpProductInventory.setUpdateTime(LocalDateTime.now());
        LambdaQueryWrapper<ErpProductInventory> inventoryWrapper= Wrappers.lambdaQuery();
        inventoryWrapper.eq(ErpProductInventory::getProductPartNumber,productPartNumber)
                .eq(ErpProductInventory::getOrderCode,orderCode);
        ErpProductInventory preInventory= inventoryMapper.selectOne(inventoryWrapper);
        if(preInventory==null){
            inventoryMapper.insert(erpProductInventory);
        }
        else {
            erpProductInventory.setId(preInventory.getId());
            inventoryMapper.updateById(erpProductInventory);
        }
    }
}