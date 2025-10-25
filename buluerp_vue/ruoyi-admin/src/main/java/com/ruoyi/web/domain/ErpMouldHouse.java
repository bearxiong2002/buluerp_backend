package com.ruoyi.web.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ruoyi.common.annotation.Example;
import com.ruoyi.common.annotation.Excel;

public class ErpMouldHouse {
    @TableId(type = IdType.AUTO)
    @Excel(name = "ID")
    private Long id;

    @Excel(name = "名称")
    @TableField(condition = SqlCondition.LIKE)
    @Example("名称")
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
