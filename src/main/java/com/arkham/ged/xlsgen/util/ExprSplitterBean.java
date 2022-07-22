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
package com.arkham.ged.xlsgen.util;

/**
 * Bean that represents an expression
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 27 août 2018
 */
public class ExprSplitterBean {
    private String mExpr;
    private boolean mIsExpr;

    /**
     * Constructor ExprSplitterBean
     *
     * @param expr The string value
     * @param isExpr true if value is an expression
     */
    public ExprSplitterBean(String expr, boolean isExpr) {
        mExpr = expr;
        mIsExpr = isExpr;
    }

    /**
     * @return the expr
     */
    public String getExpr() {
        return mExpr;
    }

    /**
     * @param expr the expr to set
     */
    public void setExpr(String expr) {
        mExpr = expr;
    }

    /**
     * @return the isExpr
     */
    public boolean isIsExpr() {
        return mIsExpr;
    }

    /**
     * @param isExpr the isExpr to set
     */
    public void setIsExpr(boolean isExpr) {
        mIsExpr = isExpr;
    }

    @Override
    public String toString() {
        return "[" + mExpr + ", " + mIsExpr + "]";
    }
}
