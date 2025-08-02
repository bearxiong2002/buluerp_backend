package com.ruoyi.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.web.domain.ErpPackagingList;
import com.ruoyi.web.request.packaging.ListPackagingListRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ErpPackagingListMapper extends BaseMapper<ErpPackagingList> {
    ErpPackagingList selectErpPackagingListById(Long id);
    List<ErpPackagingList> selectErpPackagingListList(ListPackagingListRequest request);
    List<ErpPackagingList> selectErpPackagingListListByIds(Long[] ids);
    int insertErpPackagingList(ErpPackagingList erpPackagingList);
    int updateErpPackagingList(ErpPackagingList erpPackagingList);
    int deleteErpPackagingListById(Long id);
    int deleteErpPackagingListByIds(Long[] ids);
    int deleteErpPackagingListByProductId(String productId);
}
