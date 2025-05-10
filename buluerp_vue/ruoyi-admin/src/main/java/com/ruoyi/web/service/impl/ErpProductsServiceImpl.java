package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.web.domain.ErpOrders;
import com.ruoyi.web.domain.ErpProducts;
import com.ruoyi.web.domain.ErpPurchaseOrderInvoice;
import com.ruoyi.web.mapper.ErpProductsMapper;
import com.ruoyi.web.request.product.AddProductRequest;
import com.ruoyi.web.request.product.ListProductRequest;
import com.ruoyi.web.request.product.UpdateProductRequest;
import com.ruoyi.web.service.IErpProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ErpProductsServiceImpl extends ServiceImpl<ErpProductsMapper, ErpProducts> implements IErpProductsService {

    @Autowired
    private ErpProductsMapper erpProductsMapper;

    @Override
    public List<ErpProducts> selectErpProductsList(ListProductRequest listProductRequest) {
        LambdaQueryWrapper<ErpProducts> wrapper= Wrappers.lambdaQuery();
        if(!StringUtils.isBlank(listProductRequest.getName())) wrapper.like(ErpProducts::getName,listProductRequest.getName());
        return erpProductsMapper.selectList(wrapper);
    }

    @Override
    public int insertErpProducts(AddProductRequest addProductRequest) throws IOException {
        // 获取当前登录用户信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 直接获取用户ID
        Long userId = loginUser.getUserId();

        String url=FileUploadUtils.upload(addProductRequest.getPicture());
        ErpProducts erpProducts = new ErpProducts();
        erpProducts.setName(addProductRequest.getName());
        erpProducts.setPictureUrl(url);
        erpProducts.setCreateTime(LocalDateTime.now());
        erpProducts.setUpdateTime(LocalDateTime.now());
        return erpProductsMapper.insert(erpProducts);
    }

    @Override
    public int updateErpProducts(UpdateProductRequest updateProductRequest) throws IOException {
        ErpProducts erpProducts = new ErpProducts();
        erpProducts.setId(updateProductRequest.getId());
        if(updateProductRequest.getPicture()!=null){
            //删除原先的图片
            String url1=erpProductsMapper.selectById(erpProducts.getId()).getPictureUrl();
            url1=parseActualPath(url1);
            FileUtils.deleteFile(url1);
            //保存后来的图片
            String url=FileUploadUtils.upload(updateProductRequest.getPicture());
            erpProducts.setPictureUrl(url);
        }
        if(!StringUtils.isBlank(updateProductRequest.getName()))erpProducts.setName(updateProductRequest.getName());
        erpProducts.setUpdateTime(LocalDateTime.now());
        return erpProductsMapper.updateById(erpProducts);
    }

    @Override
    public int deleteErpProductsByIds(List<Integer> ids) {
        List<ErpProducts> erpProductsList=erpProductsMapper.selectBatchIds(ids);
        for(ErpProducts erpProducts:erpProductsList){
            String url=erpProducts.getPictureUrl();
            url=parseActualPath(url);
            FileUtils.deleteFile(url);
        }
        return erpProductsMapper.deleteBatchIds(ids);
    }

    public static String parseActualPath(String url) {
        // 1. 验证URL是否以资源前缀开头
        if (!url.startsWith(Constants.RESOURCE_PREFIX)) {
            throw new IllegalArgumentException("文件URL必须以 " + Constants.RESOURCE_PREFIX + " 开头");
        }

        // 2. 移除资源前缀（保留后续路径部分）
        String relativePath = url.substring(Constants.RESOURCE_PREFIX.length());

        // 3. 拼接实际存储路径
        return Paths.get(
                RuoYiConfig.getProfile(), // 基础路径（如 D:/ruoyi/uploadPath）
                relativePath.split("/")   // 拆分路径部分（如 ["", "2025", "05", "10", "xxx.txt"]）
        ).toString();
    }
}