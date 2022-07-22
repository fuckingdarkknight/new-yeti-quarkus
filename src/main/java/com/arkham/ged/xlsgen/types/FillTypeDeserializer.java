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
package com.arkham.ged.xlsgen.types;

import com.arkham.ged.yaml.FillType;

/**
 * FillType for JsonDeserializing, mainly don't care about upper or lower case and take the default value {@link FillType#NO_FILL} in case of unexisting value.
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 13 févr. 2020
 */
public class FillTypeDeserializer extends AbstractTypeDeserializer<FillType> {
    /**
     * Constructor ImageBehaviorTypeDeserializer
     *
     * @param ea Appender for errors
     */
    public FillTypeDeserializer(ErrorAppender ea) {
        super(ea);
    }
}
