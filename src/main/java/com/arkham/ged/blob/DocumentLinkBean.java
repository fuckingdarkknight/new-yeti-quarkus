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
package com.arkham.ged.blob;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.arkham.ged.parameters.DocumentLink;
import com.arkham.ged.util.GedUtil;

/**
 * Bean that represents the DOCLNK table
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public class DocumentLinkBean extends DocumentLink implements Cloneable {
	private int mFlgtrt;

	/**
	 * Constructor DocumentLinkBean
	 */
	public DocumentLinkBean() {
		setUsername(IDocumentLinkConstants.DEFAULT_UTICOD); // Default utimod/uticod for tracing into database value
		setDatdoc(GedUtil.getTimeArc());
		setCodsocPhy(-1);
	}

	/**
	 * @return The file system path matching to the main part of MEDIA_BLOB primary key
	 * @see #getFileSystemFilename()
	 */
	public String getFileSystemPath() {
		return getEntity() + "/" + getEnttyp() + "/" + getKeydoc() + "/";
	}

	/**
	 * @return The file name appended by "-" + numver
	 * @see #getFileSystemPath()
	 */
	public String getFileSystemFilename() {
		return GedUtil.suffixFilename(getFilename(), "-" + getNumver());
	}

	/**
	 * Update the fields ACHVTE/TYPEVE/NUMEVE from NOMCLE when TYPTIE="EVE"<br/>
	 * For TYPTIE="OST", update the fields from the bean : nothing to do in this method<br/>
	 * This method should be invoked before any insert in MEDIA_BLOB table.
	 */
	public void updateEventKey() {
		final String nomcle = getKeydoc();
		// NUMEVE is 9 bytes length
		if (nomcle != null && "EVE".equals(getEnttyp()) && nomcle.length() >= 1 + 3 + 9) {
			final String achvteValue = nomcle.substring(0, 1);
			final String typeveValue = nomcle.substring(1, 4);
			final int numeveValue = GedUtil.getInt(nomcle.substring(4, 13), -1);
			if (numeveValue > -1) {
				setAchvte(achvteValue);
				setTypeve(typeveValue);
				setNumeve(numeveValue);
			}

			// Update NUMPOS if needed
			if (nomcle.length() > 1 + 3 + 9) {
				final int numposValue = GedUtil.getInt(nomcle.substring(13), -1);
				if (numposValue > -1) {
					setNumpos(numposValue);
				}
			}
		}
	}

	/**
	 * Update the fields TYPTIE1/SIGTIE1 from NOMCLE when TYPTIE=TIE or TYPTIE=EVE<br/>
	 * This method should be invoked before any insert in MEDIA_BLOB table.<br/>
	 * <b>
	 * This method is a fork from DocumentLinkBean
	 * </b>
	 */
	public void updateThirdKey() {
		final String nomcle = getKeydoc();
		if (nomcle != null && "TIE".equals(getEnttyp()) && nomcle.length() > 3) {
			final String typtieValue = nomcle.substring(0, 3);
			final String sigtieValue = nomcle.substring(3);
			setTyptie(typtieValue);
			setSigtie(sigtieValue);
		}
	}

	/**
	 * Update the fields CODPRO and/or NUMLOT from NOMCLE when TYPTIE=OST or TYPTIE=PRO<br/>
	 * This method should be invoked before any insert in MEDIA_BLOB table.
	 */
	public void updateProductKey() {
		final String nomcle = getKeydoc();
		if (nomcle != null) {
			final int len = nomcle.length();
			if ("PRO".equals(getEnttyp())) {
				setCodpro(nomcle.trim());
			} else if (len > 16 && "OST".equals(getEnttyp())) {
				setCodpro(nomcle.substring(0, 16).trim());
				setNumlot(nomcle.substring(16));
			}
		}

		// This fields are indexed, we need to set a space instead of null value
		if (getCodpro() == null || "".equals(getCodpro())) {
			setCodpro(" ");
		}
		if (getNumlot() == null || "".equals(getNumlot())) {
			setNumlot(" ");
		}
	}

	/**
	 * Updates mandatory fields : if <code>null</code>, change it to space char
	 */
	public void updateMandatoryFields() {
		if (getCoddoc() == null || "".equals(getCoddoc())) {
			setCoddoc(" ");
		}
	}

	/**
	 * Serialize a {@link DocumentLinkBean} and put the result in an array of bytes
	 *
	 * @param bean
	 * @return The array that represents the serialization
	 * @throws DocumentLinkException
	 */
	public static byte[] serialize(DocumentLinkBean bean) throws DocumentLinkException {
		if (bean == null) {
			return new byte[] {};
		}

		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
				oos.writeObject(bean);
				oos.flush();
			}

			return baos.toByteArray();
		} catch (final IOException e) {
			throw new DocumentLinkException(e);
		}
	}

	/**
	 * Unserialize an array that represents a {@link DocumentLinkBean}
	 *
	 * @param obj The byte array that represents the serialized bean
	 * @return The object or <code>null</code> if obj is <code>null</code>
	 * @throws DocumentLinkException
	 */
	public static DocumentLinkBean unserialize(byte[] obj) throws DocumentLinkException {
		if (obj == null) {
			return null;
		}

		try (ByteArrayInputStream bais = new ByteArrayInputStream(obj)) {
			try (ObjectInputStream ois = new ObjectInputStream(bais)) {
				final Object result = ois.readObject();

				return (DocumentLinkBean) result;
			}
		} catch (IOException | ClassNotFoundException e) {
			throw new DocumentLinkException(e);
		}
	}

	@Override
	public final void setUsername(String value) { // NOSONAR
		super.setUsername(value);
	}

	@Override
	public final void setDatdoc(String value) { // NOSONAR
		super.setDatdoc(value);
	}

	@Override
	public final void setCodsocPhy(int value) { // NOSONAR
		super.setCodsocPhy(value);
	}

	@Override
	public String getCoddoc() {
		final String result = super.getCoddoc();
		if (result == null || "".equals(result.trim())) {
			return " ";
		}

		return result;
	}

	/**
	 * @return the flgtrt
	 */
	public int getFlgtrt() {
		return mFlgtrt;
	}

	/**
	 * @param flgtrt the flgtrt to set
	 */
	public void setFlgtrt(int flgtrt) {
		mFlgtrt = flgtrt;
	}

	/**
	 * @return A toString like that represents the PK
	 */
	public String primaryKey() {
		return getEntity() + "/" + getEnttyp() + "/" + getKeydoc() + "/" + getCoddoc() + "/" + getFilename() + "/" + getNumver();
	}

	/**
	 * @return A toString like that represents the Unique Key (INDSEQ)
	 */
	public String uniqueKey() {
		return getEntity() + "/" + getIndseq();
	}

	@Override
	public String toString() {
		return getFileSystemPath() + getFilename() + "/" + getNumver();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
