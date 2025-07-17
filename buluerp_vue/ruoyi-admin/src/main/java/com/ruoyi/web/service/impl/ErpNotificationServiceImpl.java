package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.web.domain.ErpNotification;
import com.ruoyi.web.enums.NotificationTypeEnum;
import com.ruoyi.web.mapper.ErpNotificationMapper;
import com.ruoyi.web.service.IErpNotificationService;
import com.ruoyi.web.websocket.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ruoyi.system.mapper.SysRoleMapper;
import com.ruoyi.system.mapper.SysUserMapper;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 通知Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
@Service
public class ErpNotificationServiceImpl implements IErpNotificationService
{
    @Autowired
    private ErpNotificationMapper notificationMapper; 

    private static final Logger log = LoggerFactory.getLogger(ErpNotificationServiceImpl.class);

    @Autowired
    private ErpNotificationMapper erpNotificationMapper;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysUserService userService;

    // 添加Mapper注入，用于绕过数据权限过滤
    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    /** 通知模板配置 */
    private static final Map<NotificationTypeEnum, NotificationTemplate> NOTIFICATION_TEMPLATES = new HashMap<>();

    //初始化模板，方便调用
    static {
        // 订单创建通知模板
        NOTIFICATION_TEMPLATES.put(NotificationTypeEnum.ORDER_CREATED, 
            new NotificationTemplate("新订单待审核", "订单编号：{orderCode}，客户：{customerName}，数量：{quantity}，交货期限：{deliveryDeadline}"));
        
        // 订单审核通过通知模板（通用，用于其他状态变更）
        NOTIFICATION_TEMPLATES.put(NotificationTypeEnum.ORDER_APPROVED, 
            new NotificationTemplate("订单审核通过", "订单编号：{orderCode} 已审核通过，请安排设计工作"));
        
        // 订单审核拒绝通知模板（通用，用于其他状态变更）
        NOTIFICATION_TEMPLATES.put(NotificationTypeEnum.ORDER_REJECTED, 
            new NotificationTemplate("订单审核未通过", "订单编号：{orderCode} 审核未通过，原因：{rejectReason}"));
        
        // 订单审核通过专用通知模板（通知设计部）
        NOTIFICATION_TEMPLATES.put(NotificationTypeEnum.ORDER_AUDIT_APPROVED, 
            new NotificationTemplate("新订单待设计", "订单编号：{orderCode} 已审核通过，客户：{customerName}，数量：{quantity}，交货期限：{deliveryDeadline}，审核人：{auditor}，审核意见：{auditComment}，请安排设计工作"));
        
        // 订单审核拒绝专用通知模板（通知销售部）
        NOTIFICATION_TEMPLATES.put(NotificationTypeEnum.ORDER_AUDIT_REJECTED, 
            new NotificationTemplate("订单审核未通过", "订单编号：{orderCode} 审核未通过，客户：{customerName}，数量：{quantity}，交货期限：{deliveryDeadline}，拒绝原因：{rejectReason}，请修改订单后重新提交"));
        
        // 订单状态变更待审核通知
        NOTIFICATION_TEMPLATES.put(NotificationTypeEnum.ORDER_STATUS_CHANGE,
            new NotificationTemplate("订单状态变更待审核", "订单 {orderCode} 申请状态变更：从【{currentStatus}】到【{targetStatus}】，请及时审核。"));

        // 生产排期通知
        NOTIFICATION_TEMPLATES.put(NotificationTypeEnum.PRODUCTION_SCHEDULED,
            new NotificationTemplate("生产排期提醒", "订单 {orderCode} 已完成排期，请关注生产进度。"));

        // 库存不足通知
        NOTIFICATION_TEMPLATES.put(NotificationTypeEnum.INVENTORY_LOW,
            new NotificationTemplate("库存预警", "物料 {materialName}（编码: {materialCode}）当前库存为 {currentStock}，已低于预警值 {warningStock}，请及时补充。"));
        
        // 采购审批通过通知
        NOTIFICATION_TEMPLATES.put(NotificationTypeEnum.PURCHASE_APPROVED,
            new NotificationTemplate("采购审批通过", "您的采购申请单 {purchaseCode} 已审批通过。"));

        // 审核待处理通知模板
        NOTIFICATION_TEMPLATES.put(NotificationTypeEnum.AUDIT_PENDING, 
            new NotificationTemplate("待审核通知", "有新的业务需要您审核，请及时处理"));
        
        // 审核通过通知模板
        NOTIFICATION_TEMPLATES.put(NotificationTypeEnum.AUDIT_APPROVED, 
            new NotificationTemplate("审核通过通知", "您的业务已审核通过，可以继续下一步操作"));
        
        // 审核拒绝通知模板
        NOTIFICATION_TEMPLATES.put(NotificationTypeEnum.AUDIT_REJECTED, 
            new NotificationTemplate("审核拒绝通知", "您的业务审核未通过，请检查后重新提交"));
        
        // 布产审核通过通知模板
        NOTIFICATION_TEMPLATES.put(NotificationTypeEnum.PRODUCTION_AUDIT_APPROVED, 
            new NotificationTemplate("布产审核通过", "布产计划编号：{scheduleId} 已审核通过，订单编号：{orderCode}，产品编码：{productCode}，模具编码：{mouldCode}，布产数量：{productionQuantity}，布产时间：{productionTime}，操作员：{operator}，可以开始生产"));
        
        // 布产审核拒绝通知模板
        NOTIFICATION_TEMPLATES.put(NotificationTypeEnum.PRODUCTION_AUDIT_REJECTED, 
            new NotificationTemplate("布产审核拒绝", "布产计划编号：{scheduleId} 审核未通过，订单编号：{orderCode}，产品编码：{productCode}，模具编码：{mouldCode}，布产数量：{productionQuantity}，布产时间：{productionTime}，操作员：{operator}，原因：{rejectReason}"));
        
        // 采购审核拒绝通知模板
        NOTIFICATION_TEMPLATES.put(NotificationTypeEnum.PURCHASE_AUDIT_REJECTED, 
            new NotificationTemplate("采购审核拒绝", "采购汇总编号：{collectionId} 审核未通过，订单编号：{orderCode}，采购编码：{purchaseCode}，模具编号：{mouldNumber}，采购数量：{purchaseQuantity}，供应商：{supplier}，下单时间：{orderTime}，操作员：{operator}，原因：{rejectReason}"));
        
        // 布产创建待审核通知模板
        NOTIFICATION_TEMPLATES.put(NotificationTypeEnum.PRODUCTION_AUDIT_PENDING, 
            new NotificationTemplate("布产计划待审核", "布产计划编号：{scheduleId}，订单编号：{orderCode}，产品编码：{productCode}，模具编码：{mouldCode}，布产数量：{productionQuantity}，布产时间：{productionTime}，操作员：{operator}，请及时审核"));
        
        // 采购创建待审核通知模板
        NOTIFICATION_TEMPLATES.put(NotificationTypeEnum.PURCHASE_AUDIT_PENDING, 
            new NotificationTemplate("采购汇总待审核", "采购汇总编号：{collectionId}，订单编号：{orderCode}，采购编码：{purchaseCode}，模具编号：{mouldNumber}，采购数量：{purchaseQuantity}，供应商：{supplier}，下单时间：{orderTime}，操作员：{operator}，请及时审核"));
        
        // 布产审核通过通知注塑部模板
        NOTIFICATION_TEMPLATES.put(NotificationTypeEnum.PRODUCTION_APPROVED_TO_DEPT, 
            new NotificationTemplate("布产审核通过", "布产计划编号：{scheduleId} 已审核通过，订单编号：{orderCode}，产品编码：{productCode}，模具编码：{mouldCode}，布产数量：{productionQuantity}，布产时间：{productionTime}，操作员：{operator}，请安排注塑生产"));
        
        // 采购审核通过通知采购部模板
        NOTIFICATION_TEMPLATES.put(NotificationTypeEnum.PURCHASE_APPROVED_TO_DEPT, 
            new NotificationTemplate("采购审核通过", "采购汇总编号：{collectionId} 已审核通过，订单编号：{orderCode}，采购编码：{purchaseCode}，模具编号：{mouldNumber}，采购数量：{purchaseQuantity}，供应商：{supplier}，下单时间：{orderTime}，操作员：{operator}，请安排采购执行"));
        
        // 布产审核拒绝通知PMC模板
        NOTIFICATION_TEMPLATES.put(NotificationTypeEnum.PRODUCTION_REJECTED_TO_PMC, 
            new NotificationTemplate("布产审核拒绝", "布产计划编号：{scheduleId} 审核未通过，订单编号：{orderCode}，产品编码：{productCode}，模具编码：{mouldCode}，布产数量：{productionQuantity}，布产时间：{productionTime}，操作员：{operator}，拒绝原因：{rejectReason}，请重新调整计划"));
        
        // 采购审核拒绝通知PMC模板
        NOTIFICATION_TEMPLATES.put(NotificationTypeEnum.PURCHASE_REJECTED_TO_PMC, 
            new NotificationTemplate("采购审核拒绝", "采购汇总编号：{collectionId} 审核未通过，订单编号：{orderCode}，采购编码：{purchaseCode}，模具编号：{mouldNumber}，采购数量：{purchaseQuantity}，供应商：{supplier}，下单时间：{orderTime}，操作员：{operator}，拒绝原因：{rejectReason}，请重新调整计划"));

        // 包装清单/分包创建待审核通知模板
        NOTIFICATION_TEMPLATES.put(NotificationTypeEnum.SUBCONTRACT_AUDIT_PENDING,
            new NotificationTemplate("新包装清单待审核", "包装清单ID：{id}，关联订单号：{orderCode}，创建人：{creator}，创建时间：{creationTime}，请及时审核。"));

        // 包装清单/分包审核通过通知模板
        NOTIFICATION_TEMPLATES.put(NotificationTypeEnum.SUBCONTRACT_AUDIT_APPROVED,
            new NotificationTemplate("包装清单审核通过", "包装清单ID：{id}（订单号：{orderCode}）已审核通过。审核人：{auditor}，审核意见：{auditComment}。"));
        
        // 包装清单/分包审核拒绝通知模板
        NOTIFICATION_TEMPLATES.put(NotificationTypeEnum.SUBCONTRACT_AUDIT_REJECTED,
            new NotificationTemplate("包装清单审核未通过", "包装清单ID：{id}（订单号：{orderCode}）审核未通过。审核人：{auditor}，拒绝原因：{rejectReason}。"));
        
        // 布产完成审核待审核通知模板
        NOTIFICATION_TEMPLATES.put(NotificationTypeEnum.PRODUCTION_COMPLETE_AUDIT_PENDING,
            new NotificationTemplate("布产完成待审核", "订单号：{orderCode} 的布产已完成，请及时审核。"));
        
        // 布产完成审核通过通知模板
        NOTIFICATION_TEMPLATES.put(NotificationTypeEnum.PRODUCTION_COMPLETE_AUDIT_APPROVED,
            new NotificationTemplate("布产完成审核通过", "订单号：{orderCode} 的布产完成审核已通过。审核人：{auditor}，审核意见：{auditComment}，请安排注塑生产。"));
        
        // 布产完成审核拒绝通知模板
        NOTIFICATION_TEMPLATES.put(NotificationTypeEnum.PRODUCTION_COMPLETE_AUDIT_REJECTED,
            new NotificationTemplate("布产完成审核拒绝", "订单号：{orderCode} 的布产完成审核未通过。审核人：{auditor}，拒绝原因：{rejectReason}，请重新检查布产情况。"));
    }

