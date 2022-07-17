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

import com.arkham.ged.GedException;
import com.arkham.ged.message.GedMessage;

/**
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public class ResourceAuthenticationException extends GedException {
	/**
	 * Constructor ResourceAuthenticationException
	 *
	 * @param message
	 * @param params
	 */
	public ResourceAuthenticationException(GedMessage message, Object... params) {
		super(message, params);
	}

	/**
	 * Constructor ResourceAuthenticationException
	 *
	 * @param cause
	 * @param message
	 * @param params
	 */
	public ResourceAuthenticationException(Throwable cause, GedMessage message, Object... params) {
		super(cause, message, params);
	}
}
