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
package com.arkham.ged.properties;

/**
 * Interface used to make a proxy between initialization context and the standalone init method.
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public interface ContextProperties {
    /**
     * Get the value of contextual parameter
     *
     * @param param The parameter name
     * @return The value or <code>null</code> if the parameter is not defined
     */
    String getLocalParameter(String param);
}
