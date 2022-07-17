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
package com.arkham.ged.filekey;

import com.arkham.ged.GedException;
import com.arkham.ged.message.GedMessage;

/**
 * Exception raised by filekey package
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public class FileKeyProviderException extends GedException {
    private static final long serialVersionUID = 1L;

    /**
	 * {@link FileKeyProviderException} constructor.
	 *
	 * @param cause Root cause
	 */
	public FileKeyProviderException(final Throwable cause) {
		super(cause);
	}

	/**
	 * {@link FileKeyProviderException} constructor.
	 *
	 * @param message Message
	 * @param params Parameters array
	 */
	public FileKeyProviderException(final GedMessage message, final Object... params) {
		super(message, params);
	}

	/**
	 * Constructor FileKeyProviderException
	 *
	 * @param cause Root cause
	 * @param message Message
	 * @param params Parameters array
	 */
	public FileKeyProviderException(final Throwable cause, final GedMessage message, final Object... params) {
		super(cause, message, params);
	}
}
