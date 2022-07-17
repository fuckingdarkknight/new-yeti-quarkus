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
package com.arkham.ged.filekey;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkham.ged.message.GedMessages;
import com.arkham.ged.properties.OptionalParameterType;
import com.arkham.ged.properties.PropertiesAdapter;
import com.arkham.ged.util.GedUtil;

/**
 * Decode filenames SOCx_y.*
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 3 août 2018
 */
public class SpoolFileKeyProvider extends FileKeyProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(SpoolFileKeyProvider.class);

	/**
	 * Define if user should be used as target or entity. NOMCLE=UTIxxx or NOMCLE=SOCxxx
	 */
	private static final String SOC_QUERY = "select sigtie from v_infsoc where codsoc = ?";

	private static final String SPL_QUERY = "select tfc, uti from ut_spl where codsoc = ? and numero = ?";

	@Override
	public FileKey getKey(File file, Connection con, PropertiesAdapter pa, List<OptionalParameterType> opt) throws FileKeyProviderException {
		String filename = file.getName();
		if (filename.endsWith(PROCEXT)) {
			filename = GedUtil.removeFileExtension(filename);
		}

		final int dotPos = filename.lastIndexOf('.');
		if (dotPos != -1 && filename.length() > 3) {
			try {
				final String prefix = filename.substring(0, 3);
				final boolean userMode = "UTI".equalsIgnoreCase(prefix);

				// Remove prefix SOC and extension
				final String fn = filename.substring(3, dotPos);
				final String[] x = fn.split("_");
				final int codsoc = Integer.parseInt(x[0]);
				final int numedi = Integer.parseInt(x[1]);

				final FileKey fk = new FileKey(codsoc, "xxx", "xxx", filename);
				fk.setAttribute("numedi", numedi);

				adapt(con, fk, codsoc, numedi, userMode);

				return fk;
			} catch (final SQLException | NumberFormatException e) {
				LOGGER.info("getKey() : {} cannot be parsed", filename);

				throw new FileKeyProviderException(e, GedMessages.Scanner.decodingError);
			}
		}

		return null;
	}

	@Override
	public boolean isRefFile() {
		return false;
	}

	private static void adapt(Connection con, FileKey fk, int codsoc, int numedi, boolean userMode) throws SQLException {
		try (PreparedStatement ps = con.prepareStatement(SOC_QUERY)) {
			ps.setInt(1, codsoc);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					// Private publication
					fk.setAttribute(FileKey.TYPTIE, "TIE");
					fk.setAttribute(FileKey.NOMCLE, "SOC" + rs.getString(1));
					fk.setAttribute(FileKey.INDPUB, "1");
					fk.setAttribute(FileKey.TYPDOC, "PUB");
				} else {
					LOGGER.error("adapt() : row not found for codsoc={} query={}", codsoc, SOC_QUERY);
				}
			}
		}

		try (PreparedStatement ps = con.prepareStatement(SPL_QUERY)) {
			ps.setInt(1, codsoc);
			ps.setInt(2, numedi);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					// Private publication
					fk.setAttribute(FileKey.LIB256, rs.getString(1));
					fk.setAttribute(FileKey.UTIMOD, rs.getString(2));
					if (userMode) {
						fk.setAttribute(FileKey.NOMCLE, "UTI" + rs.getString(2));
						fk.setAttribute(FileKey.INDPUB, "0");
						fk.setAttribute(FileKey.TYPDOC, " ");
					}
				} else {
					LOGGER.error("adapt() : row not found for codsoc/numero={}/{} query={}", codsoc, numedi, SPL_QUERY);
				}
			}
		}
	}
}
