package com.ruoyi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.web.domain.ErpProductionArrange;

import java.io.IOException;
import java.util.List;

public interface IErpProductionArrangeService extends IService<ErpProductionArrange> {
    int insertErpProductionArrangeList(List<ErpProductionArrange> erpProductionArranges) throws IOException;
    int updateErpProductionArrange(ErpProductionArrange erpProductionArrange) throws IOException;
}
