package com.ruoyi.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.web.domain.ErpProducts;
import com.ruoyi.web.domain.ErpPurchaseOrder;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
@Mapper
public interface ErpPurchaseOrderMapper extends BaseMapper<ErpPurchaseOrder>
{
    List<String> selectUrl(Integer order_id);
}
