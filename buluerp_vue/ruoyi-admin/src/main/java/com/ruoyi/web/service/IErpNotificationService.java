package com.ruoyi.web.service;

import com.ruoyi.web.domain.ErpNotification;
import com.ruoyi.web.enums.NotificationTypeEnum;

import java.util.List;
import java.util.Map;

/**
 * 通知Service接口
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
public interface IErpNotificationService
{
    /**
     * 查询通知
     * 
     * @param id 通知主键
     * @return 通知
     */
    ErpNotification selectErpNotificationById(Long id);

    /**
     * 查询通知列表
     * 
     * @param erpNotification 通知
     * @return 通知集合
     */
    List<ErpNotification> selectErpNotificationList(ErpNotification erpNotification);

    /**
     * 新增通知
     * 
     * @param erpNotification 通知
     * @return 结果
     */
    int insertErpNotification(ErpNotification erpNotification);

    /**
     * 修改通知
     * 
     * @param erpNotification 通知
     * @return 结果
     */
    int updateErpNotification(ErpNotification erpNotification);

    /**
     * 批量删除通知
     * 
     * @param ids 需要删除的通知主键集合
     * @return 结果
     */
    int deleteErpNotificationByIds(Long[] ids);

    /**
     * 删除通知信息
     * 
     * @param id 通知主键
     * @return 结果
     */
    int deleteErpNotificationById(Long id);

    /**
     * 发送通知给指定角色的用户
     * 
     * @param notificationType 通知类型
     * @param roleKey 角色标识
     * @param businessId 业务ID
     * @param businessType 业务类型
     * @param templateData 模板数据
     */
    void sendNotificationToRole(NotificationTypeEnum notificationType, 
                               String roleKey, 
                               Long businessId, 
                               String businessType, 
                               Map<String, Object> templateData);

    /**
     * 发送通知给指定用户
     * 
     * @param notificationType 通知类型
     * @param userId 用户ID
     * @param businessId 业务ID
     * @param businessType 业务类型
     * @param templateData 模板数据
     */
    void sendNotificationToUser(NotificationTypeEnum notificationType,
                               Long userId,
                               Long businessId,
                               String businessType,
                               Map<String, Object> templateData);

    /**
     * 发送通知给多个用户
     * 
     * @param notificationType 通知类型
     * @param userIds 用户ID列表
     * @param businessId 业务ID
     * @param businessType 业务类型
     * @param templateData 模板数据
     */
    void sendNotificationToUsers(NotificationTypeEnum notificationType,
                                List<Long> userIds,
                                Long businessId,
                                String businessType,
                                Map<String, Object> templateData);

    /**
     * 获取用户通知列表
     * 
     * @param userId 用户ID
     * @return 通知列表
     */
    List<ErpNotification> getUserNotifications(Long userId);

    /**
     * 获取用户未读通知数量
     * 
     * @param userId 用户ID
     * @return 未读通知数量
     */
    int getUnreadNotificationCount(Long userId);

    /**
     * 标记通知为已读
     * 
     * @param notificationId 通知ID
     * @return 结果
     */
    int markNotificationAsRead(Long notificationId);

    /**
     * 批量标记通知为已读
     * 
     * @param userId 用户ID
     * @param notificationIds 通知ID列表
     * @return 结果
     */
    int batchMarkNotificationsAsRead(Long userId, List<Long> notificationIds);

    /**
     * 根据业务ID和类型查询通知
     * 
     * @param businessId 业务ID
     * @param businessType 业务类型
     * @return 通知列表
     */
    List<ErpNotification> selectNotificationsByBusiness(Long businessId, String businessType);

    /**
     * 获取用户未读通知列表
     *
     * @param userId 用户ID
     * @return 未读通知列表
     */
    List<ErpNotification> getUnreadNotifications(Long userId);

    /**
     * 根据业务标识将相关通知标记为已读
     *
     * @param businessId   业务ID
     * @param businessType 业务类型
     */
    void markNotificationsAsReadByBusiness(Long businessId, String businessType);
} 