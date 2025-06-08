package com.ruoyi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.web.domain.ErpPartInventory;
import com.ruoyi.web.domain.ErpPartInventoryChange;
import com.ruoyi.web.request.Inventory.AddPartInventoryRequest;
import com.ruoyi.web.request.Inventory.ListPartInventoryRequest;
import com.ruoyi.web.request.Inventory.UpdatePartInventoryRequest;

import java.util.Date;
import java.util.List;

public interface IErpPartInventoryService extends IService<ErpPartInventoryChange> {
    List<ErpPartInventoryChange> selectList(ListPartInventoryRequest request);
    int insertRecord(AddPartInventoryRequest request);
    int updateRecord(UpdatePartInventoryRequest request);
    int deleteByIds(List<Integer> ids);
    List<ErpPartInventory> ListStore(ErpPartInventory erpPartInventory, Date updateTimeFrom, Date updateTimeTo);
    
    /**
     * 根据库存ID修改安全库存阈值
     * @param inventoryId 库存记录ID
     * @param safeQuantity 新的安全库存阈值
     * @return 影响行数
     */
    int updateSafeQuantity(Long inventoryId, Integer safeQuantity);
    
    /**
     * 根据ID列表查询出入库记录
     * @param ids ID列表
     * @return 出入库记录列表
     */
    List<ErpPartInventoryChange> selectListByIds(List<Integer> ids);
    
    /**
     * 根据ID列表查询库存记录
     * @param ids ID列表
     * @return 库存记录列表
     */
    List<ErpPartInventory> selectStoreByIds(List<Long> ids);
}