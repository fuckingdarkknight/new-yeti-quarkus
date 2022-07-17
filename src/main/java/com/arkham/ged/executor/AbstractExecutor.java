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
package com.arkham.ged.executor;

import java.sql.Connection;
import java.sql.SQLException;

import com.arkham.common.timer.Timer;
import com.arkham.ged.properties.AbstractScanDef;
import com.arkham.ged.properties.PropertiesAdapter;
import com.arkham.ged.timer.GedTimerManager;
import com.arkham.ged.timer.TIMERDEF;

/**
 * Abstract executor
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 1 juin 2016
 * @param <T> The bus/scanner
 */
public abstract class AbstractExecutor<T extends AbstractScanDef> {
	/* @formatter:off */
	protected enum COMMIT_MODE {
		/**
		 * Commit only if all integrate actions suceed
		 */
		GLOBAL,
		/**
		 * Commit if integration in database succeed. Even if other actions fails
		 */
		INTEGRATE_ONLY
	}
	/* @formatter:on */

	private final Connection mConnection;
	private final PropertiesAdapter mPa;

	/**
	 * Constructor AbstractExecutor
	 *
	 * @param pa The properties adapter
	 */
	public AbstractExecutor(PropertiesAdapter pa) {
		mConnection = null;
		mPa = pa;
	}

	/**
	 * Constructor AbstractExecutor
	 *
	 * @param connection Database connection
	 * @param pa The properties adapter
	 */
	public AbstractExecutor(Connection connection, PropertiesAdapter pa) {
		mConnection = connection;
		mPa = pa;
	}

	/**
	 * @return The database {@link Connection}
	 */
	protected final Connection getConnection() {
		return mConnection;
	}

	/**
	 * @return The properties adapter
	 */
	protected final PropertiesAdapter getPA() {
		return mPa;
	}

	/**
	 * Commit SQL transaction. Could be overloaded in case of executor that does not use a SGBD
	 *
	 * @throws SQLException
	 */
	protected final void commit() throws SQLException {
		final Timer<TIMERDEF> timer = GedTimerManager.getProvider().create(TIMERDEF.COMMIT);
		try {
			timer.start();

			if (mConnection != null) {
				mConnection.commit();
			}
		} finally {
			timer.stopAndPublish();
		}
	}

	/**
	 * Rollback SQL transaction. Could be overloaded in case of executor that does not use a SGBD
	 *
	 * @throws SQLException
	 */
	protected final void rollback() throws SQLException {
		if (mConnection != null) {
			mConnection.rollback();
		}
	}

	/**
	 * Runs the executor
	 *
	 * @throws ExecutorException Generic package exception
	 */
	public abstract void run() throws ExecutorException;
}
