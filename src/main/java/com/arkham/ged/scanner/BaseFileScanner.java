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
import java.io.FileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkham.common.timer.Chrono;
import com.arkham.common.timer.common.ChronoDef.UNIT;
import com.arkham.ged.filefilter.FilenameExtensionFilter;
import com.arkham.ged.properties.ScanFileDef;
import com.arkham.ged.timer.LoggerMDC;
import com.arkham.ged.timer.MDC_KEY;

/**
 * A file scanner based on {@link FileFilter} that only gets ".ref" files. It's an example, and should never be used because Ant capabilities are much more convenient.
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public class BaseFileScanner extends AbstractFileScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseFileScanner.class);

    private final FileFilter mFileFilter;

    /**
     * Constructor BaseFileScanner
     *
     * @param sfd The scan file settings
     */
    public BaseFileScanner(ScanFileDef sfd) {
        super(sfd);

        mFileFilter = new FilenameExtensionFilter("ref");
    }

    @Override
    public File[] scan() {
        final var basedir = getSFD().getDir();

        File[] files = null;
        final var directory = new File(basedir);
        // Directory does not exist : create it
        if (!directory.exists()) {
            LOGGER.warn(DIR_DNE_CREATE, basedir);

            final var res = directory.mkdirs();
            if (res) {
                LOGGER.info(DIR_CREATED, basedir);
            } else {
                LOGGER.info(DIR_NOT_CREATED, basedir);
            }
        }

        if (!directory.exists() || !directory.isDirectory()) {
            LOGGER.warn(DIR_DNE, basedir);
        } else {
            final var ff = getFileFilter();

            final var chrono = Chrono.getChrono();
            try {
                chrono.start();

                files = directory.listFiles(ff);
            } finally {
                chrono.stop();

                LoggerMDC.putMDC(MDC_KEY.SCANNING, chrono.getElapsed(UNIT.MS));
            }
        }

        return files;
    }

    protected FileFilter getFileFilter() {
        return mFileFilter;
    }
}
