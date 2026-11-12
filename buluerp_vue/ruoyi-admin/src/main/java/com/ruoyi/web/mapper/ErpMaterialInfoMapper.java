package com.ruoyi.web.mapper;

import com.ruoyi.web.domain.ErpMaterialInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ErpMaterialInfoMapper {
    @Select({"<script>",
            "SELECT requested.mould_number FROM (",
            "<foreach collection='numbers' item='number' separator=' UNION ALL '>",
            "SELECT #{number} AS mould_number",
            "</foreach>",
            ") requested WHERE NOT EXISTS (SELECT 1 FROM erp_material_info m WHERE m.mould_number = requested.mould_number)",
            "</script>"})
    List<String> selectMissingMouldNumbers(@Param("numbers") List<String> numbers);

    List<ErpMaterialInfo> selectErpMaterialInfoList(ErpMaterialInfo erpMaterialInfo);

    ErpMaterialInfo selectErpMaterialInfoById(Long id);

    ErpMaterialInfo selectByMouldNumber(String mouldNumber);

    List<ErpMaterialInfo> selectListByMouldNumber(String mouldNumber);

    ErpMaterialInfo selectByMouldNumberAndMaterialType(
            @Param("mouldNumber") String mouldNumber,
            @Param("materialType") String materialType);

    List<ErpMaterialInfo> selectErpMaterialInfoListByIds(Long[] ids);

    int insertErpMaterialInfo(ErpMaterialInfo erpMaterialInfo);

    int updateErpMaterialInfo(ErpMaterialInfo erpMaterialInfo);

    int deleteErpMaterialInfoDrawingReferenceById(Long id);

    int deleteErpMaterialInfoById(Long id);

    int deleteErpMaterialInfoByIds(Long[] ids);

    List<ErpMaterialInfo> selectByMaterialType(String materialType);

    List<ErpMaterialInfo> selectByMaterialTypeAndColorCode(
            @Param("materialType") String materialType,
            @Param("colorCode") String colorCode);
}
