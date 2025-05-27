package com.ruoyi.web.service;

import com.ruoyi.web.domain.ErpMaterialInfo;

import java.io.IOException;
import java.util.List;

public interface IErpMaterialInfoService {
    List<ErpMaterialInfo> selectErpMaterialInfoList(ErpMaterialInfo erpMaterialInfo);
    List<ErpMaterialInfo> selectErpMaterialInfoListByIds(Long[] ids);
    ErpMaterialInfo selectErpMaterialInfoById(Long id);
    int insertErpMaterialInfo(ErpMaterialInfo erpMaterialInfo) throws IOException;
    int insertErpMaterialInfos(List<ErpMaterialInfo> erpMaterialInfos);
    int updateErpMaterialInfo(ErpMaterialInfo erpMaterialInfo) throws IOException;
    int deleteErpMaterialInfoByIds(Long[] ids);
    int deleteErpMaterialInfoById(Long id);
}
