package com.ruoyi.web.mapper;

import com.ruoyi.web.domain.ErpPurchaseCollection;
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
    List<ErpPurchaseCollection> selectErpPurchaseCollectionList(ErpPurchaseCollection erpPurchaseCollection);
    List<ErpPurchaseCollection> selectErpPurchaseCollectionListByIds(Long[] ids);
}
