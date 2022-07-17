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
package com.arkham.ged.util;

import com.arkham.ged.blob.DocumentLinkBean;

/**
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 31 mars 2016
 */
public final class GedEveUtil {
	private GedEveUtil() {
		// Private because it's an utility class, so we should't get an instance of this class
	}

	/**
	 * Convenient class used to convert EVE primary key into NOMCLE and CLEZOD
	 *
	 * @author arocher / Arkham asylum
	 * @version 1.0
	 * @since 7 avr. 2016
	 */
	public static class EvePkBean extends DocumentLinkBean {
		/**
		 * Constructor EvePkBean
		 *
		 * @param codsoc Codsoc
		 * @param nomcle should be formated as ACHVTE + TYPEVE + NUMEVE
		 */
		public EvePkBean(int codsoc, String nomcle) {
			setEntity(codsoc); // NOSONAR
			setKeydoc(nomcle); // NOSONAR
			setEnttyp("EVE"); // NOSONAR

			updateEventKey(); // NOSONAR
		}

		/**
		 * @return The NOMCLE format (9 digits)
		 */
		public String getFormattedNomcle() {
			return String.format("%s%s%09d", getAchvte(), getTypeve(), getNumeve());
		}

		/**
		 * @return The CLEZOD format (7 digits)
		 */
		public String getFormattedClezod() {
			return String.format("%s%s%07d", getAchvte(), getTypeve(), getNumeve());
		}
	}
}
