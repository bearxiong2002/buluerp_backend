package com.ruoyi.web.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.web.domain.ErpPackagingList;
import com.ruoyi.web.mapper.ErpPackagingListMapper;
import com.ruoyi.web.service.IErpPackagingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ErpPackagingListServiceImpl implements IErpPackagingListService {
    @Autowired
    private ErpPackagingListMapper erpPackagingListMapper;

    @Override
    public ErpPackagingList selectErpPackagingListById(Integer id) {
        return erpPackagingListMapper.selectErpPackagingListById(id);
    }

    @Override
    public List<ErpPackagingList> selectErpPackagingListList(ErpPackagingList erpPackagingList) {
        return erpPackagingListMapper.selectErpPackagingListList(erpPackagingList);
    }

    @Override
    public List<ErpPackagingList> selectErpPackagingListListByIds(Integer[] ids) {
        return erpPackagingListMapper.selectErpPackagingListListByIds(ids);
    }

    @Override
    public int insertErpPackagingList(ErpPackagingList erpPackagingList) {
        erpPackagingList.setCreationTime(DateUtils.getNowDate());
        return erpPackagingListMapper.insertErpPackagingList(erpPackagingList);
    }

    @Override
    public int updateErpPackagingList(ErpPackagingList erpPackagingList) {
        return erpPackagingListMapper.updateErpPackagingList(erpPackagingList);
    }

    @Override
    public int deleteErpPackagingListById(Integer id) {
        return erpPackagingListMapper.deleteErpPackagingListById(id);
    }

    @Override
    public int deleteErpPackagingListByIds(Integer[] ids) {
        return erpPackagingListMapper.deleteErpPackagingListByIds(ids);
    }
}
