package com.ruoyi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.web.domain.ErpPurchaseOrder;
import com.ruoyi.web.request.purchase.AddPurchaseOrderRequest;
import com.ruoyi.web.request.purchase.ListPurchaseOrderRequest;
import com.ruoyi.web.request.purchase.UpdatePurchaseOrderRequest;
import com.ruoyi.web.result.PurchaseOrderResult;

import java.io.IOException;
import java.util.List;

/**
 * 采购订单Service接口
 *
 * @author ruoyi
 * @date 2025-05-08
 */
public interface IErpPurchaseOrderService extends IService<ErpPurchaseOrder> {
    /**
     * 查询采购订单
     *
     * @param id 采购订单主键
     * @return 采购订单
     */
    PurchaseOrderResult selectErpPurchaseOrderById(Integer id);

    /**
     * 查询采购订单列表
     *
     * @param listPurchaseOrderRequest 查询请求
     * @return 采购订单集合
     */
    List<PurchaseOrderResult> selectErpPurchaseOrderList(ListPurchaseOrderRequest listPurchaseOrderRequest);

    List<PurchaseOrderResult> selectErpPurchaseOrderListByIds(Integer[] ids);

    /**
     * 新增采购订单
     *
     * @param addPurchaseOrderRequest 新增请求
     * @return 结果
     */
    int insertErpPurchaseOrder(AddPurchaseOrderRequest addPurchaseOrderRequest) throws IOException;

    /**
     * 修改采购订单
     *
     * @param updatePurchaseOrderRequest 修改请求
     * @return 结果
     */
    int updateErpPurchaseOrder(UpdatePurchaseOrderRequest updatePurchaseOrderRequest) throws IOException;

    /**
     * 批量删除采购订单
     *
     * @param ids 需要删除的采购订单主键集合
     * @return 结果
     */
    int deleteErpPurchaseOrderByIds(List<Integer> ids);

    int removeInvoice(List<Integer> ids);
}