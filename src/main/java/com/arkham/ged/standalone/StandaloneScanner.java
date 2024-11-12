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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkham.common.pattern.listener.BasicEvent;
import com.arkham.common.pattern.listener.BasicListener;
import com.arkham.common.properties.FlatProp;
import com.arkham.common.properties.PropertiesProvider;
import com.arkham.ged.properties.GedProperties;
import com.arkham.ged.properties.PropertiesAdapter.GLOBAL_EVENTS;
import com.arkham.ged.properties.PropertiesException;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.annotations.CommandLineArguments;

/**
 * Standalone document archiver properties :
 * <ul>
 * <li>log4j file name = log4j.configuration (default : log4j.xml)
 * <li>base dir = arkham.ged.basedir (default : user.dir)
 * <li>conf file name = ged.properties, (default : ged_properties.xml)
 * </ul>
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
@ApplicationScoped
public final class StandaloneScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(StandaloneScanner.class);

    @Inject
    @CommandLineArguments
    String[] mArgs;

    StandaloneScanner() {
        // Standalone launcher
    }

    void onStart(@Observes final StartupEvent ev) {
        LOGGER.info("onStart() : starting YETI");

        try {
            launch(mArgs);
        } catch (PropertiesException | IOException e) {
            LOGGER.error("onStart() : critical while starting YETI {}", e);
        }
    }

    @SuppressWarnings("static-method")
    void onStop(@Observes final ShutdownEvent ev) {
        LOGGER.info("onStop() : stopping YETI");
    }

    static class BasePropertiesProvider extends PropertiesProvider {
        private final Map<String, String> mProp = new HashMap<>();

        BasePropertiesProvider(final File file) {
            final var gfp = new FlatProp();
            try (Reader reader = new FileReader(file)) {
                gfp.load(reader);

                final var keys = gfp.getKeys();
                for (final String k : keys) {
                    mProp.put(k, gfp.getProperty(k));
                }
            } catch (@SuppressWarnings("unused") final IOException e) { // NOSONAR
                // No mind, the files are not mandatory
            }
        }

        @Override
        protected Map<String, String> getProperties() throws IOException {
            return mProp;
        }
    }

    /**
     * @param args
     * @throws PropertiesException
     * @throws IOException
     */
    private static void launch(final String[] args) throws PropertiesException, IOException {
        System.out.println("StandaloneScanner : starting ............"); // NOSONAR
        System.out.println("java.version=" + System.getProperty("java.version")); // NOSONAR
        System.out.println("classpath=" + System.getProperty("java.class.path")); // NOSONAR
        System.out.println("user.dir=" + System.getProperty("user.dir")); // NOSONAR

        // Force scanner activation
        System.setProperty(GedInit.SCANNING_ACTIVE_PARAMETER, "true");

        var basedir = System.getProperty(GedInit.BASEDIR_PARAM);
        if (basedir == null) {
            basedir = System.getProperty("user.dir");
        }

        // The baseDir should exists
        final var dirFile = new File(basedir);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            System.err.println("main() : directory=" + dirFile.getAbsolutePath() + " does not exist"); // NOSONAR
            return;
        }
        final var baseDirPath = dirFile.getAbsolutePath();
        System.out.println("basedir=" + baseDirPath); // NOSONAR

        // Try to read get.properties
        final var baseProp = new File(dirFile, "yeti.properties");
        final var bpp = new BasePropertiesProvider(baseProp);

        // Read ged_properties.xml
        final var fileProperties = System.getProperty("ged.properties", "yeti.xml");
        System.out.println("Properties=" + fileProperties); // NOSONAR

        final var gsh = GedInit.init(dirFile.getCanonicalPath(), fileProperties, bpp, LoggerFactory.getLogger(StandaloneScanner.class));

        Runtime.getRuntime().addShutdownHook(new Thread("ShudownHook") {
            @Override
            public void run() {
                System.out.println("launch() : shutdown in progress ..."); // NOSONAR
            }
        });

        System.out.println("launch() : started"); // NOSONAR
        System.out.println("launch() : scanning ..."); // NOSONAR

        GedProperties.getInstance().registerListener(new BasicListener<String, GLOBAL_EVENTS, PropertiesException>() {
            @Override
            public void eventFired(final BasicEvent<String, GLOBAL_EVENTS> event) throws PropertiesException {
                System.out.println("eventFired() : event=" + event.getType() + " source=" + event.getSource()); // NOSONAR

                if (event.getType() == GLOBAL_EVENTS.SHUTDOWN) {
                    // First stop all the jobs via the scheduler
                    gsh.destroy();
                }
            }

            @Override
            public void destroy() {
                // Nothing to do
            }
        });
    }
}
