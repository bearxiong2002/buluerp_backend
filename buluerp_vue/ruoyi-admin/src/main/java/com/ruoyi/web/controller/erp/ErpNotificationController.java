package com.ruoyi.web.controller.erp;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.web.domain.ErpNotification;
import com.ruoyi.web.enums.NotificationTypeEnum;
import com.ruoyi.web.service.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * 通知Controller
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
@RestController
@RequestMapping("/web/notification")
public class ErpNotificationController extends BaseController
{
    @Autowired
    private INotificationService notificationService;

    /**
     * 查询通知列表
     */
    @PreAuthorize("@ss.hasPermi('web:notification:list')")
    @GetMapping("/list")
    public TableDataInfo list(ErpNotification erpNotification)
    {
        startPage();
        List<ErpNotification> list = notificationService.selectErpNotificationList(erpNotification);
        return getDataTable(list);
    }

    /**
     * 导出通知列表
     */
    @PreAuthorize("@ss.hasPermi('web:notification:export')")
    @Log(title = "通知", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ErpNotification erpNotification)
    {
        List<ErpNotification> list = notificationService.selectErpNotificationList(erpNotification);
        ExcelUtil<ErpNotification> util = new ExcelUtil<ErpNotification>(ErpNotification.class);
        util.exportExcel(response, list, "通知数据");
    }

    /**
     * 获取通知详细信息
     */
    @PreAuthorize("@ss.hasPermi('web:notification:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(notificationService.selectErpNotificationById(id));
    }

    /**
     * 新增通知
     */
    @PreAuthorize("@ss.hasPermi('web:notification:add')")
    @Log(title = "通知", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ErpNotification erpNotification)
    {
        return toAjax(notificationService.insertErpNotification(erpNotification));
    }

    /**
     * 修改通知
     */
    @PreAuthorize("@ss.hasPermi('web:notification:edit')")
    @Log(title = "通知", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ErpNotification erpNotification)
    {
        return toAjax(notificationService.updateErpNotification(erpNotification));
    }

    /**
     * 删除通知
     */
    @PreAuthorize("@ss.hasPermi('web:notification:remove')")
    @Log(title = "通知", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(notificationService.deleteErpNotificationByIds(ids));
    }

    /**
     * 获取当前用户的通知列表
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
     * 获取当前用户未读通知数量
     */
    @GetMapping("/unreadCount")
    public AjaxResult getUnreadCount()
    {
        Long userId = SecurityUtils.getUserId();
        int count = notificationService.getUnreadNotificationCount(userId);
        return success(count);
    }

    /**
     * 标记通知为已读
     */
    @PostMapping("/markRead/{id}")
    public AjaxResult markAsRead(@PathVariable Long id)
    {
        int result = notificationService.markNotificationAsRead(id);
        return toAjax(result);
    }

    /**
     * 批量标记通知为已读
     */
    @PostMapping("/batchMarkRead")
    public AjaxResult batchMarkAsRead(@RequestBody List<Long> notificationIds)
    {
        Long userId = SecurityUtils.getUserId();
        int result = notificationService.batchMarkNotificationsAsRead(userId, notificationIds);
        return toAjax(result);
    }

    /**
     * 根据业务ID和类型查询通知
     */
    @PreAuthorize("@ss.hasPermi('web:notification:query')")
    @GetMapping("/byBusiness")
    public AjaxResult getByBusiness(@RequestParam Long businessId, @RequestParam String businessType)
    {
        List<ErpNotification> list = notificationService.selectNotificationsByBusiness(businessId, businessType);
        return success(list);
    }

    /**
     * 获取通知类型枚举
     */
    @GetMapping("/notificationTypes")
    public AjaxResult getNotificationTypes()
    {
        return success(Arrays.asList(NotificationTypeEnum.values()));
    }
} 