    /**
     * 通知模板内部类
     */
    private static class NotificationTemplate {
        private String title;
        private String content;
        
        public NotificationTemplate(String title, String content) {
            this.title = title;
            this.content = content;
        }
        
        public String getTitle() { return title; }
        public String getContent() { return content; }
    }

    /**
     * 查询通知
     * 
     * @param id 通知主键
     * @return 通知
     */
    @Override
    public ErpNotification selectErpNotificationById(Long id)
    {
        return erpNotificationMapper.selectById(id);
    }

    /**
     * 查询通知列表
     * 
     * @param erpNotification 通知
     * @return 通知
     */
    @Override
    public List<ErpNotification> selectErpNotificationList(ErpNotification erpNotification)
    {
        // 使用MyBatis Plus的方式查询
        LambdaQueryWrapper<ErpNotification> wrapper = new LambdaQueryWrapper<>();
        if (erpNotification.getUserId() != null) {
            wrapper.eq(ErpNotification::getUserId, erpNotification.getUserId());
        }
        if (erpNotification.getStatus() != null) {
            wrapper.eq(ErpNotification::getStatus, erpNotification.getStatus());
        }
        if (erpNotification.getNotificationType() != null) {
            wrapper.eq(ErpNotification::getNotificationType, erpNotification.getNotificationType());
        }
        wrapper.orderByDesc(ErpNotification::getCreateTime);
        return erpNotificationMapper.selectList(wrapper);
    }

