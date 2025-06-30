package com.ruoyi.web.service;

import com.ruoyi.web.domain.ErpPackagingList;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface IErpPackagingListService {
    ErpPackagingList selectErpPackagingListById(Long id);
    List<ErpPackagingList> selectErpPackagingListList(ErpPackagingList erpPackagingList);
    List<ErpPackagingList> selectErpPackagingListListByIds(Long[] ids);
    int insertErpPackagingList(ErpPackagingList erpPackagingList);
    int updateErpPackagingList(ErpPackagingList erpPackagingList);
    int deleteErpPackagingListByIds(Long[] ids);
}
