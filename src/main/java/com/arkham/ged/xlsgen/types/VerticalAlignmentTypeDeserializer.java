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

import com.arkham.ged.yaml.VerticalAlignmentType;

/**
 * VerticalAlignmentType for JsonDeserializing, mainly don't care about upper or lower case and take the default value {@link VerticalAlignmentType#TOP} in case of unexisting value.
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 13 févr. 2020
 */
public class VerticalAlignmentTypeDeserializer extends AbstractTypeDeserializer<VerticalAlignmentType> {
	private static final Logger LOGGER = LoggerFactory.getLogger(VerticalAlignmentTypeDeserializer.class);

	/**
	 * Constructor ImageBehaviorTypeDeserializer
	 *
	 * @param ea Appender for errors
	 */
	public VerticalAlignmentTypeDeserializer(final ErrorAppender ea) {
		super(ea);
	}

	@Override
	public VerticalAlignmentType deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
		final String s = p.getText();

		try {
			return VerticalAlignmentType.valueOf(s.toUpperCase());
		} catch (@SuppressWarnings("unused") final IllegalArgumentException e) { // NOSONAR
			addError(LOGGER, p, VerticalAlignmentType.class, s);
		}

		return VerticalAlignmentType.TOP;
	}
}
