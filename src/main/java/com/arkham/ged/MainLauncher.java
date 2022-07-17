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
package com.arkham.ged;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public abstract class MainLauncher {
	private static final Logger LOGGER = LoggerFactory.getLogger(MainLauncher.class);

	static {
		System.out.println("user.dir=" + System.getProperty("user.dir")); // NOSONAR
		final MemoryMXBean mmxb = ManagementFactory.getMemoryMXBean();
		final long max = mmxb.getHeapMemoryUsage().getMax() / 1024 / 1024;
		System.out.println("Xmx=" + max); // NOSONAR

		// màj du classloader afin de faciliter les lancements de tests en standalone
		try {
			UpdateSystemClassLoader.update("lib");
		} catch (final IOException e) {
			LOGGER.error("{}:{}", e.getClass().getName(), e);
			final StringWriter sw = new StringWriter();
			final PrintWriter pw = new PrintWriter(sw);

			e.printStackTrace(pw);

			System.err.println(sw.toString()); // NOSONAR
		}
	}

	protected MainLauncher() {
		// Just for some tests, no need to do anything in the constructor
	}
}
