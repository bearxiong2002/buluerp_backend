package com.ruoyi.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.web.domain.ErpProductionSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ErpProductionScheduleMapper extends BaseMapper<ErpProductionSchedule> {
    List<Long> listProductionScheduleMaterialIds(Long productionScheduleId);
    int clearProductionScheduleMaterialIds(Long productionScheduleId);
    int insertProductionScheduleMaterialIds(
            @Param("productionScheduleId") Long productionScheduleId,
            @Param("materialIds") List<Long> materialIds
    );
}
