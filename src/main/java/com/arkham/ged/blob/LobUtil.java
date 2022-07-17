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
package com.arkham.ged.blob;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.arkham.common.util.HashCoder;

/**
 * The purpose of this class is to provide convenients static methods for LOBs usages.
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public final class LobUtil {
	private static final Map<HashCoder, Integer> mCodsocCache = new ConcurrentHashMap<>();
	private static final Lock mLock = new ReentrantLock();

	private LobUtil() {
		// Private because it's an utility class, so we should't get an instance of this class
	}

	/**
	 * Get the "codsoc_phy" from given codsoc/codent/segment
	 *
	 * @param con The database connection. Cannot be null
	 * @param codsoc The logical entity
	 * @param codent The CODENT field
	 * @param segment The SEGMENT field
	 * @return The real (CODSOC_PHY) codsoc or <code>-1</code> if any unexpected error occurs or if the MEV row cannot be found
	 * @throws DocumentLinkException if an exception is raised while querying MEV table
	 */
	public static int getCodsocPhy(Connection con, int codsoc, String codent, String segment) throws DocumentLinkException {
		if (codsoc < 0) {
			return -1;
		}

		try {
			mLock.lock();

			final HashCoder hc = new HashCoder(codsoc, codent, segment);
			final Integer res = mCodsocCache.get(hc);
			if (res != null) {
				return res.intValue();
			}

			try (PreparedStatement psMev = con.prepareStatement("SELECT CODSOC_PHY FROM MEV WHERE CODSOC = ? AND CODENT = ? AND SEGMENT = ?")) {
				psMev.setInt(1, codsoc);
				psMev.setString(2, codent);
				psMev.setString(3, segment);

				try (ResultSet rs = psMev.executeQuery()) {
					if (rs.next()) {
						final int codsocphy = rs.getInt(1);
						mCodsocCache.put(hc, Integer.valueOf(codsocphy));

						return codsocphy;
					}
				}
			}
		} catch (final SQLException e) {
			throw new DocumentLinkException(e);
		} finally {
			mLock.unlock();
		}

		// If the MEV for MEDIA_BLOB/UNK cannot be found, it's OK so the codsoc_phy is the codsoc
		if ("UNK".equals(segment)) {
			return codsoc;
		}

		return -1;
	}

	/**
	 * Get the "codsoc_phy", it depends on codsoc/typtie/keydoc
	 *
	 * @param con The database connection. Cannot be null
	 * @param bean The {@link DocumentLinkBean}
	 * @return The real (CODSOC_PHY) codsoc or <code>-1</code> if any unexpected error occurs or if the MEV row cannot be found
	 * @throws DocumentLinkException if an exception is raised while querying MEV table
	 * @see #getCodsocPhy(Connection, int, String, String)
	 */
	public static int getCodsocPhy(Connection con, DocumentLinkBean bean) throws DocumentLinkException {
		return getCodsocPhy(con, bean.getEntity(), IDocumentLinkConstants.TABLE_NAME, getSegment(bean.getEnttyp(), bean.getKeydoc()));
	}

	/**
	 * Update the field "codsoc_phy" in the bean
	 *
	 * @param con The database connection. Cannot be null
	 * @param bean The {@link DocumentLinkBean}
	 * @throws DocumentLinkException if an exception is raised while querying MEV table
	 * @see #getCodsocPhy(Connection, int, String, String)
	 */
	static void adaptCodsocPhy(Connection con, DocumentLinkBean bean) throws DocumentLinkException {
		if (bean.getCodsocPhy() == -1) {
			final int codsocPhy = getCodsocPhy(con, bean);

			bean.setCodsocPhy(codsocPhy);
		}
	}

	/**
	 * Compute the segment of MEDIA_BLOB for MEV
	 *
	 * @param typtie The typtie, should not be <code>null</code>
	 * @param nomcle The nomcle should not be <code>null</code>
	 * @return The segment that is a composite if typtie="TIE"
	 */
	static String getSegment(String typtie, String nomcle) {
		if ("TIE".equals(typtie)) {
			return typtie + nomcle.substring(0, 3);
		}

		return typtie;
	}

	/**
	 * Rollback in silence
	 *
	 * @param con The connection
	 */
	public static void rollbackSilently(Connection con) {
		if (con != null) {
			try {
				con.rollback();
			} catch (final SQLException e) { // NOSONAR
				// Not a problem in this case
			}
		}
	}
}
