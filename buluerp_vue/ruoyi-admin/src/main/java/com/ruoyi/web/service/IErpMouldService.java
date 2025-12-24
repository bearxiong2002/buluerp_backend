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

    void update(UpdateMouldRequest request);

    void remove(Long id);

    void removeBatch(List<Long> ids);
}
