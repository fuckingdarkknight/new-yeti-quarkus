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
 * Get the current index (row) in Xls
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 24 août 2018
 */
public class CurrentIndexFunction extends Function {
	private final FunctionValueProvider mFvp;

	CurrentIndexFunction(FunctionValueProvider fvp) {
		mFvp = fvp;
	}

	@Override
	public String getName() {
		return "current";
	}

	@Override
	public Object invoke(Object... params) throws FunctionExecutionException {
		return mFvp.getIndex();
	}
}
