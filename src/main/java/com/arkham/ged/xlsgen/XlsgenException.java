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
package com.arkham.ged.xlsgen;

import com.arkham.ged.GedException;
import com.arkham.ged.message.GedMessage;

/**
 * Package exception for excel files generation
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 19 juil. 2018
 */
public class XlsgenException extends GedException {
	/**
	 * Constructor XlsgenException
	 *
	 * @param cause The root cause
	 */
	public XlsgenException(Throwable cause) {
		super(cause);
	}

	/**
	 * {@link GedException} constructor.
	 *
	 * @param message Message
	 * @param params Parameters array
	 */
	public XlsgenException(GedMessage message, Object... params) {
		super(message, params);
	}
}
