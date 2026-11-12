package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.excel.ListRowErrorInfo;
import com.ruoyi.common.exception.excel.ListValidationException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.web.domain.ErpManufacturer;
import com.ruoyi.web.domain.ErpMaterialInfo;
import com.ruoyi.web.domain.ErpMould;
import com.ruoyi.web.domain.ErpMouldHouse;
import com.ruoyi.web.mapper.ErpMouldMapper;
import com.ruoyi.web.request.mould.AddMouldRequest;
import com.ruoyi.web.request.mould.BatchUpdateMouldStatusRequest;
import com.ruoyi.web.request.mould.ListMouldRequest;
import com.ruoyi.web.request.mould.UpdateMouldRequest;
import com.ruoyi.web.mapper.ErpManufacturerMapper;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.web.result.MouldInfoResult;
import com.ruoyi.web.service.IErpManufacturerService;
import com.ruoyi.web.service.IErpMaterialInfoService;
import com.ruoyi.web.service.IErpMouldHouseService;
import com.ruoyi.web.service.IErpMouldService;
import com.ruoyi.web.service.support.MouldImages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ErpMouldServiceImpl
        extends ServiceImpl<ErpMouldMapper, ErpMould>
        implements IErpMouldService {

    @Autowired
    private ErpMouldMapper erpMouldMapper;

    @Autowired
    private IErpManufacturerService manufacturerService;

    @Autowired
    private IErpMaterialInfoService materialInfoService;

    @Autowired
    private IErpMouldHouseService mouldHouseService;

    @Autowired
    private ErpManufacturerMapper manufacturerMapper;

    @Override
    public void checkUnique(ErpMould mould) {
        if (mould.getMouldNumber() != null) {
            LambdaQueryWrapper<ErpMould> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ErpMould::getMouldNumber, mould.getMouldNumber());
            if (baseMapper.selectCount(queryWrapper) > 0) {
                throw new IllegalArgumentException("模具编号已存在");
            }
        }
    }

    @Override
    public void checkReferences(ErpMould mould) {
        if (mould.getManufacturerId() != null) {
            ErpManufacturer manufacturer = manufacturerService.selectErpManufacturerById(mould.getManufacturerId());
            if (manufacturer == null) {
                throw new IllegalArgumentException("厂商ID不存在");
            }
        }
        if (mould.getMouldHouseId() != null && mould.getMouldHouseId() >= 0) {
            ErpMouldHouse mouldHouse = mouldHouseService.lambdaQuery()
                    .eq(ErpMouldHouse::getId, mould.getMouldHouseId())
                    .one();
            if (mouldHouse == null) {
                throw new IllegalArgumentException("模房ID不存在");
            }
        }
    }

    @Override
    public MouldInfoResult getMouldInfo(String mouldNumber) {
        MouldInfoResult mouldInfoById = baseMapper.findMouldInfoById(mouldNumber);
        if (mouldInfoById == null) {
            throw new ServiceException("没有模具信息");
        }
        // 查询关联物料的3D模型URL和胶件图片
        ErpMaterialInfo material = materialInfoService.getByMouldNumber(mouldNumber);
        if (material != null) {
            mouldInfoById.setModelUrl(material.getModelUrl());
            mouldInfoById.setDrawingReference(material.getDrawingReference());
        }
        return mouldInfoById;
    }

    @Override
    public List<MouldInfoResult> list(ListMouldRequest request) {
        List<MouldInfoResult> results = erpMouldMapper.selectMouldInfoList(request);
        // 填充每个模具关联物料的3D模型URL和胶件图片
        for (MouldInfoResult result : results) {
            ErpMaterialInfo material = materialInfoService.getByMouldNumber(result.getMouldNumber());
            if (material != null) {
                result.setModelUrl(material.getModelUrl());
                result.setDrawingReference(material.getDrawingReference());
            }
        }
        return results;
    }

    @Override
    @Transactional
    public void add(AddMouldRequest request) {
        ErpMould mould = new ErpMould();
        BeanUtils.copyProperties(request, mould);

        checkUnique(mould);

        if (StringUtils.isNotEmpty(request.getManufacturerName())) {
            List<ErpManufacturer> manufacturers = manufacturerMapper
                    .selectList(new LambdaQueryWrapper<ErpManufacturer>()
                            .like(ErpManufacturer::getName, request.getManufacturerName()));

            if (CollectionUtils.isEmpty(manufacturers)) {
                throw new IllegalArgumentException("厂商不存在");
            }
            if (manufacturers.size() > 1) {
                throw new IllegalArgumentException("厂商名称匹配到多条记录，请提供更精确的名称");
            }
            mould.setManufacturerId(manufacturers.get(0).getId());
        }

        mould.setStatus(ErpMould.STATUS_CREATED);
        mould.setMouldHouseId(-1L);
        if (request.getPicture() != null && !request.getPicture().isEmpty()) {
            mould.setPictureUrl(MouldImages.upload(request.getPicture()));
        }
        if (baseMapper.insert(mould) <= 0) throw new ServiceException("添加模具失败");
    }

    @Override
    @Transactional
    public void addWithManufacturer(AddMouldRequest request) {
        ErpMould mould = new ErpMould();
        BeanUtils.copyProperties(request, mould);

        checkUnique(mould);

        Long manufacturerId = getOrCreateManufacturerId(request.getManufacturerName());
        mould.setManufacturerId(manufacturerId);
        mould.setStatus(ErpMould.STATUS_CREATED);
        mould.setMouldHouseId(-1L);
        if (request.getPicture() != null && !request.getPicture().isEmpty()) {
            mould.setPictureUrl(MouldImages.upload(request.getPicture()));
        }
        if (baseMapper.insert(mould) <= 0) throw new ServiceException("添加模具失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importWithManufacturer(List<AddMouldRequest> requests) {
        if (CollectionUtils.isEmpty(requests)) {
            throw new ServiceException("导入文件中没有可导入的模具数据");
        }

        List<ListRowErrorInfo> errors = new ArrayList<>();
        Map<String, List<Integer>> rowsByMouldNumber = new LinkedHashMap<>();
        Map<String, List<Integer>> rowsByManufacturerName = new LinkedHashMap<>();

        // 统一修剪文本，并记录同一编号和厂商在 Excel 中出现的行号，后续可以一次性返回全部错误。
        for (int index = 0; index < requests.size(); index++) {
            AddMouldRequest request = requests.get(index);
            int rowNum = index + 1;
            String mouldNumber = StringUtils.trim(request.getMouldNumber());
            String manufacturerName = StringUtils.trim(request.getManufacturerName());
            request.setMouldNumber(mouldNumber);
            request.setManufacturerName(manufacturerName);
            if (StringUtils.isBlank(mouldNumber)) {
                errors.add(createRowError(rowNum, "模具编号不能为空"));
            } else {
                rowsByMouldNumber.computeIfAbsent(mouldNumber, key -> new ArrayList<>()).add(rowNum);
            }
            if (StringUtils.isBlank(manufacturerName)) {
                errors.add(createRowError(rowNum, "厂商名称不能为空"));
            } else {
                rowsByManufacturerName.computeIfAbsent(manufacturerName, key -> new ArrayList<>()).add(rowNum);
            }
        }

        rowsByMouldNumber.forEach((mouldNumber, rowNumbers) -> {
            if (rowNumbers.size() > 1) {
                rowNumbers.forEach(rowNum -> errors.add(createRowError(rowNum,
                        "模具编号" + mouldNumber + "在导入文件中重复")));
            }
        });

        Set<String> existingMouldNumbers = selectExistingMouldNumbers(rowsByMouldNumber.keySet());
        rowsByMouldNumber.forEach((mouldNumber, rowNumbers) -> {
            if (existingMouldNumbers.contains(mouldNumber)) {
                rowNumbers.forEach(rowNum -> errors.add(createRowError(rowNum,
                        "模具编号" + mouldNumber + "已存在")));
            }
        });

        Map<String, Long> manufacturerIds = resolveManufacturerIds(rowsByManufacturerName, errors);
        if (!errors.isEmpty()) {
            throw new ListValidationException(errors);
        }

        List<ErpMould> moulds = requests.stream().map(request -> {
            ErpMould mould = new ErpMould();
            mould.setMouldNumber(request.getMouldNumber());
            mould.setStatus(ErpMould.STATUS_CREATED);
            mould.setManufacturerId(manufacturerIds.get(request.getManufacturerName()));
            mould.setMouldHouseId(-1L);
            mould.setPictureUrl(request.getPictureUrl());
            mould.setRemark(request.getRemark());
            return mould;
        }).collect(Collectors.toList());

        // 控制每条 SQL 的参数数量，兼顾大文件导入速度和数据库包大小限制。
        int importedCount = 0;
        final int batchSize = 500;
        for (int start = 0; start < moulds.size(); start += batchSize) {
            int end = Math.min(start + batchSize, moulds.size());
            importedCount += erpMouldMapper.insertBatch(moulds.subList(start, end));
        }
        if (importedCount != moulds.size()) {
            throw new ServiceException("模具导入失败，实际写入数量与导入数量不一致");
        }
    }

    private Set<String> selectExistingMouldNumbers(Set<String> mouldNumbers) {
        if (CollectionUtils.isEmpty(mouldNumbers)) {
            return Collections.emptySet();
        }
        List<String> mouldNumberList = new ArrayList<>(mouldNumbers);
        Set<String> existingMouldNumbers = new HashSet<>();
        final int queryBatchSize = 500;
        for (int start = 0; start < mouldNumberList.size(); start += queryBatchSize) {
            int end = Math.min(start + queryBatchSize, mouldNumberList.size());
            baseMapper.selectList(new LambdaQueryWrapper<ErpMould>()
                            .select(ErpMould::getMouldNumber)
                            .in(ErpMould::getMouldNumber, mouldNumberList.subList(start, end)))
                    .stream()
                    .map(ErpMould::getMouldNumber)
                    .forEach(existingMouldNumbers::add);
        }
        return existingMouldNumbers;
    }

    private Map<String, Long> resolveManufacturerIds(Map<String, List<Integer>> rowsByManufacturerName,
                                                      List<ListRowErrorInfo> errors) {
        Set<String> manufacturerNames = rowsByManufacturerName.keySet();
        if (CollectionUtils.isEmpty(manufacturerNames)) {
            return Collections.emptyMap();
        }
        List<ErpManufacturer> existingManufacturers = manufacturerMapper.selectList(
                new LambdaQueryWrapper<ErpManufacturer>().in(ErpManufacturer::getName, manufacturerNames));
        Map<String, List<ErpManufacturer>> manufacturersByName = existingManufacturers.stream()
                .collect(Collectors.groupingBy(ErpManufacturer::getName));
        Map<String, Long> manufacturerIds = new HashMap<>();

        manufacturersByName.forEach((name, manufacturers) -> {
            if (manufacturers.size() > 1) {
                rowsByManufacturerName.get(name).forEach(rowNum -> errors.add(createRowError(rowNum,
                        "厂商名称" + name + "匹配到多条记录，请先合并重复厂商")));
            } else {
                manufacturerIds.put(name, manufacturers.get(0).getId());
            }
        });
        if (!errors.isEmpty()) {
            return manufacturerIds;
        }

        Long currentUserId = getCurrentUserIdOrDefault();
        for (String manufacturerName : new HashSet<>(manufacturerNames)) {
            if (manufacturerIds.containsKey(manufacturerName)) {
                continue;
            }
            ErpManufacturer manufacturer = new ErpManufacturer();
            manufacturer.setName(manufacturerName);
            manufacturer.setCreateUserId(currentUserId);
            manufacturer.setCreateTime(DateUtils.getNowDate());
            if (manufacturerMapper.insert(manufacturer) <= 0 || manufacturer.getId() == null) {
                throw new ServiceException("厂商" + manufacturerName + "创建失败");
            }
            manufacturerIds.put(manufacturerName, manufacturer.getId());
        }
        return manufacturerIds;
    }

    private ListRowErrorInfo createRowError(int rowNum, String message) {
        ListRowErrorInfo errorInfo = new ListRowErrorInfo();
        errorInfo.setRowNum(rowNum);
        errorInfo.setErrorMsg(message);
        return errorInfo;
    }

    private Long getOrCreateManufacturerId(String manufacturerName) {
        if (StringUtils.isBlank(manufacturerName)) {
            throw new IllegalArgumentException("厂商名称不能为空");
        }
        String trimmedName = manufacturerName.trim();
        List<ErpManufacturer> manufacturers = manufacturerMapper
                .selectList(new LambdaQueryWrapper<ErpManufacturer>()
                        .eq(ErpManufacturer::getName, trimmedName));

        if (manufacturers.size() > 1) {
            throw new IllegalArgumentException("厂商名称匹配到多条记录，请提供更精确的名称");
        }
        if (!CollectionUtils.isEmpty(manufacturers)) {
            return manufacturers.get(0).getId();
        }

        ErpManufacturer manufacturer = new ErpManufacturer();
        manufacturer.setName(trimmedName);
        manufacturer.setCreateUserId(getCurrentUserIdOrDefault());
        manufacturer.setCreateTime(DateUtils.getNowDate());
        if (manufacturerMapper.insert(manufacturer) <= 0 || manufacturer.getId() == null) {
            throw new ServiceException("厂商创建失败");
        }
        return manufacturer.getId();
    }

    private Long getCurrentUserIdOrDefault() {
        try {
            return SecurityUtils.getUserId();
        } catch (Exception ignored) {
            return 1L;
        }
    }

    @Override
    @Transactional
    public void update(UpdateMouldRequest request) {
        ErpMould original = baseMapper.selectById(request.getId());
        if (original == null) throw new ServiceException("更新模具失败：无效的ID");
        ErpMould mould = new ErpMould();
        BeanUtils.copyProperties(request, mould);
        if (StringUtils.isNotEmpty(request.getManufacturerName())) {
            List<ErpManufacturer> manufacturers = manufacturerMapper
                    .selectList(new LambdaQueryWrapper<ErpManufacturer>()
                            .eq(ErpManufacturer::getName, request.getManufacturerName()));

            if (CollectionUtils.isEmpty(manufacturers)) {
                throw new IllegalArgumentException("厂商不存在");
            }
            if (manufacturers.size() > 1) {
                throw new IllegalArgumentException("厂商名称匹配到多条记录，请提供更精确的名称");
            }
            mould.setManufacturerId(manufacturers.get(0).getId());
        }
        checkReferences(mould);
        if (request.getStatus() != null && !ErpMould.isStatusValid(request.getStatus())) {
            throw new IllegalArgumentException("无效的模具状态");
        }
        if (request.getMouldHouseId() != null && request.getMouldHouseId() < 0) {
            // 未分配统一设置为-1便于检索
            mould.setMouldHouseId(-1L);
        }
        boolean uploaded = request.getPicture() != null && !request.getPicture().isEmpty();
        if (uploaded) mould.setPictureUrl(MouldImages.upload(request.getPicture()));
        boolean hasChanges = uploaded || mould.getStatus() != null || mould.getRemark() != null
                || mould.getTrialDate() != null || mould.getManufacturerId() != null || mould.getMouldHouseId() != null;
        if (hasChanges && baseMapper.updateById(mould) <= 0) {
            throw new IllegalArgumentException("更新模具失败：无效的ID");
        }
        boolean clearPicture = !uploaded && Boolean.TRUE.equals(request.getDeletePicture());
        if (clearPicture && erpMouldMapper.clearPicture(request.getId()) <= 0) {
            throw new ServiceException("删除模具图片失败");
        }
        if (uploaded || clearPicture) MouldImages.deleteAfterCommit(original.getPictureUrl());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchUpdateStatus(BatchUpdateMouldStatusRequest request) {
        if (request == null || !ErpMould.isStatusValid(request.getStatus())) {
            throw new ServiceException("无效的模具状态");
        }
        boolean selected = "selected".equals(request.getScope());
        boolean filtered = "filtered".equals(request.getScope());
        if (!selected && !filtered) {
            throw new ServiceException("更新范围必须为 selected 或 filtered");
        }
        if (selected && (CollectionUtils.isEmpty(request.getIds())
                || request.getIds().stream().anyMatch(id -> id == null || id <= 0))) {
            throw new ServiceException("请选择有效的模具记录");
        }
        // 缺少 filters 不应意外退化为全表更新；显式传入空对象才表示全部查询结果。
        if (filtered && (request.getFilters() == null || !CollectionUtils.isEmpty(request.getIds()))) {
            throw new ServiceException("按查询结果更新时必须提供查询条件，且不能同时提供勾选ID");
        }
        Long mouldHouseId = null;
        if (ErpMould.STATUS_REPAIRING.equals(request.getStatus())) {
            mouldHouseId = request.getMouldHouseId();
            if (mouldHouseId == null || mouldHouseId <= 0 || mouldHouseService.getById(mouldHouseId) == null) {
                throw new ServiceException("状态为模具故障送修中时，必须选择有效的送修模房");
            }
        }

        List<Long> ids;
        final int batchSize = 500;
        if (selected) {
            ids = request.getIds().stream().distinct().sorted().collect(Collectors.toList());
            // 所有勾选记录先校验，再写入，避免部分ID失效却报告整体成功。
            for (int start = 0; start < ids.size(); start += batchSize) {
                List<Long> batch = ids.subList(start, Math.min(start + batchSize, ids.size()));
                if (erpMouldMapper.selectBatchIds(batch).size() != batch.size()) {
                    throw new ServiceException("部分模具已不存在，请刷新列表后重试");
                }
            }
        } else {
            // 复用列表筛选条件，只查询ID，不加载物料模型，也不应用列表分页。
            ids = erpMouldMapper.selectMouldIds(request.getFilters());
        }
        int updatedCount = 0;
        for (int start = 0; start < ids.size(); start += batchSize) {
            updatedCount += erpMouldMapper.updateBatchStatus(
                    ids.subList(start, Math.min(start + batchSize, ids.size())), request.getStatus(), mouldHouseId);
        }
        return updatedCount;
    }

    @Override
    @Transactional
    public void remove(Long id) {
        ErpMould mould = baseMapper.selectById(id);
        if (mould == null) {
            throw new IllegalArgumentException("删除模具失败：无效的ID");
        }
        ErpMaterialInfo materialInfo = new ErpMaterialInfo();
        materialInfo.setMouldNumber(mould.getMouldNumber());
        if (!CollectionUtils.isEmpty(materialInfoService.selectErpMaterialInfoList(materialInfo))) {
            throw new ServiceException("该模具下存在物料信息，不能删除");
        }
        if (baseMapper.deleteById(id) <= 0) {
            throw new IllegalArgumentException("删除模具失败：无效的ID");
        }
        MouldImages.deleteAfterCommit(mould.getPictureUrl());
    }

    @Override
    @Transactional
    public void removeBatch(List<Long> ids) {
        for (Long id : ids) {
            remove(id);
        }
    }
}
