package com.ruoyi.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.web.domain.ErpDesignStyle;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;


/**
 * 设计造型Mapper接口
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
@Mapper
public interface ErpDesignStyleMapper  extends BaseMapper<ErpDesignStyle>
{
    /**
     * 查询设计造型
     * 
     * @param id 设计造型主键
     * @return 设计造型
     */
    ErpDesignStyle selectErpDesignStyleById(Long id);

    List<ErpDesignStyle> selectErpDesignStyleListByIds(Long[] ids);

    /**
     * 查询设计造型列表
     * 
     * @param erpDesignStyle 设计造型
     * @return 设计造型集合
     */
    List<ErpDesignStyle> selectErpDesignStyleList(ErpDesignStyle erpDesignStyle);

    /**
     * 新增设计造型
     * 
     * @param erpDesignStyle 设计造型
     * @return 结果
     */
    int insertErpDesignStyle(ErpDesignStyle erpDesignStyle);

    /**
     * 修改设计造型
     * 
     * @param erpDesignStyle 设计造型
     * @return 结果
     */
    int updateErpDesignStyle(ErpDesignStyle erpDesignStyle);

    /**
     * 删除设计造型
     * 
     * @param id 设计造型主键
     * @return 结果
     */
    int deleteErpDesignStyleById(Long id);

    /**
     * 批量删除设计造型
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteErpDesignStyleByIds(Long[] ids);

    Set<String> selectMouldNumberSet(Long id);

    Set<String> selectLddNumberSet(Long id);

    Set<String> selectMouldCategorySet(Long id);

    Set<Long> selectMouldIdSet(Long id);

    Set<String> selectPictureUrlSet(Long id);

    Set<String> selectColorSet(Long id);

    Set<String> selectProductNameSet(Long id);

    Set<String> selectMaterialSet(Long id);

    Integer sumQuantityById(Long id);

    Long selectConfirm(Long id);

}
