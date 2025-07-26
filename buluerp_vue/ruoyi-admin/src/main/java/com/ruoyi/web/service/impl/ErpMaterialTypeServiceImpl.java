package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.web.domain.ErpMaterialType;
import com.ruoyi.web.mapper.ErpMaterialTypeMapper;
import com.ruoyi.web.service.IErpMaterialInfoService;
import com.ruoyi.web.service.IErpMaterialTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Service
public class ErpMaterialTypeServiceImpl
        extends ServiceImpl<ErpMaterialTypeMapper, ErpMaterialType>
        implements IErpMaterialTypeService {
    @Autowired
    private IErpMaterialInfoService erpMaterialInfoService;

    @Override
    public void checkUnique(ErpMaterialType erpMaterialType) {
        if (erpMaterialType.getName() != null) {
            LambdaQueryWrapper<ErpMaterialType> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ErpMaterialType::getName, erpMaterialType.getName());
            ErpMaterialType materialType = this.baseMapper.selectOne(queryWrapper);
            if (materialType != null && !Objects.equals(materialType.getId(), erpMaterialType.getId())) {
                throw new ServiceException("物料类型名称已存在");
            }
        }
    }

    @Override
    public void check(ErpMaterialType materialType) {
        checkUnique(materialType);
    }

    @Override
    public boolean saveChecked(ErpMaterialType materialType) {
        check(materialType);
        return this.save(materialType);
    }

    @Override
    public boolean updateChecked(ErpMaterialType materialType) {
        check(materialType);
        return this.updateById(materialType);
    }

    @Override
    public void removeChecked(Long id) {
        ErpMaterialType erpMaterialType = this.getById(id);
        if (erpMaterialType == null) {
            throw new ServiceException("物料类型不存在");
        }
        if (!CollectionUtils.isEmpty(erpMaterialInfoService.listByMaterialType(erpMaterialType.getName()))) {
            throw new ServiceException("物料类型已被使用，不能删除");
        }
        if (!this.removeById(id)) {
            throw new ServiceException("删除失败");
        }
    }

    @Override
    @Transactional
    public void removeBatchChecked(List<Long> ids) {
        for (Long id : ids) {
            removeChecked(id);
        }
    }

    @Override
    public ErpMaterialType getByName(String name) {
        LambdaQueryWrapper<ErpMaterialType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ErpMaterialType::getName, name);
        return this.baseMapper.selectOne(queryWrapper);
    }
}
