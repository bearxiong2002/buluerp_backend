package com.ruoyi.web.service;

import com.ruoyi.web.domain.ErpPurchaseCollection;

import java.util.List;

public interface IErpPurchaseCollectionService {
    int deleteErpPurchaseCollectionById(Long id);
    int deleteErpPurchaseCollectionByIds(Long[] ids);
    int updateErpPurchaseCollection(ErpPurchaseCollection erpPurchaseCollection);
    int insertErpPurchaseCollection(ErpPurchaseCollection erpPurchaseCollection);
    int insertErpPurchaseCollections(List<ErpPurchaseCollection> erpPurchaseCollectionList);
    ErpPurchaseCollection selectErpPurchaseCollectionById(Long id);
    List<ErpPurchaseCollection> selectErpPurchaseCollectionList(ErpPurchaseCollection erpPurchaseCollection);
    List<ErpPurchaseCollection> selectErpPurchaseCollectionListByIds(Long[] ids);
}
