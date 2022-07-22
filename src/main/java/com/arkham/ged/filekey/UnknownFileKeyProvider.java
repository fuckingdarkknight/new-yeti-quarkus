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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.arkham.ged.message.GedMessages;
import com.arkham.ged.properties.OptionalParameterType;
import com.arkham.ged.properties.PropertiesAdapter;
import com.arkham.ged.util.GedUtil;

/**
 * This class should be used when integrating files that don't contain the mandatory key. Default values are set.
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public class UnknownFileKeyProvider extends FileKeyProvider {
    @Override
    public FileKey getKey(File file, Connection con, PropertiesAdapter pa, List<OptionalParameterType> optl) throws FileKeyProviderException {
        String codsocp = null;
        String typtiep = null;
        String nomclep = null; // "nomcle" prefix
        String sequence = null;
        final var localOptl = getParams();
        if (localOptl != null) {
            for (final OptionalParameterType opt : localOptl) {
                final var name = opt.getName();
                switch (name) {
                    case "codsoc":
                        codsocp = opt.getValue();
                        break;
                    case "typtie":
                        typtiep = opt.getValue();
                        break;
                    case "nomcle":
                        nomclep = opt.getValue();
                        break;
                    case "sequence":
                        sequence = opt.getValue();
                        break;
                    default:
                        break;
                }
            }
        }

        // Settings are bad !!!
        if (codsocp == null || typtiep == null || nomclep == null) {
            throw new FileKeyProviderException(GedMessages.Scanner.invalidSettings);
        }

        final var codsoc = Integer.parseInt(codsocp);

        // Determine NOMCLE by prefix and oracle sequence
        if (sequence == null) { // NOSONAR
            sequence = "MEDIA_BLOB_SEQ";
        }

        final var index = getNextVal(con, sequence);
        if (index == -1) {
            throw new FileKeyProviderException(GedMessages.Scanner.invalidSequence);
        }
        final var nomcle = nomclep + index;

        var filename = file.getName();
        if (filename.endsWith(PROCEXT)) {
            filename = GedUtil.removeFileExtension(filename);
        }

        return new FileKey(codsoc, typtiep, nomcle, filename);
    }

    private static int getNextVal(Connection con, String sequence) throws FileKeyProviderException {
        try (var ps = con.prepareStatement("SELECT " + sequence + ".NEXTVAL FROM DUAL")) {
            try (var rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (final SQLException e) {
            throw new FileKeyProviderException(e);
        }
    }

    @Override
    public boolean isRefFile() {
        return false;
    }
}
