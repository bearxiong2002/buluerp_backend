package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.web.domain.ErpDesignPatterns;
import com.ruoyi.web.domain.ErpProducts;
import com.ruoyi.web.mapper.ErpDesignPatternsMapper;
import com.ruoyi.web.mapper.ErpProductsMapper;
import com.ruoyi.web.request.product.AddProductRequest;
import com.ruoyi.web.request.product.ListProductRequest;
import com.ruoyi.web.request.product.UpdateProductRequest;
import com.ruoyi.web.service.IErpProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ErpProductsServiceImpl extends ServiceImpl<ErpProductsMapper, ErpProducts> implements IErpProductsService {

    @Autowired
    private ErpProductsMapper erpProductsMapper;

    @Autowired
    private ErpDesignPatternsMapper erpDesignPatternsMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public List<ErpProducts> selectErpProductsList(ListProductRequest listProductRequest) {
        LambdaQueryWrapper<ErpProducts> wrapper= Wrappers.lambdaQuery();
        if(listProductRequest.getId()!=null) wrapper.eq(ErpProducts::getId,listProductRequest.getId());
        if(!StringUtils.isBlank(listProductRequest.getName())) wrapper.like(ErpProducts::getName,listProductRequest.getName());
        if(!StringUtils.isBlank(listProductRequest.getCreateUsername())) wrapper.like(ErpProducts::getCreateUsername,listProductRequest.getCreateUsername());
        if(listProductRequest.getCreateTimeTo()!=null) wrapper.lt(ErpProducts::getCreateTime,listProductRequest.getCreateTimeTo());
        if(listProductRequest.getCreateTimeFrom()!=null) wrapper.gt(ErpProducts::getCreateTime,listProductRequest.getCreateTimeFrom());
        if(listProductRequest.getDesignStatus()!=null) wrapper.eq(ErpProducts::getDesignStatus,listProductRequest.getDesignStatus());
        return erpProductsMapper.selectList(wrapper);
    }

    @Override
    public int insertErpProducts(AddProductRequest addProductRequest) throws IOException {
        // 获取当前登录用户信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 直接获取用户ID
        Long userId = loginUser.getUserId();

        ErpProducts erpProducts = new ErpProducts();
        if(addProductRequest.getPicture()!=null){
            String url=FileUploadUtils.upload(addProductRequest.getPicture());
            erpProducts.setPictureUrl(url);
        }
        erpProducts.setCreateUsername(sysUserMapper.selectUserById(userId).getUserName());
        erpProducts.setName(addProductRequest.getName());
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
        if(updateProductRequest.getDesignStatus()!=null){
            LambdaQueryWrapper<ErpDesignPatterns> wrapper=Wrappers.lambdaQuery();
            wrapper.eq(ErpDesignPatterns::getProductId,updateProductRequest.getId());

            erpProductsMapper.updateStatusById(updateProductRequest.getId(),1L);
            if(updateProductRequest.getDesignStatus()==1)erpDesignPatternsMapper.confirmErpDesignPatternsById(erpDesignPatternsMapper.selectOne(wrapper).getId());
            if(updateProductRequest.getDesignStatus()==0)erpDesignPatternsMapper.cancelConfirmById(erpDesignPatternsMapper.selectOne(wrapper).getId());
        }


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