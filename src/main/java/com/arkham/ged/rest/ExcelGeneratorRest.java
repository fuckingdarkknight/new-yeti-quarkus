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

import com.arkham.ged.util.GedUtil;
import com.arkham.ged.xlsgen.ExcelGenerator;
import com.arkham.ged.xlsgen.XlsgenException;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.vertx.core.http.HttpServerRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.jboss.resteasy.reactive.NoCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;

/**
 * REST service for YAML => Excel transformation
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 27 fév 2020
 */
@RegisterForReflection
@ApplicationScoped
@Path("")
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
    @Path("file")
    @POST
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Consumes({ CT_YAML_1, CT_YAML_2, CT_YAML_3, CT_YAML_4, MediaType.APPLICATION_JSON })
    @NoCache
    @Counted(name = "performedChecks", description = "How many primality checks have been performed.")
    @Timed(name = "checksTimer", description = "A measure of how long it takes to perform the primality test.", unit = MetricUnits.MILLISECONDS)
    public byte[] generate(@Context final HttpServerRequest req, final String message) {
        try {
            // Assume UTF-8 if not specified
            final var cs = parseCharset(req.getHeader("Content-Type"));

            final var temp = Files.createTempFile("yeti_", ".yaml");
            try (Writer w = new OutputStreamWriter(Files.newOutputStream(temp), cs)) {
                w.write(message);
            }

            final var eg = new ExcelGenerator(temp, cs);
            final var result = eg.generate(null);

            try (var is = Files.newInputStream(result.toPath()); var baos = new ByteArrayOutputStream((int) result.length())) {
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

    @Path("direct")
    @POST
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Consumes({ CT_YAML_1, CT_YAML_2, CT_YAML_3, CT_YAML_4, MediaType.APPLICATION_JSON })
    @NoCache
    @Counted(name = "performedChecks", description = "How many primality checks have been performed.")
    @Timed(name = "checksTimer", description = "A measure of how long it takes to perform the primality test.", unit = MetricUnits.MILLISECONDS)
    public byte[] generateDirect(final String message) {
        try {
            final var eg = new ExcelGenerator(message);
            final var result = eg.generate(null);

            try (var is = Files.newInputStream(result.toPath()); var baos = new ByteArrayOutputStream((int) result.length())) {
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

    @Path("stream")
    @POST
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Consumes({ CT_YAML_1, CT_YAML_2, CT_YAML_3, CT_YAML_4, MediaType.APPLICATION_JSON })
    @NoCache
    @Counted(name = "performedChecks", description = "How many primality checks have been performed.")
    @Timed(name = "checksTimer", description = "A measure of how long it takes to perform the primality test.", unit = MetricUnits.MILLISECONDS)
    @SuppressWarnings("static-method")
    public InputStream generateDirectStream(final String message) {
        try {
            final var eg = new ExcelGenerator(message);
            final var result = eg.generate(null);

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
    protected void deleteFile(final File file) {
        if (!file.delete()) {
            LOGGER.warn("deleteFile() : cannot delete file {}", file);
        }
    }

    private static String parseCharset(final String ct) {
        if (ct != null && ct.indexOf('=') > 0) {
            return ct.substring(ct.indexOf('=')).trim();
        }

        return "UTF-8";
    }
}
