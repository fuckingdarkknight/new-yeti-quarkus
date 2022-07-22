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
package com.arkham.ged;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkham.ged.message.GedMessage;

/**
 * The root exception raised by main package
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public class GedException extends Exception {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(GedException.class);

    /**
     * {@link GedException} constructor.
     *
     * @param cause Root cause
     */
    public GedException(final Throwable cause) {
        super(cause);
    }

    /**
     * {@link GedException} constructor.
     *
     * @param message Message
     * @param params Parameters array
     */
    public GedException(final GedMessage message, final Object... params) {
        super(tr(message, params));
        traceException(message);
    }

    /**
     * {@link GedException} constructor.
     *
     * @param cause Root cause
     * @param message Message
     * @param params Parameters array
     */
    public GedException(final Throwable cause, final GedMessage message, final Object... params) {
        super(tr(message, params), cause);
        traceException(message);
    }

    /**
     * Return the i18n message corresponding to the given id.
     *
     * @param message Message to translate
     * @param params Parameters' list
     * @return i18n message
     */
    private static String tr(final GedMessage message, final Object... params) {
        var label = message.getLabel();

        if (params != null && params.length > 0) {
            label = substituteParams(label, params);
        }

        return label;
    }

    /**
     * Susbtitute the jokers contained in the given message with the provided parameters.
     *
     * @param message Message to handle
     * @param params List of parameters
     * @return Substituted message
     */
    private static String substituteParams(final String message, final Object[] params) {
        final var sb = new StringBuilder(message.length() * 3 / 2);
        var paramCount = 0;
        final var joker = '@';

        for (var i = 0; i < message.length(); i++) {
            final var c = message.charAt(i);

            if (c == joker) {
                sb.append(paramCount < params.length ? params[paramCount] : joker);
                paramCount++;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    private void traceException(final GedMessage message) {
        if (!message.isTrace()) {
            return;
        }

        final var sw = new StringWriter();
        final var pw = new PrintWriter(sw);

        printStackTrace(pw);

        if (LOGGER.isErrorEnabled()) {
            LOGGER.error(sw.toString());
        }
    }
}
