package com.ruoyi.web.service;

import com.ruoyi.web.domain.ErpPurchaseCollection;
import com.ruoyi.web.request.purchasecollection.AddPurchaseCollectionFromInfoRequest;

import java.io.IOException;
import java.util.List;

public interface IErpPurchaseCollectionService {
    boolean isAllPurchased(String orderCode);
    void markAllPurchased(String orderCode);

    int deleteErpPurchaseCollectionById(Long id);
    int deleteErpPurchaseCollectionByIds(Long[] ids);
    int updateErpPurchaseCollection(ErpPurchaseCollection erpPurchaseCollection) throws IOException;
    int insertFromInfo(AddPurchaseCollectionFromInfoRequest request) throws IOException;
    int insertErpPurchaseCollection(ErpPurchaseCollection erpPurchaseCollection) throws IOException;
    int insertErpPurchaseCollections(List<ErpPurchaseCollection> erpPurchaseCollectionList) throws IOException;
    ErpPurchaseCollection selectErpPurchaseCollectionById(Long id);
    List<ErpPurchaseCollection> selectErpPurchaseCollectionList(ErpPurchaseCollection erpPurchaseCollection);
    List<ErpPurchaseCollection> selectErpPurchaseCollectionListByIds(Long[] ids);

    /**
     * 在审核通过后，直接应用新的状态，不触发额外的审核流程
     * @param erpPurchaseCollection 包含新状态的采购集合对象
     */
    void applyApprovedStatus(ErpPurchaseCollection erpPurchaseCollection);
}
