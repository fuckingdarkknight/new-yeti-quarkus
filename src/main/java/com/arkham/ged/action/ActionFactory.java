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
package com.arkham.ged.action;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkham.ged.message.GedMessages;
import com.arkham.ged.properties.ActionType;

/**
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 29 déc. 2015
 */
public final class ActionFactory {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionFactory.class);

	private ActionFactory() {
		// Private because it's an utility class, so we should't get an instance of this class
	}

	/**
	 * Create a new instance of {@link AbstractAction}. className represents the canonical name and the new instance is created dynamically.
	 *
	 * @param action The definition of the action
	 * @return A new instance of {@link AbstractAction}
	 * @throws ActionException Occurs if any other exception is raised (escalation)
	 */
	public static AbstractAction create(ActionType action) throws ActionException {
		AbstractAction result;

		// By default, we use .ref files
		final String className = action.getName();
		try {
			final Class<?> clazz = Class.forName(className);
			final Constructor<?> c = clazz.getConstructor();

			result = (AbstractAction) c.newInstance();

			assert result != null;
		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassCastException e) {
			LOGGER.error("createInstance() : className=" + className + " exception=", e);

			throw new ActionException(e, GedMessages.Action.cannotInstantiate, className);
		}

		result.init(action);

		return result;
	}
}
