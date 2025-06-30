package com.ruoyi.web.mapper;

import com.ruoyi.web.domain.ErpPackagingList;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ErpPackagingListMapper {
    ErpPackagingList selectErpPackagingListById(Long id);
    List<ErpPackagingList> selectErpPackagingListList(ErpPackagingList erpPackagingList);
    List<ErpPackagingList> selectErpPackagingListListByIds(Long[] ids);
    int insertErpPackagingList(ErpPackagingList erpPackagingList);
    int updateErpPackagingList(ErpPackagingList erpPackagingList);
    int deleteErpPackagingListById(Long id);
    int deleteErpPackagingListByIds(Long[] ids);
    int deleteErpPackagingListByProductId(Long productId);
}
