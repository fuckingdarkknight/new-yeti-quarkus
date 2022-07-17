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
package com.arkham.ged.rejection;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkham.ged.filekey.FileKey;
import com.arkham.ged.properties.PropertiesAdapter;
import com.arkham.ged.properties.RejectorType;

/**
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 22 janv. 2019
 */
public interface Rejector { // NOSONAR
	Logger LOGGER = LoggerFactory.getLogger(Rejector.class); // NOSONAR

	/**
	 * Process "something" when an exception occurs while executing actions from an executor. This method should catch all possible exceptions, and never raise another.
	 *
	 * @param file The file rejected
	 * @param fk The key
	 * @param t The exception raised
	 * @param message A user message
	 * @param con Database connection
	 * @param pa The properties adapter
	 */
	void reject(File file, FileKey fk, Throwable t, String message, Connection con, PropertiesAdapter pa);

	/**
	 * Create instance of rejector
	 *
	 * @param rejector type
	 * @return A new instance of Rejector or <code>null</code> in case of exception
	 */
	static AbstractRejector createRejector(final RejectorType rejector) {
		final String className = rejector.getName();

		// By default, we use file rejection
		if (className == null || className.trim().length() == 0 || "file".equals(className)) {
			return new FileRejector();
		}

		try {
			final Class<?> clazz = Class.forName(className);
			final Constructor<?> c = clazz.getConstructor();

			final AbstractRejector result = (AbstractRejector) c.newInstance();
			result.setParam(rejector.getParam());

			return result;
		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassCastException e) {
			LOGGER.error("createRejector() : className={} exception={}", className, e);
		}

		return null;
	}

	/**
	 * Create the list of rejectors
	 *
	 * @param rejectors The rejectors types
	 * @return The list of {@link AbstractRejector} instances
	 */
	static List<AbstractRejector> createRejector(final List<RejectorType> rejectors) {
		final List<AbstractRejector> result = new ArrayList<>();
		for (final RejectorType rejector : rejectors) {
			final AbstractRejector rej = createRejector(rejector);
			if (rej != null) {
				result.add(rej);
			}
		}

		return result;
	}
}
