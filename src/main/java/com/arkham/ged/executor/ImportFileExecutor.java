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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkham.ged.action.AbstractAction;
import com.arkham.ged.blob.DocumentLinkBean;
import com.arkham.ged.filekey.FileKey;
import com.arkham.ged.filekey.FileKeyProviderException;
import com.arkham.ged.properties.GedProperties;
import com.arkham.ged.properties.InputScanFileDef;
import com.arkham.ged.properties.OnErrorType;
import com.arkham.ged.properties.OptionalParameterType;
import com.arkham.ged.properties.PropertiesAdapter;
import com.arkham.ged.timer.LoggerMDC;
import com.arkham.ged.timer.MDC_KEY;
import com.arkham.ged.util.GedUtil;

/**
 * Common methods for importing file
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 9 févr. 2015
 */
public abstract class ImportFileExecutor extends AbstractScanFileExecutor<InputScanFileDef> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImportFileExecutor.class);

	private static final String MSG_DEL_OK = "  * file={} has been deleted successfully";
	private static final String MSG_DEL_KO = "  * file={} cannot be deleted";
	private static final String MSG_REN_OK = "  * file={} has been renamed successfully to {}";
	private static final String MSG_REN_KO = "  * file={} cannot be renamed to {}";

	/**
	 * Constructor ImportFileExecutor
	 *
	 * @param connection The database connection
	 * @param pa The properties adapter
	 * @param sfd The scan definition
	 * @throws ExecutorException Generic exception
	 */
	public ImportFileExecutor(Connection connection, PropertiesAdapter pa, InputScanFileDef sfd) throws ExecutorException {
		super(connection, pa, sfd);
	}

	/**
	 * @param file The file to process
	 * @return The file that should be processed or <code>null</code> if it's not possible for some reasons
	 * @throws IOException
	 */
	protected abstract File beforeProcessFile(File file) throws IOException;

	@Override
	public void execute(File fileRef) {
		File file = fileRef;
		File integrate = null;
		FileKey fk = null;
		Throwable t = null;
		try {
			LOGGER.info("------------------------------");
			LOGGER.info("process file={}", fileRef.getCanonicalPath());

			file = beforeProcessFile(fileRef);

			if (file == null) {
				// Nothing to do
				return;
			}

			// Gets the file key
			fk = getFileKey(file, getPA());
			if (fk == null) {
				// Nothing to do, unable to process a .ref that is empty
				return;
			}

			LOGGER.info("  * permissions={}", FileAttributes.getPermission(file.canRead(), file.canWrite(), file.isHidden(), false));

			// The file to integrate depends on ref file or not
			if (fk.isRefFile()) {
				// Absolute path
				if (fk.isAbsolute()) {
					integrate = new File(fk.getFilename());
				} else {
					// Relative path to file to integrate
					integrate = new File(file.getParentFile(), fk.getFilename());
				}
			} else {
				integrate = file;
			}

			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("  * integrate={} ({})", integrate.getCanonicalPath(), FileUtils.byteCountToDisplaySize(integrate.length()));
			}

			// Launch now the actions
			integrate(file, integrate, fk);

			if (COMMIT_MODE.GLOBAL == getCommitMode()) {
				commit();
			}
		} catch (final FileKeyProviderException e) { // NOSONAR
			LOGGER.error("execute() : file={}\r\n{}", file.getName(), e);
		} catch (final FileNotFoundException e) { // NOSONAR
			// Le fichier a déjà été traité par une autre instance, ce n'est pas une erreur
			LOGGER.error("execute() : file={} already processed by a tier", file.getName());
		} catch (final IOException e) { // NOSONAR
			// Peut se produire si le fichier est locké par exemple, mais ce n'est pas une erreur en soi.
			LOGGER.info("execute() : file={} locked by a tier : {}", file.getName(), e);
		} catch (final SQLException e) {
			LOGGER.info("execute() : problem while commiting : {}", e);
		} catch (final ExecutorException e) {
			LOGGER.error("execute() : file={} because of {}", file.getName(), e.getCause());

			// In this case, the files do not have to be deleted
			t = e;
		}

		// Only if the processing is good : deletion of files integrated.
		// Eg. the tablespace is full, the insert cannot be done and the file is deleted : bad way, so we never delete any file in case of problem
		if (t == null) {
			try {
				renameOrDeleteFile(file, integrate, fk);
			} catch (final IOException e) { // NOSONAR
				// No need to rethrow the exception or display the stacktrace : it's a normal way
				LOGGER.error("execute() : file={} because of {}", file.getName(), e.getCause());
			}
		}
	}

	@Override
	protected void beforeExecute(File[] files) {
		// Nothing to do
	}

	@Override
	protected void afterExecute(File[] files) {
		// Nothing to do
	}

	/**
	 * Executed by {@link #integrate(File, File, FileKey)} before writing file to target support
	 *
	 * @param file The file to integrate
	 * @param fk
	 * @throws ExecutorException
	 */
	protected abstract void beforeIntegrate(File file, FileKey fk) throws ExecutorException;

	/**
	 * Executed by {@link #integrate(File, File, FileKey)}
	 *
	 * @param file The file to integrate
	 * @param bean The beans that represents the pk (and common fields) from LOBs tables
	 * @throws ExecutorException
	 */
	protected abstract void afterIntegrate(File file, List<DocumentLinkBean> bean) throws ExecutorException;

	/**
	 * @param sourceFile The file that defines the content to integrate (.ref or the same that integrate param)
	 * @param integrate The file to import to database from FK definition
	 * @param fk The FileKey
	 * @throws ExecutorException Probably a SQLException
	 * @see #getRejectors()
	 * @see #beforeIntegrate(File, FileKey)
	 * @see #afterIntegrate(File, List)
	 */
	protected void integrate(File sourceFile, File integrate, FileKey fk) throws ExecutorException {
		if (integrate.exists()) {
			LoggerMDC.putMDC(MDC_KEY.FILENAME, fk.getFilename());
			LoggerMDC.putMDC(MDC_KEY.FILESIZE, integrate.length());

			// First we test if the file size setting is set. If set, we have to verify that the length is not too high
			final OptionalParameterType opt = getPA().getOptionalParameter(getSFD().getParam(), GedProperties.PARAM_INTEGRATE_MAX_SIZE);
			if (opt != null) {
				final int maxFileSize = GedProperties.getMaxSize(getSFD().getParam());
				if ((int) integrate.length() > maxFileSize) {
					LOGGER.warn("integrate() : file={} size={} > {}", integrate.getName(), integrate.length(), maxFileSize);

					return;
				}
			}

			try {
				beforeIntegrate(integrate, fk);

				List<DocumentLinkBean> bean = new ArrayList<>();
				final List<AbstractAction> aal = getActionList();
				for (final AbstractAction aa : aal) {
					final OnErrorType oet = aa.getActionType().getOnError();

					// L'exécution de l'action peut provoquer une exception, onError permet de décider ce que l'on fait si l'exécution
					// échoue : soit on continue de traiter les actions suivantes, soit on arrêter pour le fichier courant
					try {
						bean = aa.execute(integrate, fk, getConnection(), getPA(), bean, getSFD());
					} catch (final Throwable t) { // NOSONAR
						if (oet == OnErrorType.STOP) {
							throw t;
						} else if (oet == OnErrorType.CONTINUE) {
							LOGGER.error("integrate() : {} continue after exception={}", aa.getClass(), t);
						}
					}

					if (COMMIT_MODE.INTEGRATE_ONLY == getCommitMode()) {
						commit();
					}
				}

				afterIntegrate(integrate, bean);
			} catch (final Throwable t) { // NOSONAR
				// SQLException, ExecutorException
				// Ne devrait pas arriver, sauf dans des cas extrêmes : tablespace plein ...
				// Néanmoins, si la PK est renseignée avec les pieds, ça peut péter aussi.

				// Arrive aussi sur les runtime
				LOGGER.error("integrate() : file={}", integrate.getName(), t);

				reject(sourceFile, fk, t, null, getConnection(), getPA());

				try {
					rollback();
				} catch (final SQLException e) {
					LOGGER.error("integrate() : cannot rollback transaction because of exception=", e);
				}

				throw new ExecutorException(t);
			}
		} else {
			LOGGER.error("  * file to integrate does not exist : {}", integrate.getName()); // NOSONAR

			reject(sourceFile, fk, null, "file to integrate does not exist :" + integrate.getAbsolutePath(), getConnection(), getPA());
		}
	}

	/**
	 * @param sourceFile
	 * @param integrate The integrated file, that could be the same than <code>sourceFile</code> (in case of direct integration, without .ref)
	 * @param fk The file key
	 */
	protected void renameOrDeleteFile(File sourceFile, File integrate, FileKey fk) throws IOException {
		if (fk != null) {
			String fileRefRename = getSFD().getFileRefRename();
			if (fileRefRename != null && fileRefRename.trim().length() == 0) {
				fileRefRename = null;
			}

			// .ref deletion : the first FKP define if its a ref file or not
			if (fk.isRefFile()) {
				// delete the .ref
				if (getSFD().isFileRefDeletion() && sourceFile != null) {
					if (sourceFile.delete()) {
						LOGGER.info(MSG_DEL_OK, sourceFile.getName());
					} else {
						LOGGER.error(MSG_DEL_KO, sourceFile.getName());
					}
				} else if (sourceFile != null && fileRefRename != null) {
					final File renamed = new File(GedUtil.replaceFileExtension(sourceFile.getCanonicalPath(), fileRefRename));
					if (sourceFile.renameTo(renamed)) {
						LOGGER.info(MSG_REN_OK, sourceFile.getName(), renamed.getName());
					} else {
						LOGGER.info(MSG_REN_KO, sourceFile.getName(), renamed.getName());
					}
				}

				// delete the integrated file
				if (getSFD().isFileDeletion() && integrate != null) {
					if (integrate.delete()) {
						LOGGER.info(MSG_DEL_OK, integrate.getName());
					} else {
						LOGGER.error(MSG_DEL_KO, integrate.getName());
					}
				}
			} else {
				// in this case, there's no file .ref and the attribute FileRefDeletion is used to delete the integrated file
				if (fileRefRename != null) {
					if (getSFD().isFileDeletion() && integrate != null && integrate.delete()) {
						LOGGER.info(MSG_DEL_OK, integrate.getName());
					} else {
						final File renamed = new File(GedUtil.replaceFileExtension(integrate.getCanonicalPath(), fileRefRename)); // NOSONAR
						if (renamed.delete()) {
							LOGGER.info(MSG_DEL_OK, renamed.getName());
						} else {
							// No file to delete before renaming : the normal case in fact
							// NOSONAR
						}

						if (integrate.renameTo(renamed)) {
							LOGGER.info(MSG_REN_OK, integrate.getName(), renamed.getName());
						} else {
							LOGGER.error(MSG_REN_KO, integrate.getName(), renamed.getName());
						}
					}
				} else if ((getSFD().isFileDeletion() || getSFD().isFileRefDeletion()) && integrate != null && integrate.delete()) {
					LOGGER.info(MSG_DEL_OK, integrate.getName());
				} else if (integrate != null) {
					LOGGER.error(MSG_DEL_KO, integrate.getName());
				} else {
					LOGGER.error("  * file null cannot be deleted");
				}
			}
		}
	}
}
