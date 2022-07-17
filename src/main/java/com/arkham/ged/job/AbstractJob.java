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
package com.arkham.ged.job;

import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkham.common.jdbc.DatabaseConnectionManager;
import com.arkham.common.jdbc.DatabaseConnectionManagerException;
import com.arkham.common.pattern.listener.BasicEvent;
import com.arkham.common.scheduler.Job;
import com.arkham.common.scheduler.JobException;
import com.arkham.common.timer.Timer;
import com.arkham.ged.executor.AbstractExecutor;
import com.arkham.ged.executor.ExecutorException;
import com.arkham.ged.properties.PropertiesAdapter;
import com.arkham.ged.properties.ScanFileDef;
import com.arkham.ged.timer.GedTimerManager;
import com.arkham.ged.timer.LoggerMDC;
import com.arkham.ged.timer.TIMERDEF;
import com.arkham.ged.util.GedUtil;

/**
 * Abstract job
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 * @param <T> Scan definition
 */
public abstract class AbstractJob<T extends ScanFileDef> extends Job implements ISettingsJob {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractJob.class);

	private final PropertiesAdapter mPA;
	private final T mSFD;
	private int mFrequency;

	/**
	 * Constructor AbstractJob
	 *
	 * @param name The job name
	 * @param pa Needed to get file extensions/typmed mappings
	 * @param sfd The directory definition to scan
	 */
	public AbstractJob(final String name, final PropertiesAdapter pa, final T sfd) {
		super(name);

		mPA = pa;
		mSFD = sfd;

		// Default value in XSD : 1s
		if (sfd != null) {
			mFrequency = (int) (sfd.getFrequency().doubleValue() * 1000.0);
		} else {
			mFrequency = -1;
		}

		registerListener(new InterruptedJobListener(this));
	}

	/**
	 * Update MDC, this method should be called from inherits implemented jobs
	 *
	 * @see com.arkham.common.scheduler.Job#execute()
	 */
	@SuppressWarnings("static-method")
	protected void initMDC() {
		GedUtil.initMDC();
	}

	@Override
	protected boolean shouldExecute() {
		if (getSFD() != null && getSFD().isActive()) {
			return super.shouldExecute();
		}

		return false;
	}

	@SuppressWarnings("resource")
	@Override
	public void execute() throws JobException {
		if (!getPA().isActive()) {
			return;
		}

		initMDC();

		Connection con = null;
		try {
			final Timer<TIMERDEF> timer = GedTimerManager.getProvider().create(TIMERDEF.DATABASE_CONNECTION);
			try {
				timer.start();

				// By default we can use DB connection but in some cases, no need to create it.
				if (getSFD().isUsedb()) {
					con = getDCM().getConnection();
				}
			} finally {
				timer.stopAndPublish();
			}

			// Dynamic instance of executor
			final AbstractExecutor<T> executor = createExecutorInstance(con);
			if (executor == null) {
				throw new JobException(createExceptionMessage("Unknown executor"));
			}

			executor.run();
		} catch (final Throwable e) { // NOSONAR : il faut tout attraper à ce niveau là
			LOGGER.error("execute() : ", e);
		} finally {
			LoggerMDC.purgeMDC(true);

			releaseConnection(con);
		}
	}

	/**
	 * @param con The database connection that will be used by the executor for this job run
	 * @return A new instance of executor as defined by the abstract {@link ScanFileDef}
	 * @throws ExecutorException
	 */
	protected abstract AbstractExecutor<T> createExecutorInstance(Connection con) throws ExecutorException;

	private static String createExceptionMessage(final String value) {
		return "Cannot instantiate the class=" + value + " please consult log for further informations";
	}

	/**
	 * Get the job frequency in ms
	 * <p>
	 * {@inheritDoc}
	 *
	 * @see com.arkham.common.scheduler.Job#getFrequency()
	 */
	@Override
	public int getFrequency() {
		return mFrequency;
	}

	/**
	 * @return The {@link ScanFileDef}
	 */
	@Override
	public final T getSFD() {
		return mSFD;
	}

	/**
	 * @return The {@link DatabaseConnectionManager}
	 * @throws DatabaseConnectionManagerException
	 */
	public final DatabaseConnectionManager getDCM() throws DatabaseConnectionManagerException {
		return mPA.getDCM();
	}

	/**
	 * Release the database connection silently
	 *
	 * @param con The databse connection
	 */
	protected final void releaseConnection(final Connection con) {
		if (con != null) {
			try {
				getDCM().releaseConnection(con);
			} catch (final DatabaseConnectionManagerException e) { // NOSONAR
				LOGGER.error("releaseConnection() : {}", e);
			}
		}
	}

	/**
	 * @return The {@link PropertiesAdapter}
	 */
	public final PropertiesAdapter getPA() {
		return mPA;
	}

	protected class InterruptedJobListener implements IJobListener {
		private final AbstractJob<T> mJob;

		/**
		 * Constructor InterruptJobListener
		 *
		 * @param job
		 */
		protected InterruptedJobListener(final AbstractJob<T> job) {
			mJob = job;
		}

		@Override
		public void destroy() {
			// Nothing to do
		}

		@Override
		public void eventFired(final BasicEvent<Job, JOB_EVENT> event) {
			if (event.getType() == JOB_EVENT.INTERRUPTED) {
				LOGGER.info("eventFired() : stopping Job={}", mJob.getName());
			}
		}
	}
}
