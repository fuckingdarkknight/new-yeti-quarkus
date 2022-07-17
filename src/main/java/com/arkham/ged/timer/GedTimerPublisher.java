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

import com.arkham.common.timer.common.ChronoDef.UNIT;
import com.arkham.common.timer.common.ITimerPublisher;

/**
 * Main timer publisher : write timer in the MDC key
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 29 mai 2015
 */
class GedTimerPublisher implements ITimerPublisher<TIMERDEF> {
	@Override
	public void publish(TIMERDEF timerdef, long elapsed, UNIT unit, double precision) {
		if (timerdef != null) {
			final double d = UNIT.convert(elapsed, timerdef.getUnit());
			if (d > timerdef.getPrecision()) {
				LoggerMDC.putMDC(timerdef.getKey(), Double.valueOf(d));
				// Is-it usefull ?
				LoggerMDC.putMDC(MDC_KEY.ELAPSE_TIME_UNIT, timerdef.getUnit());
				LoggerMDC.putMDC(MDC_KEY.ELAPSE_TIME_LEVEL, Double.valueOf(timerdef.getPrecision()));
			}
		}
	}
}
