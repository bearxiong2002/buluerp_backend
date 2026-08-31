package com.ruoyi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.web.domain.ErpMaterialType;

import java.io.IOException;
import java.util.List;

public interface IErpMaterialTypeService extends IService<ErpMaterialType> {
    void checkUnique(ErpMaterialType erpMaterialType);
    void check(ErpMaterialType materialType);

    boolean saveChecked(ErpMaterialType materialType) throws IOException;
    boolean updateChecked(ErpMaterialType materialType) throws IOException;
    void removeChecked(Long id);
    void removeBatchChecked(List<Long> ids);

    ErpMaterialType getByName(String name);
}
