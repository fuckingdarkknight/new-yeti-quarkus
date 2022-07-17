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
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import com.arkham.common.properties.FlatProp;
import com.arkham.ged.message.GedMessages;
import com.arkham.ged.properties.OptionalParameterType;
import com.arkham.ged.properties.PropertiesAdapter;
import com.arkham.ged.util.GedUtil;

/**
 * Provides {@link FileKey} from a "flat" .ref file by using {@link FlatProp} reader capabilities
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public class FlatFileKeyProvider extends FileKeyProvider {
	/**
	 * <p>
	 * Create a file key from a .ref (properties) file.
	 * </p>
	 * <u>Example :</u>
	 *
	 * <pre>
	 * filename=media/ach_generaux/AG152.jpg
	 * lib256=Photographie
	 * typtie=PRO
	 * nomcle=AG152
	 * codsoc=1
	 * codlan=FRA
	 * uticod=PACK
	 * typmed=PHR
	 * </pre>
	 *
	 * {@inheritDoc}
	 */
	@Override
	public FileKey getKey(File file, Connection con, PropertiesAdapter pa, List<OptionalParameterType> opt) throws FileKeyProviderException {
		final FlatProp p;
		try {
			p = getProp(file);
		} catch (final IOException e) {
			throw new FileKeyProviderException(e);
		}

		// If file is empty, it should have ever been processed : not an error case
		if (p.isEmpty()) {
			return null;
		}

		return getFileKey(p);
	}

	@Override
	public boolean isRefFile() {
		return true;
	}

	@SuppressWarnings("static-method")
	protected FlatProp getProp(File file) throws IOException {
		return GedUtil.getProp(file);
	}

	/**
	 * Decode the properties
	 *
	 * @param p properties
	 * @return the {@link FileKey}
	 * @throws FileKeyProviderException in case of malformed properties
	 */
	@SuppressWarnings("static-method")
	protected FileKey getFileKey(FlatProp p) throws FileKeyProviderException {
		// Define the PK
		final String codsoc = p.getProperty("codsoc");
		int entity = -1;
		try {
			entity = Integer.parseInt(codsoc); // the entity
		} catch (final NumberFormatException e) {
			throw new FileKeyProviderException(e, GedMessages.Scanner.codsocInvalid);
		}
		final String nomcle = p.getProperty("nomcle"); // the key (match to NOMCLE)
		final String typtie = p.getProperty("typtie"); // the tier type (PRO, TIE, EVE ...)
		final String filename = p.getProperty("filename"); // the filename to integrate
		if (entity < 0 || nomcle == null || typtie == null || filename == null) {
			throw new FileKeyProviderException(GedMessages.Scanner.keyMalformed);
		}

		// Others fields
		final String lib256 = p.getProperty("lib256");
		final String typmed = p.getProperty("typmed");
		final String codlan = p.getProperty("codlan");
		final String uticod = p.getProperty("uticod");
		final String updateMode = p.getProperty("updateMode");

		final FileKey fk = new FileKey(entity, typtie, nomcle, lib256, typmed, codlan, uticod, filename, updateMode, p);
		fk.setAttribute("valzn1", p.getProperty("valzn1"));
		fk.setAttribute("valzn2", p.getProperty("valzn2"));
		fk.setAttribute("valzn3", p.getProperty("valzn3"));
		fk.setAttribute("valzn4", p.getProperty("valzn4"));
		fk.setAttribute("valzn5", p.getProperty("valzn5"));
		fk.setAttribute("valzn6", p.getProperty("valzn6"));
		fk.setAttribute("valzn7", p.getProperty("valzn7"));
		fk.setAttribute("valzn8", p.getProperty("valzn8"));
		fk.setAttribute("valzn9", p.getProperty("valzn9"));
		fk.setAttribute("valzn10", p.getProperty("valzn10"));
		fk.setAttribute("catdoc", p.getProperty("catdoc"));
		fk.setAttribute("coddoc", p.getProperty("coddoc"));
		fk.setAttribute("numord", GedUtil.getInt(p.getProperty("numord"), 0));
		fk.setAttribute("typdoc", p.getProperty("typdoc"));
		fk.setAttribute("achvte", p.getProperty("achvte"));
		fk.setAttribute("typeve", p.getProperty("typeve"));
		fk.setAttribute("numeve", p.getProperty("numeve"));
		fk.setAttribute("numpos", p.getProperty("numpos"));
		fk.setAttribute("numlig", p.getProperty("numlig"));
		fk.setAttribute("numspo", p.getProperty("numspo"));
		fk.setAttribute("reldir", p.getProperty("reldir"));

		return fk;
	}
}
