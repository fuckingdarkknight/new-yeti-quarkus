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
package com.arkham.ged.timer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Utility class for date/time format conversions
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
final class Calendar {
    private Calendar() {
        // Private because it's an utility class, so we should't get an instance of this class
    }

    /**
     * Retourne un résultat correspondant au masque passé. La plage va de l'année à la milli-seconde.
     *
     * @param format le format du résultat attendu. Les valeurs disponibles sont :
     *            <ul>
     *            <li>Letter Date or Time Component Presentation Examples
     *            <li>G Era designator Text AD
     *            <li>y Year Year 1996; 96
     *            <li>M Month in year Month July; Jul; 07
     *            <li>w Week in year Number 27
     *            <li>W Week in month Number 2
     *            <li>D Day in year Number 189
     *            <li>d Day in month Number 10
     *            <li>F Day of week in month Number 2
     *            <li>E Day in week Text Tuesday; Tue
     *            <li>a Am/pm marker Text PM
     *            <li>H Hour in day (0-23) Number 0
     *            <li>k Hour in day (1-24) Number 24
     *            <li>K Hour in am/pm (0-11) Number 0
     *            <li>h Hour in am/pm (1-12) Number 12
     *            <li>m Minute in hour Number 30
     *            <li>s Second in minute Number 55
     *            <li>S Millisecond Number 978
     *            <li>z Time zone General time zone Pacific Standard Time; PST; GMT-08:00
     *            <li>Z Time zone RFC 822 time zone -0800 Exemples de masques : yyyyMMdd HH:mm:ss:SSS HHmmssSS
     * @return l'information chronologique selon le format demandé.
     */
    static String getValue(String format) {
        final DateFormat dateFormat = new SimpleDateFormat(format); // le format de la date

        return dateFormat.format(java.util.Calendar.getInstance().getTime());
    }
}
