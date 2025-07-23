package com.ruoyi.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.web.domain.ErpProductionSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ErpProductionScheduleMapper extends BaseMapper<ErpProductionSchedule> {
    int attatchToArrange(
            @Param("productionArrangeId") Long productionArrangeId,
            @Param("productionScheduleIds") List<Long> productionScheduleIds
    );
}
