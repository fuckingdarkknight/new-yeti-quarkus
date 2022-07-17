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
package com.arkham.ged.job;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkham.ged.executor.AbstractScanFileExecutor;
import com.arkham.ged.executor.ExecutorException;
import com.arkham.ged.executor.LockFileExecutor;
import com.arkham.ged.executor.RenameFileExecutor;
import com.arkham.ged.properties.InputScanFileDef;
import com.arkham.ged.properties.PropertiesAdapter;

/**
 * Job used by the scheduler to import/archive medias/documents from filesystem.
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public class GedArchiverJob extends AbstractJob<InputScanFileDef> {
	private static final Logger LOGGER = LoggerFactory.getLogger(GedArchiverJob.class);

	private static final Class[] CLASSDEF = { Connection.class, PropertiesAdapter.class, InputScanFileDef.class, };

	/**
	 * Constructor GedArchiverJob
	 *
	 * @param pa Needed to get file extensions/typmed mappings
	 * @param sfd The directory definition to scan
	 */
	public GedArchiverJob(PropertiesAdapter pa, InputScanFileDef sfd) {
		super("GedArchiverJob", pa, sfd);
	}

	@Override
	protected AbstractScanFileExecutor<InputScanFileDef> createExecutorInstance(Connection con) throws ExecutorException {
		AbstractScanFileExecutor<InputScanFileDef> result = null;
		final String className = getSFD().getExecutor();
		// By default, use input file renaming in order to process scalable integrations
		if (className == null || className.trim().length() == 0 || "rename".equals(className)) {
			result = new RenameFileExecutor(con, getPA(), getSFD());
		} else if ("lock".equals(className)) {
			result = new LockFileExecutor(con, getPA(), getSFD());
		} else {
			try {
				final Class<AbstractScanFileExecutor<InputScanFileDef>> clazz = (Class<AbstractScanFileExecutor<InputScanFileDef>>) Class.forName(className);
				final Constructor<AbstractScanFileExecutor<InputScanFileDef>> c = clazz.getConstructor(CLASSDEF);

				result = c.newInstance(con, getPA(), getSFD());
			} catch (NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassCastException e) {
				LOGGER.error("createInstance() : className={} exception={}", className, e);
			}
		}

		return result;
	}
}
