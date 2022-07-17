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
package com.arkham.ged.streams;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * File streamer
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 13 nov. 2017
 */
public class FileStreamProtocolAdapter extends AbstractStreamProtocolAdapter<String, Object> {
	/**
	 * Constructor FileStreamProtocolAdapter
	 *
	 * @param value
	 */
	FileStreamProtocolAdapter(final String value) {
		super(value);
	}

	@SuppressWarnings("resource")
    @Override
	public InputStream getStream() throws StreamProtocolException {
		final Path path = Paths.get(getValue());
		final File file = path.toFile();
		if (file.exists() && file.isFile()) {
			try {
				setStream(Files.newInputStream(path));
				setStreamName(file.getName());

				return super.getStream();
			} catch (final IOException e) {
				throw new StreamProtocolException(e);
			}
		}

		return null;
	}
}
