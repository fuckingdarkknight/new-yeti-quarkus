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
package com.arkham.ged.filekey;

import java.io.File;
import java.sql.Connection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkham.ged.message.GedMessages;
import com.arkham.ged.properties.OptionalParameterType;
import com.arkham.ged.properties.PropertiesAdapter;
import com.arkham.ged.util.GedUtil;

/**
 * File to integrate in GNX, pattern should be : <code>{codsoc}_{uticod}_*.int</code>
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 29 nov. 2017
 */
public class IntegFileKeyProvider extends FileKeyProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(IntegFileKeyProvider.class);

    @Override
    public FileKey getKey(File file, Connection con, PropertiesAdapter pa, List<OptionalParameterType> opt) throws FileKeyProviderException {
        var filename = file.getName();

        // No others informations than primary key, so we use the "basic" constructor.
        // Hack : the file name is ended by ".processing" so it's a rename file scanner that is used. The fk need the real and base file name !
        if (filename.endsWith(PROCEXT)) {
            filename = GedUtil.removeFileExtension(filename);
        }

        final var dotPos = filename.lastIndexOf('.');
        if (dotPos != -1) {
            try {
                final var s = filename.split("_");
                if (s.length > 2) {
                    final var codsoc = s[0];
                    final var uticod = s[1];

                    final var fk = new FileKey(Integer.parseInt(codsoc), "TIE", "UTI" + uticod, filename);
                    fk.setAttribute("utimod", uticod);
                    fk.setAttribute("uticod", uticod);
                    fk.setAttribute("catdoc", "INTEG");

                    return fk;
                }

                LOGGER.info("getKey() : cannot parse filename {}", filename);
            } catch (final IllegalArgumentException e) {
                throw new FileKeyProviderException(e, GedMessages.Scanner.decodingError);
            }
        }

        return null;
    }

    @Override
    public boolean isRefFile() {
        return false;
    }
}
