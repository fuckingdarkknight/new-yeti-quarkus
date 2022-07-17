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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkham.ged.util.GedUtil;

/**
 * Provides {@link FileKey} from a YAML .ref file. Transform it to JSON in order to use {@link FileKey#getJsonObject()}
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 14 janv. 2019
 */
public class YamlFileKeyProvider extends JsonFileKeyProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(YamlFileKeyProvider.class);

	@Override
	protected String transform(String content) throws IOException {
		LOGGER.info("transform() : consuming YAML message=\n{}", content);

		final String result = GedUtil.convertYamlToJson(content);

		LOGGER.info("transform() : transformed to JSON message=\n{}", result);

		return result;
	}
}
