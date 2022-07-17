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

import java.util.ArrayList;
import java.util.List;

/**
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 27 août 2018
 */
public class ExprSplitter {
	private final List<ExprSplitterBean> mList = new ArrayList<>();

	private final String mValue;
	private int mIndex;
	private int mIndexStart;
	private boolean mIsExpr;

	/**
	 * Constructor ExprSplitter
	 *
	 * @param value The value to parse
	 */
	public ExprSplitter(String value) {
		mValue = value;

		if (value != null && !"".equals(value.trim())) {
			parse();
		}
	}

	private void next() {
		mIndex++;
	}

	private void push() {
		if (mIndexStart < mIndex) {
			final ExprSplitterBean esb = new ExprSplitterBean(mValue.substring(mIndexStart, mIndex), mIsExpr);
			mList.add(esb);
		}
		mIndexStart = mIndex;
	}

	private void parse() {
		while (mIndex < mValue.length()) {
			final char c = mValue.charAt(mIndex);
			if (c == '`') {
				// Start or end, must push the expression to the list
				push();

				// Expression start
				if (!mIsExpr) {
					mIsExpr = true;
				} else {
					// End of expression
					mIsExpr = false;
				}

				// Restart to next char
				mIndexStart = mIndex + 1;
			}

			next();
		}

		push();
	}

	/**
	 * Get the list of {@link ExprSplitterBean}
	 *
	 * @return The list of splitted expressions
	 */
	public List<ExprSplitterBean> getSplitted() {
		return mList;
	}
}
