package com.ruoyi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.web.domain.ErpMouldHouse;
import com.ruoyi.web.request.mouldhouse.AddMouldHouseRequest;
import com.ruoyi.web.request.mouldhouse.ListMouldHouseRequest;
import com.ruoyi.web.request.mouldhouse.UpdateMouldHouseRequest;

import java.util.List;

public interface IErpMouldHouseService extends IService<ErpMouldHouse> {
    void checkUnique(ErpMouldHouse mouldHouse);

    Long addChecked(AddMouldHouseRequest request);

    void updateChecked(UpdateMouldHouseRequest request);

    void removeChecked(Long id);

    void removeBatchChecked(List<Long> ids);

    List<ErpMouldHouse> search(ListMouldHouseRequest request);
}
