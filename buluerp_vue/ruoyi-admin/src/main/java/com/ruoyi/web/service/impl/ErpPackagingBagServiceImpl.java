package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.excel.ListRowErrorInfo;
import com.ruoyi.common.exception.excel.ListValidationException;
import com.ruoyi.web.domain.ErpPackagingBag;
import com.ruoyi.web.domain.ErpPackagingDetail;
import com.ruoyi.web.domain.ErpPackagingList;
import com.ruoyi.web.mapper.ErpPackagingBagMapper;
import com.ruoyi.web.service.IErpPackagingBagService;
import com.ruoyi.web.service.IErpPackagingDetailService;
import com.ruoyi.web.service.IErpPackagingListService;
import com.ruoyi.web.service.IListValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    @Autowired
    IListValidationService listValidationService;

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
        for (ErpPackagingBag entity : entities) {
            fill(entity);
        }
        return entities;
    }

    @Override
    public List<ErpPackagingBag> listByPackagingList(Long listId) {
        LambdaQueryWrapper<ErpPackagingBag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ErpPackagingBag::getPackagingListId, listId);
        return fill(list(queryWrapper));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertCascade(ErpPackagingBag entity) {
        List<ListRowErrorInfo> listRowErrorInfos = listValidationService
                .collectErrors(Collections.singletonList(entity), baseMapper::insert);
        listRowErrorInfos.addAll(
                listValidationService.collectErrors(entity.getDetails(), (detail) -> {
                            detail.setPackagingBagId(entity.getId());
                            erpPackagingDetailService.checkUnique(detail);
                            erpPackagingDetailService.saveOrUpdate(detail);
                        })
                        .stream()
                        .peek((e) -> e.setRowNum(e.getRowNum() + IErpPackagingListService.BAG_TEMPLATE_HEADER_ROW))
                        .collect(Collectors.toList())
        );
        if (!listRowErrorInfos.isEmpty()) {
            throw new ListValidationException(listRowErrorInfos);
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
