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
package com.arkham.ged.xlsgen.builder;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import com.arkham.ged.yaml.RootExcel;

/**
 * Metadata writer
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 6 août 2018
 */
public class ExcelMetadataBuilder {
    private final RootExcel mRe;

    /**
     * Constructor ExcelMetadataBuilder
     *
     * @param re The root JAXB
     */
    public ExcelMetadataBuilder(RootExcel re) {
        mRe = re;
    }

    /**
     * Write metadatas (author, title, subject, comments and keywords) to workbook
     *
     * @param wb The current workbook used to create new styles
     */
    public void write(Workbook wb) {
        if (wb instanceof HSSFWorkbook) {
            final var gt = mRe.getGeneral();

            final var hwb = (HSSFWorkbook) wb;
            hwb.createInformationProperties();
            final var summaryInfo = hwb.getSummaryInformation();
            if (gt.getAuthor() != null) {
                summaryInfo.setAuthor(gt.getAuthor());
            }

            if (gt.getTitle() != null) {
                summaryInfo.setTitle(gt.getTitle());
            }

            if (gt.getSubject() != null) {
                summaryInfo.setSubject(gt.getSubject());
            }

            if (gt.getComments() != null) {
                summaryInfo.setComments(gt.getComments());
            }

            if (gt.getKeywords() != null) {
                summaryInfo.setKeywords(gt.getKeywords());
            }
        }
    }
}
