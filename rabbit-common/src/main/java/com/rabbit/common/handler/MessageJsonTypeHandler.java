package com.rabbit.common.handler;

import com.rabbit.api.Message;
import com.rabbit.common.utils.FastJsonConvertUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.util.StringUtils;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Evan
 * @create 2021/2/23 15:19
 */
public class MessageJsonTypeHandler extends BaseTypeHandler<Message> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Message message, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, FastJsonConvertUtil.convertObjectToJson(message));
    }

    @Override
    public Message getNullableResult(ResultSet resultSet, String s) throws SQLException {
        String value = resultSet.getString(s);
        if (!StringUtils.isEmpty(value)){
            return FastJsonConvertUtil.convertJsonToObject(value, Message.class);
        }
        return null;
    }

    @Override
    public Message getNullableResult(ResultSet resultSet, int i) throws SQLException {
        String value = resultSet.getString(i);
        if (!StringUtils.isEmpty(value)){
            return FastJsonConvertUtil.convertJsonToObject(value, Message.class);
        }
        return null;
    }

    @Override
    public Message getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        String value = callableStatement.getString(i);
        if (!StringUtils.isEmpty(value)){
            return FastJsonConvertUtil.convertJsonToObject(value, Message.class);
        }
        return null;
    }

}
