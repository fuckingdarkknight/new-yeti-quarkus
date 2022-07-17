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

import java.util.Map;

/**
 * Transform variables like <code>${YYY}</code> by parsing a content (string) and substituting
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 10 nov. 2017
 */
public class VariableTranslator implements Translator {
	private final Map<String, String> mVariables;

	private final String mDefaultUnknown;

	/**
	 * Constructor VariableTranslator
	 *
	 * @param var Variables mappings, should not be null else it doesn't mean anything to translate without substitution
	 * @param defaultUnknown Default value for undefined variable
	 */
	public VariableTranslator(Map<String, String> var, String defaultUnknown) {
		mVariables = var;
		mDefaultUnknown = defaultUnknown;
	}

	/**
	 * Constructor VariableTranslator
	 *
	 * @param var Variables mappings, should not be null else it doesn't mean anything to translate without substitution
	 * @see #VariableTranslator(Map, String)
	 */
	public VariableTranslator(Map<String, String> var) {
		this(var, "???");
	}

	/**
	 * Translate a content by substituting variables like <code>${YYY}</code>
	 * <p>
	 * If content is <code>null</code>, <code>null</code> will be returned
	 * <p>
	 * If no variables are defined, the content is returned directly
	 *
	 * @param content The content to translate
	 * @param p Not used in this translator
	 * @return The string translated
	 */
	@Override
	public String translate(String content, Object... p) {
		if (mVariables == null || mVariables.isEmpty() || content == null) {
			return content;
		}

		final StringBuilder res = new StringBuilder(content.length());
		int pos = 0;
		final int len = content.length();

		while (pos < len) {
			final char c = content.charAt(pos);
			// -3 = mini ${A}
			if (c == '$' && pos < len - 3 && content.charAt(pos + 1) == '{') {
				final int match = content.indexOf('}', pos);
				if (match > 0) {
					final String key = content.substring(pos + 2, match);
					final String value = mVariables.get(key);
					if (value != null) {
						res.append(value);
					} else {
						// Cannot translate, default to "???"
						res.append(mDefaultUnknown);
					}

					pos = match;
				}
			} else {
				res.append(c);
			}

			pos++;
		}

		return res.toString();
	}
}
