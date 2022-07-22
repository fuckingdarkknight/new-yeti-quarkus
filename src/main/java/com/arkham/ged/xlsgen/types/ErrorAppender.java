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

import java.util.ArrayList;
import java.util.List;

import com.arkham.ged.solver.SlfTranslator;
import com.arkham.ged.solver.Translator;

/**
 * Hold a list of warning/errors
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 13 févr. 2020
 */
public class ErrorAppender {
    private final List<String> mList = new ArrayList<>();

    private static final Translator mTranslator = new SlfTranslator();

    /**
     * Add an issue
     *
     * @param e The issue
     */
    public void add(String e) {
        mList.add(e);
    }

    /**
     * Add a translated issue to the stack
     *
     * @param message The message
     * @param p The parameters that should be translated by {@link SlfTranslator}
     */
    public void add(String message, Object... p) {
        add(mTranslator.translate(message, p));
    }

    /**
     * @return The list of issues
     */
    public List<String> getList() {
        return mList;
    }
}
