package com.ruoyi.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.web.domain.ErpProductionSchedule;
import com.ruoyi.web.request.productionschedule.ListProductionScheduleRequest;
import com.ruoyi.web.result.ProductionScheduleResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ErpProductionScheduleMapper extends BaseMapper<ErpProductionSchedule> {
    List<ProductionScheduleResult> selectResultList(ListProductionScheduleRequest request);
    ProductionScheduleResult selectResultById(Long id);
    List<ProductionScheduleResult> selectResultListByIds(List<Long> ids);

    int attatchToArrange(
            @Param("productionArrangeId") Long productionArrangeId,
            @Param("productionScheduleIds") List<Long> productionScheduleIds
    );
}
