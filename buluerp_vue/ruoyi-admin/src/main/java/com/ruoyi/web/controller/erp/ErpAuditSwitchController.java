package com.ruoyi.web.controller.erp;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.web.domain.ErpAuditSwitch;
import com.ruoyi.web.service.IErpAuditSwitchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 审核开关Controller
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
@Api(tags = "审核开关管理")
@RestController
@RequestMapping("/system/audit-switch")
public class ErpAuditSwitchController extends BaseController
{
    @Autowired
    private IErpAuditSwitchService erpAuditSwitchService;

    /**
     * 查询审核开关列表
     */
    @ApiOperation("查询审核开关列表")
    @GetMapping("/list")
    public TableDataInfo list(ErpAuditSwitch erpAuditSwitch)
    {
        startPage();
        
        // 根据用户角色过滤审核类型
        List<ErpAuditSwitch> list = erpAuditSwitchService.selectErpAuditSwitchListWithPermission(erpAuditSwitch);
        
        return getDataTable(list);
    }

    /**
     * 检查审核是否启用
     */
    @ApiOperation("检查审核是否启用")
    @GetMapping("/enabled/{auditType}")
    public AjaxResult isEnabled(@ApiParam("审核类型") @PathVariable("auditType") Integer auditType)
    {
        boolean enabled = erpAuditSwitchService.isAuditEnabled(auditType);
        return success(enabled);
    }

    /**
     * 设置审核开关状态
     */
    @ApiOperation("设置审核开关状态")
    @Log(title = "审核开关", businessType = BusinessType.UPDATE)
    @PutMapping("/status/{auditType}/{status}")
    public AjaxResult setStatus(
            @ApiParam("审核类型") @PathVariable("auditType") Integer auditType,
            @ApiParam("状态 0=关闭审核，1=开启审核") @PathVariable("status") Integer status)
    {
        // 获取当前用户的角色信息
        Set<String> userRoles = SecurityUtils.getLoginUser().getUser().getRoles()
                .stream()
                .map(role -> role.getRoleKey())
                .collect(Collectors.toSet());

        // 检查用户是否为boss或admin角色
        boolean isBossOrAdmin = userRoles.contains("boss") ||
                userRoles.contains("admin") ||
                userRoles.contains("SUPER_ADMIN") ||
                SecurityUtils.isAdmin(SecurityUtils.getUserId());

        if (!isBossOrAdmin) {
            // boss和admin角色可以查看所有审核开关
            throw new RuntimeException("只有管理员或老板能更改审核开关");
        }

        if (status != 0 && status != 1) {
            return error("状态参数错误，0=关闭审核，1=开启审核");
        }
        
        boolean result = erpAuditSwitchService.setAuditStatus(auditType, status);
        if (result) {
            String action = status == 1 ? "开启" : "关闭";
            return success("审核开关" + action + "成功");
        } else {
            return error("设置审核开关状态失败");
        }
    }

} 