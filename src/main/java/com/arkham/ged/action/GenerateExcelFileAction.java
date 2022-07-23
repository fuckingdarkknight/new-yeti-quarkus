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
package com.arkham.ged.action;

import java.io.File;
import java.sql.Connection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkham.ged.blob.DocumentLinkBean;
import com.arkham.ged.filekey.FileKey;
import com.arkham.ged.properties.InputScanFileDef;
import com.arkham.ged.properties.PropertiesAdapter;
import com.arkham.ged.xlsgen.ExcelGenerator;
import com.arkham.ged.xlsgen.XlsgenException;

/**
 * Generate an Excel file from YAML stream
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 18 juil. 2018
 */
public class GenerateExcelFileAction extends AbstractAction<InputScanFileDef, File> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateExcelFileAction.class);

    @Override
    public List<DocumentLinkBean> execute(File file, FileKey fk, Connection con, PropertiesAdapter pa, List<DocumentLinkBean> bean, InputScanFileDef sfd) throws ActionException {
        try {
            final var charset = getParamStringValue(pa, "charset", "UTF-8");
            final var path = file.toPath();

            LOGGER.info("execute() : processing YAML file {} using charset {}", path.toAbsolutePath(), charset);

            final var eg = new ExcelGenerator(path, charset);
            eg.generate(con);
        } catch (final XlsgenException e) {
            throw new ActionException(e);
        }

        return bean;
    }
}
