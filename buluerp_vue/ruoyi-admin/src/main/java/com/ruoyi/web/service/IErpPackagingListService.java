package com.ruoyi.web.service;

import com.ruoyi.web.domain.ErpPackagingList;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface IErpPackagingListService {

    public static final String LIST_TEMPLATE = "excel" + File.separator + "packaging_list.xlsx";
    public static final Integer BAG_TEMPLATE_HEADER_ROW = 2;

    void markPackagingDone(Long id);

    ErpPackagingList selectErpPackagingListById(Long id);
    List<ErpPackagingList> selectErpPackagingListList(ErpPackagingList erpPackagingList);
    List<ErpPackagingList> selectErpPackagingListListByIds(Long[] ids);
    int insertErpPackagingList(ErpPackagingList erpPackagingList);
    int updateErpPackagingList(ErpPackagingList erpPackagingList);
    int deleteErpPackagingListByIds(Long[] ids);

    void exportExcel(OutputStream outputStream, ErpPackagingList erpPackagingList) throws IOException;
    void exportExcel(HttpServletResponse response, ErpPackagingList erpPackagingList) throws IOException;
    ErpPackagingList importExcel(InputStream inputStream, List<String> basSheetNameReceiver) throws Exception;
    void insertCascade(ErpPackagingList erpPackagingList, List<String> basSheetNames);

    /**
     * 在审核通过后，直接应用新的状态，不触发额外的审核流程
     * @param packagingList 包含新状态的包装清单对象
     */
    void applyApprovedStatus(ErpPackagingList packagingList);
}
