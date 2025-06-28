package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.web.domain.ErpDesignPatterns;
import com.ruoyi.web.domain.ErpProducts;
import com.ruoyi.web.exception.ImportException;
import com.ruoyi.web.mapper.ErpDesignPatternsMapper;
import com.ruoyi.web.mapper.ErpOrdersMapper;
import com.ruoyi.web.mapper.ErpPackagingListMapper;
import com.ruoyi.web.mapper.ErpProductsMapper;
import com.ruoyi.web.request.product.AddProductRequest;
import com.ruoyi.web.request.product.ListProductRequest;
import com.ruoyi.web.request.product.UpdateProductRequest;
import com.ruoyi.web.service.IErpProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ErpProductsServiceImpl extends ServiceImpl<ErpProductsMapper, ErpProducts> implements IErpProductsService {

    @Autowired
    private ErpProductsMapper erpProductsMapper;

    @Autowired
    private ErpDesignPatternsMapper erpDesignPatternsMapper;

    @Autowired
    private ErpPackagingListMapper erpPackagingListMapper;

    @Autowired
    private ErpOrdersMapper erpOrdersMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    private ErpProducts fillMaterialIds(ErpProducts erpProducts) {
        erpProducts.setMaterialIds(erpProductsMapper.getProductMaterialIds(erpProducts.getId()));
        return erpProducts;
    }

    private List<ErpProducts> fillMaterialIds(List<ErpProducts> erpProductsList) {
        for(ErpProducts erpProducts:erpProductsList){
            erpProducts.setMaterialIds(erpProductsMapper.getProductMaterialIds(erpProducts.getId()));
        }
        return erpProductsList;
    }

    @Override
    public List<ErpProducts> selectErpProductsList(ListProductRequest listProductRequest) {
        LambdaQueryWrapper<ErpProducts> wrapper= Wrappers.lambdaQuery();
        if(listProductRequest.getId()!=null) wrapper.eq(ErpProducts::getId,listProductRequest.getId());
        if(StringUtils.isNotBlank(listProductRequest.getInnerId())) wrapper.like(ErpProducts::getInnerId,listProductRequest.getInnerId());
        if(StringUtils.isNotBlank(listProductRequest.getOuterId())) wrapper.like(ErpProducts::getOuterId,listProductRequest.getOuterId());
        if(StringUtils.isNotBlank(listProductRequest.getName())) wrapper.like(ErpProducts::getName,listProductRequest.getName());
        if(StringUtils.isNotBlank(listProductRequest.getCreateUsername())) wrapper.like(ErpProducts::getCreateUsername,listProductRequest.getCreateUsername());
        if(listProductRequest.getCreateTimeTo()!=null) wrapper.lt(ErpProducts::getCreateTime,listProductRequest.getCreateTimeTo());
        if(listProductRequest.getCreateTimeFrom()!=null) wrapper.gt(ErpProducts::getCreateTime,listProductRequest.getCreateTimeFrom());
        if(listProductRequest.getDesignStatus()!=null) wrapper.eq(ErpProducts::getDesignStatus,listProductRequest.getDesignStatus());
        if(listProductRequest.getOrderId()!=null) wrapper.eq(ErpProducts::getOrderId,listProductRequest.getOrderId());
        return fillMaterialIds(erpProductsMapper.selectList(wrapper));
    }

    @Override
    public List<ErpProducts> selectErpProductsListByIds(Long[] ids) {
        return fillMaterialIds(erpProductsMapper.selectErpProductsListByIds(ids));
    }

    @Override
    @Transactional
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
        LambdaQueryWrapper<ErpProducts> innerWrapper=Wrappers.lambdaQuery();
        innerWrapper.eq(ErpProducts::getInnerId,addProductRequest.getInnerId());
        LambdaQueryWrapper<ErpProducts> outerWrapper=Wrappers.lambdaQuery();
        outerWrapper.eq(ErpProducts::getOuterId,addProductRequest.getOuterId());
        if(erpProductsMapper.selectCount(innerWrapper)>0){
            throw new RuntimeException("内部编号已存在");
        }
        if(erpProductsMapper.selectCount(outerWrapper)>0){
            throw new RuntimeException("外部编号已存在");
        }
        erpProducts.setInnerId(addProductRequest.getInnerId());
        erpProducts.setOuterId(addProductRequest.getOuterId());
        erpProducts.setCreateUsername(sysUserMapper.selectUserById(userId).getUserName());
        erpProducts.setOrderId(addProductRequest.getOrderId());
        erpProducts.setName(addProductRequest.getName());
        erpProducts.setCreateTime(LocalDateTime.now());
        erpProducts.setUpdateTime(LocalDateTime.now());
        if (0 >= erpProductsMapper.insert(erpProducts)) {
            if(addProductRequest.getRowNumber()!=null)
                throw new ImportException(addProductRequest.getRowNumber(), "插入产品失败", addProductRequest.toString());
            else throw new ServiceException("添加失败");
        }
        for (Integer materialId : addProductRequest.getMaterialIds()) {
            if (0 >= erpProductsMapper.insertProductMaterial(erpProducts.getId(), materialId)) {
                if(addProductRequest.getRowNumber()!=null)
                    throw new ImportException(addProductRequest.getRowNumber(), "插入物料关联失败", addProductRequest.toString());
                else throw new ServiceException("添加失败");
            }
        }
        return 1;
    }

    @Override
    @Transactional
    public int updateErpProducts(UpdateProductRequest updateProductRequest) throws IOException {
        ErpProducts erpProducts = new ErpProducts();
        erpProducts.setId(updateProductRequest.getId());
        if(updateProductRequest.getPicture()!=null){
            //删除原先的图片
            String url1=erpProductsMapper.selectById(erpProducts.getId()).getPictureUrl();
            if(url1!=null){
                url1=parseActualPath(url1);
                FileUtils.deleteFile(url1);
            }
            //保存后来的图片
            String url=FileUploadUtils.upload(updateProductRequest.getPicture());
            erpProducts.setPictureUrl(url);
        }
        else{
            String url1=erpProductsMapper.selectById(erpProducts.getId()).getPictureUrl();
            if(url1!=null){
                url1=parseActualPath(url1);
                FileUtils.deleteFile(url1);
            }
            LambdaUpdateWrapper<ErpProducts> lambdaWrapper = new LambdaUpdateWrapper<>();
            lambdaWrapper.set(ErpProducts::getPictureUrl, null)
                    .eq(ErpProducts::getId, updateProductRequest.getId());
            erpProductsMapper.update(null,lambdaWrapper);
        }
        if(!StringUtils.isBlank(updateProductRequest.getName()))erpProducts.setName(updateProductRequest.getName());
        if(updateProductRequest.getOrderId()!=null) erpProducts.setOrderId(updateProductRequest.getOrderId());
        if(updateProductRequest.getDesignStatus()!=null){
            LambdaQueryWrapper<ErpDesignPatterns> wrapper=Wrappers.lambdaQuery();
            wrapper.eq(ErpDesignPatterns::getProductId,updateProductRequest.getId());
            Long designStatus=updateProductRequest.getDesignStatus();
            erpProductsMapper.updateStatusById(updateProductRequest.getId(),designStatus);
            if(erpDesignPatternsMapper.selectOne(wrapper)!=null){
                if(designStatus==1)erpDesignPatternsMapper.confirmErpDesignPatternsById(erpDesignPatternsMapper.selectOne(wrapper).getId());
                if(designStatus==0)erpDesignPatternsMapper.cancelConfirmById(erpDesignPatternsMapper.selectOne(wrapper).getId());
            }
        }


        erpProducts.setUpdateTime(LocalDateTime.now());
        if (0 >= erpProductsMapper.updateById(erpProducts)) {
            throw new ServiceException("修改失败");
        }
        List<Integer> materialIds = updateProductRequest.getMaterialIds();
        if (materialIds != null) {
            erpProductsMapper.clearProductMaterial(erpProducts.getId());
            for (Integer materialId : materialIds) {
                if (0 >= erpProductsMapper.insertProductMaterial(erpProducts.getId(), materialId)) {
                    throw new ServiceException("添加失败");
                }
            }
        }
        return 1;
    }

    @Override
    public void processMaterialIds(AddProductRequest item) {

        // 数据清洗：兼容中文逗号、空格
        String normalized = item.getMaterialString()
                .replace("，", ",")  // 中文逗号转英文
                .replaceAll("\\s+", "");  // 去除所有空格

        List<Integer> ids = Arrays.stream(normalized.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())  // 过滤空字符串
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        item.setMaterialIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteErpProductsByIds(List<Integer> ids) {
        List<ErpProducts> erpProductsList=erpProductsMapper.selectBatchIds(ids);
        for(ErpProducts erpProducts:erpProductsList){
            erpPackagingListMapper.deleteErpPackagingListByProductId(erpProducts.getId());
            erpProductsMapper.clearProductMaterial(erpProducts.getId());
            erpOrdersMapper.clearOrdersProductsByProduct(erpProducts.getId());
            String url=erpProducts.getPictureUrl();
            if(!StringUtils.isBlank(url)){
                try {
                    url=parseActualPath(url);
                    FileUtils.deleteFile(url);
                } catch (Exception e) {
                    // 记录错误日志但不中断删除流程
                    System.err.println("删除产品文件失败，产品ID: " + erpProducts.getId() + ", 文件URL: " + url + ", 错误: " + e.getMessage());
                }
            }
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