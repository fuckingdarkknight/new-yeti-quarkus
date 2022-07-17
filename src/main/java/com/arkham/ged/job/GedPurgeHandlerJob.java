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
package com.arkham.ged.job;

import com.arkham.common.scheduler.Job;
import com.arkham.common.scheduler.JobException;
import com.arkham.ged.security.SecurityHandlerProvider;

/**
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public class GedPurgeHandlerJob extends Job {
	private final SecurityHandlerProvider mSecurityProvider;
	private final int mFrequency;

	/**
	 * Constructor GedPurgeHandlerJob
	 *
	 * @param provider
	 * @param frequency
	 */
	public GedPurgeHandlerJob(SecurityHandlerProvider provider, int frequency) {
		super("GedPurgeHandlerJob");

		mSecurityProvider = provider;
		mFrequency = frequency;
	}

	@Override
	public int getFrequency() {
		return mFrequency;
	}

	@Override
	public void execute() throws JobException {
		mSecurityProvider.cleanUp();
	}
}
