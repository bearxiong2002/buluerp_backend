package com.ruoyi.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.web.domain.ErpDesignStyle;
import com.ruoyi.web.domain.ErpManufacturer;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ErpManufacturerMapper extends BaseMapper<ErpManufacturer> {
    int deleteManufacturerByIds(Long[] ids);

    List<ErpManufacturer> selectErpManufacturerList(ErpManufacturer erpManufacturer);
    List<ErpManufacturer> selectErpManufacturerListByIds(Long[] ids);
}
