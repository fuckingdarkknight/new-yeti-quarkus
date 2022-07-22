/*
 * Licensed to the Arkham asylum Software Foundation under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.arkham.ged.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.arkham.common.properties.PropertiesProvider;
import com.arkham.ged.solver.Translator;

/**
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public class LikeAntPropertiesTranslator implements Translator {
    @Override
    public String translate(String value, Object... p) {
        if (value == null) {
            return null;
        }

        var result = value;
        final var mappings = parse(value);
        for (final Entry<String, String> entry : mappings.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }

        return result;
    }

    private static Map<String, String> parse(String value) {
        final Map<String, String> result = new HashMap<>();
        var pos = 0;
        while ((pos = value.indexOf("${", pos)) != -1) {
            final var last = value.indexOf('}', pos);
            if (last != -1) {
                final var var = value.substring(pos + 2, last);
                final var key = "${" + var + "}";
                final var val = PropertiesProvider.getProperty(var, "");
                result.put(key, val);

                pos = last;
            } else {
                // Stop the parsing : the string is incorrect, should be toto${xxx}/zzz
                break;
            }
        }

        return result;
    }
}
