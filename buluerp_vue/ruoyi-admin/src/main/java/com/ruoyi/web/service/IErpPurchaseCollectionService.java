package com.ruoyi.web.service;

import com.ruoyi.web.domain.ErpOrders;
import com.ruoyi.web.domain.ErpPurchaseCollection;
import com.ruoyi.web.request.purchasecollection.AddPurchaseCollectionFromInfoRequest;
import com.ruoyi.web.request.purchasecollection.ListPurchaseCollectionRequest;
import com.ruoyi.web.result.PurchaseCollectionResult;

import java.io.IOException;
import java.util.List;

public interface IErpPurchaseCollectionService {
    boolean isAllPurchased(String orderCode);
    boolean isAllPurchaseCompleted(String orderCode);
    void markAllPurchased(String orderCode);
    void tryContinueOrder(ErpOrders order);

    int deleteErpPurchaseCollectionById(Long id);
    int deleteErpPurchaseCollectionByIds(Long[] ids);
    int updateErpPurchaseCollection(ErpPurchaseCollection erpPurchaseCollection) throws IOException;
    int insertFromInfo(AddPurchaseCollectionFromInfoRequest request) throws IOException;
    int insertErpPurchaseCollection(ErpPurchaseCollection erpPurchaseCollection) throws IOException;
    int insertErpPurchaseCollections(List<ErpPurchaseCollection> erpPurchaseCollectionList) throws IOException;
    PurchaseCollectionResult selectErpPurchaseCollectionById(Long id);
    List<PurchaseCollectionResult> selectErpPurchaseCollectionList(ListPurchaseCollectionRequest request);
    List<PurchaseCollectionResult> selectErpPurchaseCollectionListByIds(Long[] ids);
    List<PurchaseCollectionResult> selectErpPurchaseCollectionListByOrderCode(String orderCode);
    
    /**
     * 根据purchaseCode查询采购汇总
     * 
     * @param purchaseCode 外购编码
     * @return 采购汇总信息
     */
    PurchaseCollectionResult selectErpPurchaseCollectionByPurchaseCode(String purchaseCode);

    void removeChecked(Long id);
    void removeBatchChecked(List<Long> ids);

    List<PurchaseCollectionResult> listByPurchaseId(Long purchaseId);

    /**
     * 在审核通过后，直接应用新的状态，不触发额外的审核流程
     * @param erpPurchaseCollection 包含新状态的采购集合对象
     */
    void applyApprovedStatus(ErpPurchaseCollection erpPurchaseCollection);
}
