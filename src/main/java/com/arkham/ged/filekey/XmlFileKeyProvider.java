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
import java.io.InputStream;
import java.nio.file.Files;

import com.arkham.common.properties.FlatProp;
import com.arkham.common.properties.XmlProp;

/**
 * Provides {@link FileKey} from a "XML" .ref file format by using {@link XmlProp} reader capabilities
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public class XmlFileKeyProvider extends FlatFileKeyProvider {
	/**
	 * Use a {@link XmlProp} reader to get the properties from an XML file content
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	protected FlatProp getProp(File file) throws IOException {
		try (InputStream is = Files.newInputStream(file.toPath())) {
			final XmlProp p = new XmlProp();
			p.load(is);

			return p;
		}
	}
}
