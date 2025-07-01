package com.ruoyi.common.core.controller;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ruoyi.common.annotation.Example;
import com.ruoyi.common.exception.excel.ExcelRowErrorInfo;
import com.ruoyi.common.validation.Save;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.excel.ExcelImportException;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.sql.SqlUtil;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

/**
 * web层通用数据处理
 * 
 * @author ruoyi
 */
public class BaseController
{
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Validator validator;

    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder)
    {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport()
        {
            @Override
            public void setAsText(String text)
            {
                setValue(DateUtils.parseDate(text));
            }
        });
    }

    /**
     * 设置请求分页数据
     */
    protected void startPage()
    {
        PageUtils.startPage();
    }

    /**
     * 设置请求排序数据
     */
    protected void startOrderBy()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        if (StringUtils.isNotEmpty(pageDomain.getOrderBy()))
        {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            PageHelper.orderBy(orderBy);
        }
    }

    /**
     * 清理分页的线程变量
     */
    protected void clearPage()
    {
        PageUtils.clearPage();
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected TableDataInfo getDataTable(List<?> list)
    {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal(new PageInfo(list).getTotal());
        return rspData;
    }

    /**
     * 返回成功
     */
    public AjaxResult success()
    {
        return AjaxResult.success();
    }

    /**
     * 返回失败消息
     */
    public AjaxResult error()
    {
        return AjaxResult.error();
    }

    /**
     * 返回成功消息
     */
    public AjaxResult success(String message)
    {
        return AjaxResult.success(message);
    }
    
    /**
     * 返回成功消息
     */
    public AjaxResult success(Object data)
    {
        return AjaxResult.success(data);
    }

    /**
     * 返回失败消息
     */
    public AjaxResult error(String message)
    {
        return AjaxResult.error(message);
    }

    /**
     * 返回警告消息
     */
    public AjaxResult warn(String message)
    {
        return AjaxResult.warn(message);
    }

    /**
     * 响应返回结果
     * 
     * @param rows 影响行数
     * @return 操作结果
     */
    protected AjaxResult toAjax(int rows)
    {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 响应返回结果
     * 
     * @param result 结果
     * @return 操作结果
     */
    protected AjaxResult toAjax(boolean result)
    {
        return result ? success() : error();
    }

    /**
     * 页面跳转
     */
    public String redirect(String url)
    {
        return StringUtils.format("redirect:{}", url);
    }

    /**
     * 获取用户缓存信息
     */
    public LoginUser getLoginUser()
    {
        return SecurityUtils.getLoginUser();
    }

    /**
     * 获取登录用户id
     */
    public Long getUserId()
    {
        return getLoginUser().getUserId();
    }

    /**
     * 获取登录部门id
     */
    public Long getDeptId()
    {
        return getLoginUser().getDeptId();
    }

    /**
     * 获取登录用户名
     */
    public String getUsername()
    {
        return getLoginUser().getUsername();
    }

    public static <Type> Type requiresNotNull(Type value) {
        if (value == null) {
            throw new ServiceException("结果不存在", 400);
        }
        return value;
    }

    public <Type> List<Type> validateExcel(MultipartFile file, Class<Type> clazz) throws IOException {
        ExcelUtil<Type> util = new ExcelUtil<>(clazz);
        List<Type> list = util.importExcel(file.getInputStream());
        List<ExcelRowErrorInfo> errorList = validateList(list, validator);
        return list;
    }

    public static <Type> List<ExcelRowErrorInfo> validateList(List<Type> list, Validator validator) {
        List<ExcelRowErrorInfo> errorList = new ArrayList<>();
        int rowIndex = 1;
        for (Type row : list) {
            Set<ConstraintViolation<Type>> constraints = validator.validate(row, Save.class);
            if (!constraints.isEmpty()) {
                ExcelRowErrorInfo errorInfo = new ExcelRowErrorInfo();
                errorInfo.setRowNum(rowIndex);
                errorInfo.setErrorMsg(
                        constraints.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("\n"))
                );
                errorList.add(errorInfo);
            }
            rowIndex++;
        }
        return errorList;
    }

    public static <Type> ExcelUtil<Type> createTemplateExcelUtil(Class<Type> clazz) {
        ExcelUtil<Type> util = new ExcelUtil<>(clazz);
        util.showColumn(
                Stream.concat(Arrays.stream(clazz.getDeclaredFields()),
                                Arrays.stream(clazz.getSuperclass().getDeclaredFields()))
                        .filter(field -> field.isAnnotationPresent(Example.class))
                        .map(Field::getName)
                        .toArray(String[]::new)
        );
        return util;
    }
}
