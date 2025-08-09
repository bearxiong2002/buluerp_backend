package com.ruoyi.web.service;

import com.ruoyi.web.domain.ErpMaterialInfo;
import com.ruoyi.web.request.material.AddMaterialInfoRequest;
import com.ruoyi.web.request.material.AddPurchasedMaterialRequest;

import java.io.IOException;
import java.util.List;

public interface IErpMaterialInfoService {
    List<ErpMaterialInfo> selectErpMaterialInfoList(ErpMaterialInfo erpMaterialInfo);
    List<ErpMaterialInfo> selectErpMaterialInfoListByIds(Long[] ids);
    List<ErpMaterialInfo> listByMaterialType(String materialType);
    ErpMaterialInfo selectErpMaterialInfoById(Long id);
    Long insertErpMaterialInfo(AddMaterialInfoRequest request) throws IOException;
    int insertErpMaterialInfos(List<ErpMaterialInfo> erpMaterialInfos);
    Long insertPurchased(AddPurchasedMaterialRequest request) throws IOException;
    int updateErpMaterialInfo(ErpMaterialInfo erpMaterialInfo) throws IOException;
    int deleteErpMaterialInfoByIds(Long[] ids);
    int deleteErpMaterialInfoById(Long id);
}
