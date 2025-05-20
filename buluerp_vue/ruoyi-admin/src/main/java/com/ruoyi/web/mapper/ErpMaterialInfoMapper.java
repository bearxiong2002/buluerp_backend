package com.ruoyi.web.mapper;

import com.ruoyi.web.domain.ErpMaterialInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ErpMaterialInfoMapper {
    List<ErpMaterialInfo> selectErpMaterialInfoList(ErpMaterialInfo erpMaterialInfo);

    ErpMaterialInfo selectErpMaterialInfoById(Long id);

    List<ErpMaterialInfo> selectErpMaterialInfoListByIds(Long[] ids);

    int insertErpMaterialInfo(ErpMaterialInfo erpMaterialInfo);

    int updateErpMaterialInfo(ErpMaterialInfo erpMaterialInfo);

    int deleteErpMaterialInfoById(Long id);

    int deleteErpMaterialInfoByIds(Long[] ids);
}
