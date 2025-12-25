package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.web.domain.ErpManufacturer;
import com.ruoyi.web.domain.ErpMaterialInfo;
import com.ruoyi.web.domain.ErpMould;
import com.ruoyi.web.domain.ErpMouldHouse;
import com.ruoyi.web.mapper.ErpMouldMapper;
import com.ruoyi.web.request.mould.AddMouldRequest;
import com.ruoyi.web.request.mould.ListMouldRequest;
import com.ruoyi.web.request.mould.UpdateMouldRequest;
import com.ruoyi.web.mapper.ErpManufacturerMapper;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.web.result.MouldInfoResult;
import com.ruoyi.web.service.IErpManufacturerService;
import com.ruoyi.web.service.IErpMaterialInfoService;
import com.ruoyi.web.service.IErpMouldHouseService;
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

    @Autowired
    private IErpMouldHouseService mouldHouseService;

    @Autowired
    private ErpManufacturerMapper manufacturerMapper;

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
        if (mould.getMouldHouseId() != null && mould.getMouldHouseId() >= 0) {
            ErpMouldHouse mouldHouse = mouldHouseService.lambdaQuery()
                    .eq(ErpMouldHouse::getId, mould.getMouldHouseId())
                    .one();
            if (mouldHouse == null) {
                throw new IllegalArgumentException("模房ID不存在");
            }
        }
    }

    @Override
    public MouldInfoResult getMouldInfo(String mouldNumber) {
        MouldInfoResult mouldInfoById = baseMapper.findMouldInfoById(mouldNumber);
        if (mouldInfoById == null) {
            throw new ServiceException("没有模具信息");
        }
        return mouldInfoById;
    }

    @Override
    public List<MouldInfoResult> list(ListMouldRequest request) {
        return baseMapper.selectMouldInfoList(request);
    }

    @Override
    @Transactional
    public void add(AddMouldRequest request) {
        ErpMould mould = new ErpMould();
        BeanUtils.copyProperties(request, mould);

        checkUnique(mould);

        if (StringUtils.isNotEmpty(request.getManufacturerName())) {
            List<ErpManufacturer> manufacturers = manufacturerMapper
                    .selectList(new LambdaQueryWrapper<ErpManufacturer>()
                            .like(ErpManufacturer::getName, request.getManufacturerName()));

            if (CollectionUtils.isEmpty(manufacturers)) {
                throw new IllegalArgumentException("厂商不存在");
            }
            if (manufacturers.size() > 1) {
                throw new IllegalArgumentException("厂商名称匹配到多条记录，请提供更精确的名称");
            }
            mould.setManufacturerId(manufacturers.get(0).getId());
        }

        mould.setStatus(ErpMould.STATUS_CREATED);
        mould.setMouldHouseId(-1L);
        baseMapper.insert(mould);
    }

    @Override
    @Transactional
    public void update(UpdateMouldRequest request) {
        ErpMould mould = new ErpMould();
        BeanUtils.copyProperties(request, mould);
        if (StringUtils.isNotEmpty(request.getManufacturerName())) {
            List<ErpManufacturer> manufacturers = manufacturerMapper
                    .selectList(new LambdaQueryWrapper<ErpManufacturer>()
                            .eq(ErpManufacturer::getName, request.getManufacturerName()));

            if (CollectionUtils.isEmpty(manufacturers)) {
                throw new IllegalArgumentException("厂商不存在");
            }
            if (manufacturers.size() > 1) {
                throw new IllegalArgumentException("厂商名称匹配到多条记录，请提供更精确的名称");
            }
            mould.setManufacturerId(manufacturers.get(0).getId());
        }
        checkReferences(mould);
        if (request.getStatus() != null && !ErpMould.isStatusValid(request.getStatus())) {
            throw new IllegalArgumentException("无效的模具状态");
        }
        if (request.getMouldHouseId() != null && request.getMouldHouseId() < 0) {
            // 未分配统一设置为-1便于检索
            mould.setMouldHouseId(-1L);
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
        if (!CollectionUtils.isEmpty(materialInfoService.selectErpMaterialInfoList(materialInfo))) {
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
