package com.ruoyi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.web.domain.ErpProductInventoryChange;
import com.ruoyi.web.request.Inventory.AddProductInventoryRequest;
import com.ruoyi.web.request.Inventory.ListProductInventoryRequest;
import com.ruoyi.web.request.Inventory.UpdateProductInventoryRequest;

import java.util.List;

public interface IErpProductInventoryService extends IService<ErpProductInventoryChange> {
    List<ErpProductInventoryChange> selectList(ListProductInventoryRequest request);
    int insertRecord(AddProductInventoryRequest request);
    int updateRecord(UpdateProductInventoryRequest request);
    int deleteByIds(List<Integer> ids);
}