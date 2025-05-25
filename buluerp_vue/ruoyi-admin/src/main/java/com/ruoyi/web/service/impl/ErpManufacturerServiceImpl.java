package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.web.domain.ErpManufacturer;
import com.ruoyi.web.mapper.ErpManufacturerMapper;
import com.ruoyi.web.request.design.ListDesignPatternsRequest;
import com.ruoyi.web.request.manufacturer.AddManufacturerRequest;
import com.ruoyi.web.request.manufacturer.ListManufacturerRequest;
import com.ruoyi.web.request.manufacturer.UpdateManufacturerRequest;
import com.ruoyi.web.service.IErpManufacturerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ErpManufacturerServiceImpl implements IErpManufacturerService {

    @Autowired
    private ErpManufacturerMapper erpManufacturerMapper;

    public int insertErpManufacturer(AddManufacturerRequest addManufacturerRequest){

        // 获取当前登录用户信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 直接获取用户ID
        Long userId = loginUser.getUserId();

        ErpManufacturer erpManufacturer=new ErpManufacturer.Builder()
                .withCreateUserId(userId)
                .withName(addManufacturerRequest.getName())
                .withEmail(addManufacturerRequest.getEmail())
                .withTel(addManufacturerRequest.getTel())
                .withRemark(addManufacturerRequest.getRemark())
                .build();
        erpManufacturer.setCreateTime(DateUtils.getNowDate());
        return erpManufacturerMapper.insert(erpManufacturer);

    }

    public int updateErpManufacturer(UpdateManufacturerRequest updateManufacturerRequest){

        ErpManufacturer erpManufacturer=new ErpManufacturer.Builder()
                .withId(updateManufacturerRequest.getId())
                .withName(updateManufacturerRequest.getName())
                .withTel(updateManufacturerRequest.getTel())
                .withEmail(updateManufacturerRequest.getEmail())
                .withRemark(updateManufacturerRequest.getRemark())
                .build();

        return erpManufacturerMapper.updateById(erpManufacturer);
    }

    public int deleteErpManufacturerByIds(Long[] ids){
        return erpManufacturerMapper.deleteManufacturerByIds(ids);
    }

    public List<ErpManufacturer> selectErpManufacturerList(ListManufacturerRequest listManufacturerRequest){
        LambdaQueryWrapper<ErpManufacturer> wrapper= Wrappers.lambdaQuery();
        wrapper.eq(listManufacturerRequest.getId()!=null,ErpManufacturer::getId,listManufacturerRequest.getId())
                .like(StringUtils.isNotBlank(listManufacturerRequest.getTel()),ErpManufacturer::getTel,listManufacturerRequest.getTel())
                .like(StringUtils.isNotBlank(listManufacturerRequest.getEmail()),ErpManufacturer::getEmail,listManufacturerRequest.getEmail())
                .like(StringUtils.isNotBlank(listManufacturerRequest.getName()),ErpManufacturer::getName,listManufacturerRequest.getName())
                .like(StringUtils.isNotBlank(listManufacturerRequest.getRemark()),ErpManufacturer::getRemark,listManufacturerRequest.getRemark())
                .lt(listManufacturerRequest.getCreateTimeTo()!=null,ErpManufacturer::getCreateTime,listManufacturerRequest.getCreateTimeTo())
                .gt(listManufacturerRequest.getCreateTimeFrom()!=null,ErpManufacturer::getCreateTime,listManufacturerRequest.getCreateTimeFrom());
        return erpManufacturerMapper.selectList(wrapper);
    }

    @Override
    public List<ErpManufacturer> selectErpManufacturerListByIds(Long[] ids) {
        return erpManufacturerMapper.selectErpManufacturerListByIds(ids);
    }

    public ErpManufacturer selectErpManufacturerById(Long id){
        return erpManufacturerMapper.selectById(id);
    }
}
