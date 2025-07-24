package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.web.domain.ErpMaterialInfo;
import com.ruoyi.web.domain.ErpPackagingDetail;
import com.ruoyi.web.domain.ErpProductionSchedule;
import com.ruoyi.web.domain.ErpPurchaseInfo;
import com.ruoyi.web.mapper.ErpMaterialInfoMapper;
import com.ruoyi.web.service.*;
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

    @Autowired
    private IErpDesignStyleService erpDesignStyleService;

    @Autowired
    private IErpProductionScheduleService erpProductionScheduleService;

    @Autowired
    private IErpPackagingDetailService erpPackagingDetailService;

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
            // 联动删除使用该物料的设计造型图片
            erpDesignStyleService.updateDesignStylePictureByMaterialId(erpMaterialInfo.getId(), null);
        } else if (erpMaterialInfo.getDrawingReferenceFile() != null) {
            String url = FileUploadUtils.upload(erpMaterialInfo.getDrawingReferenceFile());
            erpMaterialInfo.setDrawingReference(url);
            // 联动更新使用该物料的设计造型图片
            erpDesignStyleService.updateDesignStylePictureByMaterialId(erpMaterialInfo.getId(), url);
        }
        return erpMaterialInfoMapper.updateErpMaterialInfo(erpMaterialInfo);
    }

    @Override
    @Transactional
    public int deleteErpMaterialInfoByIds(Long[] ids) {
        for (Long id : ids) {
            LambdaQueryWrapper<ErpProductionSchedule> scheduleQuery = new LambdaQueryWrapper<>();
            scheduleQuery.eq(ErpProductionSchedule::getMaterialId, id);
            if (erpProductionScheduleService.count(scheduleQuery) > 0) {
                throw new ServiceException("该物料已被布产，不能删除");
            }
            LambdaQueryWrapper<ErpPackagingDetail> packagingDetailQuery = new LambdaQueryWrapper<>();
            packagingDetailQuery.eq(ErpPackagingDetail::getMaterialId, id);
            if (erpPackagingDetailService.count(packagingDetailQuery) > 0) {
                throw new ServiceException("该物料已被拉线组包，不能删除");
            }
        }
        erpPurchaseInfoService.deleteErpPurchaseInfoByMaterialIds(ids);
        return erpMaterialInfoMapper.deleteErpMaterialInfoByIds(ids);
    }

    @Override
    @Transactional
    public int deleteErpMaterialInfoById(Long id) {
        return deleteErpMaterialInfoByIds(new Long[]{id});
    }
}
