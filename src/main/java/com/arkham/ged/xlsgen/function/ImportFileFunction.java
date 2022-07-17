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
package com.arkham.ged.xlsgen.function;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkham.common.solver.function.Function;
import com.arkham.common.solver.function.FunctionExecutionException;

/**
 * Get the content of a file as text result
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 28 janv. 2020
 */
public class ImportFileFunction extends Function {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImportFileFunction.class);

	private static final String DEFAULT_RESULT = "".intern();

	@Override
	public String getName() {
		return "import";
	}

	@Override
	protected Class<?>[] getParamsClass() {
		return new Class[] { String.class, String.class };
	}

	@Override
	protected int getMinParamCount() {
		return 1;
	}

	@Override
	public Object invoke(final Object... params) throws FunctionExecutionException {
		final String filename = (String) params[0];
		final Path path = Paths.get(filename.trim());

		String charset = "UTF-8";
		if (params.length > 1) {
			charset = (String) params[1];
		}

		try {
			final byte[] b = Files.readAllBytes(path);

			return new String(b, getCharset(charset));
		} catch (@SuppressWarnings("unused") final IOException e) { // NOSONAR : not blocking at all
			LOGGER.debug("invoke() : file \"{}\" not found", filename);
		}

		return DEFAULT_RESULT;
	}

	private static Charset getCharset(final String charset) {
		if (charset != null) {
			try {
				return Charset.forName(charset);
			} catch (@SuppressWarnings("unused") IllegalCharsetNameException | UnsupportedCharsetException e) { // NOSONAR : au moins on sera prévenu dans le log qu'on demande n'importe quoi. Mais avec un peu de bol, le charset par défaut de la
				// plateforme résoud le souci.
				LOGGER.warn("getCharset() : invalid charset {}", charset);
			}
		}

		return Charset.defaultCharset();
	}
}
