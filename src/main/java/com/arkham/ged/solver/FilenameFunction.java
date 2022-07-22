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

import com.arkham.common.solver.function.Function;
import com.arkham.common.solver.function.FunctionExecutionException;

/**
 * Function used to get its filename (simple name).
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public class FilenameFunction extends Function {
    private final File mFile;

    /**
     * Constructor FilenameFunction
     *
     * @param file The file
     */
    public FilenameFunction(File file) {
        mFile = file;
    }

    @Override
    public String getName() {
        return "filename";
    }

    @Override
    public Object invoke(Object... params) throws FunctionExecutionException {
        if (mFile != null) {
            return mFile.getName();
        }

        return "";
    }
}
