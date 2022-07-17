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
package com.arkham.ged.message;

/**
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public class GedMessage {
	private final boolean mTrace;
	private final String mLabel;

	/**
	 * Constructor GedMessage
	 *
	 * @param trace Message to trace
	 * @param message The message
	 */
	public GedMessage(boolean trace, String message) {
		mTrace = trace;
		mLabel = message;
	}

	/**
	 * @return The label of the message
	 */
	public String getLabel() {
		return mLabel;
	}

	/**
	 * @return true if it's a trace message
	 */
	public boolean isTrace() {
		return mTrace;
	}
}
