package com.ruoyi.web.util;

/**
 * 3D模型转换任务数据
 */
public class ConversionTask {

    /** 物料ID */
    private Long materialId;

    /** STP文件绝对路径 */
    private String stpAbsPath;

    /** 目标GLTF文件绝对路径 */
    private String gltfAbsPath;

    /** 上传用户ID（用于WebSocket通知） */
    private Long userId;

    /** STP文件URL路径（用于构建GLTF URL） */
    private String stpUrlPath;

    public ConversionTask() {
    }

    public ConversionTask(Long materialId, String stpAbsPath, String gltfAbsPath, Long userId, String stpUrlPath) {
        this.materialId = materialId;
        this.stpAbsPath = stpAbsPath;
        this.gltfAbsPath = gltfAbsPath;
        this.userId = userId;
        this.stpUrlPath = stpUrlPath;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public String getStpAbsPath() {
        return stpAbsPath;
    }

    public void setStpAbsPath(String stpAbsPath) {
        this.stpAbsPath = stpAbsPath;
    }

    public String getGltfAbsPath() {
        return gltfAbsPath;
    }

    public void setGltfAbsPath(String gltfAbsPath) {
        this.gltfAbsPath = gltfAbsPath;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStpUrlPath() {
        return stpUrlPath;
    }

    public void setStpUrlPath(String stpUrlPath) {
        this.stpUrlPath = stpUrlPath;
    }

    @Override
    public String toString() {
        return "ConversionTask{" +
                "materialId=" + materialId +
                ", stpAbsPath='" + stpAbsPath + '\'' +
                ", gltfAbsPath='" + gltfAbsPath + '\'' +
                ", userId=" + userId +
                '}';
    }
}
