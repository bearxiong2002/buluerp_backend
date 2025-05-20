package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
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

import java.io.File;
import java.io.IOException;
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
        purchaseOrderResult.setInvoiceUrl(erpPurchaseOrderMapper.selectUrl(erpPurchaseOrder.getPurchaseId()));
        return purchaseOrderResult;
    }

    @Override
    public List<PurchaseOrderResult> selectErpPurchaseOrderList(ListPurchaseOrderRequest listPurchaseOrderRequest) {
        LambdaQueryWrapper<ErpPurchaseOrder> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(ErpPurchaseOrder::getId,listPurchaseOrderRequest.getId())
                .eq(ErpPurchaseOrder::getPurchaseId,listPurchaseOrderRequest.getPurchaseId());
        List<PurchaseOrderResult> list=new ArrayList<>();
        for(ErpPurchaseOrder erpPurchaseOrder:erpPurchaseOrderMapper.selectList(queryWrapper)){
            PurchaseOrderResult purchaseOrderResult =new PurchaseOrderResult();
            purchaseOrderResult.setId(erpPurchaseOrder.getId());
            purchaseOrderResult.setAmount(erpPurchaseOrder.getAmount());
            purchaseOrderResult.setPurchaseId(erpPurchaseOrder.getPurchaseId());
            purchaseOrderResult.setInvoiceUrl(erpPurchaseOrderMapper.selectUrl(erpPurchaseOrder.getPurchaseId()));
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
            purchaseOrderResult.setInvoiceUrl(erpPurchaseOrderMapper.selectUrl(erpPurchaseOrder.getPurchaseId()));
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
        ErpPurchaseOrder erpPurchaseOrder = new ErpPurchaseOrder();
        erpPurchaseOrder.setId(updatePurchaseOrderRequest.getId());
        erpPurchaseOrder.setPurchaseId(updatePurchaseOrderRequest.getPurchaseId());
        erpPurchaseOrder.setAmount(updatePurchaseOrderRequest.getAmount());
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
        return erpPurchaseOrderMapper.deleteBatchIds(ids);
    }

    @Override
    public int removeInvoice(String url) {
        File file=new File(url);
        if (file.exists() && file.isFile()) {
            boolean deleted = file.delete();
            if (deleted) {
                System.out.println("File deleted successfully.");
                LambdaQueryWrapper<ErpPurchaseOrderInvoice> wrapper=Wrappers.lambdaQuery();
                wrapper.eq(ErpPurchaseOrderInvoice::getInvoiceUrl,url);
                invoiceMapper.delete(wrapper);
                return 1;
            } else {
                System.out.println("Failed to delete file.");
                return 0;
            }
        } else {
            System.out.println("File does not exist.");
            return 0;
        }
    }
}