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

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.arkham.ged.filekey.FileKey;
import com.arkham.ged.properties.PropertiesAdapter;
import com.arkham.ged.util.GedSpoolUtil.SpoolPkBean;

/**
 * Update UT_SPL.ST1=5 in case of rejection
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 30 janv. 2019
 */
public class SpoolStateUpdateRejector extends AbstractSqlRejector {
	private static final String UT_SPL_SELECT = "select nomprc from ut_spl where codsoc = ? and numero = ?";
	private static final String UT_SPL_UPDATE = "update ut_spl set st1 = ? where codsoc = ? and numero = ?";
	// private static final String UT_PRC_UPDATE = "update ut_prc set st1 = ? where codsoc = ? and nomprc = ?";

	@Override
	public void reject(File file, FileKey fk, Throwable t, String message, Connection con, PropertiesAdapter pa) {
		if (file != null) {
			updateUtSpl(con, file);
		}
	}

	private void updateUtSpl(Connection con, File file) {
		final SpoolPkBean b = SpoolPkBean.getBean(file.getName());
		if (b.getCodsoc() != -1 && b.getNumero() != -1) {
			try (PreparedStatement ps = con.prepareStatement(UT_SPL_SELECT)) {
				ps.setInt(1, b.getCodsoc());
				ps.setInt(2, b.getNumero());
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						// final String nomprc = rs.getString(1);
						executeQuery(con, UT_SPL_UPDATE, 5, b.getCodsoc(), b.getNumero());
						// Don't update UT_PRC, else uexp.exe won't release batch file.
						// executeQuery(con, UT_PRC_UPDATE, 5, b.getCodsoc(), nomprc);

						con.commit();
					}
				}
			} catch (final SQLException e) {
				LOGGER.error("updateUtSpl() : {}", e);
			}
		}
	}
}
