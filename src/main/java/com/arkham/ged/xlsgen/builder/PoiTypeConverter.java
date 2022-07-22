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
package com.arkham.ged.xlsgen.builder;

import java.util.Arrays;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkham.ged.yaml.AlignmentType;
import com.arkham.ged.yaml.BorderStyleType;
import com.arkham.ged.yaml.FillType;
import com.arkham.ged.yaml.UnderlineType;
import com.arkham.ged.yaml.VerticalAlignmentType;

/**
 * Classe utilitaire de conversions des types énumérés entre les beans JAXB et les énumérations internes à POI
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 2 août 2018
 */
class PoiTypeConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(PoiTypeConverter.class);

    /**
     * Enumerate underline possibilities
     *
     * @author arocher / Arkham asylum
     * @version 1.0
     * @since 2 août 2018
     * @see Font#U_DOUBLE
     */
    public enum U_STYLE {
        /* @formatter:off */
        U_NONE(Font.U_NONE),
        U_SINGLE(Font.U_SINGLE),
        U_DOUBLE(Font.U_DOUBLE),
        U_SINGLE_ACCOUNTING(Font.U_SINGLE_ACCOUNTING),
        U_DOUBLE_ACCOUNTING(Font.U_DOUBLE_ACCOUNTING);
        /* @formatter:on */

        private byte mV;

        U_STYLE(final byte v) { // NOSONAR : yo sonar, I can't remove this constructor, it's a joke !!!
            mV = v;
        }

        byte getValue() {
            return mV;
        }
    }

    /**
     * Constructor ExcelTypeConverter
     */
    private PoiTypeConverter() {
        // Utility class
    }

    /**
     * Conversion style de bordures
     *
     * @param bst border style
     * @return POI border style
     */
    static BorderStyle convert(final BorderStyleType bst) {
        if (bst != null) {
            try {
                return BorderStyle.valueOf(bst.toString());
            } catch (@SuppressWarnings("unused") final IllegalArgumentException e) { // NOSONAR : not a blocking problem
                LOGGER.info("convert() : bad border style \"{}\", accepted values are {}", bst, Arrays.deepToString(BorderStyle.values()));
            }
        }

        return null;
    }

    /**
     * Conversion du type de remplissage (fond de cellule)
     *
     * @param ft fill type
     * @return POI fill pattern type
     */
    static FillPatternType convert(final FillType ft) {
        if (ft != null) {
            try {
                return FillPatternType.valueOf(ft.toString());
            } catch (@SuppressWarnings("unused") final IllegalArgumentException e) { // NOSONAR : not a blocking problem
                LOGGER.info("convert() : bad fill pattern \"{}\", accepted values are {}", ft, Arrays.deepToString(FillPatternType.values()));
            }
        }

        return null;
    }

    /**
     * Conversion du type d'alignement horizontal
     *
     * @param at alignment type
     * @return POI alignment
     */
    static HorizontalAlignment convert(final AlignmentType at) {
        if (at != null) {
            try {
                return HorizontalAlignment.valueOf(at.toString());
            } catch (@SuppressWarnings("unused") final IllegalArgumentException e) { // NOSONAR : not a blocking problem
                LOGGER.info("convert() : bad horizontal alignment value \"{}\", accepted values are {}", at, Arrays.deepToString(HorizontalAlignment.values()));
            }
        }

        return null;
    }

    /**
     * Conversion du type d'alignement vertical
     *
     * @param at alignment type
     * @return POI alignment
     */
    static VerticalAlignment convert(final VerticalAlignmentType at) {
        if (at != null) {
            try {
                return VerticalAlignment.valueOf(at.toString());
            } catch (@SuppressWarnings("unused") final IllegalArgumentException e) { // NOSONAR : not a blocking problem
                LOGGER.info("convert() : bad vertical alignment value \"{}\", accepted values are {}", at, Arrays.deepToString(VerticalAlignment.values()));
            }
        }

        return null;
    }

    /**
     * Conversion du type de soulignement
     *
     * @param ut alignment type
     * @return POI underline inner value
     */
    static byte convert(final UnderlineType ut) {
        if (ut != null) {
            try {
                return U_STYLE.valueOf(ut.toString()).getValue();
            } catch (@SuppressWarnings("unused") final IllegalArgumentException e) { // NOSONAR : not a blocking problem
                LOGGER.info("convert() : bad underline value \"{}\", accepted values are {}", ut, Arrays.deepToString(U_STYLE.values()));
            }
        }

        return U_STYLE.U_NONE.getValue();
    }
}
