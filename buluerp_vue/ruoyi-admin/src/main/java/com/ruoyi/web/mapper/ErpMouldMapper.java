package com.ruoyi.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.web.domain.ErpMould;
import com.ruoyi.web.request.mould.ListMouldRequest;
import com.ruoyi.web.result.MouldInfoResult;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ErpMouldMapper extends BaseMapper<ErpMould> {

    MouldInfoResult findMouldInfoById(String mouldNumber);

    List<MouldInfoResult> selectMouldInfoList(ListMouldRequest request);

    /**
     * 批量写入已经完成编号和厂商校验的模具数据。
     *
     * @param moulds 待写入模具
     * @return 写入行数
     */
    int insertBatch(@Param("moulds") List<ErpMould> moulds);
}
