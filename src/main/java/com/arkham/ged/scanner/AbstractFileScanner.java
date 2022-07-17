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

import com.arkham.ged.properties.OptionalParameterType;
import com.arkham.ged.properties.ScanFileDef;
import com.arkham.ged.util.GedUtil;

/**
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public abstract class AbstractFileScanner {
	protected static final String DIR_DNE = "scan() : the specified directory does not exist={}";
	protected static final String DIR_DNE_CREATE = DIR_DNE + " try to create it";
	protected static final String DIR_CREATED = "scan() : directory {} have been created successfully";
	protected static final String DIR_NOT_CREATED = "scan() : directory {} could not be created";

	private final ScanFileDef mSfd;

	/**
	 * Constructor AbstractFileScanner
	 *
	 * @param sfd Scanner settings
	 */
	public AbstractFileScanner(ScanFileDef sfd) {
		mSfd = sfd;
	}

	/**
	 * @return The scanner
	 */
	protected final ScanFileDef getSFD() {
		return mSfd;
	}

	/**
	 * @param value A value that could contains some separator chars. {@link GedUtil#splitValues(String)}
	 * @return An array with splitted values
	 */
	@SuppressWarnings("static-method")
	protected String[] splitValue(String value) {
		return GedUtil.splitValues(value);
	}

	/**
	 * Launch the scanner to get the file list Process list after (sort)
	 *
	 * @return An array of files
	 */
	public File[] execute() {
		beforeScan();

		final File[] files = scan();

		return afterScan(files);
	}

	/**
	 * Launch scanning
	 *
	 * @return An array of files
	 */
	protected abstract File[] scan();

	/**
	 * Executed before scan on {@link #scan()}
	 */
	protected void beforeScan() {
		// By default, nothing to do before scanning
	}

	/**
	 * Executed after scan on {@link #scan()}, by default execute a sort on filename or file date if specified in the settings
	 *
	 * @param files
	 * @return The sorted array of File
	 */
	protected File[] afterScan(File[] files) {
		File[] result = files;

		for (final OptionalParameterType opt : getSFD().getParam()) {
			if ("sort".equals(opt.getName())) {
				final String sortOrder = opt.getValue();
				if ("byDate".equals(sortOrder)) {
					// sortByDate
					result = GedUtil.sortByDate(files);
				} else if ("byName".equals(sortOrder)) {
					// sortByName
					result = GedUtil.sortByName(files);
				}
			}
		}

		return result;
	}
}
