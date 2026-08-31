package com.ruoyi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.web.domain.ErpMould;
import com.ruoyi.web.request.mould.AddMouldRequest;
import com.ruoyi.web.request.mould.ListMouldRequest;
import com.ruoyi.web.request.mould.UpdateMouldRequest;
import com.ruoyi.web.result.MouldInfoResult;

import java.util.List;

public interface IErpMouldService extends IService<ErpMould> {
    void checkUnique(ErpMould mould);

    void checkReferences(ErpMould mould);

    MouldInfoResult getMouldInfo(String mouldNumber);

    List<MouldInfoResult> list(ListMouldRequest request);

    void add(AddMouldRequest request);

    void addWithManufacturer(AddMouldRequest request);

    /**
     * 批量导入模具，并根据 Excel 中的厂商名称复用或创建厂商。
     *
     * @param requests 已完成字段校验的模具导入数据
     */
    void importWithManufacturer(List<AddMouldRequest> requests);

    void update(UpdateMouldRequest request);

    void remove(Long id);

    void removeBatch(List<Long> ids);
}
