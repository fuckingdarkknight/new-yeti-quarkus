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
package com.arkham.ged.rest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkham.ged.util.GedUtil;
import com.arkham.ged.xlsgen.ExcelGenerator;
import com.arkham.ged.xlsgen.XlsgenException;

/**
 * REST service for YAML => Excel transformation
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 27 fév 2020
 */
@Path("/excel")
public class ExcelGeneratorRest {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelGeneratorRest.class);

	/**
	 * YAML content types are not clearly defined by W3C. Match text
	 */
	protected static final String CT_YAML_1 = "text/yaml";
	protected static final String CT_YAML_2 = "text/x-yaml";
	protected static final String CT_YAML_3 = "application/yaml";
	protected static final String CT_YAML_4 = "application/x-yaml";

	private static final String GENERIC_MESSAGE_EXCEPTION = "generate() : {}";

	/**
	 * REST service for YAML => Excel transformation
	 *
	 * @param req The context request
	 * @param message The JSON message to unserialize
	 * @return The JSON message serialized
	 */
	@POST
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Consumes({ CT_YAML_1, CT_YAML_2, CT_YAML_3, CT_YAML_4 })
	@Path("/file")
	public byte[] generate(@Context HttpServletRequest req, String message) {
		try {
			// Assume UTF-8 if not specified
			String cs = req.getCharacterEncoding();
			if (cs == null) {
				cs = "UTF-8";
			}

			final java.nio.file.Path temp = Files.createTempFile("yeti_", ".yaml");
			try (Writer w = new OutputStreamWriter(Files.newOutputStream(temp), cs)) {
				w.write(message);
			}

			final ExcelGenerator eg = new ExcelGenerator(temp, cs);
			final File result = eg.generate(null);

			try (InputStream is = Files.newInputStream(result.toPath()); ByteArrayOutputStream baos = new ByteArrayOutputStream((int) result.length())) {
				GedUtil.copyIs2Os(is, baos, 8192);

				return baos.toByteArray();
			} finally {
				// Clean temporary files
				deleteFile(result);
				Files.delete(temp);
			}
		} catch (final IOException | XlsgenException e) {
			LOGGER.error(GENERIC_MESSAGE_EXCEPTION, e);
		}

		return new byte[] {};
	}

	@POST
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Consumes({ CT_YAML_1, CT_YAML_2, CT_YAML_3, CT_YAML_4 })
	@Path("/direct")
	public byte[] generateDirect(String message) {
		try {
			final ExcelGenerator eg = new ExcelGenerator(message);
			final File result = eg.generate(null);

			try (InputStream is = Files.newInputStream(result.toPath()); ByteArrayOutputStream baos = new ByteArrayOutputStream((int) result.length())) {
				GedUtil.copyIs2Os(is, baos, 8192);

				return baos.toByteArray();
			} finally {
				// Clean temporary files
				deleteFile(result);
			}
		} catch (final IOException | XlsgenException e) {
			LOGGER.error(GENERIC_MESSAGE_EXCEPTION, e);
		}

		return new byte[] {};
	}

	@SuppressWarnings("static-method")
	@POST
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Consumes({ CT_YAML_1, CT_YAML_2, CT_YAML_3, CT_YAML_4 })
	@Path("/stream")
	public InputStream generateDirectStream(String message) {
		try {
			final ExcelGenerator eg = new ExcelGenerator(message);
			final File result = eg.generate(null);

			return Files.newInputStream(result.toPath());
		} catch (final IOException | XlsgenException e) {
			LOGGER.error(GENERIC_MESSAGE_EXCEPTION, e);
		}

		return null;
	}

	/**
	 * Delete the file and log something if it's not possible for any reason
	 *
	 * @param file The file to delete
	 */
	@SuppressWarnings("static-method")
	protected void deleteFile(File file) {
		if (!file.delete()) {
			LOGGER.warn("deleteFile() : cannot delete file {}", file);
		}
	}
}
