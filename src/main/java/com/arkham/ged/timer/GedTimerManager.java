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
package com.arkham.ged.timer;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.arkham.common.timer.TimerProvider;

/**
 * Get the main provider.
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 29 mai 2015
 */
public final class GedTimerManager {
	private static Lock mLock = new ReentrantLock();

	private static GedTimerProvider mTimerProvider;

	static {
		try {
			mLock.lock();
			if (mTimerProvider == null) {
				mTimerProvider = new GedTimerProvider((TimerProvider<TIMERDEF>) null, new GedTimerPublisher());
			}
		} finally {
			mLock.unlock();
		}
	}

	private GedTimerManager() {
		// Private and nothing to do
	}

	public static GedTimerProvider getProvider() {
		return mTimerProvider;
	}
}
