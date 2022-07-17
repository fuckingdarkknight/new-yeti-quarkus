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
package com.arkham.ged.rejection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Abstract rejector that publish a convenient method to execute update or insert into database
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 30 janv. 2019
 */
public abstract class AbstractSqlRejector extends AbstractRejector {
	/**
	 * Execute an update query
	 *
	 * @param con Database connection
	 * @param query The query, assume that is not <code>null</code>
	 * @param p Optionnals parameters
	 * @throws SQLException
	 */
	@SuppressWarnings("static-method")
	protected void executeQuery(Connection con, String query, Object... p) throws SQLException {
		try (PreparedStatement ps = con.prepareStatement(query)) {
			int i = 1;
			for (final Object o : p) {
				ps.setObject(i++, o);
			}

			ps.executeUpdate();
		}
	}
}
