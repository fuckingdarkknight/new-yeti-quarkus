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
package com.arkham.ged.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Spool file name decoder
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 29 janv. 2019
 */
public class GedSpoolUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(GedSpoolUtil.class);

    private GedSpoolUtil() {
        // Private because it's an utility class, so we should't get an instance of this class
    }

    /**
     * Bean that represent UT_SPL codsoc/numero
     *
     * @author arocher / Arkham asylum
     * @version 1.0
     * @since 29 janv. 2019
     */
    public static class SpoolPkBean {
        private int mCodsoc;
        private int mNumero;

        /**
         * Constructor SpoolPkBean
         *
         * @param codsoc Codsoc
         * @param numero Numedi
         */
        SpoolPkBean(final int codsoc, final int numero) {
            mCodsoc = codsoc;
            mNumero = numero;
        }

        /**
         * @return the codsoc
         */
        public int getCodsoc() {
            return mCodsoc;
        }

        /**
         * @return the numero
         */
        public int getNumero() {
            return mNumero;
        }

        /**
         * Decode a file name like "SOCx_y.ext" to retrieve a bean that contains Codsoc=x and Numero=y. If the file name cannot be decoded, the values should contains -1
         *
         * @param filename The filename to decode
         * @return The bean
         */
        public static SpoolPkBean getBean(final String filename) {
            // By default, -1 is not a consistent value
            final var result = new SpoolPkBean(-1, -1);
            if (filename != null && filename.startsWith("SOC") && filename.length() > 3) {
                // Name without extension and without prefix "SOC"
                final String nwe;
                final var dotIndex = filename.indexOf('.');
                if (dotIndex > 0) {
                    nwe = filename.substring(3, dotIndex);
                } else {
                    nwe = filename.substring(3);
                }

                final var x = nwe.split("_");
                // We don't care about extra "_"
                if (x.length >= 2) {
                    try {
                        result.mCodsoc = Integer.parseInt(x[0]);
                        result.mNumero = Integer.parseInt(x[1]);
                    } catch (@SuppressWarnings("unused") final NumberFormatException e) { // NOSONAR
                        LOGGER.error("getBean() : cannot decode spool file name {}", filename);
                    }
                }
            }

            return result;
        }

        @Override
        public String toString() {
            return "SOC" + mCodsoc + "_" + mNumero;
        }
    }
}
