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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkham.ged.blob.DocumentLinkBean;
import com.arkham.ged.filekey.FileKey;
import com.arkham.ged.filekey.FileKeyProvider;
import com.arkham.ged.properties.InputScanFileDef;
import com.arkham.ged.properties.PropertiesAdapter;

/**
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 * @see LockFileExecutor
 */
public class RenameFileExecutor extends ImportFileExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(RenameFileExecutor.class);

    /**
     * Constructor RenameFileExecutor
     *
     * @param connection Database connection
     * @param pa The properties adapter
     * @param sfd Scan definition
     * @throws ExecutorException Generic exception
     */
    public RenameFileExecutor(final Connection connection, final PropertiesAdapter pa, final InputScanFileDef sfd) throws ExecutorException {
        super(connection, pa, sfd);
    }

    @Override
    protected File beforeProcessFile(final File file) throws IOException {
        try {
            // On renomme le fichier ... si ça fonctionne, c'est qu'on peut traiter. Dans le cas contraire, on a un lock
            // posé par l'OS, ce n'est pas un cas d'erreur : le fichier est en train d'être écrit par un autre process
            // ou alors une autre JVM est en train de le lire aussi.
            final var renamed = new File(file.getParentFile(), file.getName() + FileKeyProvider.PROCEXT);
            if (file.renameTo(renamed)) {
                LOGGER.info("  * file successfully renamed={}", renamed.getCanonicalPath());
                return renamed;
            }

            LOGGER.info("  * file cannot be renamed (usually locked by another process)={}", file.getCanonicalPath());
        } catch (@SuppressWarnings("unused") final SecurityException e) { // NOSONAR
            // Peut se produire si on ne peut pas renommer le fichier pour des problèmes de droits (RW ...).
            LOGGER.error("beforeProcessFile(File) : file={} unable to rename because of RW rights on this file", file.getName());
        }

        return null;
    }

    @Override
    protected void beforeIntegrate(final File file, final FileKey fk) throws ExecutorException {
        // Nothing to do
    }

    @Override
    protected void afterIntegrate(final File file, final List<DocumentLinkBean> bean) throws ExecutorException {
        // Nothing to do
    }
}
