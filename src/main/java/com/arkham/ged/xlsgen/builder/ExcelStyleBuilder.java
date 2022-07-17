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

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkham.ged.yaml.BorderType;
import com.arkham.ged.yaml.ColorType;
import com.arkham.ged.yaml.FontType;
import com.arkham.ged.yaml.FormatType;
import com.arkham.ged.yaml.RootExcel;
import com.arkham.ged.yaml.StyleType;
import com.arkham.ged.yaml.UnderlineType;

/**
 * Définition des formats, polices etc ... depuis RootExcel. Cette classe est responsable du maintient des styles créés.
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 2 août 2018
 */
public final class ExcelStyleBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelStyleBuilder.class);

	private final Map<String, CellStyle> mCellStyles = new HashMap<>();
	private final Map<String, FormatType> mDataFormats = new HashMap<>();
	private final Map<String, ColorType> mColorTypes = new HashMap<>();
	private final Map<String, FontType> mFontTypes = new HashMap<>();
	private final Map<String, BorderType> mBorderTypes = new HashMap<>();

	private final RootExcel mRe;
	private final Workbook mWb;
	private final CreationHelper mHelper;

	/**
	 * Constructor ExcelStyleBuilder
	 *
	 * @param re The root JAXB
	 * @param wb The current workbook used to create new styles
	 */
	public ExcelStyleBuilder(final RootExcel re, final Workbook wb) {
		mRe = re;
		mWb = wb;

		// Keep reference helper
		mHelper = wb.getCreationHelper();

		// Now can create styles
		createStyles();
	}

	/**
	 * Get a predefined style, return <code>null</code> if style is <code>null</code> or style is not defined
	 *
	 * @param style The predefined style
	 * @return The POI cell style
	 */
	public CellStyle getCellStyle(final String style) {
		if (style != null) {
			return mCellStyles.get(style);
		}

		return null;
	}

	/**
	 * Get the workbook helper
	 *
	 * @return The helper of the workbook
	 */
	public CreationHelper getHelper() {
		return mHelper;
	}

	private void createColors() {
		if (mRe.getColor() != null) {
			for (final ColorType ct : mRe.getColor()) {
				if (ct.getColor() != null) {
					try {
						final HSSFColorPredefined color = HSSFColorPredefined.valueOf(ct.getColor());
						ct.setIndex(color.getIndex());
						mColorTypes.put(ct.getName(), ct);

						LOGGER.info("createColors() : color added \"{}\" for index={}", ct.getName(), ct.getIndex());
					} catch (@SuppressWarnings("unused") final IllegalArgumentException e) { // NOSONAR : not a blocking problem
						LOGGER.error("createColors() : bad color name \"{}\"", ct.getColor());
					}
				} else {
					LOGGER.warn("createColors() : color badly defined \"{}\"", ct.getName());
				}
			}
		}
	}

	private void createFonts() {
		if (mRe.getFont() != null) {
			for (final FontType ft : mRe.getFont()) {
				mFontTypes.put(ft.getName(), ft);

				LOGGER.info("createFonts() : font added \"{}\"", ft.getName());
			}
		}
	}

	private void createBorders() {
		if (mRe.getBorder() != null) {
			for (final BorderType bt : mRe.getBorder()) {
				mBorderTypes.put(bt.getName(), bt);

				LOGGER.info("createBorders() : border definition added \"{}\"", bt.getName());
			}
		}
	}

	private void createFormats() {
		if (mRe.getFormat() != null) {
			for (final FormatType ft : mRe.getFormat()) {
				if (ft.getValue() != null) {
					final DataFormat format = mWb.createDataFormat();
					final short idx = format.getFormat(ft.getValue());
					ft.setIndex(idx);

					mDataFormats.put(ft.getName(), ft);

					LOGGER.info("createFormats() : format added \"{}\" ({}) = {}", ft.getName(), idx, ft.getValue());
				} else {
					LOGGER.error("createFormats() : bad format \"{}\" ({})", ft.getName(), ft.getValue());
				}
			}
		}
	}

	private short getColorIndex(final String color) {
		if (color != null && !"".equals(color.trim())) {
			final ColorType ct = mColorTypes.get(color);
			if (ct != null) {
				short index = ct.getIndex();
				if (index == -1) { // Color is not defined
					index = 0x40; // default predifined value

					LOGGER.warn("getColorIndex() : color \"{}\" is not defined, using default value", color);
				}

				return index;
			}
		}

		return -1;
	}

	private void adoptFormat(final StyleType st, final CellStyle cs) {
		if (st.getFormat() != null) {
			final FormatType ft = mDataFormats.get(st.getFormat());
			if (ft != null) {
				cs.setDataFormat(ft.getIndex());

				if (ft.getAlign() != null) {
					final HorizontalAlignment ha = PoiTypeConverter.convert(ft.getAlign());
					if (ha != null) {
						cs.setAlignment(ha);
					}
				}

				if (ft.getValign() != null) {
					final VerticalAlignment va = PoiTypeConverter.convert(ft.getValign());
					if (va != null) {
						cs.setVerticalAlignment(va);
					}
				}

				cs.setWrapText(Boolean.TRUE.equals(ft.isWrap()));
			} else {
				LOGGER.warn("createStyles() : try to use format \"{}\" which is not defined in format section", st.getFormat());
			}
		}
	}

	private void adoptFont(final StyleType st, final CellStyle cs) {
		if (st.getFont() != null) {
			final FontType ft = mFontTypes.get(st.getFont());
			if (ft != null) {
				final Font f = mWb.createFont();
				f.setFontName(ft.getPolice());
				if (ft.getHeight() != null) {
					f.setFontHeightInPoints(ft.getHeight());
				}
				if (ft.isBold() != null) {
					f.setBold(ft.isBold());
				}
				if (ft.isItalic() != null) {
					f.setItalic(ft.isItalic());
				}
				if (ft.isStrikeout() != null) {
					f.setStrikeout(ft.isStrikeout());
				}
				final UnderlineType ut = ft.getUnderline();
				if (ut != null) {
					f.setUnderline(PoiTypeConverter.convert(ut));
				}

				cs.setFont(f);

				final short color = getColorIndex(ft.getColor());
				if (color > -1) {
					f.setColor(color);
				}
			} else {
				LOGGER.warn("createStyles() : reference to font \"{}\" which is not defined", st.getFont());
			}
		}
	}

	private void adoptBorder(final StyleType st, final CellStyle cs) {
		if (st.getBorder() != null) {
			final BorderType bt = mBorderTypes.get(st.getBorder());
			if (bt != null) {
				// Couleur de bordure (-1 si pas précisé, donc ne rien appliquer dans le cas)
				final short bColor = getColorIndex(bt.getBcolor());
				if (bColor != -1) {
					cs.setBottomBorderColor(bColor);
					cs.setTopBorderColor(bColor);
					cs.setLeftBorderColor(bColor);
					cs.setRightBorderColor(bColor);
				}

				// Méga casse-noix : NPE si on passe null, ça aurait été plus simple de ne rien faire
				BorderStyle bs = PoiTypeConverter.convert(bt.getBottom());
				if (bs != null) {
					cs.setBorderBottom(bs);
				}
				bs = PoiTypeConverter.convert(bt.getTop());
				if (bs != null) {
					cs.setBorderTop(bs);
				}
				bs = PoiTypeConverter.convert(bt.getLeft());
				if (bs != null) {
					cs.setBorderLeft(bs);
				}
				bs = PoiTypeConverter.convert(bt.getRight());
				if (bs != null) {
					cs.setBorderRight(bs);
				}

				// Traitement de la couleur de fond
				final short color = getColorIndex(bt.getBgcolor());
				cs.setFillForegroundColor(color);
				// Le fond
				final FillPatternType fpt = PoiTypeConverter.convert(bt.getFill());
				if (fpt != null) {
					cs.setFillPattern(fpt);
				}
			} else {
				LOGGER.info("createStyles() : try to use border \"{}\" which is not defined", st.getBorder());
			}
		}
	}

	private void createStyles() {
		// First create colors, fonts and borders
		createColors();
		createFonts();
		createBorders();

		// Secondly create formats
		createFormats();

		if (mRe.getStyle() != null) {
			for (final StyleType st : mRe.getStyle()) {
				// On loggue la création du style
				LOGGER.info("createStyles() : create new style \"{}\"", st.getName());

				final CellStyle cs = mWb.createCellStyle();
				if (st.isLock() != null) {
					cs.setLocked(st.isLock());
				} else {
					cs.setLocked(false);
				}

				// On positionne d'abord le format
				adoptFormat(st, cs);

				// Puis la police
				adoptFont(st, cs);

				// Et enfin les bordures
				adoptBorder(st, cs);

				// On ajoute à la map des styles prédéfinis
				mCellStyles.put(st.getName(), cs);
			}
		}
	}
}
