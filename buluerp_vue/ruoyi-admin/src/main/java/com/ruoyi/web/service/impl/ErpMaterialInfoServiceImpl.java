package com.ruoyi.web.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.web.domain.ErpMaterialInfo;
import com.ruoyi.web.mapper.ErpMaterialInfoMapper;
import com.ruoyi.web.service.IErpMaterialInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ErpMaterialInfoServiceImpl implements IErpMaterialInfoService {
    @Autowired
    private ErpMaterialInfoMapper erpMaterialInfoMapper;

    @Override
    public List<ErpMaterialInfo> selectErpMaterialInfoList(ErpMaterialInfo erpMaterialInfo) {
        return erpMaterialInfoMapper.selectErpMaterialInfoList(erpMaterialInfo);
    }

    @Override
    public ErpMaterialInfo selectErpMaterialInfoById(Long id) {
        return erpMaterialInfoMapper.selectErpMaterialInfoById(id);
    }

    @Override
    public int insertErpMaterialInfo(ErpMaterialInfo erpMaterialInfo) {
        erpMaterialInfo.setCreatTime(DateUtils.getNowDate());
        erpMaterialInfo.setUpdateTime(DateUtils.getNowDate());
        return erpMaterialInfoMapper.insertErpMaterialInfo(erpMaterialInfo);
    }

    @Override
    @Transactional
    public int insertErpMaterialInfos(List<ErpMaterialInfo> erpMaterialInfos) {
        int count = 0;
        for (ErpMaterialInfo erpMaterialInfo : erpMaterialInfos) {
            erpMaterialInfo.setCreatTime(DateUtils.getNowDate());
            erpMaterialInfo.setUpdateTime(DateUtils.getNowDate());
            count += erpMaterialInfoMapper.insertErpMaterialInfo(erpMaterialInfo);
        }
        return count;
    }

    @Override
    public int updateErpMaterialInfo(ErpMaterialInfo erpMaterialInfo) {
        erpMaterialInfo.setUpdateTime(DateUtils.getNowDate());
        return erpMaterialInfoMapper.updateErpMaterialInfo(erpMaterialInfo);
    }

    @Override
    public int deleteErpMaterialInfoByIds(Long[] ids) {
        return erpMaterialInfoMapper.deleteErpMaterialInfoByIds(ids);
    }

    @Override
    public int deleteErpMaterialInfoById(Long id) {
        return erpMaterialInfoMapper.deleteErpMaterialInfoById(id);
    }
}
