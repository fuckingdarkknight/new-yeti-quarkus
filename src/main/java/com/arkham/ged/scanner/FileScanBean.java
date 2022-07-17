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
package com.arkham.ged.scanner;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alfred / Arkham asylum
 * @version 1.0
 * @since 16 juil. 2017
 */
public class FileScanBean implements Comparable<String> {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileScanBean.class);

	/**
	 * Default extension used by {@link #rename(String...)} method
	 */
	public static final String RENAMED_DEFEXT = ".processing";

	private final File mScannedFile;
	private File mProcessedFile;
	private File mIntegrateFile;

	private final String mFilename;
	private final String mFiledir;
	private final long mFilesize;
	private final long mFiledate;

	/**
	 * Constructor FileScanBean
	 *
	 * @param scannedFile
	 */
	public FileScanBean(final File scannedFile) {
		mScannedFile = scannedFile;
		mProcessedFile = mScannedFile; // By default, the processed file is the same than the scanned file
		mFilename = scannedFile.getName();
		mFiledir = scannedFile.getParent();
		mFilesize = scannedFile.length();
		mFiledate = scannedFile.lastModified();
	}

	/**
	 * @return the processedFile
	 */
	public File getProcessedFile() {
		return mProcessedFile;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return mFilename;
	}

	/**
	 * @return the filedir
	 */
	public String getFiledir() {
		return mFiledir;
	}

	/**
	 * @return the filesize
	 */
	public long getFilesize() {
		return mFilesize;
	}

	/**
	 * @return the filedate
	 */
	public long getFiledate() {
		return mFiledate;
	}

	/**
	 * @param integrateFile the integrateFile to set
	 */
	public void setIntegrateFile(final File integrateFile) {
		mIntegrateFile = integrateFile;
	}

	/**
	 * @return the scannedFile
	 */
	public File getScannedFile() {
		return mScannedFile;
	}

	/**
	 * @return the integrateFile
	 */
	public File getIntegrateFile() {
		return mIntegrateFile;
	}

	/**
	 * Rename the scanned file and update the inner processed file.
	 *
	 * @param ext The optionnal extension for renaming, {@link #RENAMED_DEFEXT} will be used if not specified
	 * @return The file renamed or <code>null</code> if the file cannot be renamed
	 */
	public File rename(final String... ext) {
		final String renamingExt;
		if (ext.length > 0) {
			renamingExt = ext[0];
		} else {
			renamingExt = RENAMED_DEFEXT;
		}
		final File result = new File(mFiledir, mFilename + renamingExt);
		if (!mScannedFile.renameTo(result)) {
			return null;
		}

		mProcessedFile = result;

		return result;
	}

	/**
	 * Delete the processed file
	 */
	public void delete() {
		if (mProcessedFile != null && !mProcessedFile.delete()) {
			LOGGER.warn("deleteFile() : cannot delete file {}", mProcessedFile);
		}
	}

	@Override
	public int hashCode() {
		return mFilename.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null || obj.getClass() != String.class) {
			return false;
		}

		return mFilename.equals(obj);
	}

	@Override
	public int compareTo(final String s) {
		return mFilename.compareTo(s);
	}

	@Override
	public String toString() {
		return mFilename;
	}
}
