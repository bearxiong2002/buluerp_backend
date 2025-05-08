package com.ruoyi.web.mapper;

import com.ruoyi.web.domain.ErpPurchaseCollection;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ErpPurchaseCollectionMapper {
    int insertErpPurchaseCollection(ErpPurchaseCollection erpPurchaseCollection);
    int updateErpPurchaseCollection(ErpPurchaseCollection erpPurchaseCollection);
    int deleteErpPurchaseCollectionById(Long id);
    int deleteErpPurchaseCollectionByIds(Long[] ids);
    ErpPurchaseCollection selectErpPurchaseCollectionById(Long id);
    List<ErpPurchaseCollection> selectErpPurchaseCollectionList(ErpPurchaseCollection erpPurchaseCollection);
}
