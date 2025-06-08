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
    
    /**
     * 根据ID列表查询出入库记录
     * @param ids ID列表
     * @return 出入库记录列表
     */
    List<ErpPackagingMaterialInventoryChange> selectListByIds(List<Integer> ids);
    
    /**
     * 根据ID列表查询库存记录
     * @param ids ID列表
     * @return 库存记录列表
     */
    List<ErpPackagingMaterialInventory> selectStoreByIds(List<Long> ids);
}