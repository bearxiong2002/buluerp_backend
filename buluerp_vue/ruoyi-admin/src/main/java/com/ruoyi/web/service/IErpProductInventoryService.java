package com.ruoyi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.web.domain.ErpPartInventory;
import com.ruoyi.web.domain.ErpProductInventory;
import com.ruoyi.web.domain.ErpProductInventoryChange;
import com.ruoyi.web.request.Inventory.AddProductInventoryRequest;
import com.ruoyi.web.request.Inventory.ListProductInventoryRequest;
import com.ruoyi.web.request.Inventory.UpdateProductInventoryRequest;

import java.util.Date;
import java.util.List;

public interface IErpProductInventoryService extends IService<ErpProductInventoryChange> {
    List<ErpProductInventoryChange> selectList(ListProductInventoryRequest request);
    int insertRecord(AddProductInventoryRequest request);
    int updateRecord(UpdateProductInventoryRequest request);
    int deleteByIds(List<Integer> ids);
    List<ErpProductInventory> ListStore(ErpProductInventory erpProductInventory, Date updateTimeFrom, Date updateTimeTo);
    
    /**
     * 根据ID列表查询出入库记录
     * @param ids ID列表
     * @return 出入库记录列表
     */
    List<ErpProductInventoryChange> selectListByIds(List<Integer> ids);
    
    /**
     * 根据ID列表查询库存记录
     * @param ids ID列表
     * @return 库存记录列表
     */
    List<ErpProductInventory> selectStoreByIds(List<Long> ids);
}