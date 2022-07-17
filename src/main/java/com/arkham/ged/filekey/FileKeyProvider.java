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
package com.arkham.ged.filekey;

import java.io.File;
import java.sql.Connection;
import java.util.List;

import com.arkham.ged.properties.OptionalParameterType;
import com.arkham.ged.properties.PropertiesAdapter;

/**
 * Abstract class used to determine a key from a file (.ref, file name ...)
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public abstract class FileKeyProvider {
	/**
	 * Extension of file while it's processing
	 */
	public static final String PROCEXT = ".processing";

	private List<OptionalParameterType> mParams;

	void init(List<OptionalParameterType> params) {
		mParams = params;
	}

	/**
	 * @return The optional FileKeyProvider parameters
	 */
	public final List<OptionalParameterType> getParams() {
		return mParams;
	}

	/**
	 * Retrieve file key
	 *
	 * @param file A file that could not be <code>null</code> ({@link NullPointerException} otherwise)
	 * @param con The database connection
	 * @param pa Properties
	 * @param opt The parameter list
	 * @return The file key to integrate or <code>null</code> if the file is 0 length
	 * @throws FileKeyProviderException Generic exception
	 */
	public abstract FileKey getKey(File file, Connection con, PropertiesAdapter pa, List<OptionalParameterType> opt) throws FileKeyProviderException;

	/**
	 * @return true if the file to process is a .ref file, false otherwise
	 */
	public abstract boolean isRefFile();
}
