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
package com.arkham.ged.standalone;

import java.util.ArrayList;
import java.util.List;

import com.arkham.common.pattern.destroy.IDestroyable;
import com.arkham.common.scheduler.Scheduler;
import com.arkham.common.scheduler.Scheduler.SHUTDOWN_TYPE;

/**
 * State holder for Ged, could be used as a result of {@link GedInit} to destroy any objects created
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 23 mars 2018
 */
public class GedStateHolder implements IDestroyable {
    private final Scheduler mScheduler;
    private final List<IDestroyable> mDestroyable;

    /**
     * Constructor GedStateHolder
     *
     * @param scheduler The main scheduler of Ged
     */
    GedStateHolder(Scheduler scheduler) {
        mScheduler = scheduler;
        mDestroyable = new ArrayList<>();
    }

    /**
     * @return the scheduler
     */
    public Scheduler getScheduler() {
        return mScheduler;
    }

    /**
     * Add a new destroyable objet to the inner list
     *
     * @param destroyable Any object that implements {@link IDestroyable}
     */
    void addDestroyable(IDestroyable destroyable) {
        mDestroyable.add(destroyable);
    }

    @Override
    public void destroy() {
        for (final IDestroyable destroyable : mDestroyable) {
            destroyable.destroy();
        }

        // Stop scheduler
        mScheduler.shutdown(SHUTDOWN_TYPE.ABORT);
    }
}
