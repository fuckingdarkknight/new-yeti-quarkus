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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Excel s'amuse tout seul à changer les formats, par exemple même dans une cellule texte 150 peut être transformé en 150.0 ... ce transformer
 * permet donc de reformater correctement (String -> String) pour des valeurs numériques.
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 7 déc. 2016
 */
public class StringNumberTransformer implements ITransformer<String> {
	private static final Locale US = new Locale("en", "US");

	private static final NumberFormat NUM_FORMAT = new DecimalFormat("0", new DecimalFormatSymbols(US));

	@Override
	public Object transform(String value) {
		return NUM_FORMAT.format(value);
	}
}
