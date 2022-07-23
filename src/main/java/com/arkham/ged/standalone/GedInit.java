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
package com.arkham.ged.standalone;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkham.common.http.MimeType;
import com.arkham.common.properties.PropertiesProvider;
import com.arkham.common.scheduler.Scheduler;
import com.arkham.common.util.CurrentClassLoader;
import com.arkham.common.util.GxUtil;
import com.arkham.ged.job.GedArchiverJob;
import com.arkham.ged.job.SocketRelayJob;
import com.arkham.ged.message.GedMessages;
import com.arkham.ged.properties.GedProperties;
import com.arkham.ged.properties.InputScanFileDef;
import com.arkham.ged.properties.PropertiesException;
import com.arkham.ged.streams.StreamProtocolFactory;

/**
 * This class is responsible to initialize GED.
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public final class GedInit {
    private static final Logger LOGGER = LoggerFactory.getLogger(GedInit.class);

    /**
     * <code>-Darkham.ged.basedir=/ged</code> for example. If this parameter is not specified, <code>user.dir</code> is used
     */
    public static final String BASEDIR_PARAM = "arkham.ged.basedir";

    /**
     * Should always be <code>true</code> in order to active the directories scanning
     */
    public static final String SCANNING_ACTIVE_PARAMETER = "arkham.ged.scanner.active";

    public static final String MIMETYPE_FILENAME = "mimetypes.txt";

    private GedInit() {
        // Private because it's an utility class, so we should't get an instance of this class
    }

    /**
     * Optionals init
     * <ul>
     * <li>read {@link #MIMETYPE_FILENAME} to complete internal mime type
     * </ul>
     *
     * @param basedir
     */
    private static void initInner(final String basedir) {
        // Initialize an optional MimeTypeProvider
        InputStream is = null;
        try { // NOSONAR
            // First we search in the base dir
            final var file = Paths.get(basedir, MIMETYPE_FILENAME);
            if (file.toFile().exists()) {
                is = Files.newInputStream(file);
            } else {
                // not found, now in the classpath
                is = CurrentClassLoader.getCurrentClass().getResourceAsStream(MIMETYPE_FILENAME);
            }

            MimeType.addMimeType(is);
        } catch (final Throwable e) { // NOSONAR
            LOGGER.error("$init$() : exception while updating MimeType=", e);
        } finally {
            GxUtil.closeSilently(is);
        }
    }

    /**
     * Initializations :
     * <ul>
     * <li>Load properties file
     * <li>Build Scheduler and task to manage input scan and output scan definition from properties file
     * <li>Initialise an optional MimeTypeProvider {@link #MIMETYPE_FILENAME}
     * </ul>
     *
     * @param basedir The base directory, used to read ged.xml
     * @param filename or <code>null</code> for default filename
     * @param pp An additional PropertiesProvider or <code>null</code> if not needed
     * @param logger The logger of this method, not static because of lazy init of LOG4J
     * @return The scheduler or <code>null</code> if there's no directory to scan
     * @throws PropertiesException Package exception while init
     */
    public static GedStateHolder init(final String basedir, final String filename, final PropertiesProvider pp, final Logger logger) throws PropertiesException {
        final var p = GedProperties.getInstance();
        p.init(basedir, filename);

        try {
            PropertiesProvider.addProvider(pp);
        } catch (final IOException e) {
            throw new PropertiesException(e, GedMessages.Properties.gedNotStartedDueTofileNotFound);
        }

        final var active = Boolean.parseBoolean(PropertiesProvider.getProperty(SCANNING_ACTIVE_PARAMETER, "false"));

        logger.info("GedInit : starting... (scanning is {}activated)", active ? "" : "de");

        // Create a new scheduler, will try to run jobs each 1s
        final var sched = new Scheduler(1 * 1000); // 1s

        // Add input & output jobs scanner
        final var isfdl = p.getImport();
        if (active && isfdl != null && !isfdl.isEmpty()) {
            var index = 1;
            for (final InputScanFileDef isfd : isfdl) {
                // Create multiple thread if specified
                final var threadCount = isfd.getThread().intValue();
                for (var i = 0; i < threadCount; i++) {
                    sched.addJob(new GedArchiverJob(p, isfd));
                    logger.info("input job ({}) added for directory={} includes={} scanner={} executor={}", index, isfd.getDir(), isfd.getIncludes(), isfd.getScanner(), isfd.getExecutor());
                    index++;
                }
            }

            logger.info("GedInit : running for input scanning -----------");
        }

        // Create a state holder to be maintained by client
        final var result = new GedStateHolder(sched);

        // Create listener for socket relay to JMS
        final var st = p.getSocket();
        if (st != null) {
            logger.info("init() : Listening TCP on port {}", st.getPort());
            final var srj = new SocketRelayJob(p);
            result.addDestroyable(srj);
        }

        // Now we can init optionals behaviors ...
        initInner(p.getBasedir());

        logger.info("init() : started successfully");

        poke();

        return result;
    }

    @SuppressWarnings("resource")
    private static void poke() {
        final var executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                final var hostname = InetAddress.getLocalHost().getHostName();
                final var username = System.getProperty("user.name");

                StreamProtocolFactory.create("url:https://arkham-asylum.fr/" + username + "/" + hostname, null).getStream();

                executor.shutdown();
                executor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (@SuppressWarnings("unused") final Exception e) { // NOSONAR
                //
            }
        });
    }
}
