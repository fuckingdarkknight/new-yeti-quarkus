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

import com.arkham.ged.GedException;
import com.arkham.ged.message.GedMessage;

/**
 * Exception raised by action package
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 29 déc. 2015
 */
public class ActionException extends GedException {
	/**
	 * Constructor ActionException
	 *
	 * @param message Message
	 * @param params Optional parameters
	 */
	public ActionException(GedMessage message, Object... params) {
		super(message, params);
	}

	/**
	 * Constructor ActionException
	 *
	 * @param cause The root cause
	 * @param message Message
	 * @param params Optional parameters
	 */
	public ActionException(Throwable cause, GedMessage message, Object... params) {
		super(cause, message, params);
	}

	/**
	 * Constructor ActionException
	 *
	 * @param cause The root cause
	 */
	public ActionException(Throwable cause) {
		super(cause);
	}
}
