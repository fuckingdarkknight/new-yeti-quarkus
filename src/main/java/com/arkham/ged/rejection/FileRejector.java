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
package com.arkham.ged.rejection;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;

import com.arkham.ged.filekey.FileKey;
import com.arkham.ged.properties.PropertiesAdapter;

/**
 * Write a .rejected file in case of exception or error
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public class FileRejector extends AbstractRejector {
    @Override
    public void reject(File file, FileKey fk, Throwable t, String message, Connection con, PropertiesAdapter pa) {
        // If the file could not be decoded, the fk may should be null and we cannot trace anything
        if (fk != null && file != null) {
            final var rej = Paths.get(file.getParentFile().getAbsolutePath(), file.getName() + ".rejected");

            try (var os = Files.newOutputStream(rej)) {
                var s = "";
                if (message != null) {
                    s = "error=" + message + "\r\n";
                }
                if (t != null) {
                    s = s + "throwable=" + t;
                }

                fk.getProperties().store(os, s);
            } catch (final IOException e) {
                LOGGER.error("reject() : file={} cannot be written because of {}", file.getName(), e);
            }
        } else {
            LOGGER.error("reject() : could not reject anything because file or filekey is null");
        }
    }
}
