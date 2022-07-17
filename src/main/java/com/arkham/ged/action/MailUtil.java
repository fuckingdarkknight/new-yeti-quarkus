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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkham.ged.streams.StreamProtocolAdapter;
import com.arkham.ged.streams.StreamProtocolException;
import com.arkham.ged.streams.StreamProtocolFactory;
import com.arkham.ged.util.GedUtil;

/**
 * Utility class with convenient methods for mailing
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 15 nov. 2017
 */
public class MailUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(MailUtil.class);

	private MailUtil() {
		// Utility class
	}

	/**
	 * Gets the value if not <code>null</code> and not empty, else return <code>null</code>
	 *
	 * @param value The value to test
	 * @return null if the value is null or empty trimmed string
	 */
	public static String nullValue(String value) {
		if (value != null) {
			final String s = value.trim();
			if (!"".equals(s)) {
				return s;
			}
		}

		return null;
	}

	/**
	 * Create a zip file with entries defined by parameter <code>files</code><br/>
	 * {@link StreamProtocolFactory} is used to get the right stream depending on parameter scheme.
	 *
	 * @param files A scheme:filepath / value:name Map, value is optionnal
	 * @param zipName The zip file name without extension
	 * @param con The database connection
	 * @return The zip file created on the temporary directory
	 * @throws ActionException If the zip cannot be created for some reasons
	 */
	public static File createZip(Map<String, String> files, String zipName, Connection con) throws ActionException {
		try {
			final Path outputPath = Files.createTempFile(zipName + "-", ".zip");

			final File outputFile = outputPath.toFile();

			try (ZipOutputStream os = new ZipOutputStream(Files.newOutputStream(outputFile.toPath()))) {
				os.setLevel(Deflater.BEST_COMPRESSION);
				os.setComment("Compressed by Arkham-Ged");

				int i = 0;
				for (final Entry<String, String> entry : files.entrySet()) {
					final String key = entry.getKey();
					if (MailUtil.nullValue(key) == null) {
						LOGGER.error("createZip() : the filename is not filled, cannot add attachment");
					} else {
						addZipEntry(key, con, entry, os, i);

						i++;
					}
				}

				os.finish();
			}

			return outputFile;
		} catch (final IOException e) {
			throw new ActionException(e);
		}
	}

	private static void addZipEntry(String key, Connection con, Entry<String, String> entry, ZipOutputStream os, int index) throws IOException {
		try (final StreamProtocolAdapter spa = StreamProtocolFactory.create(key.trim(), con)) {
			// First get the stream to trigger additionals step
			@SuppressWarnings("resource")
			final InputStream is = spa.getStream();
			if (is != null) {
				String name = entry.getValue();
				// Name is optionnal, if not set we use the stream name
				if (nullValue(name) == null) {
					name = spa.getStreamName();
				}

				final ZipEntry zipEntry = new ZipEntry(index + "-" + name);
				zipEntry.setComment("#" + index);
				os.putNextEntry(zipEntry);

				GedUtil.copyIs2Os(is, os, 8192);
				os.flush();
			} else {
				LOGGER.error("createZip() : resource cannot be read={}", key);
			}
		} catch (final StreamProtocolException e) {
			LOGGER.error("createZip()", e);
		}
	}
}
