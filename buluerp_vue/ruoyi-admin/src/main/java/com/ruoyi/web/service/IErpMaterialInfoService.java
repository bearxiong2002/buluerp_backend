package com.ruoyi.web.service;

import com.ruoyi.web.domain.ErpMaterialInfo;

import java.util.List;

public interface IErpMaterialInfoService {
    List<ErpMaterialInfo> selectErpMaterialInfoList(ErpMaterialInfo erpMaterialInfo);
    ErpMaterialInfo selectErpMaterialInfoById(Long id);
    int insertErpMaterialInfo(ErpMaterialInfo erpMaterialInfo);
    int insertErpMaterialInfos(List<ErpMaterialInfo> erpMaterialInfos);
    int updateErpMaterialInfo(ErpMaterialInfo erpMaterialInfo);
    int deleteErpMaterialInfoByIds(Long[] ids);
    int deleteErpMaterialInfoById(Long id);
}
