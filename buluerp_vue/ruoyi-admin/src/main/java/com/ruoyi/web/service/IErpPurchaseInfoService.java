package com.ruoyi.web.service;

import java.io.IOException;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.web.domain.ErpPurchaseInfo;

/**
 * 外购资料，用于存储外购物料的基本信息和相关数据Service接口
 * 
 * @author ruoyi
 * @date 2025-04-08
 */
public interface IErpPurchaseInfoService extends IService<ErpPurchaseInfo> {
    int insertErpPurchaseInfoList(List<ErpPurchaseInfo> list) throws IOException;
    int updateErpPurchaseInfoList(List<ErpPurchaseInfo> list) throws IOException;
}
