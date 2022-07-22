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
package com.arkham.ged.solver;

/**
 * "Like SLF4J" translator, replace all matching "{}" by the matching parameter
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 24 févr. 2020
 */
public class SlfTranslator implements Translator {
    @Override
    public String translate(String message, Object... p) {
        if (p != null && p.length > 0 && message != null) {
            var pi = 0;
            // 40 is arbitrary
            final var result = new StringBuilder(message.length() + 40);
            final var len = message.length();
            var i = 0;
            while (i < len) {
                final var c = message.charAt(i);
                // Could be replaced
                if (c == '{' && i < len - 1 && message.charAt(i + 1) == '}') {
                    if (pi < p.length) {
                        final var o = p[pi];
                        if (o == null) {
                            result.append("null");
                        } else {
                            result.append(o);
                        }

                        pi++;
                    } else {
                        // Could not be consumed
                        result.append("{}");
                    }

                    // Skip '}'
                    i++;
                } else {
                    result.append(c);
                }

                i++;
            }

            return result.toString();
        }

        return message;
    }
}
