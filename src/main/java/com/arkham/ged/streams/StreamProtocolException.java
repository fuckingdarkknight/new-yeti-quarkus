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
package com.arkham.ged.streams;

import com.arkham.ged.GedException;
import com.arkham.ged.message.GedMessage;

/**
 * Stream package exception
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 23 nov. 2017
 */
public class StreamProtocolException extends GedException {
    private static final long serialVersionUID = 1L;

    /**
	 * Constructor StreamProtocolException
	 *
	 * @param message The message to translate with parameters
	 * @param params Parameters
	 */
	public StreamProtocolException(final GedMessage message, final Object[] params) {
		super(message, params);
	}

	/**
	 * Constructor StreamProtocolException
	 *
	 * @param cause The cause exception
	 * @param message The message to translate with parameters
	 * @param params Parameters
	 */
	public StreamProtocolException(final Throwable cause, final GedMessage message, final Object... params) {
		super(cause, message, params);
	}

	/**
	 * Constructor StreamProtocolException
	 *
	 * @param cause The cause exception
	 */
	public StreamProtocolException(final Throwable cause) {
		super(cause);
	}
}
