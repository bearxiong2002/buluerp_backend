package com.ruoyi.web.service;

import com.ruoyi.web.domain.ErpPackagingList;

import java.util.List;

public interface IErpPackagingListService {
    ErpPackagingList selectErpPackagingListById(Integer id);
    List<ErpPackagingList> selectErpPackagingListList(ErpPackagingList erpPackagingList);
    List<ErpPackagingList> selectErpPackagingListListByIds(Integer[] ids);
    int insertErpPackagingList(ErpPackagingList erpPackagingList);
    int updateErpPackagingList(ErpPackagingList erpPackagingList);
    int deleteErpPackagingListById(Integer id);
    int deleteErpPackagingListByIds(Integer[] ids);
}
