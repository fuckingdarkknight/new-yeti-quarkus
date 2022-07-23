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

import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFEvaluationWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.SharedFormula;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkham.ged.util.GedUtil;
import com.arkham.ged.xlsgen.transformer.ITransformer;

/**
 * Utility class for XLS
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 18 juil. 2018
 */
public class ExcelUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtil.class);

    /**
     * Set comment on cell
     *
     * @param cell The cell
     * @param message The message
     * @param author The author of the message or <code>null</code> if should not be specified
     * @param colspan Colspan for comment
     * @param rowspan Rowspan for comment
     */
    @SuppressWarnings("static-method")
    public void setComment(final Cell cell, final String message, final String author, final int colspan, final int rowspan) {
        if (cell.getCellComment() == null) {
            final Drawing drawing = cell.getSheet().createDrawingPatriarch();
            @SuppressWarnings("resource")
            final var factory = cell.getSheet().getWorkbook().getCreationHelper();

            // When the comment box is visible, have it show in a 1x3 space
            final var anchor = factory.createClientAnchor();
            anchor.setCol1(cell.getColumnIndex());
            anchor.setCol2(cell.getColumnIndex() + colspan);
            anchor.setRow1(cell.getRowIndex());
            anchor.setRow2(cell.getRowIndex() + rowspan);
            anchor.setDx1(100);
            anchor.setDx2(1000);
            anchor.setDy1(100);
            anchor.setDy2(1000);

            // Create the comment and set the text+author
            final var comment = drawing.createCellComment(anchor);
            final var str = factory.createRichTextString(message);
            comment.setString(str);
            if (author != null) {
                comment.setAuthor(author);
            }
            // Assign the comment to the cell
            cell.setCellComment(comment);
        }
    }

    /**
     * Set value in cell
     *
     * @param cell The cell
     * @param o The value of any type
     */
    @SuppressWarnings("static-method")
    public void setCellValue(final Cell cell, final Object o) {
        if (o instanceof String) {
            cell.setCellValue(((String) o).trim());
        } else if (o instanceof Boolean) {
            cell.setCellValue((Boolean) o);
        } else if (o instanceof Long) {
            cell.setCellValue((Long) o);
        } else if (o instanceof Integer) {
            cell.setCellValue((Integer) o);
        } else if (o instanceof Float) {
            cell.setCellValue((Float) o);
        } else if (o instanceof Double) {
            cell.setCellValue((Double) o);
        } else if (o instanceof Date) {
            cell.setCellValue((Date) o);
        } else {
            // If o is null, nothing to do or to trace
            if (o != null) {
                cell.setCellValue("Unknown type mapping");
            }
        }
    }

    /**
     * Get a row in the sheet
     *
     * @param sheet The sheet used to create a new row if needed at index
     * @param index The index of the row
     * @return The row created or just selected if ever present in sheet
     */
    @SuppressWarnings("static-method")
    public Row getRow(final Sheet sheet, final int index) {
        var res = sheet.getRow(index);
        if (res == null) {
            res = sheet.createRow(index);
        }

        return res;
    }

    /**
     * Get a cell at given position
     *
     * @param row The row (0 based index)
     * @param col The column (0 based index)
     * @return The cell created or just selected if ever present in sheet
     */
    @SuppressWarnings("static-method")
    public Cell getCell(final Row row, final int col) {
        var res = row.getCell(col);
        try {
            if (res == null) {
                res = row.createCell(col);
            }
        } catch (final IllegalArgumentException e) {
            LOGGER.error("getCell() : invalid column reference=\"{}\"", col);

            throw e;
        }

        return res;
    }

    /**
     * Clone a cell style
     *
     * @param wb The workbook
     * @param cs The cell style to copy
     * @return A new cell style
     */
    static CellStyle cloneStyle(final Workbook wb, final CellStyle cs) {
        final var r = wb.createCellStyle();

        r.setAlignment(cs.getAlignment());
        r.setBorderBottom(cs.getBorderBottom());
        r.setBorderTop(cs.getBorderTop());
        r.setBorderLeft(cs.getBorderLeft());
        r.setBorderRight(cs.getBorderRight());
        r.setBottomBorderColor(cs.getBottomBorderColor());
        r.setTopBorderColor(cs.getTopBorderColor());
        r.setLeftBorderColor(cs.getLeftBorderColor());
        r.setRightBorderColor(cs.getRightBorderColor());
        r.setDataFormat(cs.getDataFormat());
        r.setFillBackgroundColor(r.getFillBackgroundColor());
        r.setFillForegroundColor(cs.getFillForegroundColor());
        r.setFillPattern(cs.getFillPattern());
        r.setShrinkToFit(cs.getShrinkToFit());
        r.setVerticalAlignment(cs.getVerticalAlignment());
        r.setWrapText(cs.getWrapText());

        r.setFillForegroundColor((short) 15);
        r.setFillBackgroundColor((short) 12);

        return r;
    }

    /**
     * Get a sheet from a workbook
     *
     * @param wb The workbook
     * @param id The name or the index of the sheet (0 based index)
     * @return The sheet
     */
    @SuppressWarnings("static-method")
    public Sheet getSheet(final Workbook wb, final String id) {
        // Numeric value : index of sheet in workbook
        Sheet sheet = null;
        final var index = GedUtil.getInt(id, -1);
        try {
            if (index < 0) { // Name value, get by name
                sheet = wb.getSheet(id);

            } else { // Numeric value, so get by index
                sheet = wb.getSheetAt(index);
            }
        } catch (@SuppressWarnings("unused") final IllegalArgumentException e) { // NOSONAR
            // Behavior has changed with 4.0 : before null was returned, now it's an exception.
            // In all case, we just have to create the unexisting sheet.
        }

        if (sheet == null) {
            sheet = wb.createSheet(id);
        }

        return sheet;
    }

    /**
     * Copies a row from a row index on the given sheet to another row index. If the destination row is
     * already occupied, shift all rows down to make room.
     *
     * @param workbook The workbook
     * @param worksheet The sheet
     * @param from The index from
     * @param to The index to
     */
    public static void copyRow(final Workbook workbook, final Sheet worksheet, final int from, final int to) {
        // Don't cry Penelope, nothing to do if row indexes are the same
        if (from == to) {
            return;
        }

        final var sourceRow = worksheet.getRow(from);
        if (sourceRow == null) {
            LOGGER.error("copyRow() : source row at index {} does not exist", from);
            return;
        }

        var newRow = worksheet.getRow(to);

        if (alreadyExists(newRow)) {
            worksheet.shiftRows(to, worksheet.getLastRowNum(), 1);
        } else {
            newRow = worksheet.createRow(to);
        }

        for (var i = 0; i < sourceRow.getLastCellNum(); i++) {
            final var oldCell = sourceRow.getCell(i);
            final var newCell = newRow.createCell(i);
            if (oldCell != null) {
                // ROA : don't make a clone, just want to set the same cell style because there is
                // a limit of 4000 styles in an Excel workbook
                // copyCellStyle(workbook, oldCell, newCell);
                newCell.setCellStyle(oldCell.getCellStyle());
                copyCellComment(oldCell, newCell);
                copyCellHyperlink(oldCell, newCell);
                copyCellDataTypeAndValue(oldCell, newCell);
                copyCellFormula(workbook, worksheet, oldCell, newCell);
            }
        }

        copyAnyMergedRegions(worksheet, sourceRow, newRow);
    }

    private static void copyCellFormula(final Workbook workbook, final Sheet sheet, final Cell sourceCell, final Cell targetCell) {
        final var cellType = sourceCell.getCellType();
        if (cellType == CellType.FORMULA && sourceCell.getCellFormula() != null) {
            final var formulaParsingWorkbook = HSSFEvaluationWorkbook.create((HSSFWorkbook) workbook);
            final var sharedFormula = new SharedFormula(SpreadsheetVersion.EXCEL2007);
            final var sharedFormulaPtg = FormulaParser.parse(sourceCell.getCellFormula(), formulaParsingWorkbook, FormulaType.CELL, workbook.getSheetIndex(sheet));

            final var shiftFormula = targetCell.getRowIndex() - sourceCell.getRowIndex();
            final var convertedFormulaPtg = sharedFormula.convertSharedFormulas(sharedFormulaPtg, shiftFormula, 0);
            targetCell.setCellFormula(FormulaRenderer.toFormulaString(formulaParsingWorkbook, convertedFormulaPtg));
        }
    }

    private static void copyCellComment(final Cell oldCell, final Cell newCell) {
        if (newCell.getCellComment() != null) {
            newCell.setCellComment(oldCell.getCellComment());
        }
    }

    private static void copyCellHyperlink(final Cell oldCell, final Cell newCell) {
        if (oldCell.getHyperlink() != null) {
            newCell.setHyperlink(oldCell.getHyperlink());
        }
    }

    private static void copyCellDataTypeAndValue(final Cell oldCell, final Cell newCell) {
        setCellDataType(oldCell, newCell);
        setCellDataValue(oldCell, newCell);
    }

    private static void setCellDataType(final Cell oldCell, final Cell newCell) {
        newCell.setCellType(oldCell.getCellType());
    }

    private static void setCellDataValue(final Cell oldCell, final Cell newCell) {
        switch (oldCell.getCellType()) {
            case BLANK:
                newCell.setCellValue(oldCell.getStringCellValue());
                break;
            case BOOLEAN:
                newCell.setCellValue(oldCell.getBooleanCellValue());
                break;
            case ERROR:
                newCell.setCellErrorValue(oldCell.getErrorCellValue());
                break;
            case FORMULA:
                newCell.setCellFormula(oldCell.getCellFormula());
                break;
            case NUMERIC:
                newCell.setCellValue(oldCell.getNumericCellValue());
                break;
            case STRING:
                newCell.setCellValue(oldCell.getRichStringCellValue());
                break;
            default:
                LOGGER.warn("setCellDataValue() : cell type is badly defined");
        }
    }

    private static boolean alreadyExists(final Row newRow) {
        return newRow != null;
    }

    private static void copyAnyMergedRegions(final Sheet worksheet, final Row sourceRow, final Row newRow) {
        for (var i = 0; i < worksheet.getNumMergedRegions(); i++) {
            copyMergeRegion(worksheet, sourceRow, newRow, worksheet.getMergedRegion(i));
        }
    }

    private static void copyMergeRegion(final Sheet worksheet, final Row sourceRow, final Row newRow, final CellRangeAddress mergedRegion) {
        final var range = mergedRegion;
        if (range.getFirstRow() == sourceRow.getRowNum()) {
            final var lastRow = newRow.getRowNum() + range.getLastRow() - range.getFirstRow();
            final var newCellRangeAddress = new CellRangeAddress(newRow.getRowNum(), lastRow, range.getFirstColumn(), range.getLastColumn());
            worksheet.addMergedRegion(newCellRangeAddress);
        }
    }

    /**
     * Cast the input value via the optional transformer
     *
     * @param clazz The class, should ever be String.class in our case because of YAML value that is a string every time
     * @param transformer The optional transformer
     * @param value The value to cast in the good type
     * @return The value cast
     */
    @SuppressWarnings("static-method")
    public Object cast(final Class clazz, final ITransformer<Object> transformer, final Object value) {
        if (value == null || value instanceof String && ((String) value).trim().length() == 0) {
            if (clazz == double.class) {
                return 0.0D;
            } else if (clazz == int.class) {
                return 0;
            } else if (clazz == String.class) {
                return "";
            }

            return null;
        }

        // Méthode bourrin et pas très élégante, mais peu de code ...
        if (clazz == String.class) {
            // Si on a précisé un NumberFormat au niveau de la colonne, il faut l'utiliser
            if (transformer != null) {
                return transformer.transform(value);
            }

            // Mode par défaut, on laisse valueOf faire le travail
            return String.valueOf(value).trim();
        } else if (clazz == int.class) {
            return castToInt(String.valueOf(value));
        } else if (clazz == double.class) {
            return Double.valueOf(String.valueOf(value));
        }

        return value;
    }

    private static int castToInt(final String value) {
        final var result = Double.parseDouble(value);

        return (int) result;
    }
}
