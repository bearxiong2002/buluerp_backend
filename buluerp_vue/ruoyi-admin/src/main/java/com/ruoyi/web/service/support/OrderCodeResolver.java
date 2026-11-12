package com.ruoyi.web.service.support;

import com.ruoyi.web.domain.ErpOrders;
import com.ruoyi.web.mapper.ErpOrderInnerCodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** 库存允许历史自由填写订单号；已知订单的附加编号统一保存为默认编号。 */
@Component
public class OrderCodeResolver {
    @Autowired private ErpOrderInnerCodeMapper mapper;

    public String resolve(String code) {
        if (code == null || code.trim().isEmpty()) return code;
        ErpOrders order = mapper.selectOrderByCode(code.trim());
        return order == null ? code : order.getInnerId();
    }
}
