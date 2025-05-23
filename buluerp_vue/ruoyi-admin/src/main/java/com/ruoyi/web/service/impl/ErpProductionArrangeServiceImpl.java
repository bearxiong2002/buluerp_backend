package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.web.domain.ErpProductionArrange;
import com.ruoyi.web.mapper.ErpProductionArrangeMapper;
import com.ruoyi.web.service.IErpProductionArrangeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
public class ErpProductionArrangeServiceImpl
        extends ServiceImpl<ErpProductionArrangeMapper, ErpProductionArrange>
        implements IErpProductionArrangeService {
    @Override
    @Transactional
    public int insertErpProductionArrangeList(List<ErpProductionArrange> erpProductionArranges) throws IOException {
        int count = 0;
        for (ErpProductionArrange erpProductionArrange : erpProductionArranges) {
            erpProductionArrange.setCreationTime(DateUtils.getNowDate());
            erpProductionArrange.setOperator(SecurityUtils.getUsername());
            if (erpProductionArrange.getPicture() != null) {
                String url = FileUploadUtils.upload(erpProductionArrange.getPicture());
                erpProductionArrange.setPictureUrl(url);
            } else {
                erpProductionArrange.setPictureUrl(null);
            }
            count += baseMapper.insert(erpProductionArrange);
        }
        return count;
    }

    @Override
    public int updateErpProductionArrange(ErpProductionArrange erpProductionArrange) throws IOException {
        erpProductionArrange.setOperator(SecurityUtils.getUsername());
        if (erpProductionArrange.getPicture() != null) {
            String url = FileUploadUtils.upload(erpProductionArrange.getPicture());
            erpProductionArrange.setPictureUrl(url);
        } else {
            erpProductionArrange.setPictureUrl(null);
        }
        return baseMapper.updateById(erpProductionArrange);
    }
}
