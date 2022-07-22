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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

import com.arkham.ged.yaml.AlignmentType;

/**
 * AlignmentType for JsonDeserializing, mainly don't care about upper or lower case and take the default value {@link AlignmentType#GENERAL} in case of unexisting value.
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 13 févr. 2020
 */
public class AlignmentTypeDeserializer extends AbstractTypeDeserializer<AlignmentType> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlignmentTypeDeserializer.class);

    /**
     * Constructor ImageBehaviorTypeDeserializer
     *
     * @param ea Appender for errors
     */
    public AlignmentTypeDeserializer(final ErrorAppender ea) {
        super(ea);
    }

    @Override
    public AlignmentType deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final var s = p.getText();

        try {
            return AlignmentType.valueOf(s.toUpperCase());
        } catch (@SuppressWarnings("unused") final IllegalArgumentException e) { // NOSONAR
            addError(LOGGER, p, AlignmentType.class, s);
        }

        return AlignmentType.GENERAL;
    }
}
