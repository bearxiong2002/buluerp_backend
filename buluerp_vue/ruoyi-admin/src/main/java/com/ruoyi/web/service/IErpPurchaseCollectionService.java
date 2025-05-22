package com.ruoyi.web.service;

import com.ruoyi.web.domain.ErpPurchaseCollection;

import java.io.IOException;
import java.util.List;

public interface IErpPurchaseCollectionService {
    int deleteErpPurchaseCollectionById(Long id);
    int deleteErpPurchaseCollectionByIds(Long[] ids);
    int updateErpPurchaseCollection(ErpPurchaseCollection erpPurchaseCollection) throws IOException;
    int insertErpPurchaseCollection(ErpPurchaseCollection erpPurchaseCollection) throws IOException;
    int insertErpPurchaseCollections(List<ErpPurchaseCollection> erpPurchaseCollectionList) throws IOException;
    ErpPurchaseCollection selectErpPurchaseCollectionById(Long id);
    List<ErpPurchaseCollection> selectErpPurchaseCollectionList(ErpPurchaseCollection erpPurchaseCollection);
    List<ErpPurchaseCollection> selectErpPurchaseCollectionListByIds(Long[] ids);
}
