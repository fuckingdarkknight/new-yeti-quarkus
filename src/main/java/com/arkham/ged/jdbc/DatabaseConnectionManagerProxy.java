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

import java.sql.Connection;

import com.arkham.common.jdbc.DatabaseConnectionDefinition;
import com.arkham.common.jdbc.DatabaseConnectionManager;
import com.arkham.common.jdbc.DatabaseConnectionManagerException;

/**
 * Proxy on DCM to create proxy on Connection
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 28 janv. 2020
 */
public class DatabaseConnectionManagerProxy extends DatabaseConnectionManager {
	public DatabaseConnectionManagerProxy(final DatabaseConnectionDefinition dcd) throws DatabaseConnectionManagerException {
		super(dcd);
	}

	@Override
	public Connection getConnection() throws DatabaseConnectionManagerException {
		return new ConnectionProxy(this);
	}

	/**
	 * @return The real Connection to database
	 * @throws DatabaseConnectionManagerException
	 */
	Connection getInnerConnection() throws DatabaseConnectionManagerException {
		return super.getConnection();
	}

	@SuppressWarnings("resource")
    @Override
	public void releaseConnection(final Connection con) throws DatabaseConnectionManagerException {
		// Connection have to be released only if it has been created
		if (con instanceof ConnectionProxy) {
			final ConnectionProxy cp = (ConnectionProxy) con;
			if (cp.isInnerConnected()) {
				super.releaseConnection(cp.getInnerConnection());
			}
		}
	}
}
