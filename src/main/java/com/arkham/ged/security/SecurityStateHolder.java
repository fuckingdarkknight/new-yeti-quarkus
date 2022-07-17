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
package com.arkham.ged.security;

import java.util.HashMap;
import java.util.Map;

/**
 * All the security stateholder should overload this class
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 * @param <T>
 */
public abstract class SecurityStateHolder<T> {
	private final Map<String, T> mMap = new HashMap<>();

	/**
	 * @param key
	 * @return The value
	 */
	public final T get(String key) {
		return mMap.get(key);
	}

	/**
	 * @param key
	 * @param value
	 */
	public final void put(String key, T value) {
		mMap.put(key, value);
	}

	/**
	 * @param key
	 * @return true if the key is defined
	 */
	public final boolean containsKey(String key) {
		return mMap.containsKey(key);
	}
}
