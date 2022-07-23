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
package com.arkham.ged.filefilter;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

/**
 * <p>Common file filter that permit usuals wildcards, eg. : <code>toto_x*zzz.*xml</code></p>
 * The filter is case insensitive, a file name that starts with "." is excluded ({@link #accept(File)} return false)
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public class PatternFileFilter implements FileFilter {
    private final Pattern mPattern;

    /**
     * Constructor PatternFileFilter
     *
     * @param pattern
     */
    public PatternFileFilter(String pattern) {
        final var flags = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;

        final var stdFilePattern = pattern.replace(".", "\\.").replace("*", ".*");
        mPattern = Pattern.compile(stdFilePattern, flags);
    }

    @Override
    public boolean accept(File pathname) {
        final var filename = pathname.getName();
        if (filename.startsWith(".")) {
            return false;
        }

        final var matcher = mPattern.matcher(filename);

        return matcher.matches();
    }
}
