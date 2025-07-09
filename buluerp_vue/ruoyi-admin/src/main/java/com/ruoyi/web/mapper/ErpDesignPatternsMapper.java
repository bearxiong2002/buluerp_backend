package com.ruoyi.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.web.domain.ErpDesignPatterns;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import com.ruoyi.web.domain.ErpOrders;


/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
@Mapper
public interface ErpDesignPatternsMapper extends BaseMapper<ErpDesignPatterns>
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    ErpDesignPatterns selectErpDesignPatternsById(Long id);

    List<ErpDesignPatterns> selectErpDesignPatternsListByIds(Long[] ids);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param erpDesignPatterns 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    List<ErpDesignPatterns> selectErpDesignPatternsList(ErpDesignPatterns erpDesignPatterns);

    /**
     * 新增【请填写功能名称】
     * 
     * @param erpDesignPatterns 【请填写功能名称】
     * @return 结果
     */
    int insertErpDesignPatterns(ErpDesignPatterns erpDesignPatterns);

    /**
     * 修改【请填写功能名称】
     * 
     * @param erpDesignPatterns 【请填写功能名称】
     * @return 结果
     */
    int updateErpDesignPatterns(ErpDesignPatterns erpDesignPatterns);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    int deleteErpDesignPatternsById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteErpDesignPatternsByIds(Long[] ids);

    int confirmErpDesignPatternsById(Long id);

    int cancelConfirmById(Long id);

    List<ErpOrders> findOrdersByProductIdAndStatus(@Param("productId") Long productId, @Param("status") Integer status);
}
