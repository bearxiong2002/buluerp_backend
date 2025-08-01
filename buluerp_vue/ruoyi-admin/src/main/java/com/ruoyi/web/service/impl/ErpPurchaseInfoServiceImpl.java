package com.ruoyi.web.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.web.domain.ErpMaterialInfo;
import com.ruoyi.web.mapper.ErpPurchaseInfoMapper;
import com.ruoyi.web.service.IErpMaterialInfoService;
import com.ruoyi.web.service.IErpPurchaseCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.web.domain.ErpPurchaseInfo;
import com.ruoyi.web.service.IErpPurchaseInfoService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * 外购资料，用于存储外购物料的基本信息和相关数据Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-04-08
 */
@Service
public class ErpPurchaseInfoServiceImpl
        extends ServiceImpl<ErpPurchaseInfoMapper, ErpPurchaseInfo>
        implements IErpPurchaseInfoService
{
    @Autowired
    private IErpMaterialInfoService erpMaterialInfoService;

    @Autowired
    private IErpPurchaseCollectionService erpPurchaseCollectionService;

    void checkUnique(ErpPurchaseInfo erpPurchaseInfo) {
        if (erpPurchaseInfo.getPurchaseCode() != null) {
            LambdaQueryWrapper<ErpPurchaseInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ErpPurchaseInfo::getPurchaseCode, erpPurchaseInfo.getPurchaseCode());
            ErpPurchaseInfo original = this.getOne(queryWrapper);
            if (original != null && !original.getId().equals(erpPurchaseInfo.getId())) {
                throw new ServiceException("外购编码已存在，请更换外购编码");
            }
        }
        if (erpPurchaseInfo.getMaterialId() != null) {
            LambdaQueryWrapper<ErpPurchaseInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ErpPurchaseInfo::getMaterialId, erpPurchaseInfo.getMaterialId());
            ErpPurchaseInfo original = this.getOne(queryWrapper);
            if (original != null && !original.getId().equals(erpPurchaseInfo.getId())) {
                throw new ServiceException("物料ID已存在，请更换物料ID");
            }
        }
    }

    void checkReferences(ErpPurchaseInfo erpPurchaseInfo) {
        if (erpPurchaseInfo.getMaterialId() != null) {
            ErpMaterialInfo erpMaterialInfo = erpMaterialInfoService.selectErpMaterialInfoById(erpPurchaseInfo.getMaterialId());
            if (erpMaterialInfo == null) {
                throw new ServiceException("物料ID不存在，请先添加相应物料信息");
            }
        }
    }

    void check(ErpPurchaseInfo erpPurchaseInfo) {
        checkUnique(erpPurchaseInfo);
        checkReferences(erpPurchaseInfo);
    }

    @Override
    public int insertErpPurchaseInfo(ErpPurchaseInfo erpPurchaseInfo) throws IOException {
        check(erpPurchaseInfo);
        ErpMaterialInfo erpMaterialInfo = erpMaterialInfoService.selectErpMaterialInfoById(erpPurchaseInfo.getMaterialId());
        if (erpPurchaseInfo.getPictureUrl() == null && erpPurchaseInfo.getPicture() == null) {
            erpPurchaseInfo.setPictureUrl(erpMaterialInfo.getDrawingReference());
        }
        if (erpPurchaseInfo.getPicture() != null) {
            String url = FileUploadUtils.upload(erpPurchaseInfo.getPicture());
            erpPurchaseInfo.setPictureUrl(url);
        }
        return baseMapper.insert(erpPurchaseInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertErpPurchaseInfoList(List<ErpPurchaseInfo> list) throws IOException {
        int count = 0;
        for (ErpPurchaseInfo erpPurchaseInfo : list) {
            count += insertErpPurchaseInfo(erpPurchaseInfo);
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateErpPurchaseInfoList(List<ErpPurchaseInfo> list) throws IOException {
        int count = 0;
        for (ErpPurchaseInfo erpPurchaseInfo : list) {
            check(erpPurchaseInfo);
            if (erpPurchaseInfo.getPicture() != null) {
                String url = FileUploadUtils.upload(erpPurchaseInfo.getPicture());
                erpPurchaseInfo.setPictureUrl(url);
            }
            count += baseMapper.updateById(erpPurchaseInfo);
        }
        return count;
    }

    @Override
    public boolean deleteChecked(Long id) {
        if (!CollectionUtils.isEmpty(erpPurchaseCollectionService.listByPurchaseId(id))) {
            throw new ServiceException("外购资料" + id + "已被采购，不能删除");
        }
        return baseMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public void deleteBatchChecked(Long[] ids) {
        for (Long id : ids) {
            if (!deleteChecked(id)) {
                throw new ServiceException("删除失败，无法删除外购资料" + id);
            }
        }
    }

    @Override
    @Transactional
    public boolean deleteErpPurchaseInfoByMaterialIds(Long[] ids) {
        LambdaQueryWrapper<ErpPurchaseInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ErpPurchaseInfo::getMaterialId, Arrays.asList(ids));
        List<ErpPurchaseInfo> list = baseMapper.selectList(queryWrapper);
        for (ErpPurchaseInfo erpPurchaseInfo : list) {
            if (!deleteChecked(erpPurchaseInfo.getId())) {
                throw new ServiceException("删除失败，无法删除外购资料" + erpPurchaseInfo.getId());
            }
        }
        return true;
    }
}
