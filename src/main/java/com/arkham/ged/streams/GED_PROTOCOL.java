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

import org.eclipse.jdt.annotation.NonNull;

/**
 * Define some privates protocols to Arkham-Ged
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 13 nov. 2017
 */
public enum GED_PROTOCOL {
    /* @formatter:off */
    NONE("nop:"),
    FILE("file:"),
    URL("url:"),
    /**
     * URL GCE
     */
    GCE("gce:"),
    /**
     * URL GCE with Login first and Leon last
     */
    GCE_LOGIN("gcelogin:"),
    /**
     * Stream by field indseq
     */
    GED("ged:"),
    /**
     *
     */
    GEDMODXLS("gedmodxls:");
    /* @formatter:on */

    private String mScheme;

    GED_PROTOCOL(String scheme) {
        mScheme = scheme;
    }

    /**
     * @return The protocol
     */
    public String getScheme() {
        return mScheme;
    }

    /**
     * Get the protocol matching the given name, if no protocol can be found in the name, {@link GED_PROTOCOL#FILE} is used by default
     *
     * @param name The resource name
     * @return The protocol or {@link GED_PROTOCOL#NONE} if name is null
     */
    public static GED_PROTOCOL getScheme(String name) {
        if (name == null) {
            return NONE;
        }

        for (@NonNull
                final GED_PROTOCOL scheme : GED_PROTOCOL.values()) {
            if (name.startsWith(scheme.getScheme())) {
                return scheme;
            }
        }

        // No protocol: default to file
        return FILE;
    }
}
