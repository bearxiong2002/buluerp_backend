package com.ruoyi.web.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.web.domain.ErpPartInventoryChange;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;



public interface ErpPartInventoryChangeMapper extends BaseMapper<ErpPartInventoryChange> {
    @Select("SELECT COALESCE(SUM(in_out_quantity), 0) from buluerp.erp_part_inventory_change ${ew.customSqlSegment}")
    Integer sumQuantity(@Param("ew") Wrapper<ErpPartInventoryChange> wrapper);
}