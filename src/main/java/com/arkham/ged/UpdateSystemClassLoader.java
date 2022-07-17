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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.StringTokenizer;

/**
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public final class UpdateSystemClassLoader {
	private UpdateSystemClassLoader() {
		// Private because it's an utility class, so we should't get an instance of this class
	}

	/**
	 * Update the current classloader by using the list of directories specified by listDir
	 *
	 * @param listDir
	 * @throws IOException
	 */
	public static void update(String listDir) throws IOException {
		if (listDir != null) {
			final StringTokenizer st = new StringTokenizer(listDir, ";", false);
			while (st.hasMoreTokens()) {
				final String dirName = st.nextToken();
				System.out.println("classpath dir=" + dirName); // NOSONAR

				final File file = new File(dirName);
				System.out.println("==> exists=" + file.exists()); // NOSONAR
				System.out.println("==> canonical path=" + file.getCanonicalPath()); // NOSONAR

				if (file.isDirectory()) {
					final File[] jarFiles = file.listFiles(new JarFilter());

					for (final File jarFile : jarFiles) {
						try {
							final URL jarURL = jarFile.toURI().toURL();
							addURL(jarURL);
							System.out.println(jarURL + " added to your system classpath"); // NOSONAR
						} catch (final UpdateSystemClassLoaderException e) { // NOSONAR
							System.err.println(e); // NOSONAR
						}
					}
				}
			}
		}
	}

	private static void addURL(URL url) throws UpdateSystemClassLoaderException {
		try (URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader()) {
			final Class<URLClassLoader> clazz = URLClassLoader.class;

			// Use reflection
			final Method method = clazz.getDeclaredMethod("addURL", URL.class);
			method.setAccessible(true);
			method.invoke(classLoader, url);
		} catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException | IOException e) {
			throw new UpdateSystemClassLoaderException(e);
		}
	}

	static class JarFilter implements FilenameFilter {
		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(".jar");
		}
	}
}
