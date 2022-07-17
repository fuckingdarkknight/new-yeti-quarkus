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
package com.arkham.ged.filefilter;

import java.io.File;
import java.io.FileFilter;

import com.arkham.ged.util.GedUtil;

/**
 * Common file filter that could be used to get files from an extension list. A file name that starts with "." is excluded ({@link #accept(File)} return false)
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public class FilenameExtensionFilter implements FileFilter {
	private final String[] mExtensions;

	/**
	 * Constructor FilenameExtensionFilter
	 *
	 * @param ext File extensions list, separated by ";" or "," or "|" or ":"
	 */
	public FilenameExtensionFilter(String ext) {
		mExtensions = GedUtil.splitValues(ext);
		// For compareasons it's simplest to add directly the "."
		for (int i = 0; i < mExtensions.length; i++) {
			mExtensions[i] = "." + mExtensions[i];
		}
	}

	@Override
	public boolean accept(File pathname) {
		final String filename = pathname.getName();
		if (filename.startsWith(".")) {
			return false;
		}

		for (int i = 0; i < mExtensions.length; i++) {
			final String ext = mExtensions[i];
			if (filename.endsWith(ext)) {
				return true;
			}
		}

		return false;
	}
}
