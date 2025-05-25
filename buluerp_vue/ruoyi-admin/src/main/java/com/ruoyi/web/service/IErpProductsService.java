package com.ruoyi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.web.domain.ErpProducts;
import com.ruoyi.web.request.product.AddProductRequest;
import com.ruoyi.web.request.product.ListProductRequest;
import com.ruoyi.web.request.product.UpdateProductRequest;

import java.io.IOException;
import java.util.List;

public interface IErpProductsService extends IService<ErpProducts> {
    List<ErpProducts> selectErpProductsList(ListProductRequest listProductRequest);

    List<ErpProducts> selectErpProductsListByIds(Integer[] ids);

    int insertErpProducts(AddProductRequest addProductRequest) throws IOException;

    int updateErpProducts(UpdateProductRequest updateProductRequest) throws IOException;

    int deleteErpProductsByIds(List<Integer> ids);

    void processMaterialIds(AddProductRequest item);
}