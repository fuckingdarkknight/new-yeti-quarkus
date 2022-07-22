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
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import com.arkham.common.properties.FlatProp;
import com.arkham.ged.message.GedMessages;
import com.arkham.ged.properties.OptionalParameterType;
import com.arkham.ged.properties.PropertiesAdapter;

/**
 * Provides {@link FileKey} from a "flat" .ref file by using {@link FlatProp} reader capabilities
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public class ExpFileKeyProvider extends FlatFileKeyProvider {

    /**
     * Constant that define the where clause to use (excepted for CODSOC=? that is automatically generated)
     */
    public static final String WHERE_CLAUSE_P = "whereClause";

    /**
     * Should be used in order to flag the current MEDIA_BLOB row
     */
    public static final String FLAG_P = "flag";

    public static final String NUMVER_P = "numver";

    @Override
    public FileKey getKey(File file, Connection con, PropertiesAdapter pa, List<OptionalParameterType> opt) throws FileKeyProviderException {
        final FlatProp p;
        try {
            p = getProp(file);
        } catch (final IOException e) {
            throw new FileKeyProviderException(e);
        }

        // If file is empty, it should have ever been processed : not an error case
        if (p.isEmpty()) {
            return null;
        }

        // Define the PK
        final var codsocS = p.getProperty("codsoc");
        var codsoc = -1;
        try {
            codsoc = Integer.parseInt(codsocS); // the entity
        } catch (final NumberFormatException e) {
            throw new FileKeyProviderException(e, GedMessages.Scanner.codsocInvalid);
        }

        if (codsoc < 0) {
            throw new FileKeyProviderException(GedMessages.Scanner.keyMalformed);
        }

        final var key = p.getProperty("nomcle"); // the key (match to NOMCLE)
        final var typtie = p.getProperty("typtie"); // the tier type (PRO, TIE, EVE ...)
        final var filename = p.getProperty("filename"); // the filename to integrate

        // Others fields
        final var desc = p.getProperty("lib256");
        final var typmed = p.getProperty("typmed");
        final var codlan = p.getProperty("codlan");
        final var uticod = p.getProperty("uticod");
        final var whereClause = p.getProperty(WHERE_CLAUSE_P);
        final var flag = p.getProperty(FLAG_P);
        final var numver = p.getProperty(NUMVER_P);

        final var result = new FileKey(codsoc, typtie, key, desc, typmed, codlan, uticod, filename, null, p);
        result.setAttribute(WHERE_CLAUSE_P, whereClause);
        result.setAttribute(FLAG_P, flag);
        result.setAttribute(NUMVER_P, numver);

        return result;
    }
}
