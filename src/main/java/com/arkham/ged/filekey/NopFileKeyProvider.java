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
package com.arkham.ged.filekey;

import java.io.File;
import java.sql.Connection;
import java.util.List;

import com.arkham.ged.properties.OptionalParameterType;
import com.arkham.ged.properties.PropertiesAdapter;

/**
 * Do nothing as provider
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 24 janv. 2020
 */
public class NopFileKeyProvider extends FileKeyProvider {
    @Override
    public FileKey getKey(File file, Connection con, PropertiesAdapter pa, List<OptionalParameterType> opt) throws FileKeyProviderException {
        return new FileKey(0, "xxx", "xxx", file.getName());
    }

    @Override
    public boolean isRefFile() {
        return false;
    }
}
