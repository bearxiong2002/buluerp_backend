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
import com.ruoyi.web.domain.ErpPurchaseOrder;
import com.ruoyi.web.domain.ErpPurchaseOrderInvoice;
import com.ruoyi.web.mapper.ErpPurchaseOrderInvoiceMapper;
import com.ruoyi.web.mapper.ErpPurchaseOrderMapper;
import com.ruoyi.web.request.purchase.AddPurchaseOrderRequest;
import com.ruoyi.web.request.purchase.ListPurchaseOrderRequest;
import com.ruoyi.web.request.purchase.UpdatePurchaseOrderRequest;
import com.ruoyi.web.result.PurchaseOrderResult;
import com.ruoyi.web.service.IErpPurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 采购订单Service业务层处理
 *
 * @author ruoyi
 * @date 2025-04-09
 */
@Service
public class ErpPurchaseOrderServiceImpl extends ServiceImpl<ErpPurchaseOrderMapper, ErpPurchaseOrder> implements IErpPurchaseOrderService {

    @Autowired
    private ErpPurchaseOrderMapper erpPurchaseOrderMapper;

    @Autowired
    private ErpPurchaseOrderInvoiceMapper invoiceMapper;

    @Override
    public PurchaseOrderResult selectErpPurchaseOrderById(Integer id) {
        ErpPurchaseOrder erpPurchaseOrder= erpPurchaseOrderMapper.selectById(id);
        PurchaseOrderResult purchaseOrderResult =new PurchaseOrderResult();
        purchaseOrderResult.setId(erpPurchaseOrder.getId());
        purchaseOrderResult.setAmount(erpPurchaseOrder.getAmount());
        purchaseOrderResult.setPurchaseId(erpPurchaseOrder.getPurchaseId());
        purchaseOrderResult.setCreateTime(erpPurchaseOrder.getCreateTime());
        purchaseOrderResult.setCreateUser(erpPurchaseOrder.getCreateUser());
        LambdaQueryWrapper<ErpPurchaseOrderInvoice> wrapper=Wrappers.lambdaQuery();
        wrapper.eq(ErpPurchaseOrderInvoice::getOrderId,erpPurchaseOrder.getId());
        purchaseOrderResult.setInvoice(invoiceMapper.selectList(wrapper));
        return purchaseOrderResult;
    }

    @Override
    public List<PurchaseOrderResult> selectErpPurchaseOrderList(ListPurchaseOrderRequest listPurchaseOrderRequest) {
        LambdaQueryWrapper<ErpPurchaseOrder> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(listPurchaseOrderRequest.getId()!=null,ErpPurchaseOrder::getId,listPurchaseOrderRequest.getId())
                .eq(listPurchaseOrderRequest.getPurchaseId()!=null,ErpPurchaseOrder::getPurchaseId,listPurchaseOrderRequest.getPurchaseId())
                .like(StringUtils.isNotBlank(listPurchaseOrderRequest.getCreateUser()),ErpPurchaseOrder::getCreateUser,listPurchaseOrderRequest.getCreateUser())
                .eq(listPurchaseOrderRequest.getAmount()!=null,ErpPurchaseOrder::getAmount,listPurchaseOrderRequest.getAmount());
        List<PurchaseOrderResult> list=new ArrayList<>();
        for(ErpPurchaseOrder erpPurchaseOrder:erpPurchaseOrderMapper.selectList(queryWrapper)){
            PurchaseOrderResult purchaseOrderResult =new PurchaseOrderResult();
            purchaseOrderResult.setId(erpPurchaseOrder.getId());
            purchaseOrderResult.setAmount(erpPurchaseOrder.getAmount());
            purchaseOrderResult.setPurchaseId(erpPurchaseOrder.getPurchaseId());
            purchaseOrderResult.setCreateTime(erpPurchaseOrder.getCreateTime());
            purchaseOrderResult.setCreateUser(erpPurchaseOrder.getCreateUser());
            LambdaQueryWrapper<ErpPurchaseOrderInvoice> wrapper=Wrappers.lambdaQuery();
            wrapper.eq(ErpPurchaseOrderInvoice::getOrderId,erpPurchaseOrder.getId());
            purchaseOrderResult.setInvoice(invoiceMapper.selectList(wrapper));
            list.add(purchaseOrderResult);
        }
        return list;
    }

