package com.ruoyi.web.mapper;

import com.ruoyi.web.domain.ErpPurchaseCollection;
import com.ruoyi.web.request.purchasecollection.ListPurchaseCollectionRequest;
import com.ruoyi.web.result.PurchaseCollectionResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ErpPurchaseCollectionMapper {
    List<PurchaseCollectionResult> selectResultList(ListPurchaseCollectionRequest request);
    PurchaseCollectionResult selectResultById(Long id);
    List<PurchaseCollectionResult> selectResultListByIds(Long[] ids);
    List<PurchaseCollectionResult> selectResultListByOrderCode(String orderCode);
    List<PurchaseCollectionResult> selectResultListByPurchaseId(Long purchaseId);
    PurchaseCollectionResult selectResultListByPurchaseCode(String purchaseCode);

    int insertErpPurchaseCollection(ErpPurchaseCollection erpPurchaseCollection);
    int updateErpPurchaseCollection(ErpPurchaseCollection erpPurchaseCollection);
    int deleteErpPurchaseCollectionById(Long id);
    int deleteErpPurchaseCollectionByIds(Long[] ids);
    ErpPurchaseCollection selectErpPurchaseCollectionById(Long id);
    List<ErpPurchaseCollection> selectErpPurchaseCollectionList(ListPurchaseCollectionRequest request);
    List<ErpPurchaseCollection> selectErpPurchaseCollectionListByIds(Long[] ids);
    List<ErpPurchaseCollection> selectErpPurchaseCollectionListByOrderCode(String orderCode);

    List<ErpPurchaseCollection> selectErpPurchaseCollectionListByPurchaseId(Long purchaseId);

    /**
     * 根据purchaseCode查询采购汇总
     *
     * @param purchaseCode 外购编码
     * @return 采购汇总信息
     */
    ErpPurchaseCollection selectErpPurchaseCollectionByPurchaseCode(String purchaseCode);
}
