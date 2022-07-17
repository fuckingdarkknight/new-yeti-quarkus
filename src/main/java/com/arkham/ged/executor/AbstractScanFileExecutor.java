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

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkham.common.timer.Chrono;
import com.arkham.common.timer.Timer;
import com.arkham.common.timer.common.ChronoDef.UNIT;
import com.arkham.ged.action.AbstractAction;
import com.arkham.ged.action.ActionException;
import com.arkham.ged.action.ActionFactory;
import com.arkham.ged.filekey.FileKey;
import com.arkham.ged.filekey.FileKeyProvider;
import com.arkham.ged.filekey.FileKeyProviderException;
import com.arkham.ged.filekey.FileKeyProviderFactory;
import com.arkham.ged.message.GedMessages;
import com.arkham.ged.properties.ActionType;
import com.arkham.ged.properties.FileKeyProviderRefType;
import com.arkham.ged.properties.FileKeyProviderType;
import com.arkham.ged.properties.GedProperties;
import com.arkham.ged.properties.OptionalParameterType;
import com.arkham.ged.properties.PropertiesAdapter;
import com.arkham.ged.properties.ScanFileDef;
import com.arkham.ged.rejection.AbstractRejector;
import com.arkham.ged.rejection.Rejector;
import com.arkham.ged.scanner.AbstractFileScanner;
import com.arkham.ged.scanner.BaseFileScanner;
import com.arkham.ged.scanner.BasicFileScanner;
import com.arkham.ged.timer.GedTimerManager;
import com.arkham.ged.timer.LoggerMDC;
import com.arkham.ged.timer.TIMERDEF;
import com.arkham.ged.util.GedUtil;

/**
 * Abstract executor that provides convenient methods for files management. The main method to implement is {@link #execute(File)} that is launched for each file scanned by {@link #run()}
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 * @param <T> ScanFileDef extension
 */
