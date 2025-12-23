package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.web.domain.ErpMould;
import com.ruoyi.web.domain.ErpMouldHouse;
import com.ruoyi.web.mapper.ErpMouldHouseMapper;
import com.ruoyi.web.request.mouldhouse.AddMouldHouseRequest;
import com.ruoyi.web.request.mouldhouse.ListMouldHouseRequest;
import com.ruoyi.web.request.mouldhouse.UpdateMouldHouseRequest;
import com.ruoyi.web.service.IErpMouldHouseService;
import com.ruoyi.web.service.IErpMouldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class ErpMouldHouseServiceImpl
        extends ServiceImpl<ErpMouldHouseMapper, ErpMouldHouse>
        implements IErpMouldHouseService
{
    @Autowired
    private IErpMouldService erpMouldService;

    @Override
    public void checkUnique(ErpMouldHouse mouldHouse) {
        if (mouldHouse.getName() != null) {
            ErpMouldHouse existing = lambdaQuery()
                    .eq(ErpMouldHouse::getName, mouldHouse.getName())
                    .one();
            if (existing != null && !Objects.equals(existing.getId(), mouldHouse.getId())) {
                throw new ServiceException("名称已存在");
            }
        }
    }

    @Override
    public Long addChecked(AddMouldHouseRequest request) {
        ErpMouldHouse mouldHouse = new ErpMouldHouse();
        BeanUtils.copyProperties(request, mouldHouse);
        checkUnique(mouldHouse);
        save(mouldHouse);
        return mouldHouse.getId();
    }

    @Override
    public void updateChecked(UpdateMouldHouseRequest request) {
        ErpMouldHouse mouldHouse = new ErpMouldHouse();
        BeanUtils.copyProperties(request, mouldHouse);
        checkUnique(mouldHouse);
        if (!updateById(mouldHouse)) {
            throw new ServiceException("模房不存在");
        }
    }

    @Override
    public void removeChecked(Long id) {
        long count = erpMouldService.lambdaQuery()
                .eq(ErpMould::getMouldHouseId, id)
                .count();
        if (count > 0) {
            throw new ServiceException("模房有模具，不能删除");
        }
        removeById(id);
    }

    @Override
    @Transactional
    public void removeBatchChecked(List<Long> ids) {
        for (Long id : ids) {
            removeChecked(id);
        }
    }

    @Override
    public List<ErpMouldHouse> search(ListMouldHouseRequest request) {
        ErpMouldHouse mouldHouse = new ErpMouldHouse();
        BeanUtils.copyProperties(request, mouldHouse);

        Long id = mouldHouse.getId();
        mouldHouse.setId(null);
        LambdaQueryWrapper<ErpMouldHouse> queryWrapper = new LambdaQueryWrapper<>(mouldHouse);

        if (id != null) {
            queryWrapper.like(ErpMouldHouse::getId, id);
        }

        return list(queryWrapper);
    }
}
