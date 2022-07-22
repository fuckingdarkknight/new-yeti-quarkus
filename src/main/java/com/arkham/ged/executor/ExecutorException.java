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
package com.arkham.ged.executor;

import com.arkham.ged.GedException;
import com.arkham.ged.message.GedMessage;

/**
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public class ExecutorException extends GedException {
    private static final long serialVersionUID = 1L;

    /**
     * {@link ExecutorException} constructor.
     *
     * @param message Message
     * @param params Parameters array
     */
    public ExecutorException(final GedMessage message, final Object... params) {
        super(message, params);
    }

    /**
     * {@link ExecutorException} constructor.
     *
     * @param cause Root cause
     * @param message Message
     * @param params Parameters array
     */
    public ExecutorException(final Throwable cause, final GedMessage message, final Object... params) {
        super(cause, message, params);
    }

    /**
     * {@link ExecutorException} constructor.
     *
     * @param cause Root cause
     */
    public ExecutorException(final Throwable cause) {
        super(cause);
    }

    @Override
    public String toString() {
        if (getCause() != null) {
            return getMessage() + "\r\n" + getCause();
        }

        return getMessage();
    }
}
