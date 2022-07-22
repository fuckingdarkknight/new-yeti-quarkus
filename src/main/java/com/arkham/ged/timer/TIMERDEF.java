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

import com.arkham.common.timer.common.ChronoDef.UNIT;

/**
 * Define timers used by Arkham Ged
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public enum TIMERDEF {
    /**
     * Authentication process
     */
    AUTHENTICATION("AUTHENTICATION", MDC_KEY.AUTHENTICATION),
    /**
     * Database connection
     */
    DATABASE_CONNECTION("CONNECTION", MDC_KEY.CONNECTION),
    /**
     * Scanning
     */
    SCANNING("SCANNING", MDC_KEY.SCANNING),
    /**
     * Database import to MEDIA_BLOB
     */
    DATABASE_IMPORT_BLOB("IMPORT_BLOB", MDC_KEY.IMPORT_BLOB),
    /**
     * Database export from MEDIA_BLOB
     */
    DATABASE_EXPORT_BLOB("EXPORT_BLOB", MDC_KEY.EXPORT_BLOB),
    /**
     * Database import to DOC_REC
     */
    DATABASE_IMPORT_CLOB("IMPORT_CLOB", MDC_KEY.IMPORT_CLOB),
    /**
     * Database export from DOC_REC
     */
    DATABASE_EXPORT_CLOB("EXPORT_CLOB", MDC_KEY.EXPORT_CLOB),
    /**
     * Extractor elapsed
     */
    EXTRACTING("EXTRACTING", MDC_KEY.EXTRACTING),
    /**
     * Indexing content for full text search
     */
    INDEXING("INDEXING", MDC_KEY.INDEXING),
    /**
     * Commit
     */
    COMMIT("COMMIT", MDC_KEY.COMMIT),
    /**
     * Global elapsed time for a file execution
     */
    ELAPSED_EXECUTE("ELAPSED_EXECUTE", MDC_KEY.ELAPSE_TIME_EXECUTE),
    /**
     * Global elapsed time for a run
     */
    ELAPSED_RUN("ELAPSED_RUN", MDC_KEY.ELAPSE_TIME_RUN),
    /**
     * Global elapsed time for a thread execution
     */
    GLOBAL_ELAPSED("GLOBAL_ELAPSED", MDC_KEY.ELAPSE_TIME_GLOBAL);

    private String mName;
    private transient UNIT mUnit;
    private double mPrecision;
    private MDC_KEY mKey;

    /**
     * Constructor TIMER
     *
     * @param name
     */
    TIMERDEF(String name, MDC_KEY key) {
        this(name, UNIT.MS, 0.0D, key);
    }

    TIMERDEF(String name, UNIT unit, double precision, MDC_KEY key) {
        mName = name;
        mUnit = unit;
        mPrecision = precision;
        mKey = key;
    }

    public String getName() {
        return mName;
    }

    public UNIT getUnit() {
        return mUnit;
    }

    public double getPrecision() {
        return mPrecision;
    }

    public MDC_KEY getKey() {
        return mKey;
    }
}
