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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import org.apache.poi.ss.usermodel.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transformation d'une date au format "d/M/yyyy" vers le format habituel attendu par POI ("yyyy/MM/dd")
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 19 juil. 2018
 */
public class DateTransformer implements ITransformer<String> {
    private static final Logger LOG = LoggerFactory.getLogger(DateTransformer.class);

    private static final DateTimeFormatter SRC_FORMATTER = DateTimeFormatter.ofPattern("d/M/yyyy");
    private static final DateTimeFormatter POI_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Override
    public Object transform(final String value) {
        if (value == null) {
            return "";
        }

        try {
            final var parsedDate = LocalDate.parse(value, getSourceFormatter());

            // Format attendu par la méthode utilitaire de POI
            final var d = POI_FORMATTER.format(parsedDate);
            final var datum = DateUtil.parseYYYYMMDDDate(d); // NOSONAR

            return datum;
        } catch (@SuppressWarnings("unused") final DateTimeParseException e) { // NOSONAR
            LOG.error("transform() : unable to convert date format, value=\"{}\"", value);
        }

        return "";
    }

    /**
     * Méthode surchargeable afin de spécifier le format date en entrée
     *
     * @return Le transformer de date au format d'entrée
     */
    @SuppressWarnings("static-method")
    protected DateTimeFormatter getSourceFormatter() {
        return SRC_FORMATTER;
    }
}
