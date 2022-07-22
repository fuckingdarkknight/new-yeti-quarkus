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

import com.arkham.ged.message.GedMessages;
import com.arkham.ged.properties.OptionalParameterType;
import com.arkham.ged.properties.PropertiesAdapter;
import com.arkham.ged.util.GedUtil;

/**
 * Provides {@link FileKey} from the name of the file. It should respect this convention : ACHVTE+TYPEVE+CODSOC_NUMEVE.*
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public class ByNameFileKeyProvider extends FileKeyProvider {
    @Override
    public FileKey getKey(File file, Connection con, PropertiesAdapter pa, List<OptionalParameterType> opt) throws FileKeyProviderException {
        // The file is ever renamed, we have to get its name without extension in order to decode it
        // Eg. : PRO1023AG333.jpg_tmp => filename=PRO1023AG333.jpg
        var filename = file.getName();

        // No others informations than primary key, so we use the "basic" constructor.
        // Hack : the file name is ended by the value of PROCEXT so it's a renamed file scanner that is used.
        // The fk need the real and base file name !
        if (filename.endsWith(PROCEXT)) {
            filename = GedUtil.removeFileExtension(filename);
        }

        final var dotPos = filename.lastIndexOf('.');
        if (dotPos != -1) {
            try {
                final var fn = filename.substring(0, dotPos);
                if (fn.length() < 3) {
                    return null;
                }
                final var type = fn.substring(0, 3);
                final var dim = DirectIntegMode.valueOf(type);
                final var keys = GedUtil.splitKey(fn, dim.getSplit());

                final var typtie = keys[0];
                final var codsoc = Integer.parseInt(keys[1]);
                // La clé est la concaténation du reste des valeurs disponibles
                final var nomcle = new StringBuilder();
                for (var i = 2; i < keys.length; i++) {
                    final var key = keys[i];
                    if (key != null) {
                        nomcle.append(key);
                    }
                }

                return new FileKey(codsoc, typtie, nomcle.toString(), filename);
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

    /**
     * Enumeration used to define the files splitting
     *
     * @author arocher / Arkham asylum
     * @version 1.0
     * @since 28 mai 2015
     */
    enum DirectIntegMode {
        /* @formatter:off */
        PRO("PRO", new int[] { 3, 4, 16 }),
        TIE("TIE", new int[] { 3, 4, 3, 12 }),
        EVE("EVE", new int[] { 3, 4, 1, 3, 9 }),
        P_E("P_E", new int[] { 3, 4, 12 });
        /* @formatter:on */

        private final String mType;
        private final int[] mSplit;

        DirectIntegMode(String type, int[] split) {
            mType = type;
            mSplit = split.clone();
        }

        String getType() {
            return mType;
        }

        int[] getSplit() {
            return mSplit;
        }
    }
}
