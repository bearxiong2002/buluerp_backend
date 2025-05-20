package com.ruoyi.web.mapper;

import com.ruoyi.web.domain.ErpPackagingList;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ErpPackagingListMapper {
    ErpPackagingList selectErpPackagingListById(Integer id);
    List<ErpPackagingList> selectErpPackagingListList(ErpPackagingList erpPackagingList);
    List<ErpPackagingList> selectErpPackagingListListByIds(Integer[] ids);
    int insertErpPackagingList(ErpPackagingList erpPackagingList);
    int updateErpPackagingList(ErpPackagingList erpPackagingList);
    int deleteErpPackagingListById(Integer id);
    int deleteErpPackagingListByIds(Integer[] ids);
    int deleteErpPackagingListByProductId(Integer productId);
}
