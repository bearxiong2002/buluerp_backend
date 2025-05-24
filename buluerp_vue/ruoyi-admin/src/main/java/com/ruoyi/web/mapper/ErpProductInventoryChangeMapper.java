package com.ruoyi.web.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.web.domain.ErpPackagingMaterialInventoryChange;
import com.ruoyi.web.domain.ErpProductInventoryChange;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ErpProductInventoryChangeMapper extends BaseMapper<ErpProductInventoryChange> {
    @Select("SELECT SUM(in_out_quantity) from buluerp.erp_product_inventory_change where ${ew.customSqlSegment}")
    Integer sumQuantity(@Param("ew") Wrapper<ErpProductInventoryChange> wrapper);

}