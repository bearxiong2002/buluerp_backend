package com.ruoyi.web.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.web.domain.ErpPackagingBag;
import com.ruoyi.web.domain.ErpPackagingList;
import com.ruoyi.web.mapper.ErpPackagingListMapper;
import com.ruoyi.web.service.IErpOrdersService;
import com.ruoyi.web.service.IErpPackagingBagService;
import com.ruoyi.web.service.IErpPackagingListService;
import com.ruoyi.web.service.IErpProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ErpPackagingListServiceImpl implements IErpPackagingListService {
    @Autowired
    private ErpPackagingListMapper erpPackagingListMapper;

    @Autowired
    private IErpOrdersService erpOrdersService;

    @Autowired
    private IErpProductsService erpProductsService;

    @Autowired
    private IErpPackagingBagService erpPackagingBagService;

    public void checkReferences(ErpPackagingList erpPackagingList) {
        if (erpPackagingList.getOrderCode() != null) {
            if (erpOrdersService.selectByOrderCode(erpPackagingList.getOrderCode()) == null) {
                throw new ServiceException("订单不存在");
            }
        }
        if (erpPackagingList.getProductId() != null) {
            if (erpProductsService.selectErpProductsListByIds(
                    new Long[]{erpPackagingList.getProductId()}).isEmpty()) {
                throw new ServiceException("产品不存在");
            }
        }
    }

    public ErpPackagingList fill(ErpPackagingList entity) {
        if (entity == null) {
            return null;
        }
        List<ErpPackagingBag> erpPackagingBags = erpPackagingBagService.listByPackagingList(entity.getId());
        entity.setBagList(erpPackagingBags);
        return entity;
    }

    public List<ErpPackagingList> fill(List<ErpPackagingList> entities) {
        return entities.stream().map(this::fill).collect(Collectors.toList());
    }

    @Override
    public ErpPackagingList selectErpPackagingListById(Long id) {
        return fill(erpPackagingListMapper.selectErpPackagingListById(id));
    }

    @Override
    public List<ErpPackagingList> selectErpPackagingListList(ErpPackagingList erpPackagingList) {
        return fill(erpPackagingListMapper.selectErpPackagingListList(erpPackagingList));
    }

    @Override
    public List<ErpPackagingList> selectErpPackagingListListByIds(Integer[] ids) {
        return fill(erpPackagingListMapper.selectErpPackagingListListByIds(ids));
    }

    @Override
    public int insertErpPackagingList(ErpPackagingList erpPackagingList) {
        erpPackagingList.setCreationTime(DateUtils.getNowDate());
        erpPackagingList.setOperator(SecurityUtils.getUsername());
        checkReferences(erpPackagingList);
        return erpPackagingListMapper.insertErpPackagingList(erpPackagingList);
    }

    @Override
    public int updateErpPackagingList(ErpPackagingList erpPackagingList) {
        erpPackagingList.setOperator(SecurityUtils.getUsername());
        checkReferences(erpPackagingList);
        return erpPackagingListMapper.updateErpPackagingList(erpPackagingList);
    }

    @Override
    @Transactional
    public int deleteErpPackagingListByIds(Long[] ids) {
        erpPackagingBagService.deleteCascadeByListIds(ids);
        return erpPackagingListMapper.deleteErpPackagingListByIds(ids);
    }
}
