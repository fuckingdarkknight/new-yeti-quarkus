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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkham.ged.action.AbstractAction;
import com.arkham.ged.action.ActionException;
import com.arkham.ged.blob.DocumentLinkBean;
import com.arkham.ged.filekey.FileKey;
import com.arkham.ged.filekey.FileKeyProviderException;
import com.arkham.ged.properties.OutputScanFileDef;
import com.arkham.ged.properties.PropertiesAdapter;

/**
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public class ExportFileExecutor extends AbstractScanFileExecutor<OutputScanFileDef> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExportFileExecutor.class);

	private static final String LOG_EXECUTE = "execute() : file={}";
	private static final String LOG_DELREN = "  * file={}";

	private List<File> mProcessedFiles;

	/**
	 * Constructor ExportFileExecutor
	 *
	 * @param connection Database connection
	 * @param pa Properties
	 * @param sfd ScanFileDef
	 * @throws ExecutorException if any exception occurs
	 */
	public ExportFileExecutor(Connection connection, PropertiesAdapter pa, OutputScanFileDef sfd) throws ExecutorException {
		super(connection, pa, sfd);
	}

	@Override
	public void execute(File file) {
		try {
			LOGGER.info("------------------------------");
			LOGGER.info("process file={}", file.getCanonicalPath());

			// Gets the file key
			final FileKey fk = getFileKey(file, getPA());

			mProcessedFiles.add(file);

			// Just to transport some informations between actions
			final List<DocumentLinkBean> dlb = new ArrayList<>();

			// Execute all the actions for this export
			for (final AbstractAction aa : getActionList()) {
				aa.execute(file, fk, getConnection(), getPA(), dlb, getSFD());
			}

			commit();

			renameOrDeleteFile(file);
		} catch (final FileKeyProviderException e) { // NOSONAR
			LOGGER.error(LOG_EXECUTE, file.getName() + "\r\n" + e);
		} catch (final ActionException e) { // NOSONAR
			LOGGER.error(LOG_EXECUTE, file.getName(), e);
		} catch (final FileNotFoundException e) { // NOSONAR
			// Le fichier a déjà été traité par une autre instance, ce n'est pas une erreur
			LOGGER.error(LOG_EXECUTE, file.getName() + " already processed by a tier");
		} catch (final IOException e) { // NOSONAR
			// Peut se produire si le fichier est locké par exemple, mais ce n'est pas une erreur en soi.
			// LOG.fatal("executeFile() : file=" + file.getName() + "\r\n" + e);
			LOGGER.info(LOG_EXECUTE, file.getName() + " locked by a tier");
		} catch (final SQLException e) {
			LOGGER.info("execute() : problem while commiting", e);
		}
	}

	@Override
	protected void beforeExecute(File[] files) {
		if (mProcessedFiles == null) {
			mProcessedFiles = new ArrayList<>();
		} else {
			mProcessedFiles.clear();
		}
	}

	@Override
	protected void afterExecute(File[] files) {
		if (mProcessedFiles != null && !mProcessedFiles.isEmpty()) {
			if (getSFD().isFileDeletion() || getSFD().isFileRefDeletion()) {
				for (final File file : mProcessedFiles) {
					if (file.exists()) {
						try {
							renameOrDeleteFile(file);
						} catch (final IOException e) { // NOSONAR
							// No need to rethrow the exception or display the stacktrace : it's a normal way
							LOGGER.info("afterExecute() : file={} locked by a tier", file.getName());
						}
					}
				}
			}

			mProcessedFiles.clear();
		}
	}

	private void renameOrDeleteFile(File file) throws IOException {
		// Rename fileref is priority against deletion
		if (getSFD().getFileRefRename() != null) {
			final File renameTo = new File(file.getCanonicalPath() + getSFD().getFileRefRename());
			if (file.renameTo(renameTo)) {
				LOGGER.info(LOG_DELREN, file.getName() + " has been renamed successfully to " + renameTo.getName());
			} else {
				LOGGER.error(LOG_DELREN, file.getName() + " cannot be renamed");
			}
		} else {
			// Any deletion have to delete, it's absolutely a .ref file to export !
			if (getSFD().isFileDeletion() || getSFD().isFileRefDeletion()) {
				if (file.delete()) {
					LOGGER.info(LOG_DELREN, file.getName() + " has been deleted successfully");
				} else {
					LOGGER.error(LOG_DELREN, file.getName() + " cannot be deleted");
				}
			}
		}
	}
}
