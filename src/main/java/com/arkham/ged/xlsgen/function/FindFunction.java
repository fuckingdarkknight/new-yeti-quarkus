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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;

import com.arkham.common.solver.function.Function;
import com.arkham.common.solver.function.FunctionExecutionException;
import com.arkham.ged.xlsgen.FunctionValueProvider;

/**
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 24 août 2018
 */
public class FindFunction extends Function {
    private static final Integer DEF = Integer.valueOf(-1);
    private static final int MAX_SCAN = 1000;

    private final FunctionValueProvider mFvp;

    FindFunction(FunctionValueProvider fvp) {
        mFvp = fvp;
    }

    @Override
    public String getName() {
        return "find";
    }

    @Override
    protected Class<?>[] getParamsClass() {
        return new Class[] { String.class, Long.class };
    }

    @Override
    protected int getMinParamCount() {
        return 2;
    }

    @Override
    public Object invoke(Object... params) throws FunctionExecutionException {
        final var ta = (String) params[0];
        final var indexStart = (long) params[1];

        // "A:C"
        final var cols = ta.split(":");

        final var c1 = new CellReference(cols[0]);
        final var c2 = new CellReference(cols[1]);

        // Ensure col1 >= col2
        final var col1 = Math.min(c1.getCol(), c2.getCol());
        final var col2 = Math.max(c1.getCol(), c2.getCol());

        var index = indexStart;
        final var indexMax = indexStart + MAX_SCAN;

        // Security : stop scanning after index + MAX
        while (index < indexMax) {
            final var row = mFvp.getSheet().getRow((int) index);
            if (isEmpty(col1, col2, row)) {
                return index;
            }

            index++;
        }

        return DEF;
    }

    private static boolean isEmpty(int col1, int col2, Row row) {
        if (row == null) {
            return true;
        }

        // Bound included
        var res = true;
        for (var i = col1; i <= col2; i++) {
            res = res && isEmpty(row.getCell(i));
        }

        return res;
    }

    private static boolean isEmpty(Cell cell) {
        if (cell == null) {
            return true;
        }

        // final CellType ct = cell.getCellTypeEnum();
        switch (cell.getCellType()) {
            case BLANK:
                return true;

            case NUMERIC:
                return false;

            default:
                break;
        }

        final var v = cell.getStringCellValue();

        return v == null || "".equals(v.trim());
    }
}
