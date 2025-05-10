package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
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
            File file=new File(url);
            if (file.exists() && file.isFile()) {
                boolean deleted = file.delete();
                if (deleted) {
                    System.out.println(url+"File deleted successfully.");
                } else {
                    System.out.println(url+"Failed to delete file.");
                }
            } else {
                System.out.println(url+"File does not exist.");
            }
        }
        return erpProductsMapper.deleteBatchIds(ids);
    }
}