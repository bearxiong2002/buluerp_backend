package com.ruoyi.common.core.domain;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ruoyi.common.annotation.Example;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.exception.excel.UnsupportedExampleTypeException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.reflect.ReflectUtils;

/**
 * Entity基类
 * 
 * @author ruoyi
 */
public class BaseEntity implements Serializable
{
    private static final long serialVersionUID = 1L;

    public static final String DATE_SQL_CONDITION = "DATE(%s) = DATE(#{%s})";

    /** 搜索值 */
    @JsonIgnore
    private String searchValue;

    /** 分页页数参数 */
    private Integer pageNum;

    /** 分页每页条数参数 */
    private Integer pageSize;

    /** 排序列 */
    private String orderByColumn;

    /** 排序的方向  */
    private String isAsc;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /** 备注 */
    private String remark;

    /** 请求参数 */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;

    public String getSearchValue()
    {
        return searchValue;
    }

    public void setSearchValue(String searchValue)
    {
        this.searchValue = searchValue;
    }

    public String getCreateBy()
    {
        return createBy;
    }

    public void setCreateBy(String createBy)
    {
        this.createBy = createBy;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public String getUpdateBy()
    {
        return updateBy;
    }

    public void setUpdateBy(String updateBy)
    {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public Map<String, Object> getParams()
    {
        if (params == null)
        {
            params = new HashMap<>();
        }
        return params;
    }

    public void setParams(Map<String, Object> params)
    {
        this.params = params;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public String getOrderByColumn() {
        return orderByColumn;
    }

    public void setOrderByColumn(String orderByColumn) {
        this.orderByColumn = orderByColumn;
    }

    public String getIsAsc() {
        return isAsc;
    }

    public void setIsAsc(String isAsc) {
        this.isAsc = isAsc;
    }

    public static <T> T createExample(Class<T> clazz) throws InstantiationException, IllegalAccessException {
        T example = clazz.newInstance();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Example exampleAnnotation = field.getAnnotation(Example.class);
            if (exampleAnnotation != null) {
                Class<?> type = field.getType();
                String value = exampleAnnotation.value();
                if (value.equals(Example.GEN_UUID)) {
                    if (String.class == type) {
                        ReflectUtils.invokeSetter(example, field.getName(), UUID.randomUUID().toString());
                    } else if (UUID.class == type) {
                        ReflectUtils.invokeSetter(example, field.getName(), UUID.randomUUID());
                    }
                } else if (String.class == type) {
                    ReflectUtils.invokeSetter(example, field.getName(), value);
                } else if (Integer.class == type) {
                    ReflectUtils.invokeSetter(example, field.getName(), Convert.toInt(value));
                } else if (Long.class == type) {
                    ReflectUtils.invokeSetter(example, field.getName(), Convert.toLong(value));
                } else if (Double.class == type) {
                    ReflectUtils.invokeSetter(example, field.getName(), Convert.toDouble(value));
                } else if (Float.class == type) {
                    ReflectUtils.invokeSetter(example, field.getName(), Convert.toFloat(value));
                } else if (BigDecimal.class == type) {
                    ReflectUtils.invokeSetter(example, field.getName(), Convert.toBigDecimal(value));
                } else if (Date.class == type) {
                    ReflectUtils.invokeSetter(example, field.getName(), DateUtils.parseDate(value));
                } else if (Boolean.class == type) {
                    ReflectUtils.invokeSetter(example, field.getName(), Convert.toBool(value));
                } else {
                    throw new UnsupportedExampleTypeException("不支持的示例值类型");
                }
            }
        }
        return example;
    }
}
