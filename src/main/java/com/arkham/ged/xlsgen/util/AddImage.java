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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;

import com.arkham.ged.util.GedUtil;
import com.arkham.ged.yaml.ImageBehaviorType;

/**
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 22 janv. 2020
 */
public class AddImage {
    private final Sheet mSheet;

    /**
     * Constructor AddImage
     *
     * @param sheet
     */
    public AddImage(final Sheet sheet) {
        mSheet = sheet;
    }

    public void addImageToSheet(final CellReference cr, final InputStream imageFile, final double w, final double h, final ImageBehaviorType resizeBehaviour, final int imageFormat) throws IOException {
        final var baos = new ByteArrayOutputStream();
        GedUtil.copyIs2Os(imageFile, baos, 8192);
        final var b = baos.toByteArray();

        @SuppressWarnings("resource")
        final var wb = mSheet.getWorkbook();
        final var helper = wb.getCreationHelper();
        final var pictureIdx = wb.addPicture(b, imageFormat);
        final var anchor = helper.createClientAnchor();
        anchor.setCol1(cr.getCol());
        anchor.setRow1(cr.getRow());
        final Drawing drawing = mSheet.createDrawingPatriarch();
        final var pict = drawing.createPicture(anchor, pictureIdx);

        // final Dimension dim = pict.getImageDimension();
        // final double widthRatio = w/dim.getWidth();
        pict.resize(1);
        // pict.resize(4, 2);
    }
}
