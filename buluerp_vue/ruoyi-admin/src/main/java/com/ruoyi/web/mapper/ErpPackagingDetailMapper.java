package com.ruoyi.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.web.domain.ErpPackagingDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ErpPackagingDetailMapper extends BaseMapper<ErpPackagingDetail> {
    int deleteByPackagingListIds(Long[] listIds);
}
