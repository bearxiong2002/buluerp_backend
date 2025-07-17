package com.ruoyi.web.mapper;

import com.ruoyi.web.domain.ErpPurchaseCollection;
import com.ruoyi.web.request.purchasecollection.ListPurchaseCollectionRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ErpPurchaseCollectionMapper {
    int insertErpPurchaseCollection(ErpPurchaseCollection erpPurchaseCollection);
    int updateErpPurchaseCollection(ErpPurchaseCollection erpPurchaseCollection);
    int deleteErpPurchaseCollectionById(Long id);
    int deleteErpPurchaseCollectionByIds(Long[] ids);
    int clearErpPurchaseCollectionMaterials(Long id);
    int insertErpPurchaseCollectionMaterials(@Param("id") Long id, @Param("materialIds") List<Long> materialIds);
    List<Long> getErpPurchaseCollectionMaterialIds(Long id);
    ErpPurchaseCollection selectErpPurchaseCollectionById(Long id);
    List<ErpPurchaseCollection> selectErpPurchaseCollectionList(ListPurchaseCollectionRequest request);
    List<ErpPurchaseCollection> selectErpPurchaseCollectionListByIds(Long[] ids);
    List<ErpPurchaseCollection> selectErpPurchaseCollectionListByOrderCode(String orderCode);
    
    /**
     * 根据purchaseCode查询采购汇总
     * 
     * @param purchaseCode 外购编码
     * @return 采购汇总信息
     */
    ErpPurchaseCollection selectErpPurchaseCollectionByPurchaseCode(String purchaseCode);
}
