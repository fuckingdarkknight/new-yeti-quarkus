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
package com.arkham.ged.rejection;

import java.util.List;

import com.arkham.ged.properties.OptionalParameterType;

/**
 * In case of exception or error during processing
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public abstract class AbstractRejector implements Rejector {
    private List<OptionalParameterType> mParams;

    void setParam(List<OptionalParameterType> params) {
        mParams = params;
    }

    public List<OptionalParameterType> getParam() {
        return mParams;
    }
}
