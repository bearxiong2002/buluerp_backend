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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    public int deleteByIds(List<Integer> ids) {
        for(Integer id:ids){
            refresh(id);
        }
        return erpPartInventoryChangeMapper.deleteBatchIds(ids);
    }

    public List<ErpPartInventory> ListStore(ErpPartInventory erpPartInventory,LocalDateTime updateTimeFrom,LocalDateTime updateTimeTo){
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
        erpPartInventory.setInQuantity(erpPartInventoryChangeMapper.sumQuantity(inWrapper));
        erpPartInventory.setOutQuantity(erpPartInventoryChangeMapper.sumQuantity(outWrapper));
        erpPartInventory.total();
        erpPartInventory.setOrderCode(changeEntity.getOrderCode());
        erpPartInventory.setMouldNumber(changeEntity.getMouldNumber());
        erpPartInventory.setUpdateTime(LocalDateTime.now());
        LambdaQueryWrapper<ErpPartInventory> inventoryWrapper= Wrappers.lambdaQuery();
        inventoryWrapper.eq(ErpPartInventory::getMouldNumber,mouldNumber)
                .eq(ErpPartInventory::getOrderCode,orderCode);
        ErpPartInventory preInventory=inventoryMapper.selectOne(inventoryWrapper);
        if(preInventory==null){
            inventoryMapper.insert(erpPartInventory);
        }
        else {
            erpPartInventory.setId(preInventory.getId());
            inventoryMapper.updateById(erpPartInventory);
        }
    }
}