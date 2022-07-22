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
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 28 mai 2015
 */
public class EventByNameFileKeyProvider extends FileKeyProvider {
    @Override
    public FileKey getKey(File file, Connection con, PropertiesAdapter pa, List<OptionalParameterType> opt) throws FileKeyProviderException {
        // The file is ever renamed, we have to get its name without extension in order to decode it
        // Eg. : PRO1023AG333.jpg_tmp => filename=PRO1023AG333.jpg
        // Sample of use : ACDE121_666.pdf : achvte+typeve+codsoc+_+numeve
        var filename = file.getName();

        // No others informations than primary key, so we use the "basic" constructor.
        // Hack : the file name is ended by "_tmp" so it's a rename file scanner that is used. The fk need the real and base file name !
        if (filename.endsWith(PROCEXT)) {
            filename = GedUtil.removeFileExtension(filename);
        }

        final var dotPos = filename.lastIndexOf('.');
        if (dotPos != -1) {
            try {
                final var fn = filename.substring(0, dotPos);
                if (fn.length() < 7) {
                    return null;
                }

                final var achvte = fn.substring(0, 1);
                final var typeve = fn.substring(1, 4);
                final var underscore = fn.indexOf('_');
                if (underscore == -1 || underscore < 4) {
                    return null;
                }
                final var codsoc = fn.substring(4, underscore);
                final var numeve = fn.substring(underscore + 1);
                final var keydoc = achvte + typeve + String.format("%07d", Integer.valueOf(numeve));

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

    // public static void main(String[] args) throws FileKeyProviderException {
    // System.out.println(String.format("%07d", Integer.valueOf(1234)) + "#");
    //
    // final EventByNameFileKeyProvider a = new EventByNameFileKeyProvider();
    // final FileKey f = a.getKey(new File("ACDE13_8888.pdf"), null, null);
    // System.out.println(f);
    // }
}
