package com.ruoyi.web.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.web.domain.ErpPurchaseCollection;
import com.ruoyi.web.mapper.ErpPurchaseCollectionMapper;
import com.ruoyi.web.service.IErpPurchaseCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
    public int updateErpPurchaseCollection(ErpPurchaseCollection erpPurchaseCollection) throws IOException {
        erpPurchaseCollection.setUpdateTime(DateUtils.getNowDate());
        erpPurchaseCollection.setOperator(
                SecurityUtils.getLoginUser().getUsername()
        );
        if (erpPurchaseCollection.getPicture() != null) {
            erpPurchaseCollection.setPictureUrl(
                    FileUploadUtils.upload(erpPurchaseCollection.getPicture())
            );
        }
        return erpPurchaseCollectionMapper.updateErpPurchaseCollection(erpPurchaseCollection);
    }

    @Override
    public int insertErpPurchaseCollection(ErpPurchaseCollection erpPurchaseCollection) throws IOException {
        erpPurchaseCollection.setCreateTime(DateUtils.getNowDate());
        erpPurchaseCollection.setUpdateTime(DateUtils.getNowDate());
        erpPurchaseCollection.setOperator(
                SecurityUtils.getLoginUser().getUsername()
        );
        if (erpPurchaseCollection.getPicture() != null) {
            erpPurchaseCollection.setPictureUrl(
                    FileUploadUtils.upload(erpPurchaseCollection.getPicture())
            );
        }
        return erpPurchaseCollectionMapper.insertErpPurchaseCollection(erpPurchaseCollection);
    }

    @Override
    @Transactional
    public int insertErpPurchaseCollections(List<ErpPurchaseCollection> erpPurchaseCollectionList) throws IOException {
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
