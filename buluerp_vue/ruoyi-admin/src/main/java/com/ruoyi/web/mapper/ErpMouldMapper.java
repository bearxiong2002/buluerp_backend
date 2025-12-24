package com.ruoyi.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.web.domain.ErpMould;
import com.ruoyi.web.request.mould.ListMouldRequest;
import com.ruoyi.web.result.MouldInfoResult;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ErpMouldMapper extends BaseMapper<ErpMould> {

    MouldInfoResult findMouldInfoById(String mouldNumber);

    List<MouldInfoResult> selectMouldInfoList(ListMouldRequest request);
}
