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
package com.arkham.ged.executor;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkham.ged.blob.DocumentLinkBean;
import com.arkham.ged.filekey.FileKey;
import com.arkham.ged.properties.InputScanFileDef;
import com.arkham.ged.properties.PropertiesAdapter;

/**
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 * @see RenameFileExecutor
 */
public class LockFileExecutor extends ImportFileExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(LockFileExecutor.class);

    private List<File> mProcessedFiles;

    /**
     * Constructor LockFileExecutor
     *
     * @param connection
     * @param pa
     * @param sfd
     * @throws ExecutorException
     */
    public LockFileExecutor(Connection connection, PropertiesAdapter pa, InputScanFileDef sfd) throws ExecutorException {
        super(connection, pa, sfd);
    }

    @Override
    protected File beforeProcessFile(File file) throws IOException {
        mProcessedFiles.add(file);

        return file;
    }

    @Override
    protected void beforeExecute(File[] files) {
        if (mProcessedFiles == null) {
            mProcessedFiles = new ArrayList<>();
        } else {
            mProcessedFiles.clear();
        }
    }

    @Override
    protected void afterExecute(File[] files) {
        if (mProcessedFiles != null && !mProcessedFiles.isEmpty()) {
            if (getSFD().isFileDeletion()) {
                for (final File file : mProcessedFiles) {
                    if (file.exists()) {
                        if (file.delete()) {
                            LOGGER.debug("  * {} has been deleted successfully", file.getName());
                        } else {
                            LOGGER.warn("  * {} cannot be deleted", file.getName());
                        }
                    }
                }
            }

            mProcessedFiles.clear();
        }
    }

    @Override
    protected void beforeIntegrate(File file, FileKey fk) throws ExecutorException {
        // Nothing to do
    }

    @Override
    protected void afterIntegrate(File file, List<DocumentLinkBean> bean) throws ExecutorException {
        // Nothing to do
    }
}