    @Override
    public List<PurchaseOrderResult> selectErpPurchaseOrderListByIds(Integer[] ids) {
        List<PurchaseOrderResult> list=new ArrayList<>();
        for(ErpPurchaseOrder erpPurchaseOrder:erpPurchaseOrderMapper.selectErpPurchaseOrderListByIds(ids)){
            PurchaseOrderResult purchaseOrderResult =new PurchaseOrderResult();
            purchaseOrderResult.setId(erpPurchaseOrder.getId());
            purchaseOrderResult.setAmount(erpPurchaseOrder.getAmount());
            purchaseOrderResult.setPurchaseId(erpPurchaseOrder.getPurchaseId());
            purchaseOrderResult.setCreateTime(erpPurchaseOrder.getCreateTime());
            purchaseOrderResult.setCreateUser(erpPurchaseOrder.getCreateUser());
            LambdaQueryWrapper<ErpPurchaseOrderInvoice> wrapper=Wrappers.lambdaQuery();
            wrapper.eq(ErpPurchaseOrderInvoice::getOrderId,erpPurchaseOrder.getId());
            purchaseOrderResult.setInvoice(invoiceMapper.selectList(wrapper));
            list.add(purchaseOrderResult);
        }
        return list;
    }

    @Override
    public int insertErpPurchaseOrder(AddPurchaseOrderRequest addPurchaseOrderRequest) throws IOException {
        // 获取当前登录用户信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 直接获取用户ID
        Long userId = loginUser.getUserId();

        ErpPurchaseOrder erpPurchaseOrder = new ErpPurchaseOrder();
        erpPurchaseOrder.setCreateTime(LocalDateTime.now());
        erpPurchaseOrder.setCreateUser(SecurityUtils.getUsername());
        erpPurchaseOrder.setPurchaseId(addPurchaseOrderRequest.getPurchaseId());
        erpPurchaseOrder.setAmount(addPurchaseOrderRequest.getAmount());
        erpPurchaseOrderMapper.insert(erpPurchaseOrder);
        Integer orderId = erpPurchaseOrder.getId();
        ErpPurchaseOrderInvoice invoice=new ErpPurchaseOrderInvoice();
        invoice.setOrderId(orderId);//设置关联采购单id
        for (MultipartFile file : addPurchaseOrderRequest.getInvoice()){
            String url= FileUploadUtils.upload(file);
            invoice.setId(null);
            invoice.setInvoiceUrl(url);
            invoiceMapper.insert(invoice);
        }
        return 1;
    }

    @Override
    public int updateErpPurchaseOrder(UpdatePurchaseOrderRequest updatePurchaseOrderRequest) throws IOException {
        if(erpPurchaseOrderMapper.selectById(updatePurchaseOrderRequest.getId())==null) return 0;
        ErpPurchaseOrder erpPurchaseOrder = new ErpPurchaseOrder();
        erpPurchaseOrder.setId(updatePurchaseOrderRequest.getId());
        if(updatePurchaseOrderRequest.getPurchaseId()!=null)erpPurchaseOrder.setPurchaseId(updatePurchaseOrderRequest.getPurchaseId());
        if(updatePurchaseOrderRequest.getAmount()!=null)erpPurchaseOrder.setAmount(updatePurchaseOrderRequest.getAmount());
        if(updatePurchaseOrderRequest.getInvoice()!=null){
            ErpPurchaseOrderInvoice invoice=new ErpPurchaseOrderInvoice();
            invoice.setOrderId(updatePurchaseOrderRequest.getId());//设置关联采购单id
            for (MultipartFile file : updatePurchaseOrderRequest.getInvoice()){
                String url= FileUploadUtils.upload(file);
                invoice.setId(null);
                invoice.setInvoiceUrl(url);
                invoiceMapper.insert(invoice);
            }
        }
        return erpPurchaseOrderMapper.updateById(erpPurchaseOrder);
    }

    @Override
    public int deleteErpPurchaseOrderByIds(List<Integer> ids) {
        int count=0;
        for(Integer orderId:ids){
            LambdaQueryWrapper<ErpPurchaseOrderInvoice> wrapper=Wrappers.lambdaQuery();
            wrapper.eq(ErpPurchaseOrderInvoice::getOrderId,orderId);
            for(ErpPurchaseOrderInvoice erpPurchaseOrderInvoice:invoiceMapper.selectList(wrapper)){
                String url= invoiceMapper.selectById(erpPurchaseOrderInvoice.getId()).getInvoiceUrl();
                url=parseActualPath(url);
                FileUtils.deleteFile(url);
                invoiceMapper.deleteById(erpPurchaseOrderInvoice.getId());
            }
            if(erpPurchaseOrderMapper.deleteById(orderId)>=0) count++;
        }
        return count;
    }

    @Override
    public int removeInvoice(List<Integer> ids) {
        int count=0;
        for(Integer id:ids){
            String url= invoiceMapper.selectById(id).getInvoiceUrl();
            url=parseActualPath(url);
            FileUtils.deleteFile(url);
            if (invoiceMapper.deleteById(id)>=0) count++;
        }
        return count;
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