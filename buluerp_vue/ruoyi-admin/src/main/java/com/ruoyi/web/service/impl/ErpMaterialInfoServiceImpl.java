package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.web.domain.*;
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

    @Autowired
    private IErpMaterialTypeService erpMaterialTypeService;

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

    private void checkReferences(ErpMaterialInfo erpMaterialInfo) {
        if (erpMaterialInfo.getMaterialType() == null) {
            ErpMaterialType type = erpMaterialTypeService.getByName(erpMaterialInfo.getMaterialType());
            if (type == null) {
                throw new ServiceException("对应物料类型不存在");
            }
        }
    }

    private void check(ErpMaterialInfo erpMaterialInfo) {
        checkReferences(erpMaterialInfo);
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
    public List<ErpMaterialInfo> listByMaterialType(String materialType) {
        return fill(erpMaterialInfoMapper.selectByMaterialType(materialType));
    }

    @Override
    public ErpMaterialInfo selectErpMaterialInfoById(Long id) {
        return erpMaterialInfoMapper.selectErpMaterialInfoById(id);
    }

    @Override
    public Long insertErpMaterialInfo(ErpMaterialInfo erpMaterialInfo) throws IOException {
        erpMaterialInfo.setCreatTime(DateUtils.getNowDate());
        erpMaterialInfo.setUpdateTime(DateUtils.getNowDate());
        check(erpMaterialInfo);
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
        check(erpMaterialInfo);
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
