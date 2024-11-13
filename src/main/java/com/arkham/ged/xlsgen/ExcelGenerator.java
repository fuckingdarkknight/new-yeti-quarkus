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

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.arkham.common.solver.expr.ExprException;
import com.arkham.common.solver.expr.ExprSolver;
import com.arkham.ged.message.GedMessages;
import com.arkham.ged.streams.StreamProtocolAdapter;
import com.arkham.ged.streams.StreamProtocolException;
import com.arkham.ged.streams.StreamProtocolFactory;
import com.arkham.ged.util.GedUtil;
import com.arkham.ged.xlsgen.builder.ExcelMetadataBuilder;
import com.arkham.ged.xlsgen.builder.ExcelStyleBuilder;
import com.arkham.ged.xlsgen.function.XlsgenExprValueProvider;
import com.arkham.ged.xlsgen.transformer.DateInnerTransformer;
import com.arkham.ged.xlsgen.transformer.DateTransformer;
import com.arkham.ged.xlsgen.transformer.DateZnTransformer;
import com.arkham.ged.xlsgen.transformer.ITransformer;
import com.arkham.ged.xlsgen.transformer.NumberTransformer;
import com.arkham.ged.xlsgen.types.AlignmentTypeDeserializer;
import com.arkham.ged.xlsgen.types.BorderStyleTypeDeserializer;
import com.arkham.ged.xlsgen.types.CellValueTypeDeserializer;
import com.arkham.ged.xlsgen.types.ErrorAppender;
import com.arkham.ged.xlsgen.types.FillTypeDeserializer;
import com.arkham.ged.xlsgen.types.ImageBehaviorTypeDeserializer;
import com.arkham.ged.xlsgen.types.ModeBehaviorTypeDeserializer;
import com.arkham.ged.xlsgen.types.RowModeTypeDeserializer;
import com.arkham.ged.xlsgen.types.UnderlineTypeDeserializer;
import com.arkham.ged.xlsgen.types.VerticalAlignmentTypeDeserializer;
import com.arkham.ged.xlsgen.util.AddDimensionedImage;
import com.arkham.ged.xlsgen.util.AddImage;
import com.arkham.ged.xlsgen.util.ExcelUtil;
import com.arkham.ged.xlsgen.util.ExprSplitter;
import com.arkham.ged.xlsgen.util.ExprSplitterBean;
import com.arkham.ged.yaml.AlignmentType;
import com.arkham.ged.yaml.BodyType;
import com.arkham.ged.yaml.BorderStyleType;
import com.arkham.ged.yaml.CellType;
import com.arkham.ged.yaml.CellValueType;
import com.arkham.ged.yaml.FillType;
import com.arkham.ged.yaml.GroupType;
import com.arkham.ged.yaml.HeaderType;
import com.arkham.ged.yaml.ImageBehaviorType;
import com.arkham.ged.yaml.ModeBehaviorType;
import com.arkham.ged.yaml.PostType;
import com.arkham.ged.yaml.RootExcel;
import com.arkham.ged.yaml.RowModeType;
import com.arkham.ged.yaml.RowType;
import com.arkham.ged.yaml.TabType;
import com.arkham.ged.yaml.UnderlineType;
import com.arkham.ged.yaml.VerticalAlignmentType;
import org.yaml.snakeyaml.LoaderOptions;

/**
 * Generate XLS file from YAML flow
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 18 juil. 2018
 */
