package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.web.domain.ErpPackagingBag;
import com.ruoyi.web.domain.ErpPackagingDetail;
import com.ruoyi.web.domain.ErpPackagingList;
import com.ruoyi.web.mapper.ErpPackagingBagMapper;
import com.ruoyi.web.service.IErpPackagingBagService;
import com.ruoyi.web.service.IErpPackagingDetailService;
import com.ruoyi.web.service.IErpPackagingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ErpPackagingBagServiceImpl
        extends ServiceImpl<ErpPackagingBagMapper, ErpPackagingBag>
        implements IErpPackagingBagService {
    @Autowired
    IErpPackagingListService erpPackagingListService;

    @Autowired
    IErpPackagingDetailService erpPackagingDetailService;

    public void checkReferences(ErpPackagingBag entity) {
        if (entity.getPackagingListId() != null) {
            if (erpPackagingListService.selectErpPackagingListById(entity.getPackagingListId()) == null) {
                throw new ServiceException("分包列表不存在");
            }
        }
    }

    @Override
    public void check(ErpPackagingBag entity) {
        checkReferences(entity);
    }

    public ErpPackagingBag fill(ErpPackagingBag entity) {
        if (entity == null) {
            return null;
        }
        List<ErpPackagingDetail> details = erpPackagingDetailService.listByBag(entity.getId());
        entity.setDetails(details);
        return entity;
    }

    public List<ErpPackagingBag> fill(List<ErpPackagingBag> entities) {
        return entities.stream().map(this::fill).collect(Collectors.toList());
    }

    @Override
    public List<ErpPackagingBag> listByPackagingList(Long listId) {
        LambdaQueryWrapper<ErpPackagingBag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ErpPackagingBag::getPackagingListId, listId);
        return fill(list(queryWrapper));
    }

    @Override
    @Transactional
    public void insertCascade(ErpPackagingBag entity) {
        saveOrUpdate(entity);
        for (ErpPackagingDetail detail : entity.getDetails()) {
            detail.setPackagingBagId(entity.getId());
            erpPackagingDetailService.checkUnique(detail);
            erpPackagingDetailService.saveOrUpdate(detail);
        }
    }

    @Override
    @Transactional
    public boolean deleteCascadeByListIds(Long[] listIds) {
        erpPackagingDetailService.deleteByListIds(listIds);

        LambdaQueryWrapper<ErpPackagingBag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ErpPackagingBag::getPackagingListId, (Object[]) listIds);
        return this.remove(queryWrapper);
    }

    @Override
    @Transactional
    public boolean deleteCascade(Long[] ids) {
        erpPackagingDetailService.deleteByBagIds(ids);
        return this.removeByIds(Arrays.asList(ids));
    }
}
