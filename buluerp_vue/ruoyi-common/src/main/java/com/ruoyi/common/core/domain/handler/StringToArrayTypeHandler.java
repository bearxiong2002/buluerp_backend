package com.ruoyi.common.core.domain.handler;


import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StringToArrayTypeHandler extends BaseTypeHandler<String[]> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String[] parameter, JdbcType jdbcType) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String roleNames = rs.getString(columnName);
        return roleNames != null ? roleNames.split(",") : null;
    }

    @Override
    public String[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String roleNames = rs.getString(columnIndex);
        return roleNames != null ? roleNames.split(",") : null;
    }

    @Override
    public String[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String roleNames = cs.getString(columnIndex);
        return roleNames != null ? roleNames.split(",") : null;
    }
}