public class ExcelGenerator implements FunctionValueProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelGenerator.class);

    /**
     * Date transformer
     */
    private static final ITransformer DT = new DateTransformer();

    private static final ITransformer DTZN = new DateZnTransformer();

    private static final ITransformer DTINNER = new DateInnerTransformer();

    /**
     * Numeric string to double transformer (optimus prime)
     */
    private static final ITransformer NT = new NumberTransformer();

    /**
     * Either a file to parse in the given charset, either directly the message
     */
    private Path mPath;
    private String mCharset;

    /**
     * YAML Message
     */
    private String mMessage;

    private String mAuthor;

    private final ExcelUtil mEu;
    private ExcelStyleBuilder mEsb;

    private ExprSolver mEs;

    private final ErrorAppender mEa;

    /**
     * Global behavior in case of exception : continue or stop processing
     */
    private ModeBehaviorType mReportMode;

    /**
     * Current workbook
     */
    private Workbook mWorkbook;

    /**
     * Current sheet
     */
    private Sheet mSheet;

    /**
     * Current index
     */
    private int mIndex;

    /**
     * Global properties used by "property" function
     */
    private Map<String, String> mProperties;

    /**
     * Constructor ExcelGenerator
     *
     * @param path The YAML filename to parse
     * @param charset The charset of YAML encoding
     */
    public ExcelGenerator(final Path path, final String charset) {
        this();

        mPath = path;
        mCharset = charset;
    }

    /**
     * Constructor ExcelGenerator
     *
     * @param message The YAML message
     */
    public ExcelGenerator(final String message) {
        this();

        mMessage = message;
    }

    /**
     * Private constructor ExcelGenerator
     */
    private ExcelGenerator() {
        mEu = new ExcelUtil();
        mEa = new ErrorAppender();
    }

    /**
     * @param re The general information about YAML
     * @return The output filename to generate
     */
    private static String getOutputFilename(final RootExcel re) {
        final var f = re.getGeneral().getOutput();
        if (f != null) {
            return f;
        }

        return GedUtil.suffixFilename(re.getGeneral().getModel(), "_processed");
    }

    private HyperlinkType getLinkProtocol(final String link) {
        if (link != null) {
            try {
                final var p = link.substring(0, link.indexOf(':'));
                return HyperlinkType.valueOf(p);
            } catch (IndexOutOfBoundsException | IllegalArgumentException e) { // NOSONAR : not blocking
                processException(e, "getLinkProtocol() : cannot find link protocol for {}", link);
            }
        }

        return null;
    }

    private String getLinkValue(final String link) {
        if (link != null) {
            try {
                return link.substring(link.indexOf(':') + 1);
            } catch (final IndexOutOfBoundsException e) { // NOSONAR : not blocking
                processException(e, "getLinkProtocol() : invalid link for {}", link);
            }
        }

        return null;
    }

    private static ExprSolver createSolver(final FunctionValueProvider fvp) {
        final var vp = new XlsgenExprValueProvider(fvp);
        return new ExprSolver(vp);
    }

    private int getIntSolved(final String expr) {
        try {
            final var result = mEs.solve(expr);
            if (result.getClass() == long.class || result.getClass() == Long.class) {
                // Little mic-mac,
                final var res = (long) result;
                return (int) res;
            }
        } catch (final ExprException e) {
            processException(e, "getIntSolved() : unable to solve expression=\"{}\" because of exception={}", expr, e);
        }

        return -1;
    }

    private String getStringSolved(final String expr) {
        if (expr == null) {
            return null;
        }

        final var splitter = new ExprSplitter(expr);
        final var esb = splitter.getSplitted();
        final var result = new StringBuilder();
        for (final ExprSplitterBean b : esb) {
            if (b.isIsExpr()) {
                try {
                    final var o = mEs.solve(b.getExpr());
                    if (o != null) {
                        result.append(o);
                    }
                } catch (final ExprException e) {
                    processException(e, "getStringSolved() : unable to solve expression=\"{}\" because of exception={}", b.getExpr(), e);
                }
            } else {
                result.append(b.getExpr());
            }
        }

        return result.toString();
    }

    private void updateCell(final Cell cell, final CellType ct) {
        // Convenient test : the cell should never be null at this point
        if (cell == null) {
            return;
        }

        // Create a link on the cell, independently of cell value (see above for types of hyperlinks)
        applyLink(cell, ct);
        // Apply the style
        applyStyle(cell, ct.getStyle());

        final var value = ct.getValue();
        Object o = value;

        switch (ct.getType()) {
            case NUMERIC:
                o = mEu.cast(String.class, NT, value);
                break;
            case DATE:
                o = mEu.cast(String.class, DT, value);
                break;
            case DATEZN:
                o = mEu.cast(String.class, DTZN, value);
                break;
            case DATEINNER:
                o = mEu.cast(String.class, DTINNER, value);
                break;
            case STRING:
                o = getStringSolved(value);
                break;
            case FORMULA:
                applyFormula(cell, getStringSolved(value));
                break;
            case URL:
            case FILE:
            case EMAIL:
                // Map POI hyperlinks from ged type : are the same, not a problem
                final var link = mEsb.getHelper().createHyperlink(HyperlinkType.valueOf(ct.getType().toString()));
                link.setAddress(value.trim());
                cell.setHyperlink(link);
                break;
            default:
                break;
        }

        if (ct.getType() != CellValueType.FORMULA) {
            mEu.setCellValue(cell, o);
        }
    }

    /**
     * Create a link on the cell, independtly of cell value (see above for types of hyperlinks)
     *
     * @param cell The cell
     * @param ct The cell type
     */
    private void applyLink(final Cell cell, final CellType ct) {
        if (ct.getLink() != null) {
            final var lt = getLinkProtocol(ct.getLink());
            final var linkValue = getLinkValue(ct.getLink());
            if (lt != null && linkValue != null && !"".equals(linkValue)) {
                final var link = mEsb.getHelper().createHyperlink(lt);
                link.setAddress(linkValue);
                cell.setHyperlink(link);
            }
        }
    }

    /**
     * Apply a pre-defined style at given cell
     *
     * @param cell The cell
     * @param styleRef The style reference that should exist in predefined styles
     */
    private void applyStyle(final Cell cell, final String styleRef) {
        final var cs = mEsb.getCellStyle(styleRef);
        if (cell != null && cs != null) {
            cell.setCellStyle(cs);
        }
    }

    private void applyFormula(final Cell cell, final String formula) {
        if (formula != null && cell != null) {
            try {
                cell.setCellFormula(formula);
            } catch (final FormulaParseException e) { // NOSONAR
                processException(e, "applyFormula({}) : formula \"{}\" is invalid, probably because of range : {}", getSheetName(), formula, e.getMessage());

                mEu.setCellValue(cell, formula);
            }
        }
    }

    /**
     * Set a comment for given cell
     *
     * @param comment The comment (do nothing if <code>null</code>)
     * @param cell The cell
     * @param author The author of the message or <code>null</code> if should not be specified
     * @param colspan Colspan for comment
     * @param rowspan Rowspan for comment
     */
    private void applyComment(final String comment, final Cell cell, final String author, final int colspan, final int rowspan) {
        if (comment != null) {
            if (cell.getCellComment() == null) {
                mEu.setComment(cell, comment, author, colspan, rowspan);
            } else {
                final var addr = new CellAddress(cell.getRowIndex(), cell.getColumnIndex());

                processException(null, "applyComment({}) : cell {} already contains a comment \"{}\"", getSheetName(), addr.formatAsString(), cell.getCellComment().getString());
            }
        }
    }

    private static void applyHeight(final Row row, final Float height) {
        if (height != null) {
            row.setHeightInPoints(height);
        }
    }

    private void applyGroupings(final List<GroupType> lgt) {
        if (lgt != null) {
            for (final GroupType gt : lgt) {
                if (gt.getStart() != null && gt.getEnd() != null) {
                    final var start = new CellReference(gt.getStart());
                    final var end = new CellReference(gt.getEnd());
                    if (start.getCol() >= 0 && end.getCol() >= start.getCol()) {
                        LOGGER.info("applyGroupings() : grouping columns {} to {}", gt.getStart(), gt.getEnd());

                        mSheet.groupColumn(start.getCol(), end.getCol());
                    } else {
                        processException(null, "applyGroupings({}) : grouping badly defined for {} to {}", getSheetName(), gt.getStart(), gt.getEnd());
                    }
                }
            }
        }
    }

    private void applyImage(final Connection con, final Sheet sheet, final CellType ct) {
        if (con != null) {
            final var it = ct.getImage();
            final var cr = new CellReference(ct.getRef());

            if (it != null && cr.getCol() >= 0 && cr.getRow() >= 0) {
                try (final var spa = StreamProtocolFactory.create(it.getValue(), con)) {
                    @SuppressWarnings("resource")
                    final var is = spa.getStream(); // need to get the stream before the filename (else null depending on SPA type)
                    final var ext = GedUtil.getFileExtension(spa.getStreamName());
                    var imageFormat = -1;
                    if ("png".equalsIgnoreCase(ext)) {
                        imageFormat = Workbook.PICTURE_TYPE_PNG;
                    } else if ("jpg".equalsIgnoreCase(ext)) {
                        imageFormat = Workbook.PICTURE_TYPE_JPEG;
                    }

                    if (imageFormat != -1) {
                        if (it.getBehavior() == ImageBehaviorType.LETITBE) {
                            final var ai = new AddImage(sheet);
                            ai.addImageToSheet(cr, is, it.getWidth(), it.getHeight(), it.getBehavior(), imageFormat);
                        } else {
                            final var adi = new AddDimensionedImage(sheet);
                            adi.addImageToSheet(cr.getCol(), cr.getRow(), is, it.getWidth(), it.getHeight(), it.getBehavior(), imageFormat);
                        }
                    } else {
                        processException(null, "applyImage({}) : bad file extension, should be jpg or png", getSheetName());
                    }
                } catch (final StreamProtocolException | IOException e) { // NOSONAR : not a blocking problem
                    processException(e, "applyImage({}) : exception raised while adding image {} at {}", getSheetName(), it.getValue(), cr);
                }
            }
        }
    }

    private static void applyColspan(final Sheet sheet, final int col, final int row, final Integer colspan) {
        if (colspan != null && colspan > 1) {
            final var coltarget = col - 1 + colspan;
            final var r = new CellRangeAddress(row, row, col, coltarget);

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("applyColspan() : merging region {} => {}, {}, {}, {}", r.formatAsString(sheet.getSheetName(), false), Integer.valueOf(row), Integer.valueOf(row), Integer.valueOf(col), Integer.valueOf(coltarget));
            }

            sheet.addMergedRegion(new CellRangeAddress(row, row, col, coltarget));
        }
    }

    /**
     * Load YAML file given by constructor using provided charset (use Jackson deserializer)
     *
     * @return RootExcel object type
     * @throws XlsgenException Escalation from IOException
     */
    private RootExcel readYaml() throws XlsgenException {
        if (mMessage == null && mPath == null) {
            throw new XlsgenException(GedMessages.Xls.badMessageException);
        }

        try (var reader = mMessage != null ? new StringReader(mMessage) : new InputStreamReader(Files.newInputStream(mPath), Charset.forName(mCharset))) {
            return readYaml(reader);
        } catch (final IOException e) {
            throw new XlsgenException(e);
        }
    }

    private RootExcel readYaml(final Reader reader) throws IOException {
        // Fonctionnalité inopérante avec le parser YAML (snake). Une exception est levée systématiquement, sans possibilité
        // d'intervenir sur la gravité du truc (change caractère de remplacement, ce qui m'irait très très bien).
        // org.yaml.snakeyaml.reader.StreamReader
        final var builder = YAMLFactory.builder();
        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setCodePointLimit(50 * 1024 * 1024); // 50 MB
        builder.loaderOptions(loaderOptions);
        // builder.enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS); // NOSONAR
        // builder.enable(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES); // NOSONAR
        // builder.enable(JsonReadFeature.ALLOW_YAML_COMMENTS); // NOSONAR
        final var factory = builder.build();
        final var mapper = new ObjectMapper(factory);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        final var module = new SimpleModule();
        module.addDeserializer(RowModeType.class, new RowModeTypeDeserializer(mEa));
        module.addDeserializer(CellValueType.class, new CellValueTypeDeserializer(mEa));
        module.addDeserializer(AlignmentType.class, new AlignmentTypeDeserializer(mEa));
        module.addDeserializer(VerticalAlignmentType.class, new VerticalAlignmentTypeDeserializer(mEa));
        module.addDeserializer(BorderStyleType.class, new BorderStyleTypeDeserializer(mEa));
        module.addDeserializer(FillType.class, new FillTypeDeserializer(mEa));
        module.addDeserializer(UnderlineType.class, new UnderlineTypeDeserializer(mEa));
        module.addDeserializer(ImageBehaviorType.class, new ImageBehaviorTypeDeserializer(mEa));
        module.addDeserializer(ModeBehaviorType.class, new ModeBehaviorTypeDeserializer(mEa));
        mapper.registerModule(module);

        return mapper.readValue(reader, RootExcel.class);
    }

    private static int getGreaterColumn(final Sheet sheet) {
        var res = 0;
        // Arbitraire : uniquement sur les 100 premières lignes ... si là on ne trouve rien, on peut
        // supposé que le modèle est vraiment moisi.
        for (var i = 0; i < 100; i++) {
            final var row = sheet.getRow(i);
            if (row != null && res < row.getLastCellNum()) {
                res = row.getLastCellNum();
            }
        }

        return res;
    }

    /**
     * Autosize all columns contained in the sheet
     *
     * @param sheet The sheet
     */
    @SuppressWarnings("static-method")
    protected void autosizeColumns(final Sheet sheet) {
        if (sheet.getPhysicalNumberOfRows() > 0) {
            final var last = getGreaterColumn(sheet);
            for (var i = 0; i < last; i++) {
                final var c = new CellReference(0, i);
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("generate() : adjusting width of column {} => {}", c.formatAsString(), Integer.valueOf(i));
                }

                sheet.autoSizeColumn(i);
            }
        }
    }

    /**
     * Generate XLS file from YAML and model included in flow
     *
     * @param con Optional database connection, may be used if {@link StreamProtocolAdapter} use a DB streamer
     * @return The generated file
     * @throws XlsgenException Exception escalation
     */
    public File generate(final Connection con) throws XlsgenException { // NOSONAR
        final var re = readYaml();

        LOGGER.info("generate() : using model {}", re.getGeneral().getModel());

        try (var wb = WorkbookFactory.create(StreamProtocolFactory.create(re.getGeneral().getModel(), con).getStream(), re.getGeneral().getPassword())) {
            mWorkbook = wb;
            mEsb = new ExcelStyleBuilder(re, mWorkbook);

            // Create global solver with this class as value provider
            mEs = createSolver(this);

            // Author is needed for optional comments on cell
            mAuthor = re.getGeneral().getAuthor();

            // Global properties
            mProperties = readProperties(re.getGeneral().getProperties());

            // Global mode for reporting exception and warning
            mReportMode = re.getGeneral().getReport();

            for (final TabType tab : re.getTab()) {
                // Current sheet
                mSheet = mEu.getSheet(mWorkbook, tab.getId());

                // Cannot process sheet, continue the loop to next ID
                if (mSheet == null) {
                    LOGGER.info("generate() : cannot process sheet {} because it cannot be found in workbook. Should never occurs !!!", tab.getId());
                    continue;
                }

                LOGGER.info("generate() : processing sheet \"{}\"", mSheet.getSheetName());

                // Header is optional
                applyHeader(tab.getHeader(), con);

                applyBody(tab.getBody());

                // Post actions facultatives
                applyPostProcessing(tab.getPost());
            }

            // Help for debugging YAML input
            createSheetErrors();

            // Should ever generate with the same file extension than the source file
            final var ext = GedUtil.getFileExtension(re.getGeneral().getModel());
            final var outFilename = GedUtil.replaceFileExtension(getOutputFilename(re), ext);

            LOGGER.info("generate() : writing workbook {}", outFilename);

            // Write metadatas if specified
            final var emb = new ExcelMetadataBuilder(re);
            emb.write(mWorkbook);

            final var outFile = Paths.get(outFilename);
            try (var os = Files.newOutputStream(outFile)) {
                // Force le recalcul des formules à l'ouverture dans Excel
                mWorkbook.setForceFormulaRecalculation(true);

                mWorkbook.write(os);
            }

            return outFile.toFile();
        } catch (IOException | StreamProtocolException e) {
            throw new XlsgenException(e);
        }
    }

    private void applyHeader(final HeaderType ht, final Connection con) {
        if (ht != null) {
            for (final CellType ct : ht.getCell()) {
                final var name = ct.getRef();
                final var cr = new CellReference(name);

                if (cr.getRow() > -1 && cr.getCol() > -1) {
                    final var row = mEu.getRow(mSheet, cr.getRow());

                    applyHeight(row, ht.getHeight());
                    applyColspan(mSheet, cr.getCol(), cr.getRow(), ct.getColspan());
                    applyImage(con, mSheet, ct);

                    final var cell = mEu.getCell(row, cr.getCol());
                    applyStyle(cell, ct.getStyle());
                    mEu.setCellValue(cell, ct.getValue());
                    applyComment(ct.getComment(), cell, mAuthor, ct.getCcolspan().intValue(), ct.getCrowspan().intValue());
                } else {
                    // Just consider logging when cell reference is bad
                    processException(null, "generate({}) : header ref {} is badly referenced, should be \"B8\" for example", getSheetName(), name);
                }
            }
        }
    }

    private void applyBody(final BodyType bt) {
        // Body is optional
        if (bt != null) {
            // Columns grouping
            applyGroupings(bt.getGroup());

            // Start index and create a stack to push/pop values
            mIndex = bt.getIndex();

            final Deque<Integer> stack = new ArrayDeque<>();
            stack.push(Integer.valueOf(mIndex));

            // Loop for each row to process
            for (final RowType rt : bt.getRow()) {
                int copyFromRow;
                switch (rt.getMode()) {
                    // On pousse l'index courant sur la pile pour pouvoir le réutiliser plus tard
                    case PUSH:
                        stack.push(Integer.valueOf(mIndex));
                        LOGGER.debug("applyBody() : push index {} to stack", Integer.valueOf(mIndex));
                        break;

                    case POP:
                        mIndex = stack.pop();
                        LOGGER.debug("applyBody() : pop index {} from stack", Integer.valueOf(mIndex));
                        break;

                    case INDEX:
                        mIndex = rt.getIndex();
                        LOGGER.debug("applyBody() : set row index at {}", Integer.valueOf(mIndex));
                        break;

                    case CURRENT:
                        LOGGER.debug("applyBody() : set row index at current {}", Integer.valueOf(mIndex));
                        break;

                    case SOLVED:
                        mIndex = getIntSolved(rt.getExpr());
                        LOGGER.debug("applyBody() : set calculated row index at current {} for expression={}", Integer.valueOf(mIndex), rt.getExpr());
                        break;

                    case COPYFROM:
                        // O based index
                        copyFromRow = rt.getIndex();
                        ExcelUtil.copyRow(mWorkbook, mSheet, copyFromRow, mIndex);
                        LOGGER.debug("applyBody() : copy from row={} to row={}", Integer.valueOf(copyFromRow), Integer.valueOf(mIndex));
                        break;

                    case COPYFROMCURRENT:
                        // O based index : previous index
                        copyFromRow = mIndex - 1;
                        ExcelUtil.copyRow(mWorkbook, mSheet, copyFromRow, mIndex);
                        LOGGER.debug("applyBody() : copy from row={} to row={}", Integer.valueOf(copyFromRow), Integer.valueOf(mIndex));
                        break;

                    case INSERTAT:
                        if (rt.getIndex() < mSheet.getLastRowNum()) {
                            // Pas la peine de décaler si on se positionne après le dernier row de la feuille.
                            // (en l'occurence ça lève même une exception, pas choquant)
                            mSheet.shiftRows(rt.getIndex(), mSheet.getLastRowNum(), 1);
                            LOGGER.debug("applyBody() : insert row at index={} (shifting down)", Integer.valueOf(mIndex));
                        }
                        break;

                    default:
                        break;
                }

                // Convenient test to exclude and trace bad index
                // Pour les mode PUSH et POP, il ne faut pas faire de traitement supplémentaire
                if (mIndex < 0 || rt.getMode() == RowModeType.PUSH || rt.getMode() == RowModeType.POP) {
                    if (mIndex < 0) {
                        LOGGER.warn("applyBody() : skipping row for mode={} because of bad row index={}", rt.getMode(), Integer.valueOf(mIndex));
                    }

                    continue;
                }

                // Si la ligne n'existe pas, on en profite pour la créer. Ce cas d'utilisation est peu probable
                // car on va plutôt dupliquer des rows existants dans la plupart des cas.
                final var row = mEu.getRow(mSheet, mIndex);

                for (final CellType ct : rt.getCell()) {
                    final var cellRef = ct.getRef();
                    // Décodage pour récupérer la référence de colonne, le numéro de ligne étant dynamique en fait.
                    // Du coup le formalisme "C8" n'utilise que "C" par exemple
                    final var cr = new CellReference(cellRef);
                    final var cell = mEu.getCell(row, cr.getCol());

                    applyColspan(mSheet, cr.getCol(), mIndex, ct.getColspan());

                    updateCell(cell, ct);
                    applyComment(ct.getComment(), cell, mAuthor, ct.getCcolspan().intValue(), ct.getCrowspan().intValue());
                }

                applyHeight(row, rt.getHeight());

                mIndex++;
            }
        }
    }

    private void applyPostProcessing(final PostType postAction) {
        if (postAction != null) {
            for (final CellType ct : postAction.getCell()) {
                final var cellRef = ct.getRef();
                final var cr = new CellReference(cellRef);

                if (ct.getAdjustment() != null) {
                    mSheet.autoSizeColumn(cr.getCol());

                    LOGGER.info("applyPostProcessing() : adjusting width of column {}", Integer.valueOf(cr.getCol()));
                }

                if ("hidden".equalsIgnoreCase(ct.getVisibility())) {
                    mSheet.setColumnHidden(cr.getCol(), true);

                    LOGGER.info("applyPostProcessing() : hide column {}", Integer.valueOf(cr.getCol()));
                }
            }

            // Seul cas global pris en compte : auto pour toutes les colonnes
            if ("auto".equalsIgnoreCase(postAction.getAdjustment())) {
                autosizeColumns(mSheet);
            }

            var fcol = 0;
            if (postAction.getFreezecol() != null) {
                fcol = postAction.getFreezecol();
            }
            var frow = 0;
            if (postAction.getFreezerow() != null) {
                frow = postAction.getFreezerow();
            }

            if (frow != 0 || fcol != 0) {
                mSheet.createFreezePane(fcol, frow);

                LOGGER.info("applyPostProcessing({}) : freeze pane for col {} row {}", getSheetName(), Integer.valueOf(fcol), Integer.valueOf(frow));
            }

            if (postAction.getProtect() != null) {
                mSheet.protectSheet(postAction.getProtect());

                LOGGER.info("applyPostProcessing({}) : sheet is protected for locked cells", getSheetName());
            }

            if (postAction.isSelect() != null) {
                mWorkbook.setActiveSheet(mWorkbook.getSheetIndex(mSheet));
                // Move to top-left corner
                mSheet.showInPane(0, 0);
                // Buggy in 3.x releases but is fixed in next 4.0 version
                // https://bz.apache.org/bugzilla/show_bug.cgi?id=61905
                mSheet.setActiveCell(new CellAddress(0, 5));
            }

            if (postAction.getAutofilter() != null) {
                final var range = getStringSolved(postAction.getAutofilter());
                LOGGER.info("applyPostProcessing() : compute range {}", range);

                try {
                    final var cra = CellRangeAddress.valueOf(range);
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("applyPostProcessing() : autofilter on range {}", cra.formatAsString());
                    }

                    mSheet.setAutoFilter(cra);
                } catch (final IllegalArgumentException e) {
                    processException(e, "applyPostProcessing({}) : cannot apply autofilter because of exception {}", getSheetName(), e);
                }
            }
        }
    }

    /**
     * Create a new sheet if warning have occured while deserializing or updating cells
     */
    private void createSheetErrors() {
        final var errors = mEa.getList();
        if (!errors.isEmpty() && mReportMode == ModeBehaviorType.REPORT) {
            final var sheet = mWorkbook.createSheet("Warning report");
            for (var i = 0; i < errors.size(); i++) {
                final var row = sheet.createRow(i);
                final var cell = row.createCell(0);
                cell.setCellValue(errors.get(i));
            }
        }
    }

    /**
     * @return The current sheet name
     */
    private String getSheetName() {
        if (mSheet != null) {
            return getSheet().getSheetName();
        }

        return null;
    }

    /**
     * @param e The optional exception, if <code>null</code> it's considered as a warning
     * @param message The message
     * @param params The optional parameters used for translate the message
     * @throws XslgenRuntimeException In case of STRICT mode, the given exception is re-raised as a runtime
     */
    private void processException(final Exception e, final String message, final Object... params) {
        if (e == null) {
            LOGGER.warn(message, params);
        } else {
            LOGGER.error(message, params);
        }

        if (mReportMode == ModeBehaviorType.REPORT) {
            mEa.add(message, params);
        }

        if (mReportMode == ModeBehaviorType.STRICT && e != null) {
            throw new XslgenRuntimeException(e);
        }
    }

    /**
     * Read a property file (UTF-8 charset mandatory)
     *
     * @param filename The file name to read
     * @return A unmodifiable Map of properties
     */
    private final Map<String, String> readProperties(final String filename) {
        if (filename != null) {
            final var p = new Properties();
            try (Reader reader = new InputStreamReader(Files.newInputStream(Paths.get(filename)), StandardCharsets.UTF_8)) {
                p.load(reader);

                return Collections.unmodifiableMap(new HashMap(p));
            } catch (final IOException e) {
                processException(e, "readProperties() : property file cannot be read because of exception {}", e);
            }
        }

        return Collections.unmodifiableMap(new HashMap<String, String>());
    }

    @Override
    public Sheet getSheet() {
        return mSheet;
    }

    @Override
    public int getIndex() {
        return mIndex;
    }

    @Override
    public String getProperty(final String name) {
        return mProperties.get(name);
    }
}
