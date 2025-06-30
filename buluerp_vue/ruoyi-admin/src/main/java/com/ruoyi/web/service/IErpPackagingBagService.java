package com.ruoyi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.web.domain.ErpPackagingBag;

import java.util.List;

public interface IErpPackagingBagService extends IService<ErpPackagingBag> {
    void checkReferences(ErpPackagingBag entity);

    void check(ErpPackagingBag entity);

    ErpPackagingBag fill(ErpPackagingBag entity);

    List<ErpPackagingBag> fill(List<ErpPackagingBag> entities);

    List<ErpPackagingBag> listByPackagingList(Long listId);

    boolean deleteCascadeByListIds(Long[] listIds);

    boolean deleteCascade(Long[] ids);
}
