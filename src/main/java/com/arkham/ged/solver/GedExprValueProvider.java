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
package com.arkham.ged.solver;

import java.io.File;

import com.arkham.common.solver.expr.ExprValueProvider;

/**
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public class GedExprValueProvider extends ExprValueProvider {
    /**
     * Constructor GedExprValueProvider, register functions
     *
     * @param file Current file
     */
    public GedExprValueProvider(File file) {
        registerFunction(new FilenameFunction(file));
        registerFunction(new FilesizeFunction(file));
    }
}
