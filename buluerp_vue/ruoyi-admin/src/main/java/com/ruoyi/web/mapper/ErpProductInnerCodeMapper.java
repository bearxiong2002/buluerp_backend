package com.ruoyi.web.mapper;

import com.ruoyi.web.domain.ErpProductInnerCode;
import com.ruoyi.web.domain.ErpProducts;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ErpProductInnerCodeMapper {
    @Select("SELECT * FROM erp_products WHERE id = #{id} FOR UPDATE")
    ErpProducts lockProduct(Long id);

    @Select({"<script>SELECT product_id, inner_code FROM erp_product_inner_code WHERE product_id IN",
        "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach>",
        "ORDER BY product_id, sort_order, id</script>"})
    List<ErpProductInnerCode> selectByProductIds(@Param("ids") List<Long> ids);

    @Select("SELECT p.* FROM erp_products p WHERE p.inner_id = #{code} OR EXISTS " +
        "(SELECT 1 FROM erp_product_inner_code c WHERE c.product_id=p.id AND c.inner_code=#{code})")
    ErpProducts selectProductByCode(String code);

    @Select({"<script>SELECT COUNT(*) FROM erp_products p WHERE",
        "<if test='productId != null'>p.id != #{productId} AND</if>",
        "(p.inner_id IN <foreach collection='codes' item='code' open='(' separator=',' close=')'>#{code}</foreach>",
        "OR EXISTS (SELECT 1 FROM erp_product_inner_code c WHERE c.product_id=p.id AND c.inner_code IN",
        "<foreach collection='codes' item='code' open='(' separator=',' close=')'>#{code}</foreach>))</script>"})
    int countConflicts(@Param("productId") Long productId, @Param("codes") List<String> codes);

    @Delete("DELETE FROM erp_product_inner_code WHERE product_id=#{productId}")
    int deleteByProductId(Long productId);

    @Insert({"<script>INSERT INTO erp_product_inner_code(product_id,inner_code,sort_order) VALUES",
        "<foreach collection='codes' item='code' index='i' separator=','>(#{productId},#{code},#{i})</foreach></script>"})
    int insertCodes(@Param("productId") Long productId, @Param("codes") List<String> codes);

    @Select("SELECT (EXISTS(SELECT 1 FROM erp_orders WHERE product_id=#{code}) OR " +
        "EXISTS(SELECT 1 FROM erp_design_patterns WHERE product_id=#{code}) OR " +
        "EXISTS(SELECT 1 FROM erp_production_schedule WHERE product_id=#{code}) OR " +
        "EXISTS(SELECT 1 FROM erp_packaging_list WHERE product_id=#{code}) OR " +
        "EXISTS(SELECT 1 FROM erp_purchase_collection WHERE product_id=#{code}))")
    boolean isReferenced(String code);
}
