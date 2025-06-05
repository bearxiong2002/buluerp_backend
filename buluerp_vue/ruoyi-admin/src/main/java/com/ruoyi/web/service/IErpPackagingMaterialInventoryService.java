package com.ruoyi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.web.domain.ErpPackagingMaterialInventory;
import com.ruoyi.web.domain.ErpPackagingMaterialInventoryChange;
import com.ruoyi.web.request.Inventory.AddPackagingMaterialRequest;
import com.ruoyi.web.request.Inventory.ListPackagingMaterialRequest;
import com.ruoyi.web.request.Inventory.UpdatePackagingMaterialRequest;

import java.util.Date;
import java.util.List;

public interface IErpPackagingMaterialInventoryService extends IService<ErpPackagingMaterialInventoryChange> {

    List<ErpPackagingMaterialInventoryChange> selectList(ListPackagingMaterialRequest request);

    int insertRecord(AddPackagingMaterialRequest request);

    int updateRecord(UpdatePackagingMaterialRequest request);

    int deleteByIds(List<Integer> ids);

    List<ErpPackagingMaterialInventory> ListStore(ErpPackagingMaterialInventory erpPackagingMaterialInventory, Date updateTimeFrom, Date updateTimeTo);
}