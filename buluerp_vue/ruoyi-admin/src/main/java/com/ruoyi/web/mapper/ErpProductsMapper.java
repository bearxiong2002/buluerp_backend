package com.ruoyi.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.web.domain.ErpDesignPatterns;
import com.ruoyi.web.domain.ErpProducts;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
@Mapper
public interface ErpProductsMapper extends BaseMapper<ErpProducts>
{
    int updateStatusById(@Param("id")Long id,@Param("design_status")Long design_status);

    List<ErpProducts> selectErpProductsListByIds(Integer[] ids);
}
