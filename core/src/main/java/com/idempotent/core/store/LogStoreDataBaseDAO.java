
/*
 * File Name:com.sdd.asker.utils.idempotent.LogStoreDataBaseDAO is created on 2023/3/305:10 下午 by liuzongliang
 *
 * Copyright (c) 2023, shengdiudiu technology All Rights Reserved.
 *
 */
package com.idempotent.core.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.idempotent.core.util.IOUtil;
import com.idempotent.core.util.KeyGenerator;

import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @author liuzongliang
 * @Description: 幂等数据DAO
 * @date: 2023/3/30 5:10 下午
 * @since JDK 1.8
 */
@Component
public class LogStoreDataBaseDAO implements LogStore {

    private static final String INSERT_SQL = "insert into idempotent_log"
        + "(`transaction_signature`, `idempotent_param_list`, `idempotent_value_hash`, `idempotent_value`,"
        + " `full_param_value`,`return_value`, `gmt_create`, `status`)"
        + " values (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String QUERY_SQL = "select * from idempotent_log where transaction_signature = ? and "
        + "idempotent_value_hash = ? and idempotent_value = ? and `status` = ?";

    @Resource
    private DataSourceTransactionManager dataSourceTransactionManager;

    @Override
    public int insert(IdempotentLogDO idempotentLogDO) {
        final DataSource dataSource = dataSourceTransactionManager.getDataSource();
        ConnectionHolder conHolder = (ConnectionHolder)TransactionSynchronizationManager.getResource(dataSource);
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            int index = 1;
            conn = conHolder.getConnection();
            ps = conn.prepareStatement(INSERT_SQL);
            ps.setString(index++, idempotentLogDO.getTransactionSignature());
            ps.setString(index++, idempotentLogDO.getIdempotentParamList());
            ps.setString(index++, idempotentLogDO.getIdempotentValueHash());
            ps.setString(index++, idempotentLogDO.getIdempotentValue());

            ps.setBytes(index++, idempotentLogDO.getFullParamValue());
            ps.setBytes(index++, idempotentLogDO.getReturnValue());
            ps.setLong(index++, idempotentLogDO.getGmtCreate());
            ps.setInt(index++, idempotentLogDO.getStatus());

            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("LogStoreDataBaseDAO#insert args" + idempotentLogDO.toString(), e);
        }finally {
            IOUtil.close(rs, ps);
        }
    }

    @Override
    public List<IdempotentLogDO> queryByParam(String transactionSignature, String hash, String idempotentArgs, Integer status) {
        final DataSource dataSource = dataSourceTransactionManager.getDataSource();
        ConnectionHolder conHolder = (ConnectionHolder)TransactionSynchronizationManager.getResource(dataSource);
        List<IdempotentLogDO> ret = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = conHolder.getConnection();
            ps = conn.prepareStatement(QUERY_SQL);
            ps.setString(1, transactionSignature);
            ps.setString(2, hash);
            ps.setString(3, idempotentArgs);
            ps.setInt(4, status);
            rs = ps.executeQuery();
            while (rs.next()) {
                ret.add(convertIdempotentLogDO(rs));
            }
            return ret;
        } catch (SQLException e) {
            String args = "";
            try {
                args = KeyGenerator
                    .simpleJoinToBuilder(new String[] {"transactionSignature", "hash", "idempotentArgs", "status"},
                        new Object[] {transactionSignature, hash, idempotentArgs, status}, "=", ",").toString();
            } catch (JsonProcessingException jsonProcessingException) {
                args = "transactionSignature=" + transactionSignature + ",hash=" + hash + ",idempotentArgs"
                    + idempotentArgs;
            }
            throw new RuntimeException("LogStoreDataBaseDAO#queryByParam args" + args, e);
        } finally {
            IOUtil.close(rs, ps);
        }
    }

    /**
     * 转换成幂等DO
     * @param rs
     * @return
     * @throws SQLException
     */
    private IdempotentLogDO convertIdempotentLogDO(ResultSet rs) throws SQLException{
        IdempotentLogDO idempotentLogDO = new IdempotentLogDO();
        idempotentLogDO.setId(rs.getLong("id"));
        idempotentLogDO.setTransactionSignature(rs.getString("transaction_signature"));
        idempotentLogDO.setIdempotentParamList(rs.getString("idempotent_param_list"));
        idempotentLogDO.setIdempotentValueHash(rs.getString("idempotent_value_hash"));
        idempotentLogDO.setIdempotentValue(rs.getString("idempotent_value"));
        idempotentLogDO.setFullParamValue(rs.getBytes("full_param_value"));
        idempotentLogDO.setReturnValue(rs.getBytes("return_value"));
        idempotentLogDO.setGmtCreate(rs.getLong("gmt_create"));
        idempotentLogDO.setStatus(rs.getInt("status"));
        return idempotentLogDO;
    }
}