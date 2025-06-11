package com.ruoyi.web.service.impl;

import java.io.IOException;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.web.mapper.ErpPurchaseInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.web.domain.ErpPurchaseInfo;
import com.ruoyi.web.service.IErpPurchaseInfoService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 外购资料，用于存储外购物料的基本信息和相关数据Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-04-08
 */
@Service
public class ErpPurchaseInfoServiceImpl
        extends ServiceImpl<ErpPurchaseInfoMapper, ErpPurchaseInfo>
        implements IErpPurchaseInfoService
{
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertErpPurchaseInfoList(List<ErpPurchaseInfo> list) throws IOException {
        int count = 0;
        for (ErpPurchaseInfo erpPurchaseInfo : list) {
            if (erpPurchaseInfo.getPicture() != null) {
                String url = FileUploadUtils.upload(erpPurchaseInfo.getPicture());
                erpPurchaseInfo.setPictureUrl(url);
            }
            count += baseMapper.insert(erpPurchaseInfo);
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateErpPurchaseInfoList(List<ErpPurchaseInfo> list) throws IOException {
        int count = 0;
        for (ErpPurchaseInfo erpPurchaseInfo : list) {
            if (erpPurchaseInfo.getPicture() != null) {
                String url = FileUploadUtils.upload(erpPurchaseInfo.getPicture());
                erpPurchaseInfo.setPictureUrl(url);
            }
            count += baseMapper.updateById(erpPurchaseInfo);
        }
        return count;
    }
}
