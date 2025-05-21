package com.ruoyi.web.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.web.domain.ErpPurchaseCollection;
import com.ruoyi.web.mapper.ErpPurchaseCollectionMapper;
import com.ruoyi.web.service.IErpPurchaseCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ErpPurchaseCollectionServiceImpl implements IErpPurchaseCollectionService {
    @Autowired
    private ErpPurchaseCollectionMapper erpPurchaseCollectionMapper;

    @Override
    public int deleteErpPurchaseCollectionById(Long id) {
        return erpPurchaseCollectionMapper.deleteErpPurchaseCollectionById(id);
    }

    @Override
    public int deleteErpPurchaseCollectionByIds(Long[] ids) {
        return erpPurchaseCollectionMapper.deleteErpPurchaseCollectionByIds(ids);
    }

    @Override
    public int updateErpPurchaseCollection(ErpPurchaseCollection erpPurchaseCollection) {
        erpPurchaseCollection.setUpdateTime(DateUtils.getNowDate());
        erpPurchaseCollection.setOperator(
                SecurityUtils.getLoginUser().getUsername()
        );
        return erpPurchaseCollectionMapper.updateErpPurchaseCollection(erpPurchaseCollection);
    }

    @Override
    public int insertErpPurchaseCollection(ErpPurchaseCollection erpPurchaseCollection) {
        erpPurchaseCollection.setCreateTime(DateUtils.getNowDate());
        erpPurchaseCollection.setUpdateTime(DateUtils.getNowDate());
        erpPurchaseCollection.setOperator(
                SecurityUtils.getLoginUser().getUsername()
        );
        return erpPurchaseCollectionMapper.insertErpPurchaseCollection(erpPurchaseCollection);
    }

    @Override
    @Transactional
    public int insertErpPurchaseCollections(List<ErpPurchaseCollection> erpPurchaseCollectionList) {
        int count = 0;
        for (ErpPurchaseCollection erpPurchaseCollectionItem : erpPurchaseCollectionList) {
            count += insertErpPurchaseCollection(erpPurchaseCollectionItem);
        }
        return count;
    }

    @Override
    public ErpPurchaseCollection selectErpPurchaseCollectionById(Long id) {
        return erpPurchaseCollectionMapper.selectErpPurchaseCollectionById(id);
    }

    @Override
    public List<ErpPurchaseCollection> selectErpPurchaseCollectionList(ErpPurchaseCollection erpPurchaseCollection) {
        return erpPurchaseCollectionMapper.selectErpPurchaseCollectionList(erpPurchaseCollection);
    }

    @Override
    public List<ErpPurchaseCollection> selectErpPurchaseCollectionListByIds(Long[] ids) {
        return erpPurchaseCollectionMapper.selectErpPurchaseCollectionListByIds(ids);
    }
}
