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
 * Provides {@link FileKey} from the name of the file. It should respect this convention : CODSOC_ACHVTE+TYPEVE_NUMEVE.*
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 28 mai 2015
 */
public class EditiqueEventByNameFileKeyProvider extends FileKeyProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(EditiqueEventByNameFileKeyProvider.class);

    @Override
    public FileKey getKey(File file, Connection con, PropertiesAdapter pa, List<OptionalParameterType> opt) throws FileKeyProviderException {
        // The file is ever renamed, we have to get its name without extension in order to decode it
        // Eg. : PRO1023AG333.jpg_tmp => filename=PRO1023AG333.jpg
        // Sample of use : 121_ACDE_666.pdf : codsoc_achvte+typeve_numeve
        var filename = file.getName();

        // No others informations than primary key, so we use the "basic" constructor.
        // Hack : the file name is ended by ".processing" so it's a rename file scanner that is used. The fk need the real and base file name !
        if (filename.endsWith(PROCEXT)) {
            filename = GedUtil.removeFileExtension(filename);
        }

        final var dotPos = filename.lastIndexOf('.');
        if (dotPos != -1) {
            try {
                final var fn = filename.substring(0, dotPos);
                if (fn.length() < 8) {
                    LOGGER.info("getKey() : cannot decode file {} with {} - length issue", file.getName(), getClass().getSimpleName());
                    return null;
                }

                final var items = fn.split("_");
                if (items.length < 3) {
                    LOGGER.info("getKey() : cannot decode file {} with {} - underscore issue", file.getName(), getClass().getSimpleName());
                    return null;
                }

                final var codsoc = items[0];
                final var achvtetypeve = items[1];
                var numeve = items[2];

                if (achvtetypeve.length() < 4) {
                    LOGGER.info("getKey() : cannot decode file {} with {} - ACHVTE+TYPEVE issue", file.getName(), getClass().getSimpleName());
                    return null;
                }

                final var achvte = achvtetypeve.substring(0, 1);
                final var typeve = achvtetypeve.substring(1, 4);

                if (numeve.indexOf('-') > -1) {
                    numeve = numeve.substring(0, numeve.indexOf('-'));
                }
                final var keydoc = achvte + typeve + String.format("%09d", Integer.valueOf(numeve));

                return new FileKey(Integer.parseInt(codsoc), "EVE", keydoc, filename);
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
