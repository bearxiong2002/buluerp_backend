package com.ruoyi.web.controller.erp;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.web.domain.ErpNotification;
import com.ruoyi.web.service.INotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知Controller - 精简版
 * 主要用于用户查看和管理自己的通知
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
@Api(tags = "通知管理")
@RestController
@RequestMapping("/system/notification")
public class ErpNotificationController extends BaseController
{
    @Autowired
    private INotificationService notificationService;

    /**
     * 获取当前用户的通知列表（支持分页）
     */
    @GetMapping("/my")
    public TableDataInfo getMyNotifications()
    {
        startPage();
        Long userId = SecurityUtils.getUserId();
        List<ErpNotification> list = notificationService.getUserNotifications(userId);
        return getDataTable(list);
    }

    /**
     * 获取当前用户未读通知列表
     */
    @ApiOperation("获取当前用户未读通知列表")
    @GetMapping("/unread")
    public TableDataInfo getUnread()
    {
        startPage();
        Long userId = SecurityUtils.getUserId();
        List<ErpNotification> list = notificationService.getUnreadNotifications(userId);
        return getDataTable(list);
    }

    /**
     * 标记通知为已读（支持单个或批量）
     * @param ids 通知ID，多个ID用逗号分隔，例如：1,2,3
     */
    @PostMapping("/markRead/{ids}")
    public AjaxResult markAsRead(@PathVariable String ids)
    {
        Long userId = SecurityUtils.getUserId();
        
        // 解析ID字符串
        String[] idArray = ids.split(",");
        List<Long> notificationIds = new java.util.ArrayList<>();
        
        for (String idStr : idArray) {
            try {
                notificationIds.add(Long.parseLong(idStr.trim()));
            } catch (NumberFormatException e) {
                return error("无效的通知ID格式：" + idStr);
            }
        }
        
        if (notificationIds.isEmpty()) {
            return error("通知ID不能为空");
        }
        
        // 单个或批量处理
        int result;
        if (notificationIds.size() == 1) {
            result = notificationService.markNotificationAsRead(notificationIds.get(0));
        } else {
            result = notificationService.batchMarkNotificationsAsRead(userId, notificationIds);
        }
        
        return toAjax(result);
    }

    /**
     * 标记所有通知为已读
     */
    @PostMapping("/markAllRead")
    public AjaxResult markAllAsRead()
    {
        Long userId = SecurityUtils.getUserId();
        // 获取用户所有未读通知
        List<ErpNotification> unreadNotifications = notificationService.getUserNotifications(userId);
        List<Long> unreadIds = unreadNotifications.stream()
                .filter(n -> n.getStatus() == 0) // 未读状态
                .map(ErpNotification::getId)
                .collect(java.util.stream.Collectors.toList());
        
        if (!unreadIds.isEmpty()) {
            int result = notificationService.batchMarkNotificationsAsRead(userId, unreadIds);
        return toAjax(result);
    }
        return success("没有未读通知");
    }
} 