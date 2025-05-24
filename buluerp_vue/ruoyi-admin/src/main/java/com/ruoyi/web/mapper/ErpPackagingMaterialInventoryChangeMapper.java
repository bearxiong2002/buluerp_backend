package com.ruoyi.web.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.web.domain.ErpPackagingMaterialInventoryChange;
import com.ruoyi.web.domain.ErpPartInventoryChange;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ErpPackagingMaterialInventoryChangeMapper extends BaseMapper<ErpPackagingMaterialInventoryChange> {
    @Select("SELECT SUM(in_out_quantity) from buluerp.erp_packaging_material_inventory_change where ${ew.customSqlSegment}")
    Integer sumQuantity(@Param("ew") Wrapper<ErpPackagingMaterialInventoryChange> wrapper);
}