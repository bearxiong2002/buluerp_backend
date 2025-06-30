package com.ruoyi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.web.domain.ErpPackagingDetail;

import java.io.IOException;
import java.util.List;

public interface IErpPackagingDetailService extends IService<ErpPackagingDetail> {
    void checkReferences(ErpPackagingDetail entity);
    void checkUnique(ErpPackagingDetail entity);
    void check(ErpPackagingDetail entity);
    void uploadImage(ErpPackagingDetail entity) throws IOException;

    List<ErpPackagingDetail> listByBag(Long bagId);
    boolean deleteByListIds(Long[] listIds);
    boolean deleteByBagIds(Long[] bagIds);
}
