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

import com.arkham.common.solver.expr.ExprValueProvider;
import com.arkham.ged.xlsgen.FunctionValueProvider;

/**
 * Global provider referencing functions used by Xls transformer.
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 24 août 2018
 */
public class XlsgenExprValueProvider extends ExprValueProvider {
	/**
	 * Constructor XlsgenExprValueProvider
	 *
	 * @param fvp The provider used to solve values
	 */
	public XlsgenExprValueProvider(FunctionValueProvider fvp) {
		registerFunction(new FindFunction(fvp));
		registerFunction(new CurrentIndexFunction(fvp));

		// Global
		registerFunction(new ImportFileFunction());
		registerFunction(new PropertyFunction(fvp));
	}
}
