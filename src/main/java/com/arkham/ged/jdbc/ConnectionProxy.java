/*
 * Licensed to the Arkham asylum Software Foundation under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.arkham.ged.jdbc;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import com.arkham.common.jdbc.DatabaseConnectionManagerException;

/**
 * Proxy on JDBC Connection
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 27 janv. 2020
 */
public class ConnectionProxy implements Connection {
    private final DatabaseConnectionManagerProxy mDcm;
    private Connection mConnection;

    /**
     * Constructor GedConnection
     *
     * @param dcm DCM
     */
    public ConnectionProxy(final DatabaseConnectionManagerProxy dcm) {
        mDcm = dcm;
    }

    private void initInner() throws SQLException {
        if (mConnection == null) {
            try {
                mConnection = mDcm.getInnerConnection();
            } catch (final DatabaseConnectionManagerException e) {
                throw new SQLException(e);
            }
        }
    }

    private Connection getInner() throws SQLException {
        initInner();

        return mConnection;
    }

    Connection getInnerConnection() {
        return mConnection;
    }

    boolean isInnerConnected() {
        return mConnection != null;
    }

    @SuppressWarnings("resource")
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        return getInner().unwrap(iface);
    }

    @SuppressWarnings("resource")
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return getInner().isWrapperFor(iface);
    }

    @SuppressWarnings("resource")
    @Override
    public Statement createStatement() throws SQLException {
        return getInner().createStatement();
    }

    @SuppressWarnings("resource")
    @Override
    public PreparedStatement prepareStatement(final String sql) throws SQLException {
        return getInner().prepareStatement(sql);
    }

    @SuppressWarnings("resource")
    @Override
    public CallableStatement prepareCall(final String sql) throws SQLException {
        return getInner().prepareCall(sql);
    }

    @SuppressWarnings("resource")
    @Override
    public String nativeSQL(final String sql) throws SQLException {
        return getInner().nativeSQL(sql);
    }

    @SuppressWarnings("resource")
    @Override
    public void setAutoCommit(final boolean autoCommit) throws SQLException {
        getInner().setAutoCommit(autoCommit);
    }

    @SuppressWarnings("resource")
    @Override
    public boolean getAutoCommit() throws SQLException {
        return getInner().getAutoCommit();
    }

    @Override
    public void commit() throws SQLException {
        if (mConnection != null) {
            mConnection.commit();
        }
    }

    @Override
    public void rollback() throws SQLException {
        if (mConnection != null) {
            mConnection.rollback();
        }
    }

    @Override
    public void close() throws SQLException {
        if (mConnection != null) {
            mConnection.close();
        }
    }

    @SuppressWarnings("resource")
    @Override
    public boolean isClosed() throws SQLException {
        return mConnection == null || getInner().isClosed();
    }

    @SuppressWarnings("resource")
    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return getInner().getMetaData();
    }

    @SuppressWarnings("resource")
    @Override
    public void setReadOnly(final boolean readOnly) throws SQLException {
        getInner().setReadOnly(readOnly);
    }

    @SuppressWarnings("resource")
    @Override
    public boolean isReadOnly() throws SQLException {
        return getInner().isReadOnly();
    }

    @SuppressWarnings("resource")
    @Override
    public void setCatalog(final String catalog) throws SQLException {
        getInner().setCatalog(catalog);
    }

    @SuppressWarnings("resource")
    @Override
    public String getCatalog() throws SQLException {
        return getInner().getCatalog();
    }

    @SuppressWarnings("resource")
    @Override
    public void setTransactionIsolation(final int level) throws SQLException {
        getInner().setTransactionIsolation(level);
    }

    @SuppressWarnings("resource")
    @Override
    public int getTransactionIsolation() throws SQLException {
        return getInner().getTransactionIsolation();
    }

    @SuppressWarnings("resource")
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return getInner().getWarnings();
    }

    @SuppressWarnings("resource")
    @Override
    public void clearWarnings() throws SQLException {
        getInner().clearWarnings();
    }

    @SuppressWarnings("resource")
    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLException {
        return getInner().createStatement(resultSetType, resultSetConcurrency);
    }

    @SuppressWarnings("resource")
    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        return getInner().prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @SuppressWarnings("resource")
    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        return getInner().prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @SuppressWarnings("resource")
    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return getInner().getTypeMap();
    }

    @SuppressWarnings("resource")
    @Override
    public void setTypeMap(final Map<String, Class<?>> map) throws SQLException {
        getInner().setTypeMap(map);
    }

    @SuppressWarnings("resource")
    @Override
    public void setHoldability(final int holdability) throws SQLException {
        getInner().setHoldability(holdability);
    }

    @SuppressWarnings("resource")
    @Override
    public int getHoldability() throws SQLException {
        return getInner().getHoldability();
    }

    @SuppressWarnings("resource")
    @Override
    public Savepoint setSavepoint() throws SQLException {
        return getInner().setSavepoint();
    }

    @SuppressWarnings("resource")
    @Override
    public Savepoint setSavepoint(final String name) throws SQLException {
        return getInner().setSavepoint(name);
    }

    @SuppressWarnings("resource")
    @Override
    public void rollback(final Savepoint savepoint) throws SQLException {
        getInner().rollback(savepoint);
    }

    @SuppressWarnings("resource")
    @Override
    public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
        getInner().releaseSavepoint(savepoint);
    }

    @SuppressWarnings("resource")
    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        return getInner().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @SuppressWarnings("resource")
    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        return getInner().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @SuppressWarnings("resource")
    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        return getInner().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @SuppressWarnings("resource")
    @Override
    public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {
        return getInner().prepareStatement(sql, autoGeneratedKeys);
    }

    @SuppressWarnings("resource")
    @Override
    public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) throws SQLException {
        return getInner().prepareStatement(sql, columnIndexes);
    }

    @SuppressWarnings("resource")
    @Override
    public PreparedStatement prepareStatement(final String sql, final String[] columnNames) throws SQLException {
        return getInner().prepareStatement(sql, columnNames);
    }

    @SuppressWarnings("resource")
    @Override
    public Clob createClob() throws SQLException {
        return getInner().createClob();
    }

    @SuppressWarnings("resource")
    @Override
    public Blob createBlob() throws SQLException {
        return getInner().createBlob();
    }

    @SuppressWarnings("resource")
    @Override
    public NClob createNClob() throws SQLException {
        return getInner().createNClob();
    }

    @SuppressWarnings("resource")
    @Override
    public SQLXML createSQLXML() throws SQLException {
        return getInner().createSQLXML();
    }

    @SuppressWarnings("resource")
    @Override
    public boolean isValid(final int timeout) throws SQLException {
        return getInner().isValid(timeout);
    }

    @SuppressWarnings("resource")
    @Override
    public void setClientInfo(final String name, final String value) throws SQLClientInfoException {
        try {
            getInner().setClientInfo(name, value);
        } catch (@SuppressWarnings("unused") final SQLException e) {
            throw new SQLClientInfoException();
        }
    }

    @SuppressWarnings("resource")
    @Override
    public void setClientInfo(final Properties properties) throws SQLClientInfoException {
        try {
            getInner().setClientInfo(properties);
        } catch (@SuppressWarnings("unused") final SQLException e) {
            throw new SQLClientInfoException();
        }
    }

    @SuppressWarnings("resource")
    @Override
    public String getClientInfo(final String name) throws SQLException {
        return getInner().getClientInfo(name);
    }

    @SuppressWarnings("resource")
    @Override
    public Properties getClientInfo() throws SQLException {
        return getInner().getClientInfo();
    }

    @SuppressWarnings("resource")
    @Override
    public Array createArrayOf(final String typeName, final Object[] elements) throws SQLException {
        return getInner().createArrayOf(typeName, elements);
    }

    @SuppressWarnings("resource")
    @Override
    public Struct createStruct(final String typeName, final Object[] attributes) throws SQLException {
        return getInner().createStruct(typeName, attributes);
    }

    @SuppressWarnings("resource")
    @Override
    public void setSchema(final String schema) throws SQLException {
        getInner().setSchema(schema);
    }

    @SuppressWarnings("resource")
    @Override
    public String getSchema() throws SQLException {
        return getInner().getSchema();
    }

    @SuppressWarnings("resource")
    @Override
    public void abort(final Executor executor) throws SQLException {
        getInner().abort(executor);
    }

    @SuppressWarnings("resource")
    @Override
    public void setNetworkTimeout(final Executor executor, final int milliseconds) throws SQLException {
        getInner().setNetworkTimeout(executor, milliseconds);
    }

    @SuppressWarnings("resource")
    @Override
    public int getNetworkTimeout() throws SQLException {
        return getInner().getNetworkTimeout();
    }
}
