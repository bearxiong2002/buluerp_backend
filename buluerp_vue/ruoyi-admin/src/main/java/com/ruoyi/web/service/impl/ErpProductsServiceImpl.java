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
import com.ruoyi.web.domain.ErpOrders;
import com.ruoyi.web.domain.ErpOrdersProduct;
import com.ruoyi.web.enums.OrderStatus;
import com.ruoyi.web.request.order.ListOrderRequest;
import com.ruoyi.web.service.IErpOrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import com.ruoyi.web.mapper.ErpProductInnerCodeMapper;
import com.ruoyi.web.domain.ErpProductInnerCode;
import com.ruoyi.web.service.support.ProductInnerCodes;
import org.springframework.dao.DuplicateKeyException;
import java.util.Collections;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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

    @Autowired
    private ErpProductInnerCodeMapper innerCodeMapper;

    private void fillInnerCodes(List<ErpProducts> products) {
        if (products.isEmpty()) return;
        List<Long> ids = products.stream().map(ErpProducts::getId).collect(Collectors.toList());
        Map<Long, List<ErpProductInnerCode>> grouped = innerCodeMapper.selectByProductIds(ids).stream()
            .collect(Collectors.groupingBy(ErpProductInnerCode::getProductId));
        for (ErpProducts product : products) {
            List<String> codes = new ArrayList<>();
            codes.add(product.getInnerId());
            for (ErpProductInnerCode code : grouped.getOrDefault(product.getId(), Collections.emptyList())) {
                if (!codes.contains(code.getInnerCode())) codes.add(code.getInnerCode());
            }
            product.setInnerIds(codes);
        }
    }

    @Override
    public ErpProducts selectProductDetail(Long id) {
        ErpProducts product = erpProductsMapper.selectById(id);
        return product == null ? null : fillMaterialIds(product);
    }

    private ErpProducts fillMaterialIds(ErpProducts erpProducts) {
        erpProducts.setMaterialIds(erpProductsMapper.getProductMaterialIds(erpProducts.getId()));
        fillInnerCodes(Collections.singletonList(erpProducts));
        return erpProducts;
    }

    private List<ErpProducts> fillMaterialIds(List<ErpProducts> erpProductsList) {
        for(ErpProducts erpProducts:erpProductsList){
            erpProducts.setMaterialIds(erpProductsMapper.getProductMaterialIds(erpProducts.getId()));
        }
        fillInnerCodes(erpProductsList);
        return erpProductsList;
    }

    @Override
    public List<ErpProducts> selectErpProductsList(ListProductRequest listProductRequest) {
        LambdaQueryWrapper<ErpProducts> wrapper= Wrappers.lambdaQuery();
        wrapper.orderByDesc(ErpProducts::getUpdateTime);
        if(listProductRequest.getId()!=null) wrapper.like(ErpProducts::getId,listProductRequest.getId().toString());
        if (StringUtils.isNotBlank(listProductRequest.getInnerId())) {
            String search = listProductRequest.getInnerId().trim();
            wrapper.and(w -> w.like(ErpProducts::getInnerId, search).or().apply(
                "EXISTS (SELECT 1 FROM erp_product_inner_code c WHERE c.product_id=erp_products.id AND c.inner_code LIKE {0})", "%" + search + "%"));
        }
        if(StringUtils.isNotBlank(listProductRequest.getOuterId())) wrapper.like(ErpProducts::getOuterId,listProductRequest.getOuterId());
        if(StringUtils.isNotBlank(listProductRequest.getName())) wrapper.like(ErpProducts::getName,listProductRequest.getName());
        if(StringUtils.isNotBlank(listProductRequest.getCreateUsername())) wrapper.like(ErpProducts::getCreateUsername,listProductRequest.getCreateUsername());
        if(listProductRequest.getCreateTimeTo()!=null) wrapper.lt(ErpProducts::getCreateTime,listProductRequest.getCreateTimeTo());
        if(listProductRequest.getCreateTimeFrom()!=null) wrapper.gt(ErpProducts::getCreateTime,listProductRequest.getCreateTimeFrom());
        if(listProductRequest.getDesignStatus()!=null) wrapper.eq(ErpProducts::getDesignStatus,listProductRequest.getDesignStatus());
        return fillMaterialIds(erpProductsMapper.selectList(wrapper));
    }

    @Override
    public List<ErpProducts> selectErpProductsListByIds(Long[] ids) {
        return fillMaterialIds(erpProductsMapper.selectErpProductsListByIds(ids));
    }

    @Override
    public List<ErpProducts> selectListByIds(List<Integer> ids){
        return fillMaterialIds(erpProductsMapper.selectBatchIds(ids));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertErpProducts(AddProductRequest addProductRequest) throws IOException {
        List<String> codes = ProductInnerCodes.forCreate(addProductRequest.getInnerId(),
            addProductRequest.getInnerIds(), addProductRequest.getAdditionalInnerCodes());
        assertCodesAvailable(null, codes);
        // 获取当前登录用户信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 直接获取用户ID
        Long userId = loginUser.getUserId();

        ErpProducts erpProducts = new ErpProducts();
        if(addProductRequest.getPicture()!=null){
            String url=FileUploadUtils.upload(addProductRequest.getPicture());
            erpProducts.setPictureUrl(url);
        }
        LambdaQueryWrapper<ErpProducts> outerWrapper=Wrappers.lambdaQuery();
        outerWrapper.eq(ErpProducts::getOuterId,addProductRequest.getOuterId());
        if(erpProductsMapper.selectCount(outerWrapper)>0){
            throw new RuntimeException("外部编号已存在");
        }
        erpProducts.setInnerId(codes.get(0));
        erpProducts.setOuterId(addProductRequest.getOuterId());
        erpProducts.setCreateUsername(sysUserMapper.selectUserById(userId).getUserName());
        erpProducts.setName(addProductRequest.getName());
        erpProducts.setCreateTime(LocalDateTime.ofInstant(Instant.now(), ZoneId.of("Asia/Shanghai")));
        erpProducts.setUpdateTime(LocalDateTime.ofInstant(Instant.now(), ZoneId.of("Asia/Shanghai")));
        int inserted;
        try {
            inserted = erpProductsMapper.insert(erpProducts);
        } catch (DuplicateKeyException e) {
            throw new ServiceException("内部编号或外部编号已存在，请刷新后重试");
        }
        if (0 >= inserted) {
            if(addProductRequest.getRowNumber()!=null)
                throw new ImportException(addProductRequest.getRowNumber(), "插入产品失败", addProductRequest.toString());
            else throw new ServiceException("添加失败");
        }
        saveCodes(erpProducts.getId(), codes);
        List<Long> materialIds = addProductRequest.getMaterialIds();
        if (materialIds != null && !materialIds.isEmpty()) {
            // 批量绑定
            List<Integer> intIds = materialIds.stream()
                    .map(Long::intValue)
                    .collect(Collectors.toList());
            if (0 >= erpProductsMapper.insertProductMaterials(erpProducts.getId(), intIds)) {
                if (addProductRequest.getRowNumber() != null)
                    throw new ImportException(addProductRequest.getRowNumber(), "插入物料关联失败", addProductRequest.toString());
                else throw new ServiceException("添加失败");
            }
        } else if (addProductRequest.getMaterialId() != null) {
            // 单物料绑定（兼容旧逻辑）
            if (0 >= erpProductsMapper.insertProductMaterial(erpProducts.getId(), addProductRequest.getMaterialId().intValue())) {
                if (addProductRequest.getRowNumber() != null)
                    throw new ImportException(addProductRequest.getRowNumber(), "插入物料关联失败", addProductRequest.toString());
                else throw new ServiceException("添加失败");
            }
        }
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateErpProducts(UpdateProductRequest updateProductRequest) throws IOException {
        ErpProducts erpProducts = new ErpProducts();
        erpProducts.setId(updateProductRequest.getId());
        ErpProducts original = innerCodeMapper.lockProduct(updateProductRequest.getId());
        if (original == null) throw new ServiceException("产品不存在");
        if (updateProductRequest.getInnerIds() != null) {
            List<String> codes = ProductInnerCodes.normalize(updateProductRequest.getInnerIds());
            if (!original.getInnerId().equals(codes.get(0))) {
                throw new ServiceException("默认内部编号不能修改或删除，请添加附加编号");
            }
            assertCodesAvailable(original.getId(), codes);
            List<ErpProductInnerCode> oldCodes = innerCodeMapper.selectByProductIds(Collections.singletonList(original.getId()));
            for (ErpProductInnerCode oldCode : oldCodes) {
                if (!codes.contains(oldCode.getInnerCode()) && innerCodeMapper.isReferenced(oldCode.getInnerCode())) {
                    throw new ServiceException("内部编号已被业务引用，不能修改或删除：" + oldCode.getInnerCode());
                }
            }
            innerCodeMapper.deleteByProductId(original.getId());
            saveCodes(original.getId(), codes);
        }
        if(Objects.equals(updateProductRequest.getDeletePicture(), 1)&&updateProductRequest.getPicture()==null){
            //删除原先的图片
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
        if (updateProductRequest.getPicture()!=null){
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
        if(!StringUtils.isBlank(updateProductRequest.getName()))erpProducts.setName(updateProductRequest.getName());

        //design_status无法通过这种渠道修改
//        if(updateProductRequest.getDesignStatus()!=null){
//            LambdaQueryWrapper<ErpDesignPatterns> wrapper=Wrappers.lambdaQuery();
//            wrapper.eq(ErpDesignPatterns::getProductId,updateProductRequest.getId());
//            Long designStatus=updateProductRequest.getDesignStatus();
//            erpProductsMapper.updateStatusById(updateProductRequest.getId(),designStatus);
//            if(erpDesignPatternsMapper.selectOne(wrapper)!=null){
//                if(designStatus==1)erpDesignPatternsMapper.confirmErpDesignPatternsById(erpDesignPatternsMapper.selectOne(wrapper).getId());
//                if(designStatus==0)erpDesignPatternsMapper.cancelConfirmById(erpDesignPatternsMapper.selectOne(wrapper).getId());
//            }
//        }

        erpProducts.setUpdateTime(LocalDateTime.ofInstant(Instant.now(), ZoneId.of("Asia/Shanghai")));
        if (0 >= erpProductsMapper.updateById(erpProducts)) {
            throw new ServiceException("修改失败");
        }
        List<Long> materialIds = updateProductRequest.getMaterialIds();
        if (materialIds != null && !materialIds.isEmpty()) {
            // 批量绑定：清空旧关联后批量插入
            erpProductsMapper.clearProductMaterial(erpProducts.getId());
            List<Integer> intIds = materialIds.stream()
                    .map(Long::intValue)
                    .collect(Collectors.toList());
            if (0 >= erpProductsMapper.insertProductMaterials(erpProducts.getId(), intIds)) {
                throw new ServiceException("添加失败");
            }
        } else {
            Long materialId = updateProductRequest.getMaterialId();
            if (materialId != null) {
                erpProductsMapper.clearProductMaterial(erpProducts.getId());
                if (0 >= erpProductsMapper.insertProductMaterial(erpProducts.getId(), materialId.intValue())) {
                    throw new ServiceException("添加失败");
                }
            }
        }
        
        return 1;
    }

    @Override
    public void processMaterialIds(AddProductRequest item) {

        if (item.getMaterialString() == null || item.getMaterialString().trim().isEmpty()) {
            return;
        }

        // 数据清洗：兼容中文逗号、空格，解析所有ID
        String normalized = item.getMaterialString()
                .replace("，", ",")
                .replaceAll("\\s+", "");

        String[] parts = normalized.split(",");
        List<Long> ids = new ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                try {
                    ids.add(Long.parseLong(trimmed));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        if (!ids.isEmpty()) {
            item.setMaterialId(ids.get(0)); // 兼容旧的单值逻辑
            item.setMaterialIds(ids);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteErpProductsByIds(List<Integer> ids) {
        List<ErpProducts> erpProductsList=erpProductsMapper.selectBatchIds(ids);
        for(ErpProducts erpProducts:erpProductsList){
            erpPackagingListMapper.deleteErpPackagingListByProductId(erpProducts.getInnerId());
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

    private void assertCodesAvailable(Long productId, List<String> codes) {
        if (innerCodeMapper.countConflicts(productId, codes) > 0) {
            throw new ServiceException("内部编号已被其他产品使用，请检查编号列表");
        }
    }

    private void saveCodes(Long productId, List<String> codes) {
        try {
            innerCodeMapper.insertCodes(productId, codes);
        } catch (DuplicateKeyException e) {
            throw new ServiceException("内部编号已被其他产品使用，请刷新后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int importProducts(List<AddProductRequest> requests) throws IOException {
        java.util.Set<String> seen = new java.util.HashSet<>();
        for (int i = 0; i < requests.size(); i++) {
            AddProductRequest request = requests.get(i);
            try {
                List<String> codes = ProductInnerCodes.forCreate(request.getInnerId(), request.getInnerIds(), request.getAdditionalInnerCodes());
                for (String code : codes) {
                    if (!seen.add(code)) throw new ServiceException("文件中内部编号重复：" + code);
                }
                assertCodesAvailable(null, codes);
            } catch (ServiceException e) {
                throw new ServiceException("Excel第" + (i + 2) + "行：" + e.getMessage());
            }
        }
        for (AddProductRequest request : requests) {
            processMaterialIds(request);
            insertErpProducts(request);
        }
        return requests.size();
    }

    @Override
    public ErpProducts findByInnerCode(String code) {
        if (StringUtils.isBlank(code)) return null;
        ErpProducts product = innerCodeMapper.selectProductByCode(code.trim());
        if (product != null) fillInnerCodes(Collections.singletonList(product));
        return product;
    }

    @Override
    public String requireDefaultInnerCode(String code) {
        ErpProducts product = findByInnerCode(code);
        if (product == null) throw new ServiceException("产品不存在：" + code);
        return product.getInnerId();
    }

    @Override
    public Long getIdByInnerId(String innerId) {
        ErpProducts product = findByInnerCode(innerId);
        return product == null ? null : product.getId();
    }
}
