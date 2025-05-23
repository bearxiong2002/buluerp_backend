package com.ruoyi.web.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.web.domain.ErpPackagingMaterialInventory;
import com.ruoyi.web.mapper.ErpPackagingMaterialInventoryMapper;
import com.ruoyi.web.request.Inventory.AddPackagingMaterialRequest;
import com.ruoyi.web.request.Inventory.ListPackagingMaterialRequest;
import com.ruoyi.web.request.Inventory.UpdatePackagingMaterialRequest;
import com.ruoyi.web.service.IErpPackagingMaterialInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class ErpPackagingMaterialInventoryServiceImpl extends ServiceImpl<ErpPackagingMaterialInventoryMapper, ErpPackagingMaterialInventory> implements IErpPackagingMaterialInventoryService {

    @Autowired
    private ErpPackagingMaterialInventoryMapper inventoryMapper;

    @Override
    public List<ErpPackagingMaterialInventory> selectList(ListPackagingMaterialRequest request) {
        LambdaQueryWrapper<ErpPackagingMaterialInventory> query = new LambdaQueryWrapper<>();
        query.eq(request.getId()!=null, ErpPackagingMaterialInventory::getId,request.getId())
                .eq(StringUtils.isNotBlank(request.getOrderCode()), ErpPackagingMaterialInventory::getOrderCode, request.getOrderCode())
                .like(StringUtils.isNotBlank(request.getOperator()), ErpPackagingMaterialInventory::getOperator, request.getOperator())
                .eq(StringUtils.isNotBlank(request.getProductPartNumber()), ErpPackagingMaterialInventory::getProductPartNumber, request.getProductPartNumber())
                .eq(StringUtils.isNotBlank(request.getEditAction()) , ErpPackagingMaterialInventory::getEditAction, request.getEditAction())
                .like(StringUtils.isNotBlank(request.getPackagingNumber()),ErpPackagingMaterialInventory::getPackagingNumber,request.getPackagingNumber())
                .eq(request.getInOutQuantity()!=null,ErpPackagingMaterialInventory::getInOutQuantity,request.getInOutQuantity())
                .like(StringUtils.isNotBlank(request.getStorageLocation()),ErpPackagingMaterialInventory::getStorageLocation,request.getStorageLocation())
                .like(StringUtils.isNotBlank(request.getRemarks()),ErpPackagingMaterialInventory::getRemarks,request.getRemarks())
                .lt(request.getChangeDateTo()!=null,ErpPackagingMaterialInventory::getChangeDate,request.getChangeDateTo())
                .lt(request.getCreateTimeTo()!=null,ErpPackagingMaterialInventory::getCreationTime,request.getCreateTimeTo())
                .gt(request.getChangeDateFrom()!=null,ErpPackagingMaterialInventory::getChangeDate,request.getChangeDateFrom())
                .gt(request.getCreateTimeFrom()!=null,ErpPackagingMaterialInventory::getCreationTime,request.getCreateTimeFrom());
        return inventoryMapper.selectList(query);
    }

    @Override
    public int insertRecord(AddPackagingMaterialRequest request) {
        // 获取当前登录用户信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 获取用户名
        String username = loginUser.getUser().getUserName();

        ErpPackagingMaterialInventory erpPackagingMaterialInventory = new ErpPackagingMaterialInventory.Builder()
                .orderCode(request.getOrderCode())
                .creationTime(LocalDateTime.now())
                .operator(username)
                .changeDate(request.getChangeDate())
                .editAction(request.getEditAction())
                .productPartNumber(request.getProductPartNumber())
                .packagingNumber(request.getPackagingNumber())
                .inOutQuantity(request.getInOutQuantity())
                .storageLocation(request.getStorageLocation())
                .remarks(request.getRemarks())
                .build();
        return inventoryMapper.insert(erpPackagingMaterialInventory);
    }

    @Override
    public int updateRecord(UpdatePackagingMaterialRequest request) {
        ErpPackagingMaterialInventory erpPackagingMaterialInventory = inventoryMapper.selectById(request.getId());
        if (erpPackagingMaterialInventory == null) {
            throw new RuntimeException("记录不存在");
        }

        /* 更新时更改操作人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUser().getUserName();
        erpPackagingMaterialInventory.setOperator(username);
        */

        if (StringUtils.isNotBlank(request.getOrderCode())) {
            erpPackagingMaterialInventory.setOrderCode(request.getOrderCode());
        }
        if (StringUtils.isNotBlank(request.getProductPartNumber())) {
            erpPackagingMaterialInventory.setProductPartNumber(request.getProductPartNumber());
        }
        if (StringUtils.isNotBlank(request.getEditAction())) {
            erpPackagingMaterialInventory.setEditAction(request.getEditAction());
        }
        if (request.getInOutQuantity() != null) {
            erpPackagingMaterialInventory.setInOutQuantity(request.getInOutQuantity());
        }
        if (StringUtils.isNotBlank(request.getStorageLocation())) {
            erpPackagingMaterialInventory.setStorageLocation(request.getStorageLocation());
        }
        if (StringUtils.isNotBlank(request.getPackagingNumber())) {
            erpPackagingMaterialInventory.setPackagingNumber(request.getPackagingNumber());
        }
        if (StringUtils.isNotBlank(request.getRemarks())) {
            erpPackagingMaterialInventory.setRemarks(request.getRemarks());
        }
        if (request.getChangeDate()!=null){
            erpPackagingMaterialInventory.setChangeDate(request.getChangeDate());
        }
        return inventoryMapper.updateById(erpPackagingMaterialInventory);
    }

    @Override
    public int deleteByIds(List<Integer> ids) {
        return inventoryMapper.deleteBatchIds(ids);
    }

}