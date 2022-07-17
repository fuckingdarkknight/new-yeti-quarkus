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

/**
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 7 avr. 2016
 */
public enum MDC_KEY {
	/* @formatter:off */
	TIMER_NAME,
	DATE,
	TIME,
	ENTITY,
	USER,
	FILENAME,
	FILESIZE,
	INDSEQ,
	AUTHENTICATION,
	CONNECTION(true),
	SCANNING(true),
	IMPORT_BLOB,
	EXPORT_BLOB,
	IMPORT_CLOB,
	EXPORT_CLOB,
	EXTRACTING,
	INDEXING,
	COMMIT,
	ELAPSE_TIME_EXECUTE,
	ELAPSE_TIME_RUN(true),
	ELAPSE_TIME_GLOBAL(true),
	ELAPSE_TIME_LEVEL,
	ELAPSE_TIME_UNIT,
	// Others
	ARKHAM_GED_NODE,
	ARKHAM_GED_TYPE;
	/* @formatter:on */

	private boolean mIsGlobal;

	MDC_KEY() {
		mIsGlobal = false;
	}

	MDC_KEY(boolean global) {
		mIsGlobal = global;
	}

	boolean isGlobal() {
		return mIsGlobal;
	}
}
