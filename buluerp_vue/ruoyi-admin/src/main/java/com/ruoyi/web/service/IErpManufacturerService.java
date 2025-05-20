package com.ruoyi.web.service;

import com.ruoyi.web.domain.ErpDesignPatterns;
import com.ruoyi.web.domain.ErpManufacturer;
import com.ruoyi.web.request.design.AddDesignPatternsRequest;
import com.ruoyi.web.request.design.ListDesignPatternsRequest;
import com.ruoyi.web.request.design.UpdateDesignPatternsRequest;
import com.ruoyi.web.request.manufacturer.AddManufacturerRequest;
import com.ruoyi.web.request.manufacturer.ListManufacturerRequest;
import com.ruoyi.web.request.manufacturer.UpdateManufacturerRequest;
import com.ruoyi.web.result.DesignPatternsResult;

import java.util.List;

public interface IErpManufacturerService {
    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public ErpManufacturer selectErpManufacturerById(Long id);

    /**
     * 查询【请填写功能名称】列表
     *
     * @param listDesignPatternsRequest 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<ErpManufacturer> selectErpManufacturerList(ListManufacturerRequest listDesignPatternsRequest);

    List<ErpManufacturer> selectErpManufacturerListByIds(Long[] ids);

    /**
     * 新增【请填写功能名称】
     *
     * @param addManufacturerRequest 【请填写功能名称】
     * @return 结果
     */
    public int insertErpManufacturer(AddManufacturerRequest addManufacturerRequest);

    /**
     * 修改【请填写功能名称】
     *
     * @param updateManufacturerRequest 【请填写功能名称】
     * @return 结果
     */
    public int updateErpManufacturer(UpdateManufacturerRequest updateManufacturerRequest);

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】主键集合
     * @return 结果
     */
    public int deleteErpManufacturerByIds(Long[] ids);

}
