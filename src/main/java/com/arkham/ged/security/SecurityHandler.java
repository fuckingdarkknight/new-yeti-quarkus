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

/**
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 * @param <H>
 */
public interface SecurityHandler<H extends SecurityStateHolder> {
	/**
	 * @param ssh
	 * @throws SecurityHandlerException
	 */
	void handleBefore(H ssh) throws SecurityHandlerException;

	/**
	 * @param ssh
	 */
	void handlerAfter(H ssh);

	/**
	 * Purge the handler
	 */
	void purge();

	/**
	 * Reset the handler
	 */
	void reset();
}
