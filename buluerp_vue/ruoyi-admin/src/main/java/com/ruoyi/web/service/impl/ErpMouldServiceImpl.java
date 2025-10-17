package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.web.domain.ErpManufacturer;
import com.ruoyi.web.domain.ErpMaterialInfo;
import com.ruoyi.web.domain.ErpMould;
import com.ruoyi.web.mapper.ErpMouldMapper;
import com.ruoyi.web.request.mould.AddMouldRequest;
import com.ruoyi.web.request.mould.ListMouldRequest;
import com.ruoyi.web.request.mould.UpdateMouldRequest;
import com.ruoyi.web.service.IErpManufacturerService;
import com.ruoyi.web.service.IErpMaterialInfoService;
import com.ruoyi.web.service.IErpMouldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class ErpMouldServiceImpl
        extends ServiceImpl<ErpMouldMapper, ErpMould>
        implements IErpMouldService {

    @Autowired
    private IErpManufacturerService manufacturerService;

    @Autowired
    private IErpMaterialInfoService materialInfoService;

    @Override
    public void checkUnique(ErpMould mould) {
        if (mould.getMouldNumber() != null) {
            LambdaQueryWrapper<ErpMould> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ErpMould::getMouldNumber, mould.getMouldNumber());
            if (baseMapper.selectCount(queryWrapper) > 0) {
                throw new IllegalArgumentException("模具编号已存在");
            }
        }
    }

    @Override
    public void checkReferences(ErpMould mould) {
        if (mould.getManufacturerId() != null) {
            ErpManufacturer manufacturer = manufacturerService.selectErpManufacturerById(mould.getManufacturerId());
            if (manufacturer == null) {
                throw new IllegalArgumentException("厂商ID不存在");
            }
        }
    }

    @Override
    public List<ErpMould> list(ListMouldRequest request) {
        ErpMould condition = new ErpMould();
        BeanUtils.copyProperties(request, condition);
        condition.setId(null);
        LambdaQueryWrapper<ErpMould> queryWrapper = new LambdaQueryWrapper<>(condition);

        if (request.getId() != null) {
            queryWrapper.like(ErpMould::getId, request.getId());
        }
        if (request.getTrialDateFrom() != null) {
            queryWrapper.isNotNull(ErpMould::getTrialDate);
            queryWrapper.ge(ErpMould::getTrialDate, request.getTrialDateFrom());
        }
        if (request.getTrialDateTo() != null) {
            queryWrapper.isNotNull(ErpMould::getTrialDate);
            queryWrapper.le(ErpMould::getTrialDate, request.getTrialDateTo());
        }

        return baseMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public void add(AddMouldRequest request) {
        ErpMould mould = new ErpMould();
        BeanUtils.copyProperties(request, mould);

        checkUnique(mould);
        checkReferences(mould);

        mould.setStatus(ErpMould.STATUS_CREATED);
        baseMapper.insert(mould);
    }

    @Override
    @Transactional
    public void update(UpdateMouldRequest request) {
        ErpMould mould = new ErpMould();
        BeanUtils.copyProperties(request, mould);
        checkReferences(mould);
        if (!ErpMould.isStatusValid(request.getStatus())) {
            throw new IllegalArgumentException("无效的模具状态");
        }
        if (baseMapper.updateById(mould) <= 0) {
            throw new IllegalArgumentException("更新模具失败：无效的ID");
        }
    }

    @Override
    @Transactional
    public void remove(Long id) {
        ErpMould mould = baseMapper.selectById(id);
        if (mould == null) {
            throw new IllegalArgumentException("删除模具失败：无效的ID");
        }
        ErpMaterialInfo materialInfo = new ErpMaterialInfo();
        materialInfo.setMouldNumber(mould.getMouldNumber());
        if (CollectionUtils.isEmpty(materialInfoService.selectErpMaterialInfoList(materialInfo))) {
            throw new ServiceException("该模具下存在物料信息，不能删除");
        }
        if (baseMapper.deleteById(id) <= 0) {
            throw new IllegalArgumentException("删除模具失败：无效的ID");
        }
    }

    @Override
    @Transactional
    public void removeBatch(List<Long> ids) {
        for (Long id : ids) {
            remove(id);
        }
    }
}
