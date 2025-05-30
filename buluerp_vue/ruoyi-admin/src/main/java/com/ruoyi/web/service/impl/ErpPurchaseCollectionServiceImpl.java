package com.ruoyi.web.service.impl;

import com.ruoyi.common.exception.ServiceException;
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
    @Transactional
    public int deleteErpPurchaseCollectionById(Long id) {
        erpPurchaseCollectionMapper.clearErpPurchaseCollectionMaterials(id);
        return erpPurchaseCollectionMapper.deleteErpPurchaseCollectionById(id);
    }

    @Override
    @Transactional
    public int deleteErpPurchaseCollectionByIds(Long[] ids) {
        for (Long id : ids) {
            erpPurchaseCollectionMapper.clearErpPurchaseCollectionMaterials(id);
        }
        return erpPurchaseCollectionMapper.deleteErpPurchaseCollectionByIds(ids);
    }

    @Override
    @Transactional
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
        if (0 >= erpPurchaseCollectionMapper.updateErpPurchaseCollection(erpPurchaseCollection)) {
            throw new ServiceException("更新失败");
        }
        if (erpPurchaseCollection.getMaterialIds() != null) {
            erpPurchaseCollectionMapper.clearErpPurchaseCollectionMaterials(erpPurchaseCollection.getId());
            erpPurchaseCollectionMapper.insertErpPurchaseCollectionMaterials(
                    erpPurchaseCollection.getId(),
                    erpPurchaseCollection.getMaterialIds()
            );
        }
        return 1;
    }

    @Override
    @Transactional
    public int insertErpPurchaseCollection(ErpPurchaseCollection erpPurchaseCollection) throws IOException {
        erpPurchaseCollection.setCreateTime(DateUtils.getNowDate());
        erpPurchaseCollection.setUpdateTime(DateUtils.getNowDate());
        erpPurchaseCollection.setCreationTime(DateUtils.getNowDate());
        erpPurchaseCollection.setOperator(
                SecurityUtils.getLoginUser().getUsername()
        );
        if (erpPurchaseCollection.getPicture() != null) {
            erpPurchaseCollection.setPictureUrl(
                    FileUploadUtils.upload(erpPurchaseCollection.getPicture())
            );
        }
        if (0 >= erpPurchaseCollectionMapper.insertErpPurchaseCollection(erpPurchaseCollection)) {
            throw new ServiceException("添加失败");
        }
        if (erpPurchaseCollection.getMaterialIds() != null) {
            erpPurchaseCollectionMapper.insertErpPurchaseCollectionMaterials(
                    erpPurchaseCollection.getId(),
                    erpPurchaseCollection.getMaterialIds()
            );
        }
        return 1;
    }

    @Override
    @Transactional
    public int insertErpPurchaseCollections(List<ErpPurchaseCollection> erpPurchaseCollectionList) throws IOException {
        int count = 0;
        for (ErpPurchaseCollection erpPurchaseCollection : erpPurchaseCollectionList) {
            count += insertErpPurchaseCollection(erpPurchaseCollection);
        }
        return count;
    }

    private ErpPurchaseCollection fillMaterialIds(ErpPurchaseCollection erpPurchaseCollection) {
        erpPurchaseCollection.setMaterialIds(
                erpPurchaseCollectionMapper
                        .getErpPurchaseCollectionMaterialIds(erpPurchaseCollection.getId())
        );
        return erpPurchaseCollection;
    }

    private List<ErpPurchaseCollection> fillMaterialIds(List<ErpPurchaseCollection> erpPurchaseCollectionList) {
        for (ErpPurchaseCollection erpPurchaseCollection : erpPurchaseCollectionList) {
            fillMaterialIds(erpPurchaseCollection);
        }
        return erpPurchaseCollectionList;
    }

    @Override
    public ErpPurchaseCollection selectErpPurchaseCollectionById(Long id) {
        return fillMaterialIds(erpPurchaseCollectionMapper.selectErpPurchaseCollectionById(id));
    }

    @Override
    public List<ErpPurchaseCollection> selectErpPurchaseCollectionList(ErpPurchaseCollection erpPurchaseCollection) {
        return fillMaterialIds(erpPurchaseCollectionMapper.selectErpPurchaseCollectionList(erpPurchaseCollection));
    }

    @Override
    public List<ErpPurchaseCollection> selectErpPurchaseCollectionListByIds(Long[] ids) {
        return fillMaterialIds(erpPurchaseCollectionMapper.selectErpPurchaseCollectionListByIds(ids));
    }
}
