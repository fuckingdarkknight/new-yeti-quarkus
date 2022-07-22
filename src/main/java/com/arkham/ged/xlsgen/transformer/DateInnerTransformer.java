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
package com.arkham.ged.xlsgen.transformer;

import java.time.format.DateTimeFormatter;

/**
 * GX inner format date transformer ("yyyyMMdd")
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 23 août 2018
 */
public class DateInnerTransformer extends DateTransformer {
    private static final DateTimeFormatter SRC_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * Format "yyyyMMdd"
     * {@inheritDoc}
     *
     * @see com.arkham.ged.xlsgen.transformer.DateTransformer#getSourceFormatter()
     */
    @Override
    protected DateTimeFormatter getSourceFormatter() {
        return SRC_FORMATTER;
    }
}
