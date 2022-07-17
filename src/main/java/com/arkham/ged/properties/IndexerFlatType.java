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
package com.arkham.ged.properties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public class IndexerFlatType {
	private final IndexerType mIndexType;
	private final Map<String, String> mParams;

	IndexerFlatType(IndexerType it) {
		mIndexType = it;

		// Flat parameters
		final List<OptionalParameterType> params = mIndexType.getParam();

		mParams = new HashMap<>(params.size());

		for (final OptionalParameterType opt : params) {
			mParams.put(opt.getName(), opt.getValue());
		}
	}

	public String getExtractor() {
		return mIndexType.getExtractor();
	}

	public String getTarget() {
		return mIndexType.getTarget();
	}

	public Map<String, String> getParams() {
		return mParams;
	}
}
