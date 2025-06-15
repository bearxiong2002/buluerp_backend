package com.ruoyi.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.web.domain.ErpNotification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知Mapper接口
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
@Mapper
public interface ErpNotificationMapper extends BaseMapper<ErpNotification>
{
    /**
     * 查询通知列表
     * 
     * @param erpNotification 通知
     * @return 通知集合
     */
    List<ErpNotification> selectErpNotificationList(ErpNotification erpNotification);

    /**
     * 根据用户ID查询通知列表
     * 
     * @param userId 用户ID
     * @return 通知列表
     */
    List<ErpNotification> selectNotificationsByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询未读通知数量
     * 
     * @param userId 用户ID
     * @return 未读通知数量
     */
    int countUnreadNotificationsByUserId(@Param("userId") Long userId);

    /**
     * 批量插入通知
     * 
     * @param notifications 通知列表
     * @return 插入记录数
     */
    int batchInsertNotifications(@Param("notifications") List<ErpNotification> notifications);

    /**
     * 批量更新通知为已读
     * 
     * @param userId 用户ID
     * @param ids 通知ID列表
     * @return 更新记录数
     */
    int batchUpdateNotificationsAsRead(@Param("userId") Long userId, 
                                      @Param("ids") List<Long> ids);

    /**
     * 更新推送状态
     * 
     * @param id 通知ID
     * @param pushStatus 推送状态
     * @return 更新记录数
     */
    int updatePushStatus(@Param("id") Long id, @Param("pushStatus") Integer pushStatus);

    /**
     * 删除过期通知
     * 
     * @return 删除记录数
     */
    int deleteExpiredNotifications();

    /**
     * 根据业务ID和类型查询通知
     * 
     * @param businessId 业务ID
     * @param businessType 业务类型
     * @return 通知列表
     */
    List<ErpNotification> selectNotificationsByBusiness(@Param("businessId") Long businessId, 
                                                       @Param("businessType") String businessType);
} 