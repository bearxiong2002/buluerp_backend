package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.web.domain.ErpMaterialInfo;
import com.ruoyi.web.domain.ErpPurchaseInfo;
import com.ruoyi.web.mapper.ErpMaterialInfoMapper;
import com.ruoyi.web.service.IErpMaterialInfoService;
import com.ruoyi.web.service.IErpPurchaseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ErpMaterialInfoServiceImpl implements IErpMaterialInfoService {
    @Autowired
    private ErpMaterialInfoMapper erpMaterialInfoMapper;

    @Autowired
    private IErpPurchaseInfoService erpPurchaseInfoService;

    private ErpMaterialInfo fill(ErpMaterialInfo erpMaterialInfo) {
        LambdaQueryWrapper<ErpPurchaseInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ErpPurchaseInfo::getMaterialId, erpMaterialInfo.getId());
        List<ErpPurchaseInfo> erpPurchaseInfos =
                erpPurchaseInfoService.list(queryWrapper);
        erpMaterialInfo.setPurchaseInfos(erpPurchaseInfos);
        return erpMaterialInfo;
    }

    private List<ErpMaterialInfo> fill(List<ErpMaterialInfo> erpMaterialInfoList) {
        for (ErpMaterialInfo erpMaterialInfo : erpMaterialInfoList) {
            fill(erpMaterialInfo);
        }
        return erpMaterialInfoList;
    }

    @Override
    public List<ErpMaterialInfo> selectErpMaterialInfoList(ErpMaterialInfo erpMaterialInfo) {
        return fill(erpMaterialInfoMapper.selectErpMaterialInfoList(erpMaterialInfo));
    }

    @Override
    public List<ErpMaterialInfo> selectErpMaterialInfoListByIds(Long[] ids) {
        return fill(erpMaterialInfoMapper.selectErpMaterialInfoListByIds(ids));
    }

    @Override
    public ErpMaterialInfo selectErpMaterialInfoById(Long id) {
        return erpMaterialInfoMapper.selectErpMaterialInfoById(id);
    }

    @Override
    public Long insertErpMaterialInfo(ErpMaterialInfo erpMaterialInfo) throws IOException {
        erpMaterialInfo.setCreatTime(DateUtils.getNowDate());
        erpMaterialInfo.setUpdateTime(DateUtils.getNowDate());
        // if (erpMaterialInfoMapper.selectErpMaterialInfoByMaterialType(erpMaterialInfo.getMaterialType()) != null) {
        //     throw new ServiceException("物料类型已存在");
        // }
        if (erpMaterialInfo.getDrawingReferenceFile() != null) {
            String url = FileUploadUtils.upload(erpMaterialInfo.getDrawingReferenceFile());
            erpMaterialInfo.setDrawingReference(url);
        }
        return 0 == erpMaterialInfoMapper.insertErpMaterialInfo(erpMaterialInfo) ?
                null : erpMaterialInfo.getId();
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
    @Transactional
    public int updateErpMaterialInfo(ErpMaterialInfo erpMaterialInfo) throws IOException {
        erpMaterialInfo.setUpdateTime(DateUtils.getNowDate());
        if (erpMaterialInfo.getDeleteDrawingReference()) {
            erpMaterialInfoMapper.deleteErpMaterialInfoDrawingReferenceById(erpMaterialInfo.getId());
        } else if (erpMaterialInfo.getDrawingReferenceFile() != null) {
            String url = FileUploadUtils.upload(erpMaterialInfo.getDrawingReferenceFile());
            erpMaterialInfo.setDrawingReference(url);
        }
        return erpMaterialInfoMapper.updateErpMaterialInfo(erpMaterialInfo);
    }

    @Override
    @Transactional
    public int deleteErpMaterialInfoByIds(Long[] ids) {
        erpPurchaseInfoService.deleteErpPurchaseInfoByMaterialIds(ids);
        return erpMaterialInfoMapper.deleteErpMaterialInfoByIds(ids);
    }

    @Override
    public int deleteErpMaterialInfoById(Long id) {
        return erpMaterialInfoMapper.deleteErpMaterialInfoById(id);
    }
}
