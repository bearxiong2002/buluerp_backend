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
import com.ruoyi.web.request.audit.AuditRequest;
import com.ruoyi.web.service.IErpAuditRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("/system/audit")
public class ErpAuditRecordController extends BaseController
{
    @Autowired
    private IErpAuditRecordService erpAuditRecordService;

    // ==================== 通用审核记录管理接口 ====================

    /**
     * 查询审核记录列表（管理员权限）
     * 支持通过审核类型和审核对象ID进行精确查询
     */
    @ApiOperation("查询审核记录列表")
    @PreAuthorize("@ss.hasPermi('system:audit:list')")
    @GetMapping("/list")
    public TableDataInfo list(ErpAuditRecord erpAuditRecord,
                             @ApiParam("审核类型（可选）") @RequestParam(required = false) Integer auditType,
                             @ApiParam("审核对象ID（可选）") @RequestParam(required = false) Long auditId)
    {
        startPage();
        List<ErpAuditRecord> list = erpAuditRecordService.selectAuditRecords(erpAuditRecord, auditType, auditId, null, null);
        return getDataTable(list);
    }

    /**
     * 导出审核记录列表（管理员权限）
     */
    @ApiOperation("导出审核记录列表")
    @PreAuthorize("@ss.hasPermi('system:audit:export')")
    @Log(title = "审核记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ErpAuditRecord erpAuditRecord)
    {
        List<ErpAuditRecord> list = erpAuditRecordService.selectAuditRecords(erpAuditRecord, null, null, null, null);
        ExcelUtil<ErpAuditRecord> util = new ExcelUtil<ErpAuditRecord>(ErpAuditRecord.class);
        util.exportExcel(response, list, "审核记录数据");
    }

    /**
     * 获取审核记录详细信息（管理员权限）
     */
    @ApiOperation("获取审核记录详细信息")
    @PreAuthorize("@ss.hasPermi('system:audit:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@ApiParam("审核记录ID") @PathVariable("id") Long id)
    {
        ErpAuditRecord queryRecord = new ErpAuditRecord();
        queryRecord.setId(id);
        List<ErpAuditRecord> list = erpAuditRecordService.selectAuditRecords(queryRecord, null, null, null, null);
        return success(list.isEmpty() ? null : list.get(0));
    }



    /**
     * 根据审核ID获取审核记录的具体内容
     */
    @ApiOperation("根据审核ID获取审核记录的具体内容")
    @PreAuthorize("@ss.hasPermi('system:audit:query')")
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
                    // 调用审核记录服务获取订单详情
                    businessDetail = erpAuditRecordService.getOrderDetail(auditId);
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
    @PreAuthorize("@ss.hasPermi('system:audit:order:list')")
    @GetMapping("/order/pending")
    public TableDataInfo getOrderPendingList()
    {
        startPage();
        List<ErpAuditRecord> list = erpAuditRecordService.selectAuditRecords(null, AuditTypeEnum.ORDER_AUDIT.getCode(), null, true, null);
        return getDataTable(list);
    }

    /**
     * 订单审核（订单审核人权限）
     * @param id 审核记录ID
     * @param auditRequest 审核请求，包含confirm字段（1=通过，-1=拒绝）和审核意见
     */
    @ApiOperation("订单审核")
    @PreAuthorize("@ss.hasPermi('system:audit:order:audit')")
    @Log(title = "订单审核", businessType = BusinessType.UPDATE)
    @PostMapping("/order/audit/{id}")
    public AjaxResult auditOrder(@ApiParam("审核记录ID") @PathVariable Long id, @Validated @RequestBody AuditRequest auditRequest)
    {
        if (!auditRequest.getConfirm().equals(1) && !auditRequest.getConfirm().equals(-1)) {
            return error("审核状态参数错误，1=通过，-1=拒绝");
        }
        
        String auditor = SecurityUtils.getUsername();
        
        // 验证是否为订单审核记录
        ErpAuditRecord queryRecord = new ErpAuditRecord();
        queryRecord.setId(id);
        List<ErpAuditRecord> records = erpAuditRecordService.selectAuditRecords(queryRecord, null, null, null, null);
        if (records.isEmpty()) {
            return error("审核记录不存在");
        }
        
        ErpAuditRecord auditRecord = records.get(0);
        if (!AuditTypeEnum.ORDER_AUDIT.getCode().equals(auditRecord.getAuditType())) {
            return error("该记录不是订单审核记录");
        }
        
        // 处理审核
        int result = erpAuditRecordService.processAudit(Arrays.asList(id), auditRequest.getConfirm(), auditor, auditRequest.getAuditComment());
        if (result > 0) {
            // 根据confirm字段调用相应的业务处理
            if (auditRequest.getConfirm().equals(1)) {
                erpAuditRecordService.handleOrderApproved(id, auditor, auditRequest.getAuditComment());
                return success("订单审核通过成功");
            } else {
                erpAuditRecordService.handleOrderRejected(id, auditor, auditRequest.getAuditComment());
                return success("订单审核拒绝成功");
            }
        } else {
            return error("审核处理失败");
        }
    }



    // ==================== 委外加工单审核接口 ====================

    /**
     * 查询待审核委外加工单列表（委外审核人权限）
     */
    @ApiOperation("查询待审核委外加工单列表")
    @PreAuthorize("@ss.hasPermi('system:audit:outsourcing:list')")
    @GetMapping("/outsourcing/pending")
    public TableDataInfo getOutsourcingPendingList()
    {
        startPage();
        List<ErpAuditRecord> list = erpAuditRecordService.selectAuditRecords(null, AuditTypeEnum.OUTSOURCING_AUDIT.getCode(), null, true, null);
        return getDataTable(list);
    }

    /**
     * 委外加工单审核（委外审核人权限）
     * @param id 审核记录ID
     * @param auditRequest 审核请求，包含confirm字段（1=通过，-1=拒绝）和审核意见
     */
    @ApiOperation("委外加工单审核")
    @PreAuthorize("@ss.hasPermi('system:audit:outsourcing:audit')")
    @Log(title = "委外加工单审核", businessType = BusinessType.UPDATE)
    @PostMapping("/outsourcing/audit/{id}")
    public AjaxResult auditOutsourcing(@ApiParam("审核记录ID") @PathVariable Long id, 
                                      @Validated @RequestBody AuditRequest auditRequest)
    {
        if (!auditRequest.getConfirm().equals(1) && !auditRequest.getConfirm().equals(-1)) {
            return error("审核状态参数错误，1=通过，-1=拒绝");
        }
        
        String auditor = SecurityUtils.getUsername();
        
        // 验证是否为委外审核记录
        ErpAuditRecord queryRecord = new ErpAuditRecord();
        queryRecord.setId(id);
        List<ErpAuditRecord> records = erpAuditRecordService.selectAuditRecords(queryRecord, null, null, null, null);
        if (records.isEmpty()) {
            return error("审核记录不存在");
        }
        
        ErpAuditRecord auditRecord = records.get(0);
        if (!AuditTypeEnum.OUTSOURCING_AUDIT.getCode().equals(auditRecord.getAuditType())) {
            return error("该记录不是委外加工单审核记录");
        }
        
        // 处理审核
        int result = erpAuditRecordService.processAudit(Arrays.asList(id), auditRequest.getConfirm(), auditor, auditRequest.getAuditComment());
        if (result > 0) {
            String action = auditRequest.getConfirm().equals(1) ? "通过" : "拒绝";
            return success("委外加工单审核" + action + "成功");
        } else {
            return error("审核处理失败");
        }
    }



    // ==================== 布产审核接口 ====================

    /**
     * 查询待审核布产列表（布产审核人权限）
     */
    @ApiOperation("查询待审核布产列表")
    @PreAuthorize("@ss.hasPermi('system:audit:production:list')")
    @GetMapping("/production/pending")
    public TableDataInfo getProductionPendingList()
    {
        startPage();
        List<ErpAuditRecord> list = erpAuditRecordService.selectAuditRecords(null, AuditTypeEnum.PRODUCTION_AUDIT.getCode(), null, true, null);
        return getDataTable(list);
    }

    /**
     * 布产审核（布产审核人权限）
     * @param id 审核记录ID
     * @param auditRequest 审核请求，包含confirm字段（1=通过，-1=拒绝）和审核意见
     */
    @ApiOperation("布产审核")
    @PreAuthorize("@ss.hasPermi('system:audit:production:audit')")
    @Log(title = "布产审核", businessType = BusinessType.UPDATE)
    @PostMapping("/production/audit/{id}")
    public AjaxResult auditProduction(@ApiParam("审核记录ID") @PathVariable Long id, 
                                     @Validated @RequestBody AuditRequest auditRequest)
    {
        if (!auditRequest.getConfirm().equals(1) && !auditRequest.getConfirm().equals(-1)) {
            return error("审核状态参数错误，1=通过，-1=拒绝");
        }
        
        String auditor = SecurityUtils.getUsername();
        
        // 验证是否为布产审核记录
        ErpAuditRecord queryRecord = new ErpAuditRecord();
        queryRecord.setId(id);
        List<ErpAuditRecord> records = erpAuditRecordService.selectAuditRecords(queryRecord, null, null, null, null);
        if (records.isEmpty()) {
            return error("审核记录不存在");
        }
        
        ErpAuditRecord auditRecord = records.get(0);
        if (!AuditTypeEnum.PRODUCTION_AUDIT.getCode().equals(auditRecord.getAuditType())) {
            return error("该记录不是布产审核记录");
        }
        
        // 处理审核
        int result = erpAuditRecordService.processAudit(Arrays.asList(id), auditRequest.getConfirm(), auditor, auditRequest.getAuditComment());
        if (result > 0) {
            String action = auditRequest.getConfirm().equals(1) ? "通过" : "拒绝";
            return success("布产审核" + action + "成功");
        } else {
            return error("审核处理失败");
        }
    }



    // ==================== 分包审核接口 ====================

    /**
     * 查询待审核分包列表（分包审核人权限）
     */
    @ApiOperation("查询待审核分包列表")
    @PreAuthorize("@ss.hasPermi('system:audit:subcontract:list')")
    @GetMapping("/subcontract/pending")
    public TableDataInfo getSubcontractPendingList()
    {
        startPage();
        List<ErpAuditRecord> list = erpAuditRecordService.selectAuditRecords(null, AuditTypeEnum.SUBCONTRACT_AUDIT.getCode(), null, true, null);
        return getDataTable(list);
    }

    /**
     * 分包审核（分包审核人权限）
     * @param id 审核记录ID
     * @param auditRequest 审核请求，包含confirm字段（1=通过，-1=拒绝）和审核意见
     */
    @ApiOperation("分包审核")
    @PreAuthorize("@ss.hasPermi('system:audit:subcontract:audit')")
    @Log(title = "分包审核", businessType = BusinessType.UPDATE)
    @PostMapping("/subcontract/audit/{id}")
    public AjaxResult auditSubcontract(@ApiParam("审核记录ID") @PathVariable Long id, 
                                      @Validated @RequestBody AuditRequest auditRequest)
    {
        if (!auditRequest.getConfirm().equals(1) && !auditRequest.getConfirm().equals(-1)) {
            return error("审核状态参数错误，1=通过，-1=拒绝");
        }
        
        String auditor = SecurityUtils.getUsername();
        
        // 验证是否为分包审核记录
        ErpAuditRecord queryRecord = new ErpAuditRecord();
        queryRecord.setId(id);
        List<ErpAuditRecord> records = erpAuditRecordService.selectAuditRecords(queryRecord, null, null, null, null);
        if (records.isEmpty()) {
            return error("审核记录不存在");
        }
        
        ErpAuditRecord auditRecord = records.get(0);
        if (!AuditTypeEnum.SUBCONTRACT_AUDIT.getCode().equals(auditRecord.getAuditType())) {
            return error("该记录不是分包审核记录");
        }
        
        // 处理审核
        int result = erpAuditRecordService.processAudit(Arrays.asList(id), auditRequest.getConfirm(), auditor, auditRequest.getAuditComment());
        if (result > 0) {
            String action = auditRequest.getConfirm().equals(1) ? "通过" : "拒绝";
            return success("分包审核" + action + "成功");
        } else {
            return error("审核处理失败");
        }
    }


}