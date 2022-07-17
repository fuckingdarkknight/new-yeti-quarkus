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
package com.arkham.ged.filekey;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkham.ged.message.GedMessages;
import com.arkham.ged.properties.OptionalParameterType;

/**
 * The {@link FileKeyProvider} factory
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public final class FileKeyProviderFactory {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileKeyProviderFactory.class);

	private FileKeyProviderFactory() {
		// Private because it's an utility class, so we should't get an instance of this class
	}

	/**
	 * Create a new instance of file key provider. If the class name is <code>null</code> or empty or equals to "ref", by default a new instance of {@link XmlFileKeyProvider} will be returned. The value "name" can be
	 * used to return a new instance of {@link ByNameFileKeyProvider}. Otherwise, className represents the canonical name and the new instance is created dynamically.
	 *
	 * @param className the class name
	 * @param params the optional parameters of FileKeyProvider
	 * @return A new instance of {@link FileKeyProvider}
	 * @throws FileKeyProviderException
	 */
	public static FileKeyProvider create(String className, List<OptionalParameterType> params) throws FileKeyProviderException {
		FileKeyProvider result;

		// By default, we use .ref files
		if (className == null || className.trim().length() == 0 || "ref".equals(className)) {
			result = new XmlFileKeyProvider();
		} else if ("name".equals(className)) {
			result = new ByNameFileKeyProvider();
		} else {
			try {
				final Class<?> clazz = Class.forName(className);
				final Constructor<?> c = clazz.getConstructor();

				result = (FileKeyProvider) c.newInstance();

				assert result != null;
			} catch (NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassCastException e) {
				LOGGER.error("createInstance() : className={} exception={}", className, e);

				throw new FileKeyProviderException(e, GedMessages.Scanner.cannotInstantiate, className);
			}
		}

		result.init(params);

		return result;
	}
}
