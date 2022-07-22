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
package com.arkham.ged.scanner;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkham.common.timer.Chrono;
import com.arkham.common.timer.common.ChronoDef.UNIT;
import com.arkham.ged.properties.GedProperties;
import com.arkham.ged.properties.OptionalParameterType;
import com.arkham.ged.properties.ScanFileDef;
import com.arkham.ged.timer.LoggerMDC;
import com.arkham.ged.timer.MDC_KEY;
import com.arkham.ged.util.GedUtil;

/**
 * A Java 8 based scanner, it uses embedded Regexp computed from "includes" to match the filenames.<br/>
 * By default the directory depth is 10.
 *
 * @author Alfred / Arkham asylum
 * @version 1.0
 * @since 29 juil. 2017
 */
public class BasicFileScanner extends AbstractFileScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicFileScanner.class);

    /**
     * Default directories depth for scanning
     */
    private static final int DEFAULT_DEPTH = 4;

    /**
     * Threshold to consider file to be considerer in ms
     */
    private static final int THRESHOLD_LASTMODIFIED = 250;

    private int mDepth = DEFAULT_DEPTH;

    private boolean mConsiderZeroLengthFile;

    /**
     * Constructor BasicFileScanner
     *
     * @param sfd The scan definition
     */
    public BasicFileScanner(final ScanFileDef sfd) {
        super(sfd);

        final var depth = GedProperties.getOptionalParameters(sfd.getParam(), "depth");
        if (depth != null) {
            mDepth = GedUtil.getInt(depth.getValue(), DEFAULT_DEPTH);
        }

        final var zeroLength = GedProperties.getOptionalParameters(sfd.getParam(), "empty");
        if (zeroLength != null) {
            mConsiderZeroLengthFile = GedUtil.getBoolean(zeroLength.getValue(), false);
        }
    }

    static Pattern[] buildPatterns(final String[] patterns) {
        final List<Pattern> result = new ArrayList<>(patterns.length);
        for (final String ext : patterns) {
            final var pattern = Pattern.compile(buildRegexp(ext), Pattern.CASE_INSENSITIVE);
            result.add(pattern);
        }

        return result.toArray(new Pattern[result.size()]);
    }

    static String buildRegexp(final String s) {
        final var result = new StringBuilder();

        s.chars().forEach((final var c) -> {
            if (c == '*') {
                // All chars
                result.append('.');
                result.append('*');
            } else if (c == '.') {
                // Escape dot
                result.append('\\');
                result.append('.');
            } else if (c == '_') {
                // Escape underscore
                result.append('\\');
                result.append('_');
            } else if (c == '?') {
                // Unique char wildcard
                result.append('_');
            } else {
                // Need to cast, else c is appended as an int
                result.append((char) c);
            }
        });

        return result.toString();
    }

    static boolean isIncluded(final String filename, final Pattern[] patterns) {
        for (final Pattern pattern : patterns) {
            final var matcher = pattern.matcher(filename);
            if (matcher.matches()) {
                return true;
            }
        }

        return false;
    }

    static boolean isIncluded(final Path path, final Pattern[] patterns) {
        return path != null && isIncluded(path.getFileName().toString(), patterns);
    }

    @Override
    protected File[] scan() {
        final var chrono = Chrono.getChrono();
        try {
            chrono.start();

            return runScan();
        } finally {
            chrono.stop();

            LoggerMDC.putMDC(MDC_KEY.SCANNING, chrono.getElapsed(UNIT.MS));
        }
    }

    private File[] runScan() {
        final var startpath = Paths.get(getSFD().getDir());
        LOGGER.trace("scan() : start scanning for path={}", startpath.toAbsolutePath());

        final var included = GedUtil.splitValues(getSFD().getIncludes());
        final var excluded = GedUtil.splitValues(getSFD().getExcludes());
        final var includedPatterns = buildPatterns(included);
        final var excludedPatterns = buildPatterns(excluded);

        if (LOGGER.isDebugEnabled()) {
            Arrays.stream(includedPatterns).forEach(pat -> LOGGER.trace("runScan() : included patterns={}", pat));
            Arrays.stream(excludedPatterns).forEach(pat -> LOGGER.trace("runScan() : excluded patterns={}", pat));
        }

        final var fromTime = System.currentTimeMillis() + THRESHOLD_LASTMODIFIED;

        final List<File> result = new ArrayList<>();
        try (final var stream = Files.walk(startpath, mDepth)) {
            stream.filter(path -> isIncluded(path, includedPatterns) && !isIncluded(path, excludedPatterns)).sorted().forEach((final Path path) -> {
                // Don't consider empty files for this scanner, except if settings accept it
                final var f = path.toFile();

                if ((f.length() > 0 || mConsiderZeroLengthFile) && f.lastModified() < fromTime) {
                    result.add(f);
                }
            });
        } catch (@SuppressWarnings("unused") final NoSuchFileException | UncheckedIOException e) { // NOSONAR
            // Don't care about it, may occur when several scanner scan the same directory at the same time and a file
            // is ever consumed by another job
        } catch (final IOException e) {
            LOGGER.trace("runScan()", e);
        }

        return result.toArray(new File[result.size()]);
    }
}
