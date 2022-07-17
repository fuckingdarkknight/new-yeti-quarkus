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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkham.ged.properties.OptionalParameterType;
import com.arkham.ged.properties.PropertiesAdapter;

/**
 * Provides {@link FileKey} from a JSON .ref file
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 10 nov. 2017
 */
public class JsonFileKeyProvider extends FileKeyProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonFileKeyProvider.class);

	@Override
	public FileKey getKey(File file, Connection con, PropertiesAdapter pa, List<OptionalParameterType> opt) throws FileKeyProviderException {
		try {
			String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);

			content = transform(content);

			final JSONObject o = new JSONObject(content);
			final int codsoc = o.getInt("codsoc");
			final String typtie = o.getString("typtie");
			final String nomcle = o.getString("nomcle");
			final String filename = o.getString("filename");

			final FileKey fk = new FileKey(codsoc, typtie, nomcle, filename);
			fk.setJsonObject(o);

			return fk;
		} catch (final IOException | JSONException e) {
			throw new FileKeyProviderException(e);
		}
	}

	/**
	 * Transform the input string
	 *
	 * @param content Content to transform
	 * @return Transformed content
	 * @throws IOException Generic exception
	 */
	@SuppressWarnings("static-method")
	protected String transform(String content) throws IOException { // NOSONAR
		LOGGER.info("transform() : consuming JSON message=\n{}", content);

		return content;
	}

	@Override
	public boolean isRefFile() {
		return true;
	}
}
