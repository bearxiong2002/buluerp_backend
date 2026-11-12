package com.ruoyi.web.mapper;

import com.ruoyi.web.domain.ErpOrderInnerCode;
import com.ruoyi.web.domain.ErpOrders;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ErpOrderInnerCodeMapper {
    @Select("SELECT * FROM erp_orders WHERE id = #{id} FOR UPDATE")
    ErpOrders lockOrder(Long id);

    @Select({"<script>SELECT order_id, inner_code FROM erp_order_inner_code WHERE order_id IN",
        "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach>",
        "ORDER BY order_id, sort_order, id</script>"})
    List<ErpOrderInnerCode> selectByOrderIds(@Param("ids") List<Long> ids);

    @Select("SELECT p.* FROM erp_orders p WHERE p.inner_id = #{code} OR EXISTS " +
        "(SELECT 1 FROM erp_order_inner_code c WHERE c.order_id=p.id AND c.inner_code=#{code})")
    ErpOrders selectOrderByCode(String code);

    @Select({"<script>SELECT COUNT(*) FROM erp_orders p WHERE",
        "<if test='orderId != null'>p.id != #{orderId} AND</if>",
        "(p.inner_id IN <foreach collection='codes' item='code' open='(' separator=',' close=')'>#{code}</foreach>",
        "OR EXISTS (SELECT 1 FROM erp_order_inner_code c WHERE c.order_id=p.id AND c.inner_code IN",
        "<foreach collection='codes' item='code' open='(' separator=',' close=')'>#{code}</foreach>))</script>"})
    int countConflicts(@Param("orderId") Long orderId, @Param("codes") List<String> codes);

    @Delete("DELETE FROM erp_order_inner_code WHERE order_id=#{orderId}")
    int deleteByOrderId(Long orderId);

    @Insert({"<script>INSERT INTO erp_order_inner_code(order_id,inner_code,sort_order) VALUES",
        "<foreach collection='codes' item='code' index='i' separator=','>(#{orderId},#{code},#{i})</foreach></script>"})
    int insertCodes(@Param("orderId") Long orderId, @Param("codes") List<String> codes);

    @Select("SELECT (EXISTS(SELECT 1 FROM erp_design_patterns WHERE order_id=#{code}) OR " +
        "EXISTS(SELECT 1 FROM erp_production_schedule WHERE order_code=#{code}) OR " +
        "EXISTS(SELECT 1 FROM erp_packaging_list WHERE order_code=#{code}) OR " +
        "EXISTS(SELECT 1 FROM erp_purchase_collection WHERE order_code=#{code}) OR " +
        "EXISTS(SELECT 1 FROM erp_product_inventory_change WHERE order_code=#{code}) OR " +
        "EXISTS(SELECT 1 FROM erp_part_inventory_change WHERE order_code=#{code}) OR " +
        "EXISTS(SELECT 1 FROM erp_packaging_material_inventory_change WHERE order_code=#{code}) OR " +
        "EXISTS(SELECT 1 FROM erp_audit_record WHERE audit_id=#{code} AND audit_type=1))")
    boolean isReferenced(String code);
}