    /**
     * 新增通知
     * 
     * @param erpNotification 通知
     * @return 结果
     */
    @Override
    public int insertErpNotification(ErpNotification erpNotification)
    {
        if (erpNotification.getCreateTime() == null) {
            erpNotification.setCreateTime(DateUtils.getNowDate());
        }
        return erpNotificationMapper.insert(erpNotification);
    }

    /**
     * 修改通知
     * 
     * @param erpNotification 通知
     * @return 结果
     */
    @Override
    public int updateErpNotification(ErpNotification erpNotification)
    {
        return erpNotificationMapper.updateById(erpNotification);
    }

    /**
     * 批量删除通知
     * 
     * @param ids 需要删除的通知主键
     * @return 结果
     */
    @Override
    public int deleteErpNotificationByIds(Long[] ids)
    {
        return erpNotificationMapper.deleteBatchIds(Arrays.asList(ids));
    }

    /**
     * 删除通知信息
     * 
     * @param id 通知主键
     * @return 结果
     */
    @Override
    public int deleteErpNotificationById(Long id)
    {
        return erpNotificationMapper.deleteById(id);
    }

    /**
     * 发送通知给指定角色的用户
     * 
     * @param notificationType 通知类型
     * @param roleKey 角色标识
     * @param businessId 业务ID
     * @param businessType 业务类型
     * @param templateData 模板数据
     */
    @Override
    @Transactional
    public void sendNotificationToRole(NotificationTypeEnum notificationType, 
                                     String roleKey, 
                                     String businessId, //订单id等
                                     String businessType, //通知的业务类型
                                     Map<String, Object> templateData)
    {
        try {
            // 1. 查找角色用户
            List<SysUser> users = getUsersByRoleKey(roleKey);
            if (users.isEmpty()) {
                log.warn("未找到角色{}的用户", roleKey);
                return;
            }
            
            // 2. 发送通知给所有用户
            List<Long> userIds = users.stream().map(SysUser::getUserId).collect(Collectors.toList());
            sendNotificationToUsers(notificationType, userIds, businessId, businessType, templateData);
            
        } catch (Exception e) {
            log.error("发送角色通知失败，角色：{}，通知类型：{}", roleKey, notificationType, e);
        }
    }

