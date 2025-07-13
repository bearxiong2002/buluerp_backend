package com.ruoyi.web.service.impl;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.file.ImageUtils;
import com.ruoyi.web.domain.ErpDesignStyle;
import com.ruoyi.web.domain.ErpMaterialInfo;
import com.ruoyi.web.mapper.ErpDesignPatternsMapper;
import com.ruoyi.web.mapper.ErpDesignStyleMapper;
import com.ruoyi.web.mapper.ErpMaterialInfoMapper;
import com.ruoyi.web.request.design.AddDesignRequest;
import com.ruoyi.web.request.design.ListDesignRequest;
import com.ruoyi.web.request.design.UpdateDesignRequest;
import com.ruoyi.web.service.IErpDesignStyleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



/**
 * 设计造型Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
@Service
public class ErpDesignStyleServiceImpl implements IErpDesignStyleService
{
    @Autowired
    private ErpDesignStyleMapper erpDesignStyleMapper;

    @Autowired
    private ErpDesignPatternsMapper erpDesignPatternsMapper;

    @Autowired
    private ErpMaterialInfoMapper erpMaterialInfoMapper;

    /**
     * 查询设计造型
     * 
     * @param id 设计造型主键
     * @return 设计造型
     */
    @Override
    public ErpDesignStyle selectErpDesignStyleById(Long id)
    {
        return erpDesignStyleMapper.selectErpDesignStyleById(id);
    }

    /**
     * 查询设计造型列表
     * 
     * @param listDesignRequest 设计造型
     * @return 设计造型
     */
    @Override
    public List<ErpDesignStyle> selectErpDesignStyleList(ListDesignRequest listDesignRequest)
    {
        LambdaQueryWrapper<ErpDesignStyle> wrapper= Wrappers.lambdaQuery();
        wrapper.eq(listDesignRequest.getId()!=null,ErpDesignStyle::getId,listDesignRequest.getId())
                .eq(listDesignRequest.getGroupId()!=null,ErpDesignStyle::getProductId,listDesignRequest.getGroupId())
                .like(listDesignRequest.getProductId()!=null,ErpDesignStyle::getProductId,listDesignRequest.getProductId().toString());
        return erpDesignStyleMapper.selectList(wrapper);
    }

    @Override
    public List<ErpDesignStyle> selectErpDesignStyleListByIds(List<Integer> ids) {
        return erpDesignStyleMapper.selectBatchIds(ids);
    }

    /**
     * 新增设计造型
     * 
     * @param addDesignRequest 造型表新增请求
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertErpDesignStyle(AddDesignRequest addDesignRequest) throws IOException {

        String url=null;
        if(addDesignRequest.getPicture()!=null) url= FileUploadUtils.upload(addDesignRequest.getPicture());
        //从物料表获得图片
        else {
            ErpMaterialInfo erpMaterialInfo=erpMaterialInfoMapper.selectErpMaterialInfoById(addDesignRequest.getMaterialId());
            if (erpMaterialInfo != null && erpMaterialInfo.getDrawingReference() != null && !erpMaterialInfo.getDrawingReference().trim().isEmpty()) {
                // 使用若依框架的ImageUtils直接读取图片文件
                byte[] fileBytes = ImageUtils.getImage(erpMaterialInfo.getDrawingReference());
                if (fileBytes != null) {
                    try {
                        String fileName = FileUtils.getName(erpMaterialInfo.getDrawingReference());
                        String contentType = getContentTypeByExtension(fileName);
                        
                        MultipartFile multipartFile = new FileMultipartFile(fileBytes, fileName, contentType);
                        url = FileUploadUtils.upload(multipartFile);
                    } catch (IOException e) {
                        // 文件上传失败，继续执行，url保持为null
                    }
                }
            }
        }
        ErpDesignStyle erpDesignStyle=new ErpDesignStyle(addDesignRequest.getProductId(), addDesignRequest.getGroupId(), addDesignRequest.getMouldNumber(), addDesignRequest.getLddNumber(), addDesignRequest.getMouldCategory(), addDesignRequest.getMaterialId(), url, addDesignRequest.getColor(), addDesignRequest.getProductName(), addDesignRequest.getQuantity(), addDesignRequest.getMaterial());

        return erpDesignStyleMapper.insert(erpDesignStyle);
    }

    /**
     * 修改设计造型
     * 
     * @param updateDesignRequest 设计造型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateErpDesignStyle(UpdateDesignRequest updateDesignRequest) throws IOException {
        String url=null;
        //Long productId=null;
        //if(updateDesignRequest.getDesignPatternId()!=null) productId=erpDesignPatternsMapper.selectById(updateDesignRequest.getDesignPatternId()).getProductId();
        if(updateDesignRequest.getPicture()==null){
            String preUrl=erpDesignStyleMapper.selectById(updateDesignRequest.getId()).getPictureUrl();
            if(preUrl!=null){
                preUrl=parseActualPath(preUrl);
                FileUtils.deleteFile(preUrl);
                LambdaUpdateWrapper<ErpDesignStyle> lambdaWrapper = new LambdaUpdateWrapper<>();
                lambdaWrapper.set(ErpDesignStyle::getPictureUrl, null)
                        .eq(ErpDesignStyle::getId, updateDesignRequest.getId());
                erpDesignStyleMapper.update(null,lambdaWrapper);
            }
        }
        if(updateDesignRequest.getPicture()!=null){
            //删除原本的文件
            String preUrl=erpDesignStyleMapper.selectById(updateDesignRequest.getId()).getPictureUrl();
            if(preUrl!=null){
                preUrl=parseActualPath(preUrl);
                FileUtils.deleteFile(preUrl);
            }
            url= FileUploadUtils.upload(updateDesignRequest.getPicture());
        }
        ErpDesignStyle erpDesignStyle=new ErpDesignStyle(updateDesignRequest.getId(), updateDesignRequest.getProductId(), updateDesignRequest.getGroupId(), updateDesignRequest.getMouldNumber(), updateDesignRequest.getLddNumber(), updateDesignRequest.getMouldCategory(), updateDesignRequest.getMaterialId(), url, updateDesignRequest.getColor(), updateDesignRequest.getProductName(), updateDesignRequest.getQuantity(), updateDesignRequest.getMaterial());
        return erpDesignStyleMapper.updateById(erpDesignStyle);
    }

    /**
     * 批量删除设计造型
     * 
     * @param ids 需要删除的设计造型主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteErpDesignStyleByIds(List<Integer> ids)
    {
        for(Integer id:ids){
            //删除原本的文件
            String preUrl=erpDesignStyleMapper.selectById(id).getPictureUrl();
            if(preUrl!=null){
                preUrl=parseActualPath(preUrl);
                FileUtils.deleteFile(preUrl);
            }
        }
        return erpDesignStyleMapper.deleteBatchIds(ids);
    }

    /**
     * 删除设计造型信息
     * 
     * @param id 设计造型主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteErpDesignStyleById(Long id)
    {
        //删除原本的文件
        String preUrl=erpDesignStyleMapper.selectById(id).getPictureUrl();
        preUrl=parseActualPath(preUrl);
        FileUtils.deleteFile(preUrl);
        return erpDesignStyleMapper.deleteErpDesignStyleById(id);
    }

    public static String parseActualPath(String url) {
        // 1. 验证URL是否以资源前缀开头
        if (!url.startsWith(Constants.RESOURCE_PREFIX)) {
            throw new IllegalArgumentException("文件URL必须以 " + Constants.RESOURCE_PREFIX + " 开头");
        }

        // 2. 移除资源前缀（保留后续路径部分）
        String relativePath = url.substring(Constants.RESOURCE_PREFIX.length());

        // 3. 拼接实际存储路径
        return Paths.get(
                RuoYiConfig.getProfile(), // 基础路径（如 D:/ruoyi/uploadPath）
                relativePath.split("/")   // 拆分路径部分（如 ["", "2025", "05", "10", "xxx.txt"]）
        ).toString();
    }

    /**
     * 根据文件扩展名获取ContentType
     */
    private String getContentTypeByExtension(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "bmp":
                return "image/bmp";
            default:
                return "image/jpeg"; // 默认类型
        }
    }

    /**
     * 文件MultipartFile实现类
     */
    private static class FileMultipartFile implements MultipartFile {
        private final byte[] content;
        private final String filename;
        private final String contentType;

        public FileMultipartFile(byte[] content, String filename, String contentType) {
            this.content = content;
            this.filename = filename;
            this.contentType = contentType;
        }

        @Override
        public String getName() {
            return "file";
        }

        @Override
        public String getOriginalFilename() {
            return filename;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public boolean isEmpty() {
            return content.length == 0;
        }

        @Override
        public long getSize() {
            return content.length;
        }

        @Override
        public byte[] getBytes() throws IOException {
            return content;
        }

        @Override
        public java.io.InputStream getInputStream() throws IOException {
            return new java.io.ByteArrayInputStream(content);
        }

        @Override
        public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(dest)) {
                fos.write(content);
            }
        }
    }
}