public abstract class AbstractScanFileExecutor<T extends ScanFileDef> extends AbstractExecutor<T> implements Rejector {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractScanFileExecutor.class);

	/**
	 * Constant that defines the file name that prevent directory scanning
	 */
	protected static final String NO_SCAN = "noscan";
	protected static final String LINUX_NO_SCAN = ".noscan";

	private final T mSfd;
	private final AbstractFileScanner mAfs;
	private final List<FileKeyProvider> mFkpList;
	private final List<AbstractRejector> mRejector;
	private final List<AbstractAction> mActionList;

	/**
	 * Constructor AbstractExecutor
	 *
	 * @param connection Database connection
	 * @param pa Adapter to properties
	 * @param sfd type T extends ScanFileDef
	 * @throws ExecutorException Should occurs if dynamic instanciation of classes fail
	 */
	@SuppressWarnings("null")
	public AbstractScanFileExecutor(Connection connection, PropertiesAdapter pa, T sfd) throws ExecutorException {
		super(connection, pa);

		mSfd = sfd;
		mAfs = createScanner(sfd.getScanner(), sfd);
		mFkpList = createFileKeyProvider(pa, sfd.getFileKeyProvider());
		mRejector = Rejector.createRejector(sfd.getRejector());
		mActionList = createActions(sfd.getAction());
	}

	/**
	 * @return The directory scan definition
	 */
	protected final T getSFD() {
		return mSfd;
	}

	/**
	 * @return The file scanner that will be used to scan directories using the SFT definition
	 */
	protected final AbstractFileScanner getAFS() {
		return mAfs;
	}

	/**
	 * @return The rejectors list that will be used in case of exception or error
	 */
	protected final List<AbstractRejector> getRejectors() {
		return mRejector;
	}

	/**
	 * @return The order list of actions to execute by executor
	 */
	protected final List<AbstractAction> getActionList() {
		return mActionList;
	}

	/**
	 * @param file The file to process
	 * @param pa Properties
	 * @return The IntFileKey
	 * @throws FileKeyProviderException
	 */
	@SuppressWarnings("null")
	protected final FileKey getFileKey(File file, PropertiesAdapter pa) throws FileKeyProviderException {
		FileKey fk = null;
		for (int i = 0; i < mFkpList.size(); i++) {
			final FileKeyProvider fkp = mFkpList.get(i);
			try {
				fk = fkp.getKey(file, getConnection(), pa, getSFD().getParam());
			} catch (final FileKeyProviderException e) {
				// If its the last FKP defined, rethrow the exception, otherwise try the next one
				if (i == mFkpList.size() - 1) {
					throw e;
				}
			}
			if (fk != null) {
				break;
			}

			LOGGER.info("getActionList() : {} cannot be decoded by {}", file.getName(), fkp.getClass().getCanonicalName());
		}

		return fk;
	}

	/**
	 * Execute action for the given file.
	 *
	 * @param file The file to process
	 */
	public abstract void execute(File file);

	/**
	 * <p>
	 * The main method that launch the scanner to get the file list, then process each file
	 * </p>
	 * <em>If a file named {@value #NO_SCAN} or {@value #LINUX_NO_SCAN} is found in the current directory, this method won't do anything</em>
	 *
	 * @throws ExecutorException
	 * @see #execute(File)
	 */
	@Override
	public final void run() throws ExecutorException {
		// If we find a file named NO_SCAN at the scan root directory, don't process
		if (isFileExists(NO_SCAN) || isFileExists(LINUX_NO_SCAN)) {
			return;
		}

		final Timer<TIMERDEF> timerGlobal = GedTimerManager.getProvider().create(TIMERDEF.GLOBAL_ELAPSED);
		timerGlobal.start();

		Timer<TIMERDEF> timer = GedTimerManager.getProvider().create(TIMERDEF.SCANNING);
		File[] files = null;
		try {
			timer.start();
			files = getAFS().execute();
		} finally {
			timer.stopAndPublish();
		}

		boolean toPublish = false;
		timer = GedTimerManager.getProvider().create(TIMERDEF.ELAPSED_RUN);
		try {
			timer.start();
			if (files != null && files.length > 0) {
				toPublish = true;
				final Chrono chrono = Chrono.getChrono();
				chrono.start();

				LOGGER.info("run() : start periodic job, files scanned={}", files.length);

				boolean considerZeroLengthFile = false;
				@SuppressWarnings("null")
				final OptionalParameterType zeroLength = GedProperties.getOptionalParameters(getSFD().getParam(), "empty");
				if (zeroLength != null) {
					considerZeroLengthFile = GedUtil.getBoolean(zeroLength.getValue(), false);
				}

				// If set in properties, limit the files processed by executor
				files = limitFileProcessing(files);

				beforeExecute(files);

				for (final File file : files) {
					// Purge local MDC values
					LoggerMDC.purgeMDC(false);

					// Dont't process a 0 length file
					final long filesize = file.length();
					if (filesize > 0 || considerZeroLengthFile) {
						final Timer<TIMERDEF> timerExecute = GedTimerManager.getProvider().create(TIMERDEF.ELAPSED_EXECUTE);
						try {
							timerExecute.start();

							execute(file);
						} finally {
							timerExecute.stopAndPublish();

							// Publish all the timers
							GedTimerManager.getProvider().publishDetail();
						}
					}
				}

				afterExecute(files);

				chrono.stop();
				LOGGER.info("run() : end of periodic job, files scanned={} processed in {}", files.length, chrono.getFormattedElapsed(UNIT.MS));
			}
		} finally {
			timer.stopAndPublish();
			timerGlobal.stopAndPublish();

			if (toPublish) {
				GedTimerManager.getProvider().publishRun();
			}

			LoggerMDC.purgeMDC(true);
		}
	}

	/**
	 * Executed before loop on {@link #execute(File)}
	 *
	 * @param files
	 * @throws ExecutorException
	 */
	protected abstract void beforeExecute(File[] files) throws ExecutorException;

	/**
	 * Executed after loop on {@link #execute(File)}
	 *
	 * @param files
	 * @throws ExecutorException
	 */
	protected abstract void afterExecute(File[] files) throws ExecutorException;

	/**
	 * @param filename A file name that is relative to getScan().getDir() definition
	 * @return true is the file exists
	 */
	private boolean isFileExists(String filename) {
		@SuppressWarnings("null")
		final File file = new File(new File(getSFD().getDir()), filename);

		return file.exists();
	}

	/**
	 * @param files The source files array that shouldn't be <code>null</code>
	 * @return A new array limited by <code>maxFileProcessing</code> parameter if set
	 */
	protected File[] limitFileProcessing(File[] files) {
		@SuppressWarnings("null")
		final OptionalParameterType opt = getPA().getOptionalParameter(getSFD().getParam(), "maxFileProcessing");
		if (opt != null) {
			final String mfp = opt.getValue();
			if (mfp != null && mfp.trim().length() > 0) {
				try {
					final int size = Integer.parseInt(mfp);
					if (size > 0 && size < files.length) {
						final File[] result = new File[size];
						System.arraycopy(files, 0, result, 0, size);

						return result;
					}
				} catch (final NumberFormatException e) {
					LOGGER.error("limitFileProcessing() : maxFileProcessing is set but the value={} is invalid ==> all the filenames will be processed", mfp);
				}
			}
		}

		return files;
	}

	protected static final class FileAttributes {
		private static final String ALL = "rwha";

		private FileAttributes() {
			// Private because it's an utility class, so we should't get an instance of this class
		}

		static String getPermission(boolean canRead, boolean canWrite, boolean isHidden, boolean isArchivable) {
			final StringBuilder result = new StringBuilder(4);
			if (canRead) {
				result.append(ALL.charAt(0));
			} else {
				result.append(' ');
			}
			if (canWrite) {
				result.append(ALL.charAt(1));
			} else {
				result.append(' ');
			}
			if (isHidden) {
				result.append(ALL.charAt(2));
			} else {
				result.append(' ');
			}
			if (isArchivable) {
				result.append(ALL.charAt(3));
			} else {
				result.append(' ');
			}

			return result.toString();
		}
	}

	private static List<FileKeyProvider> createFileKeyProvider(PropertiesAdapter pa, List<FileKeyProviderRefType> fkprtl) throws ExecutorException {
		final List<FileKeyProvider> fkpList = new ArrayList<>();
		for (final FileKeyProviderRefType fkprt : fkprtl) {
			final String name = fkprt.getName();
			final FileKeyProviderType fkpt = pa.getFKPType(name);
			if (fkpt == null) {
				throw new ExecutorException(GedMessages.Executor.GED_0106, name);
			}

			final String classref = fkpt.getClassref();
			final List<OptionalParameterType> params = fkpt.getParam();

			final FileKeyProvider fkp;
			try {
				fkp = FileKeyProviderFactory.create(classref, params);
				fkpList.add(fkp);
			} catch (final FileKeyProviderException e) {
				throw new ExecutorException(e);
			}
		}

		return fkpList;
	}

	private AbstractFileScanner createScanner(String className, T sfd) {
		AbstractFileScanner result = null;
		// By default, we use ant capabilities to scan directories
		if (className == null || className.trim().length() == 0 || "ant".equals(className)) {
			result = new BasicFileScanner(sfd);
		} else if ("base_filter".equals(className)) {
			result = new BaseFileScanner(sfd);
		} else {
			try {
				final Class<?> clazz = Class.forName(className);
				final Constructor<?> c = clazz.getConstructor(ScanFileDef.class);

				result = (AbstractFileScanner) c.newInstance(sfd);
			} catch (NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassCastException e) {
				LOGGER.error("createScanner() : className={} exception=", className, e);
			}
		}

		return result;
	}

	/**
	 * @return The action list to be executed by this executor
	 * @throws ExecutorException
	 */
	private static List<AbstractAction> createActions(List<ActionType> atl) throws ExecutorException {
		final List<AbstractAction> aaList = new ArrayList<>();
		for (final ActionType at : atl) {
			final AbstractAction aa;
			try {
				aa = ActionFactory.create(at);
				aaList.add(aa);
			} catch (final ActionException e) {
				throw new ExecutorException(e);
			}
		}

		return aaList;
	}

	protected COMMIT_MODE getCommitMode() {
		COMMIT_MODE result = COMMIT_MODE.GLOBAL;

		@SuppressWarnings("null")
		final OptionalParameterType opt = getPA().getOptionalParameter(getSFD().getParam(), "commit");
		if (opt != null) {
			try {
				result = COMMIT_MODE.valueOf(opt.getValue());
			} catch (final IllegalArgumentException e) { // NOSONAR
				LOGGER.error("getCommitMode() : the commit mode is badly defined, correct values are global and integrateOnly ==> set default value to global");
			}
		}

		return result;
	}

	@Override
	public void reject(File file, FileKey fk, Throwable t, String message, Connection con, PropertiesAdapter pa) {
		for (final AbstractRejector rejector : mRejector) {
			rejector.reject(file, fk, t, message, con, pa);
		}
	}
}
