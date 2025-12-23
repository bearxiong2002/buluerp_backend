package com.ruoyi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.web.domain.ErpMould;
import com.ruoyi.web.request.mould.AddMouldRequest;
import com.ruoyi.web.request.mould.ListMouldRequest;
import com.ruoyi.web.request.mould.UpdateMouldRequest;

import java.util.List;

public interface IErpMouldService extends IService<ErpMould> {
    void checkUnique(ErpMould mould);
    void checkReferences(ErpMould mould);

    List<ErpMould> list(ListMouldRequest request);
    void add(AddMouldRequest request);
    void update(UpdateMouldRequest request);
    void remove(Long id);
    void removeBatch(List<Long> ids);
}
