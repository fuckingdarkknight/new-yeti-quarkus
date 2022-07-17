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
	public ConnectionProxy(DatabaseConnectionManagerProxy dcm) {
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

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return getInner().unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return getInner().isWrapperFor(iface);
	}

	@Override
	public Statement createStatement() throws SQLException {
		return getInner().createStatement();
	}

	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return getInner().prepareStatement(sql);
	}

	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		return getInner().prepareCall(sql);
	}

	@Override
	public String nativeSQL(String sql) throws SQLException {
		return getInner().nativeSQL(sql);
	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		getInner().setAutoCommit(autoCommit);
	}

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

	@Override
	public boolean isClosed() throws SQLException {
		return mConnection == null || getInner().isClosed();
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		return getInner().getMetaData();
	}

	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		getInner().setReadOnly(readOnly);
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		return getInner().isReadOnly();
	}

	@Override
	public void setCatalog(String catalog) throws SQLException {
		getInner().setCatalog(catalog);
	}

	@Override
	public String getCatalog() throws SQLException {
		return getInner().getCatalog();
	}

	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		getInner().setTransactionIsolation(level);

	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		return getInner().getTransactionIsolation();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return getInner().getWarnings();
	}

	@Override
	public void clearWarnings() throws SQLException {
		getInner().clearWarnings();
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		return getInner().createStatement(resultSetType, resultSetConcurrency);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return getInner().prepareStatement(sql, resultSetType, resultSetConcurrency);
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return getInner().prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return getInner().getTypeMap();
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		getInner().setTypeMap(map);
	}

	@Override
	public void setHoldability(int holdability) throws SQLException {
		getInner().setHoldability(holdability);
	}

	@Override
	public int getHoldability() throws SQLException {
		return getInner().getHoldability();
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		return getInner().setSavepoint();
	}

	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		return getInner().setSavepoint(name);
	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		getInner().rollback(savepoint);
	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		getInner().releaseSavepoint(savepoint);
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return getInner().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return getInner().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return getInner().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		return getInner().prepareStatement(sql, autoGeneratedKeys);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		return getInner().prepareStatement(sql, columnIndexes);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		return getInner().prepareStatement(sql, columnNames);
	}

	@Override
	public Clob createClob() throws SQLException {
		return getInner().createClob();
	}

	@Override
	public Blob createBlob() throws SQLException {
		return getInner().createBlob();
	}

	@Override
	public NClob createNClob() throws SQLException {
		return getInner().createNClob();
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		return getInner().createSQLXML();
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		return getInner().isValid(timeout);
	}

	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		try {
			getInner().setClientInfo(name, value);
		} catch (final SQLException e) {
			throw new SQLClientInfoException();
		}
	}

	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		try {
			getInner().setClientInfo(properties);
		} catch (final SQLException e) {
			throw new SQLClientInfoException();
		}
	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		return getInner().getClientInfo(name);
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		return getInner().getClientInfo();
	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		return getInner().createArrayOf(typeName, elements);
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		return getInner().createStruct(typeName, attributes);
	}

	@Override
	public void setSchema(String schema) throws SQLException {
		getInner().setSchema(schema);
	}

	@Override
	public String getSchema() throws SQLException {
		return getInner().getSchema();
	}

	@Override
	public void abort(Executor executor) throws SQLException {
		getInner().abort(executor);
	}

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		getInner().setNetworkTimeout(executor, milliseconds);
	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		return getInner().getNetworkTimeout();
	}
}