    /**
     * 发送通知给指定用户
     * 
     * @param notificationType 通知类型
     * @param userId 用户ID
     * @param businessId 业务ID
     * @param businessType 业务类型
     * @param templateData 模板数据
     */
    @Override
    public void sendNotificationToUser(NotificationTypeEnum notificationType,
                                     Long userId,
                                     String businessId,
                                     String businessType,
                                     Map<String, Object> templateData)
    {
        sendNotificationToUsers(notificationType, Arrays.asList(userId), businessId, businessType, templateData);
    }

    /**
     * 发送通知给多个用户
     * 
     * @param notificationType 通知类型
     * @param userIds 用户ID列表
     * @param businessId 业务ID
     * @param businessType 业务类型
     * @param templateData 模板数据
     */
    @Override
    @Transactional
    public void sendNotificationToUsers(NotificationTypeEnum notificationType,
                                       List<Long> userIds,
                                       String businessId,
                                       String businessType,
                                       Map<String, Object> templateData)
    {
        try {
            // 1. 构建通知内容
            NotificationTemplate template = NOTIFICATION_TEMPLATES.get(notificationType);
            if (template == null) {
                log.error("未找到通知类型{}的模板", notificationType);
                return;
            }
            
            String title = template.getTitle();
            //将templateData中的数据"填充"进static的模板中，生成审核记录中的内容
            String content = buildContentFromTemplate(template.getContent(), templateData);
            
            // 2. 创建通知记录
            List<ErpNotification> notifications = new ArrayList<>();
            LoginUser currentUser = SecurityUtils.getLoginUser();
            
            for (Long userId : userIds) {
                // 获取用户信息
                SysUser user = userService.selectUserById(userId);
                if (user == null) {
                    log.warn("用户不存在，用户ID：{}", userId);
                    continue;
                }
                
                ErpNotification notification = new ErpNotification();
                notification.setNotificationType(notificationType.getCode());
                notification.setTitle(title);
                notification.setContent(content);
                notification.setUserId(userId);
                notification.setUserName(user.getUserName());
                notification.setBusinessId(businessId);
                notification.setBusinessType(businessType);
                notification.setStatus(0); // 未读
                notification.setPushStatus(0); // 未推送
                notification.setCreateTime(DateUtils.getNowDate());
                
                if (currentUser != null) {
                    notification.setSenderId(currentUser.getUserId());
                    notification.setSenderName(currentUser.getUsername());
                    notification.setCreateBy(currentUser.getUsername());
                }
                
                notifications.add(notification);
            }
            
            // 3. 批量保存到数据库
            if (!notifications.isEmpty()) {
                // 使用循环插入代替批量插入（避免SQL映射问题）
                for (ErpNotification notification : notifications) {
                    erpNotificationMapper.insert(notification);
                }
                log.info("批量保存通知成功，通知数量：{}", notifications.size());
            }
            
            // 4. 推送WebSocket通知
            pushWebSocketNotifications(notifications);
            
        } catch (Exception e) {
            log.error("发送通知失败，通知类型：{}，用户数量：{}", notificationType, userIds.size(), e);
        }
    }

