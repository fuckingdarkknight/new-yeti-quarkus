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
package com.arkham.ged.xlsgen.function;

import com.arkham.common.solver.function.Function;
import com.arkham.common.solver.function.FunctionExecutionException;
import com.arkham.ged.xlsgen.FunctionValueProvider;

/**
 * Get a global property value
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 4 févr. 2020
 */
public class PropertyFunction extends Function {
    private final FunctionValueProvider mFvp;

    PropertyFunction(FunctionValueProvider fvp) {
        mFvp = fvp;
    }

    @Override
    public String getName() {
        return "property";
    }

    @Override
    protected Class<?>[] getParamsClass() {
        return new Class[] { String.class };
    }

    @Override
    protected int getMinParamCount() {
        return 1;
    }

    @Override
    public Object invoke(Object... o) throws FunctionExecutionException {
        final var name = (String) o[0];

        return mFvp.getProperty(name);
    }
}
