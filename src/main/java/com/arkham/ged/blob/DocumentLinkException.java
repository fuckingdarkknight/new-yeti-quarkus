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
package com.arkham.ged.blob;

import com.arkham.ged.GedException;
import com.arkham.ged.message.GedMessage;

/**
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 12 févr. 2015
 */
public class DocumentLinkException extends GedException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor MediaBlobException
     *
     * @param message
     * @param params
     */
    public DocumentLinkException(final GedMessage message, final Object... params) {
        super(message, params);
    }

    /**
     * Constructor DocumentLinkException
     *
     * @param cause
     * @param message
     * @param params
     */
    public DocumentLinkException(final Throwable cause, final GedMessage message, final Object... params) {
        super(cause, message, params);
    }

    /**
     * Constructor DocumentLinkException
     *
     * @param cause
     */
    public DocumentLinkException(final Throwable cause) {
        super(cause);
    }
}
