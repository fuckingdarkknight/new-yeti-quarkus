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
package com.arkham.ged.action;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkham.ged.blob.DocumentLinkBean;
import com.arkham.ged.filekey.FileKey;
import com.arkham.ged.properties.AbstractScanDef;
import com.arkham.ged.properties.ActionType;
import com.arkham.ged.properties.OptionalParameterType;
import com.arkham.ged.properties.PropertiesAdapter;
import com.arkham.ged.util.GedUtil;

/**
 * The abstract action, use {@link ActionType} for defining the specifics behaviors
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 29 déc. 2015
 * @param <T> The scanner definition
 * @param <D> The data type
 */
public abstract class AbstractAction<T extends AbstractScanDef, D> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAction.class);

	private ActionType mAt;

	/**
	 * Initialize the action type for the current abstract action
	 *
	 * @param action
	 */
	void init(final ActionType action) {
		mAt = action;
	}

	/**
	 * @return The action type
	 */
	public final ActionType getActionType() {
		return mAt;
	}

	/**
	 * @param pa Adapter
	 * @param key The property name
	 * @param defaultValue The default value
	 * @return The string value of the action parameter or <code>null</code> if no parameter was found else default value
	 */
	protected String getParamStringValue(final PropertiesAdapter pa, final String key, final String defaultValue) {
		String result = defaultValue;
		final OptionalParameterType opt = pa.getOptionalParameter(mAt.getParam(), key);
		if (opt != null) {
			result = opt.getValue();
			if (result == null) {
				return defaultValue;
			}
		}

		return result;
	}

	/**
	 * @param pa Adapter
	 * @param key The property name
	 * @param defaultValue The default value
	 * @return The int value of the action parameter else default value
	 */
	protected int getParamIntValue(final PropertiesAdapter pa, final String key, final int defaultValue) {
		return GedUtil.getInt(getParamStringValue(pa, key, String.valueOf(defaultValue)), defaultValue);
	}

	/**
	 * @param pa Adapter
	 * @param key The property name
	 * @param defaultValue The default value
	 * @return The boolean value of the action parameter else default value
	 */
	protected boolean getParamBoolValue(final PropertiesAdapter pa, final String key, final boolean defaultValue) {
		return GedUtil.getBoolean(getParamStringValue(pa, key, String.valueOf(defaultValue)), defaultValue);
	}

	/**
	 * Delete the file and log something if it's not possible for any reason
	 *
	 * @param file The file to delete
	 */
	@SuppressWarnings("static-method")
	protected void deleteFile(final File file) {
		if (!file.delete()) {
			LOGGER.warn("deleteFile() : cannot delete file {}", file);
		}
	}

	/**
	 * Rename the file and log something if it's not possible for any reason
	 *
	 * @param fileFrom The file to rename
	 * @param fileTo The renamed name
	 */
	@SuppressWarnings("static-method")
	protected void renameFiles(final File fileFrom, final File fileTo) {
		if (!fileFrom.renameTo(fileTo)) {
			LOGGER.warn("renameFiles() : {} cannot be renamed in {}", fileFrom, fileTo);
		}
	}

	/**
	 * Rename the file and log something if it's not possible for any reason
	 *
	 * @param fileFrom The file to rename
	 * @param fileTo The renamed name
	 */
	@SuppressWarnings("static-method")
	protected void renameFiles(final Path fileFrom, final Path fileTo) {
		try {
			Files.move(fileFrom, fileTo, StandardCopyOption.REPLACE_EXISTING);
		} catch (@SuppressWarnings("unused") final IOException e) {
			LOGGER.warn("renameFiles() : {} cannot be renamed in {}", fileFrom.toAbsolutePath(), fileTo.toAbsolutePath());
		}
	}

	/**
	 * Execute the action.
	 *
	 * @param data The file or datas used to execute an action on it
	 * @param fk The file key
	 * @param con Database connection
	 * @param pa Properties
	 * @param bean The list of {@link DocumentLinkBean} computed by the previous action or <code>null</code>
	 * @param asd The {@link AbstractScanDef}
	 * @return The list of {@link DocumentLinkBean} to continue the chain pattern
	 * @throws ActionException Generic exception that probably escalate the real exception
	 */
	public abstract List<DocumentLinkBean> execute(D data, FileKey fk, Connection con, PropertiesAdapter pa, List<DocumentLinkBean> bean, T asd) throws ActionException;
}
