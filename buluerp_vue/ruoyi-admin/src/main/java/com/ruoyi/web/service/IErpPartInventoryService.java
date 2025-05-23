package com.ruoyi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.web.domain.ErpPartInventoryChange;
import com.ruoyi.web.request.Inventory.AddPartInventoryRequest;
import com.ruoyi.web.request.Inventory.ListPartInventoryRequest;
import com.ruoyi.web.request.Inventory.UpdatePartInventoryRequest;

import java.util.List;

public interface IErpPartInventoryService extends IService<ErpPartInventoryChange> {
    List<ErpPartInventoryChange> selectList(ListPartInventoryRequest request);
    int insertRecord(AddPartInventoryRequest request);
    int updateRecord(UpdatePartInventoryRequest request);
    int deleteByIds(List<Integer> ids);


}