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

import com.arkham.common.timer.Timer;
import com.arkham.common.timer.TimerProvider;
import com.arkham.common.timer.common.ITimerPublisher;

/**
 * Timer provider for Ged
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 29 mai 2015
 */
public class GedTimerProvider extends TimerProvider<TIMERDEF> {
	/**
	 * Constructor GedTimerProvider
	 *
	 * @param parent
	 * @param publisher
	 */
	GedTimerProvider(TimerProvider<TIMERDEF> parent, ITimerPublisher<TIMERDEF> publisher) {
		super(parent, publisher);
	}

	@Override
	public long getElapsed() {
		return 0;
	}

	@Override
	public double getElapsed(UNIT arg0) {
		return 0;
	}

	@Override
	public String getFormattedElapsed() {
		return null;
	}

	@Override
	public String getFormattedElapsed(UNIT arg0) {
		return null;
	}

	@Override
	public UNIT getUnit() {
		return null;
	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public boolean isStopped() {
		return false;
	}

	@Override
	public boolean isWaiting() {
		return false;
	}

	@Override
	public void reset() {
		// Nothing
	}

	@Override
	public void resume() {
		// Nothing
	}

	@Override
	public void start() {
		// Nothing
	}

	@Override
	public void stop() {
		// Nothing
	}

	@Override
	public void suspend() {
		// Nothing
	}

	/**
	 * <p>
	 * Create a timer and active it : for the Ged the timers are always activated
	 * </p>
	 * {@inheritDoc}
	 */
	@Override
	public Timer<TIMERDEF> create(TIMERDEF timer) {
		final Timer<TIMERDEF> result = super.create(timer);
		changeTimerState(timer, true);

		return result;
	}

	/**
	 * Publish detailed timers
	 */
	@SuppressWarnings("static-method")
	public void publishDetail() {
		LoggerMDC.putMDC(MDC_KEY.DATE, Calendar.getValue("yyyyMMdd"));
		LoggerMDC.putMDC(MDC_KEY.TIME, Calendar.getValue("HHmmssSSS"));

		// Publish through log4j activity appender/logger
		LoggerMDC.getActivityDetailLogger().info("timer_publishing");
	}

	/**
	 * Publish global timer for a file processing
	 */
	@SuppressWarnings("static-method")
	public void publishRun() {
		LoggerMDC.putMDC(MDC_KEY.DATE, Calendar.getValue("yyyyMMdd"));
		LoggerMDC.putMDC(MDC_KEY.TIME, Calendar.getValue("HHmmssSSS"));

		// Publish through log4j activity appender/logger
		LoggerMDC.getActivityRunLogger().info("timer_publishing");
	}
}
