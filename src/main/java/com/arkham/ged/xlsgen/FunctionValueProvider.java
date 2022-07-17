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
package com.arkham.ged.xlsgen;

import org.apache.poi.ss.usermodel.Sheet;

/**
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 27 août 2018
 */
public interface FunctionValueProvider {
	/**
	 * Get the current sheet
	 *
	 * @return The current sheet
	 */
	Sheet getSheet();

	/**
	 * Get the current index
	 *
	 * @return The current index
	 */
	int getIndex();

	/**
	 * Get the value of a global property. The property file is read from general/properties element
	 *
	 * @param name The property name
	 * @return The value of property
	 */
	String getProperty(String name);
}
