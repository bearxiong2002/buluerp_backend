package com.ruoyi.web.controller.erp;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.web.domain.ErpAuditRecord;
import com.ruoyi.web.enums.AuditTypeEnum;
import com.ruoyi.web.service.IErpAuditRecordService;
import com.ruoyi.web.service.IOrderAuditService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * 审核记录Controller
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
@Api(tags = "审核记录管理")
@RestController
@RequestMapping("/web/audit")
public class ErpAuditRecordController extends BaseController
{
    @Autowired
    private IErpAuditRecordService erpAuditRecordService;

    @Autowired
    private IOrderAuditService orderAuditService;

    // ==================== 通用审核记录管理接口 ====================

    /**
     * 查询审核记录列表（管理员权限）
     */
    @ApiOperation("查询审核记录列表")
    @PreAuthorize("@ss.hasPermi('web:audit:list')")
    @GetMapping("/list")
    public TableDataInfo list(ErpAuditRecord erpAuditRecord)
    {
        startPage();
        List<ErpAuditRecord> list = erpAuditRecordService.selectErpAuditRecordList(erpAuditRecord);
        return getDataTable(list);
    }

    /**
     * 导出审核记录列表（管理员权限）
     */
    @ApiOperation("导出审核记录列表")
    @PreAuthorize("@ss.hasPermi('web:audit:export')")
    @Log(title = "审核记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ErpAuditRecord erpAuditRecord)
    {
        List<ErpAuditRecord> list = erpAuditRecordService.selectErpAuditRecordList(erpAuditRecord);
        ExcelUtil<ErpAuditRecord> util = new ExcelUtil<ErpAuditRecord>(ErpAuditRecord.class);
        util.exportExcel(response, list, "审核记录数据");
    }

    /**
     * 获取审核记录详细信息（管理员权限）
     */
    @ApiOperation("获取审核记录详细信息")
    @PreAuthorize("@ss.hasPermi('web:audit:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@ApiParam("审核记录ID") @PathVariable("id") Long id)
    {
        return success(erpAuditRecordService.selectErpAuditRecordById(id));
    }

    /**
     * 根据审核类型和对象ID查询审核记录（管理员权限）
     */
    @ApiOperation("根据审核类型和对象ID查询审核记录")
    @PreAuthorize("@ss.hasPermi('web:audit:query')")
    @GetMapping("/byBusiness")
    public AjaxResult getByBusiness(@ApiParam("审核类型") @RequestParam Integer auditType, 
                                   @ApiParam("审核对象ID") @RequestParam Long auditId)
    {
        List<ErpAuditRecord> list = erpAuditRecordService.selectByAuditTypeAndId(auditType, auditId);
        return success(list);
    }

    /**
     * 获取审核类型枚举
     */
    @ApiOperation("获取审核类型枚举")
    @GetMapping("/auditTypes")
    public AjaxResult getAuditTypes()
    {
        return success(Arrays.asList(AuditTypeEnum.values()));
    }

    /**
     * 根据审核ID获取审核记录的具体内容
     */
    @ApiOperation("根据审核ID获取审核记录的具体内容")
    @PreAuthorize("@ss.hasPermi('web:audit:query')")
    @GetMapping("/detail/{auditId}")
    public AjaxResult getAuditDetail(@ApiParam("审核对象ID") @PathVariable("auditId") Long auditId,
                                    @ApiParam("审核类型") @RequestParam Integer auditType)
    {
        try {
            AuditTypeEnum auditTypeEnum = AuditTypeEnum.getByCode(auditType);
            if (auditTypeEnum == null) {
                return error("不支持的审核类型");
            }
            
            Object businessDetail = null;
            switch (auditTypeEnum) {
                case ORDER_AUDIT:
                    // 调用订单审核服务获取订单详情
                    businessDetail = orderAuditService.getOrderDetail(auditId);
                    break;
                case OUTSOURCING_AUDIT:
                    // TODO: 调用委外加工单服务获取详情
                    // businessDetail = outsourcingAuditService.getOutsourcingDetail(auditId);
                    return error("委外加工单审核详情功能待实现");
                case PRODUCTION_AUDIT:
                    // TODO: 调用布产服务获取详情
                    // businessDetail = productionAuditService.getProductionDetail(auditId);
                    return error("布产审核详情功能待实现");
                case SUBCONTRACT_AUDIT:
                    // TODO: 调用分包服务获取详情
                    // businessDetail = subcontractAuditService.getSubcontractDetail(auditId);
                    return error("分包审核详情功能待实现");
                default:
                    return error("不支持的审核类型");
            }
            
            if (businessDetail == null) {
                return error("未找到相关业务记录");
            }
            
            return success(businessDetail);
            
        } catch (Exception e) {
            logger.error("获取审核详情失败", e);
            return error("获取审核详情失败：" + e.getMessage());
        }
    }

    // ==================== 订单审核接口 ====================

    /**
     * 查询待审核订单列表（订单审核人权限）
     */
    @ApiOperation("查询待审核订单列表")
    @PreAuthorize("@ss.hasPermi('web:audit:order:list')")
    @GetMapping("/order/pending")
    public TableDataInfo getOrderPendingList()
    {
        startPage();
        List<ErpAuditRecord> list = erpAuditRecordService.selectPendingAuditRecords(AuditTypeEnum.ORDER_AUDIT.getCode());
        return getDataTable(list);
    }

    /**
     * 订单审核通过（订单审核人权限）
     */
    @ApiOperation("订单审核通过")
    @PreAuthorize("@ss.hasPermi('web:audit:order:approve')")
    @Log(title = "订单审核通过", businessType = BusinessType.UPDATE)
    @PostMapping("/order/approve/{id}")
    public AjaxResult approveOrder(@ApiParam("审核记录ID") @PathVariable Long id, 
                                  @ApiParam("审核意见") @RequestBody(required = false) String auditComment)
    {
        String auditor = SecurityUtils.getUsername();
        
        // 验证是否为订单审核记录
        ErpAuditRecord auditRecord = erpAuditRecordService.selectErpAuditRecordById(id);
        if (auditRecord == null) {
            return error("审核记录不存在");
        }
        if (!AuditTypeEnum.ORDER_AUDIT.getCode().equals(auditRecord.getAuditType())) {
            return error("该记录不是订单审核记录");
        }
        
        orderAuditService.handleOrderApproved(id, auditor, auditComment);
        return success("订单审核通过成功");
    }

    /**
     * 订单审核拒绝（订单审核人权限）
     */
    @ApiOperation("订单审核拒绝")
    @PreAuthorize("@ss.hasPermi('web:audit:order:reject')")
    @Log(title = "订单审核拒绝", businessType = BusinessType.UPDATE)
    @PostMapping("/order/reject/{id}")
    public AjaxResult rejectOrder(@ApiParam("审核记录ID") @PathVariable Long id, 
                                 @ApiParam("审核意见") @RequestBody(required = false) String auditComment)
    {
        String auditor = SecurityUtils.getUsername();
        
        // 验证是否为订单审核记录
        ErpAuditRecord auditRecord = erpAuditRecordService.selectErpAuditRecordById(id);
        if (auditRecord == null) {
            return error("审核记录不存在");
        }
        if (!AuditTypeEnum.ORDER_AUDIT.getCode().equals(auditRecord.getAuditType())) {
            return error("该记录不是订单审核记录");
        }
        
        orderAuditService.handleOrderRejected(id, auditor, auditComment);
        return success("订单审核拒绝成功");
    }

    /**
     * 查询订单审核人的待审核记录数量
     */
    @ApiOperation("查询订单审核人的待审核记录数量")
    @PreAuthorize("@ss.hasPermi('web:audit:order:count')")
    @GetMapping("/order/pendingCount")
    public AjaxResult getOrderPendingCount()
    {
        String auditor = SecurityUtils.getUsername();
        int count = erpAuditRecordService.countPendingAuditsByAuditor(auditor, AuditTypeEnum.ORDER_AUDIT.getCode());
        return success(count);
    }

    // ==================== 委外加工单审核接口 ====================

    /**
     * 查询待审核委外加工单列表（委外审核人权限）
     */
    @ApiOperation("查询待审核委外加工单列表")
    @PreAuthorize("@ss.hasPermi('web:audit:outsourcing:list')")
    @GetMapping("/outsourcing/pending")
    public TableDataInfo getOutsourcingPendingList()
    {
        startPage();
        List<ErpAuditRecord> list = erpAuditRecordService.selectPendingAuditRecords(AuditTypeEnum.OUTSOURCING_AUDIT.getCode());
        return getDataTable(list);
    }

    /**
     * 委外加工单审核通过（委外审核人权限）
     */
    @ApiOperation("委外加工单审核通过")
    @PreAuthorize("@ss.hasPermi('web:audit:outsourcing:approve')")
    @Log(title = "委外加工单审核通过", businessType = BusinessType.UPDATE)
    @PostMapping("/outsourcing/approve/{id}")
    public AjaxResult approveOutsourcing(@ApiParam("审核记录ID") @PathVariable Long id, 
                                        @ApiParam("审核意见") @RequestBody(required = false) String auditComment)
    {
        String auditor = SecurityUtils.getUsername();
        
        // 验证是否为委外审核记录
        ErpAuditRecord auditRecord = erpAuditRecordService.selectErpAuditRecordById(id);
        if (auditRecord == null) {
            return error("审核记录不存在");
        }
        if (!AuditTypeEnum.OUTSOURCING_AUDIT.getCode().equals(auditRecord.getAuditType())) {
            return error("该记录不是委外加工单审核记录");
        }
        
        erpAuditRecordService.approveAudit(id, auditor, auditComment);
        return success("委外加工单审核通过成功");
    }

    /**
     * 委外加工单审核拒绝（委外审核人权限）
     */
    @ApiOperation("委外加工单审核拒绝")
    @PreAuthorize("@ss.hasPermi('web:audit:outsourcing:reject')")
    @Log(title = "委外加工单审核拒绝", businessType = BusinessType.UPDATE)
    @PostMapping("/outsourcing/reject/{id}")
    public AjaxResult rejectOutsourcing(@ApiParam("审核记录ID") @PathVariable Long id, 
                                       @ApiParam("审核意见") @RequestBody(required = false) String auditComment)
    {
        String auditor = SecurityUtils.getUsername();
        
        // 验证是否为委外审核记录
        ErpAuditRecord auditRecord = erpAuditRecordService.selectErpAuditRecordById(id);
        if (auditRecord == null) {
            return error("审核记录不存在");
        }
        if (!AuditTypeEnum.OUTSOURCING_AUDIT.getCode().equals(auditRecord.getAuditType())) {
            return error("该记录不是委外加工单审核记录");
        }
        
        erpAuditRecordService.rejectAudit(id, auditor, auditComment);
        return success("委外加工单审核拒绝成功");
    }

    /**
     * 查询委外审核人的待审核记录数量
     */
    @ApiOperation("查询委外审核人的待审核记录数量")
    @PreAuthorize("@ss.hasPermi('web:audit:outsourcing:count')")
    @GetMapping("/outsourcing/pendingCount")
    public AjaxResult getOutsourcingPendingCount()
    {
        String auditor = SecurityUtils.getUsername();
        int count = erpAuditRecordService.countPendingAuditsByAuditor(auditor, AuditTypeEnum.OUTSOURCING_AUDIT.getCode());
        return success(count);
    }

    // ==================== 布产审核接口 ====================

    /**
     * 查询待审核布产列表（布产审核人权限）
     */
    @ApiOperation("查询待审核布产列表")
    @PreAuthorize("@ss.hasPermi('web:audit:production:list')")
    @GetMapping("/production/pending")
    public TableDataInfo getProductionPendingList()
    {
        startPage();
        List<ErpAuditRecord> list = erpAuditRecordService.selectPendingAuditRecords(AuditTypeEnum.PRODUCTION_AUDIT.getCode());
        return getDataTable(list);
    }

    /**
     * 布产审核通过（布产审核人权限）
     */
    @ApiOperation("布产审核通过")
    @PreAuthorize("@ss.hasPermi('web:audit:production:approve')")
    @Log(title = "布产审核通过", businessType = BusinessType.UPDATE)
    @PostMapping("/production/approve/{id}")
    public AjaxResult approveProduction(@ApiParam("审核记录ID") @PathVariable Long id, 
                                       @ApiParam("审核意见") @RequestBody(required = false) String auditComment)
    {
        String auditor = SecurityUtils.getUsername();
        
        // 验证是否为布产审核记录
        ErpAuditRecord auditRecord = erpAuditRecordService.selectErpAuditRecordById(id);
        if (auditRecord == null) {
            return error("审核记录不存在");
        }
        if (!AuditTypeEnum.PRODUCTION_AUDIT.getCode().equals(auditRecord.getAuditType())) {
            return error("该记录不是布产审核记录");
        }
        
        erpAuditRecordService.approveAudit(id, auditor, auditComment);
        return success("布产审核通过成功");
    }

    /**
     * 布产审核拒绝（布产审核人权限）
     */
    @ApiOperation("布产审核拒绝")
    @PreAuthorize("@ss.hasPermi('web:audit:production:reject')")
    @Log(title = "布产审核拒绝", businessType = BusinessType.UPDATE)
    @PostMapping("/production/reject/{id}")
    public AjaxResult rejectProduction(@ApiParam("审核记录ID") @PathVariable Long id, 
                                      @ApiParam("审核意见") @RequestBody(required = false) String auditComment)
    {
        String auditor = SecurityUtils.getUsername();
        
        // 验证是否为布产审核记录
        ErpAuditRecord auditRecord = erpAuditRecordService.selectErpAuditRecordById(id);
        if (auditRecord == null) {
            return error("审核记录不存在");
        }
        if (!AuditTypeEnum.PRODUCTION_AUDIT.getCode().equals(auditRecord.getAuditType())) {
            return error("该记录不是布产审核记录");
        }
        
        erpAuditRecordService.rejectAudit(id, auditor, auditComment);
        return success("布产审核拒绝成功");
    }

    /**
     * 查询布产审核人的待审核记录数量
     */
    @ApiOperation("查询布产审核人的待审核记录数量")
    @PreAuthorize("@ss.hasPermi('web:audit:production:count')")
    @GetMapping("/production/pendingCount")
    public AjaxResult getProductionPendingCount()
    {
        String auditor = SecurityUtils.getUsername();
        int count = erpAuditRecordService.countPendingAuditsByAuditor(auditor, AuditTypeEnum.PRODUCTION_AUDIT.getCode());
        return success(count);
    }

    // ==================== 分包审核接口 ====================

    /**
     * 查询待审核分包列表（分包审核人权限）
     */
    @ApiOperation("查询待审核分包列表")
    @PreAuthorize("@ss.hasPermi('web:audit:subcontract:list')")
    @GetMapping("/subcontract/pending")
    public TableDataInfo getSubcontractPendingList()
    {
        startPage();
        List<ErpAuditRecord> list = erpAuditRecordService.selectPendingAuditRecords(AuditTypeEnum.SUBCONTRACT_AUDIT.getCode());
        return getDataTable(list);
    }

    /**
     * 分包审核通过（分包审核人权限）
     */
    @ApiOperation("分包审核通过")
    @PreAuthorize("@ss.hasPermi('web:audit:subcontract:approve')")
    @Log(title = "分包审核通过", businessType = BusinessType.UPDATE)
    @PostMapping("/subcontract/approve/{id}")
    public AjaxResult approveSubcontract(@ApiParam("审核记录ID") @PathVariable Long id, 
                                        @ApiParam("审核意见") @RequestBody(required = false) String auditComment)
    {
        String auditor = SecurityUtils.getUsername();
        
        // 验证是否为分包审核记录
        ErpAuditRecord auditRecord = erpAuditRecordService.selectErpAuditRecordById(id);
        if (auditRecord == null) {
            return error("审核记录不存在");
        }
        if (!AuditTypeEnum.SUBCONTRACT_AUDIT.getCode().equals(auditRecord.getAuditType())) {
            return error("该记录不是分包审核记录");
        }
        
        erpAuditRecordService.approveAudit(id, auditor, auditComment);
        return success("分包审核通过成功");
    }

    /**
     * 分包审核拒绝（分包审核人权限）
     */
    @ApiOperation("分包审核拒绝")
    @PreAuthorize("@ss.hasPermi('web:audit:subcontract:reject')")
    @Log(title = "分包审核拒绝", businessType = BusinessType.UPDATE)
    @PostMapping("/subcontract/reject/{id}")
    public AjaxResult rejectSubcontract(@ApiParam("审核记录ID") @PathVariable Long id, 
                                       @ApiParam("审核意见") @RequestBody(required = false) String auditComment)
    {
        String auditor = SecurityUtils.getUsername();
        
        // 验证是否为分包审核记录
        ErpAuditRecord auditRecord = erpAuditRecordService.selectErpAuditRecordById(id);
        if (auditRecord == null) {
            return error("审核记录不存在");
        }
        if (!AuditTypeEnum.SUBCONTRACT_AUDIT.getCode().equals(auditRecord.getAuditType())) {
            return error("该记录不是分包审核记录");
        }
        
        erpAuditRecordService.rejectAudit(id, auditor, auditComment);
        return success("分包审核拒绝成功");
    }

    /**
     * 查询分包审核人的待审核记录数量
     */
    @ApiOperation("查询分包审核人的待审核记录数量")
    @PreAuthorize("@ss.hasPermi('web:audit:subcontract:count')")
    @GetMapping("/subcontract/pendingCount")
    public AjaxResult getSubcontractPendingCount()
    {
        String auditor = SecurityUtils.getUsername();
        int count = erpAuditRecordService.countPendingAuditsByAuditor(auditor, AuditTypeEnum.SUBCONTRACT_AUDIT.getCode());
        return success(count);
    }
}