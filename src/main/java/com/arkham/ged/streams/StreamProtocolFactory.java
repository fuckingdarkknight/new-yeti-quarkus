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
package com.arkham.ged.streams;

import java.sql.Connection;

import com.arkham.ged.message.GedMessages;

/**
 * Adapter factory for {@link GED_PROTOCOL}
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 13 nov. 2017
 */
public final class StreamProtocolFactory {
    private StreamProtocolFactory() {
        // Factory class, cannot be instanciated
    }

    /**
     * Create the adapter and return it, if the scheme is not known return <code>null</code>
     *
     * @param filepath The filepath that could contains a scheme
     * @param con Optional database connection, depends if needed by {@link StreamProtocolAdapter}
     * @param o Optional parameters that depends on type
     * @return The adapter choosed from filepath scheme {@link GED_PROTOCOL}
     * @throws StreamProtocolException Occurs when scheme is invalid or filepath is invalid
     */
    public static StreamProtocolAdapter create(String filepath, Connection con, Object o) throws StreamProtocolException {
        final var scheme = GED_PROTOCOL.getScheme(filepath);
        final var value = getValue(filepath);
        AbstractStreamProtocolAdapter result = switch (scheme) {
            case FILE -> new FileStreamProtocolAdapter(value);
            case URL -> new HttpStreamProtocolAdapter(value);
            default -> null;
        };

        if (result != null) {
            return result;
        }

        throw new StreamProtocolException(GedMessages.Streams.schemeException, new Object[] { filepath });
    }

    /**
     * Create the adapter and return it, if the scheme is not known return <code>null</code>
     *
     * @param filepath The filepath that could contains a scheme
     * @param con Optional database connection, depends if needed by {@link StreamProtocolAdapter}
     * @return The adapter choosed from filepath scheme {@link GED_PROTOCOL}
     * @throws StreamProtocolException Occurs when scheme is invalid or filepath is invalid
     */
    public static StreamProtocolAdapter create(String filepath, Connection con) throws StreamProtocolException {
        return create(filepath, con, null);
    }

    /**
     * @param filepath The filepath with scheme
     * @return The real file path
     */
    private static String getValue(String filepath) {
        final var i = filepath.indexOf(':');
        if (i > 0) {
            return filepath.substring(i + 1);
        }

        return filepath;
    }
}
