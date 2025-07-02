package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.web.domain.ErpPackagingDetail;
import com.ruoyi.web.mapper.ErpPackagingDetailMapper;
import com.ruoyi.web.service.IErpPackagingBagService;
import com.ruoyi.web.service.IErpPackagingDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class ErpPackagingDetailServiceImpl
        extends ServiceImpl<ErpPackagingDetailMapper, ErpPackagingDetail>
        implements IErpPackagingDetailService {
    @Autowired
    private IErpPackagingBagService erpPackagingBagService;

    @Override
    public void checkReferences(ErpPackagingDetail entity) {
        if (entity.getPackagingBagId() != null) {
            if (erpPackagingBagService.getById(entity.getPackagingBagId()) == null) {
                throw new ServiceException("分包袋不存在");
            }
        }
    }

    @Override
    public void checkUnique(ErpPackagingDetail entity) {
        // if (entity.getMouldNumber() == null) {
        //     return;
        // }
        // LambdaQueryWrapper<ErpPackagingDetail> queryWrapper = new LambdaQueryWrapper<>();
        // queryWrapper.eq(ErpPackagingDetail::getMouldNumber, entity.getMouldNumber());
        // ErpPackagingDetail original = baseMapper.selectOne(queryWrapper);
        // if (original != null && !Objects.equals(original.getId(), entity.getId())) {
        //     throw new ServiceException("此模号已存在：" + entity.getMouldNumber());
        // }
    }

    @Override
    public void check(ErpPackagingDetail entity) {
        checkUnique(entity);
        checkReferences(entity);
    }

    @Override
    public void uploadImage(ErpPackagingDetail entity) throws IOException {
        if (entity.getPartImageFile() != null) {
            String imageUrl = FileUploadUtils.upload(entity.getPartImageFile());
            entity.setPartImageUrl(imageUrl);
        }
    }

    @Override
    public List<ErpPackagingDetail> listByBag(Long bagId) {
        LambdaQueryWrapper<ErpPackagingDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ErpPackagingDetail::getPackagingBagId, bagId);
        return list(wrapper);
    }

    @Override
    public boolean deleteByListIds(Long[] listIds) {
        return baseMapper.deleteByPackagingListIds(listIds) > 0;
    }

    @Override
    public boolean deleteByBagIds(Long[] bagIds) {
        LambdaQueryWrapper<ErpPackagingDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ErpPackagingDetail::getPackagingBagId, (Object[]) bagIds);
        return this.remove(queryWrapper);
    }
}
