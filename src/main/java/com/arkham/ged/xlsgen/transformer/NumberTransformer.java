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
package com.arkham.ged.xlsgen.transformer;

/**
 * Transformation d'une chaîne de caractère "numérique" en vraie valeur numérique, type Double.class
 * <br/>
 * Une valeur <code>null</code> en entrée ou une valeur non numérique renvoie systématiquement la valeur 0,
 * dans tous les cas cela signifie qu'on a pas de donnée en entrée ou alors que le type spécifié n'est pas le bon.
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 19 juil. 2018
 */
public class NumberTransformer implements ITransformer<String> {
    @Override
    public Object transform(final String value) {
        if (value == null) {
            return 0.0d;
        }

        final var s = value.trim();
        if (s.length() > 0) {
            try {
                return Double.valueOf(s);
            } catch (@SuppressWarnings("unused") final NumberFormatException e) { // NOSONAR
                // Will return default value
            }
        }

        return 0.0d;
    }
}
