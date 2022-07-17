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
package com.arkham.ged.timer;

import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MDC management
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public final class LoggerMDC {
	private static final String ACTIVITY_DETAIL_LOGGER_NAME = "gedActivityDetail";
	private static final Logger LOGGER_ACTIVITY_DETAIL = LoggerFactory.getLogger(ACTIVITY_DETAIL_LOGGER_NAME);

	private static final String ACTIVITY_RUN_LOGGER_NAME = "gedActivityRun";
	private static final Logger LOGGER_ACTIVITY_RUN = LoggerFactory.getLogger(ACTIVITY_RUN_LOGGER_NAME);

	private LoggerMDC() {
		// Private because it's an utility class, so we should't get an instance of this class
	}

	/**
	 * Initialise le MDC pour le thread courant.
	 * <p>
	 * Les clés initialisées sont les suivantes :
	 * </p>
	 * <ul>
	 * <li>MDC_KEY.DATE
	 * <li>MDC_KEY.TIME
	 * </ul>
	 */
	public static void initMDC() {
		purgeMDC(true);
	}

	/**
	 * Remove all entries in MDC for the current Thread
	 *
	 * @param global true for remove all the MDC entries, false for "local" entries only
	 */
	public static void purgeMDC(boolean global) {
		for (final MDC_KEY key : MDC_KEY.values()) {
			if (global || !key.isGlobal()) {
				ThreadContext.remove(key.name());
			}
		}
	}

	/**
	 * Méthode qui permet d'accéder au MDC en se protégeant le cas échéant des "value" qui ont une valeur nulle (ça ne fait rien dans ce cas là).
	 *
	 * @param key clé dans le MDC
	 * @param value valeur à placer dans le MDC
	 */
	public static void putMDC(MDC_KEY key, Object value) {
		if (key != null) {
			if (value != null) {
				ThreadContext.put(key.name(), value.toString());
			} else {
				ThreadContext.put(key.name(), "null");
			}
		}
	}

	public static Logger getActivityDetailLogger() {
		return LOGGER_ACTIVITY_DETAIL;
	}

	public static Logger getActivityRunLogger() {
		return LOGGER_ACTIVITY_RUN;
	}
}
