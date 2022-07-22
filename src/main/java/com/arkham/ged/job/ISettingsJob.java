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

import com.arkham.ged.properties.ScanFileDef;

/**
 * {@link ScanFileDef} interface publication
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 4 janv. 2019
 * @param <T> Concrete type of {@link ScanFileDef}
 */
public interface ISettingsJob<T extends ScanFileDef> {
    /**
     * @return Settings of current consumer
     */
    T getSFD();
}