    /**
     * 获取用户通知列表
     * 
     * @param userId 用户ID
     * @return 通知列表
     */
    @Override
    public List<ErpNotification> getUserNotifications(Long userId)
    {
        LambdaQueryWrapper<ErpNotification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ErpNotification::getUserId, userId)
                .orderByDesc(ErpNotification::getCreateTime)
               .ne(ErpNotification::getStatus, 2) // 排除已删除的通知
               .orderByDesc(ErpNotification::getCreateTime);
        return erpNotificationMapper.selectList(wrapper);
    }

    /**
     * 获取用户未读通知数量
     * 
     * @param userId 用户ID
     * @return 未读通知数量
     */
    @Override
    public int getUnreadNotificationCount(Long userId)
    {
        LambdaQueryWrapper<ErpNotification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ErpNotification::getUserId, userId)
                .orderByDesc(ErpNotification::getCreateTime)
                .eq(ErpNotification::getStatus, 0); // 未读状态
        return Math.toIntExact(erpNotificationMapper.selectCount(wrapper));
    }

    /**
     * 标记通知为已读
     * 
     * @param notificationId 通知ID
     * @return 结果
     */
    @Override
    public int markNotificationAsRead(Long notificationId)
    {
        ErpNotification notification = new ErpNotification();
        notification.setId(notificationId);
        notification.setStatus(1); // 已读
        notification.setReadTime(new Date());
        return erpNotificationMapper.updateById(notification);
    }

    /**
     * 批量标记通知为已读
     * 
     * @param userId 用户ID
     * @param notificationIds 通知ID列表
     * @return 结果
     */
    @Override
    public int batchMarkNotificationsAsRead(Long userId, List<Long> notificationIds)
    {
        // 使用MyBatis Plus的方式批量更新
        LambdaQueryWrapper<ErpNotification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ErpNotification::getUserId, userId)
               .in(ErpNotification::getId, notificationIds);
        
        ErpNotification updateEntity = new ErpNotification();
        updateEntity.setStatus(1); // 已读
        updateEntity.setReadTime(new Date());
        
        return erpNotificationMapper.update(updateEntity, wrapper);
    }

    /**
     * 根据业务ID和类型查询通知
     * 
     * @param businessId 业务ID
     * @param businessType 业务类型
     * @return 通知列表
     */
    @Override
    public List<ErpNotification> selectNotificationsByBusiness(String businessId, String businessType)
    {
        LambdaQueryWrapper<ErpNotification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ErpNotification::getBusinessId, businessId)
               .eq(ErpNotification::getBusinessType, businessType)
               .orderByDesc(ErpNotification::getCreateTime);
        return erpNotificationMapper.selectList(wrapper);
    }

    /**
     * 根据角色标识获取用户列表
     * 
     * @param roleKey 角色标识
     * @return 用户列表
     */
    private List<SysUser> getUsersByRoleKey(String roleKey) {
        try {
            // 1. 直接使用Mapper查询角色，绕过Service层的数据权限过滤
            SysRole queryRole = new SysRole();
            queryRole.setRoleKey(roleKey);
            List<SysRole> roles = sysRoleMapper.selectRoleList(queryRole);
            
            if (roles.isEmpty()) {
                log.warn("未找到角色，角色标识：{}", roleKey);
                return new ArrayList<>();
            }
            
            SysRole role = roles.get(0);
            
            // 2. 直接使用Mapper查询用户，绕过Service层的数据权限过滤
            SysUser queryUser = new SysUser();
            queryUser.setRoleId(role.getRoleId());
            return sysUserMapper.selectAllocatedList(queryUser);
            
        } catch (Exception e) {
            log.error("根据角色标识获取用户列表失败，角色标识：{}", roleKey, e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据模板构建内容
     * 
     * @param template 模板内容
     * @param templateData 模板数据
     * @return 构建后的内容
     */
    private String buildContentFromTemplate(String template, Map<String, Object> templateData) {
        String content = template;
        if (templateData != null) {
            for (Map.Entry<String, Object> entry : templateData.entrySet()) {
                String placeholder = "{" + entry.getKey() + "}";//组合成模板中的对应{key}
                String value = entry.getValue() != null ? entry.getValue().toString() : "";//提取templateData中对应key的value
                content = content.replace(placeholder, value);//替换模板中的{key}为value
            }
        }
        return content;
    }

    /**
     * 推送WebSocket通知
     * 
     * @param notifications 通知列表
     */
    private void pushWebSocketNotifications(List<ErpNotification> notifications) {
        for (ErpNotification notification : notifications) {
            try {
                // 构建WebSocket消息
                Map<String, Object> message = new HashMap<>();
                message.put("type", "NOTIFICATION");
                message.put("notificationId", notification.getId());
                message.put("notificationType", notification.getNotificationType());
                message.put("title", notification.getTitle());
                message.put("content", notification.getContent());
                message.put("businessId", notification.getBusinessId());
                message.put("businessType", notification.getBusinessType());
                message.put("timestamp", System.currentTimeMillis());
                
                // 发送WebSocket消息
                boolean success = WebSocketServer.sendObjectToUser(
                    notification.getUserId(), message);
                
                // 更新推送状态
                int pushStatus = success ? 1 : 2; // 1:已推送 2:推送失败
                ErpNotification updateNotification = new ErpNotification();
                updateNotification.setId(notification.getId());
                updateNotification.setPushStatus(pushStatus);
                updateNotification.setPushTime(new Date());
                erpNotificationMapper.updateById(updateNotification);
                
                if (success) {
                    log.debug("WebSocket通知推送成功，用户ID：{}，通知ID：{}", 
                            notification.getUserId(), notification.getId());
                } else {
                    log.warn("WebSocket通知推送失败，用户ID：{}，通知ID：{}", 
                            notification.getUserId(), notification.getId());
                }
                
            } catch (Exception e) {
                log.error("推送WebSocket通知失败，用户ID：{}，通知ID：{}", 
                        notification.getUserId(), notification.getId(), e);
                
                // 标记推送失败
                ErpNotification failedNotification = new ErpNotification();
                failedNotification.setId(notification.getId());
                failedNotification.setPushStatus(2);
                failedNotification.setPushTime(new Date());
                erpNotificationMapper.updateById(failedNotification);
            }
        }
    }

    /**
     * 获取用户未读通知列表
     *
     * @param userId 用户ID
     * @return 未读通知列表
     */
    @Override
    public List<ErpNotification> getUnreadNotifications(Long userId)
    {
        LambdaQueryWrapper<ErpNotification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ErpNotification::getUserId, userId)
               .eq(ErpNotification::getStatus, 0)  // 0表示未读
               .orderByDesc(ErpNotification::getCreateTime);  // 按创建时间倒序
        return notificationMapper.selectList(wrapper);
    }

    @Override
    public void markNotificationsAsReadByBusiness(String businessId, String businessType) {
        if (businessId == null || !StringUtils.hasText(businessType)) {
            return;
        }
        ErpNotification notificationUpdate = new ErpNotification();
        notificationUpdate.setStatus(1); // 1 = 已读

        LambdaUpdateWrapper<ErpNotification> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ErpNotification::getBusinessId, businessId)
               .eq(ErpNotification::getBusinessType, businessType)
               .eq(ErpNotification::getStatus, 0); // 0 = 未读

        notificationMapper.update(notificationUpdate, wrapper);
        log.info("已将业务(ID: {}, 类型: {}) 的未读通知标记为已读。", businessId, businessType);
    }
} 