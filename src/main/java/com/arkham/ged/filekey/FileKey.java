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

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.arkham.common.properties.FlatProp;
import com.arkham.ged.blob.IDocumentLinkConstants;
import com.arkham.ged.util.GedUtil;

/**
 * A scanner key bean that provides the mandatory (primary key) and optional values for MEDIA_BLOB table
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public final class FileKey {
    public static final String CODSOC = "codsoc";
    public static final String TYPTIE = "typtie";
    public static final String NOMCLE = "nomcle";
    public static final String CODDOC = "coddoc";
    public static final String FILENAME = "filename";
    public static final String DATMOD = "datmod";
    public static final String UTIMOD = "utimod";
    public static final String LIB256 = "lib256";
    public static final String TYPMED = "typmed";
    public static final String CODLAN = "codlan";
    public static final String REFFILE = "refFile";
    public static final String TIMEARC = "timeArc";
    public static final String UPDATE_MODE = "updateMode";
    public static final String ABSOLUTE = "absolute";
    public static final String CATDOC = "catdoc";
    public static final String NUMORD = "numord";
    public static final String TYPDOC = "typdoc";
    public static final String INDPUB = "indpub";

    private final Map<String, Object> mAttrs = new HashMap<>();
    private final FlatProp mProp;
    private JSONObject mJsonObject;

    /**
     * Constructor FileKey with refFile=true
     *
     * @param codsoc
     * @param typtie
     * @param nomcle
     * @param lib256
     * @param typmed
     * @param codlan
     * @param utimod
     * @param filename
     * @param updateMode
     * @param prop
     */
    public FileKey(final int codsoc, final String typtie, final String nomcle, final String lib256, final String typmed, final String codlan, final String utimod, final String filename, final String updateMode, final FlatProp prop) {
        initInner();

        setEntity(codsoc);
        setTyptie(typtie);
        setNomcle(nomcle);
        setFilename(filename);
        setUtimod(utimod);

        setLib256(lib256);
        setTypmed(typmed);
        setCodlan(codlan);
        setUpdateMode(updateMode);

        mProp = prop;

        setRefFile(true);
        setAbsolute(prop.getProperty(ABSOLUTE));
        setCatdoc(prop.getProperty(CATDOC));
        setNumord(prop.getProperty(NUMORD));
        setCoddoc(prop.getProperty(CODDOC));
    }

    /**
     * Constructor FileKey with refFile=false
     *
     * @param codsoc
     * @param typtie
     * @param nomcle
     * @param filename
     */
    public FileKey(final int codsoc, final String typtie, final String nomcle, final String filename) {
        initInner();

        setEntity(codsoc);
        setTyptie(typtie);
        setNomcle(nomcle);
        setCoddoc(" ");
        setFilename(filename);

        mProp = new FlatProp();

        setRefFile(false);
        setAbsolute("false");
        setNumord("0");
    }

    private void initInner() {
        setUtimod(IDocumentLinkConstants.DEFAULT_UTICOD);
        setDatmod("19720809"); // This is my birth date as default value, don't modify it ;-) (CAF => lol)
        setTimearc(GedUtil.getTimeArc());
    }

    @Override
    public String toString() {
        return getEntity() + "_" + getEnttyp() + "_" + getKeydoc() + "_" + getFilename();
    }

    /**
     * @return The properties
     */
    public FlatProp getProperties() {
        return mProp;
    }

    // Abstract accessors
    void setAttribute(final String key, final Object value) {
        mAttrs.put(key, value);
    }

    /**
     * Get the value of an attribute
     *
     * @param param Attribute name
     * @return The value for attribute param
     */
    public Object getAttribute(final String param) {
        return mAttrs.get(param);
    }

    /**
     * Test if an attribute is defined
     *
     * @param param Attribute name
     * @return true if the param is defined as an attribute
     */
    public boolean isAttribute(final String param) {
        return mAttrs.containsKey(param);
    }

    // -- Beans accessors privates --------------------------------------------

    private void setEntity(final int codsoc) {
        setAttribute(CODSOC, codsoc);
    }

    private void setTyptie(final String typtie) {
        setAttribute(TYPTIE, typtie);
    }

    public void setNomcle(final String nomcle) {
        setAttribute(NOMCLE, nomcle);
    }

    private void setCoddoc(final String coddoc) {
        setAttribute(CODDOC, coddoc);
    }

    /**
     * Public accessor
     *
     * @param filename The filename
     */
    public void setFilename(final String filename) {
        setAttribute(FILENAME, filename);
    }

    private void setUtimod(final String uticod) {
        setAttribute(UTIMOD, uticod);
    }

    private void setLib256(final String lib256) {
        setAttribute(LIB256, lib256);
    }

    private void setTypmed(final String typmed) {
        setAttribute(TYPMED, typmed);
    }

    private void setCodlan(final String codlan) {
        setAttribute(CODLAN, codlan);
    }

    private void setRefFile(final boolean refFile) {
        setAttribute(REFFILE, refFile);
    }

    private void setUpdateMode(final String updateMode) {
        setAttribute(UPDATE_MODE, updateMode);
    }

    private void setDatmod(final String datmod) {
        setAttribute(DATMOD, datmod);
    }

    private void setTimearc(final String timeArc) {
        setAttribute(TIMEARC, timeArc);
    }

    private void setAbsolute(final String absolute) {
        setAttribute(ABSOLUTE, Boolean.valueOf(absolute));
    }

    private void setCatdoc(final String catdoc) {
        setAttribute(CATDOC, catdoc);
    }

    private void setNumord(final String numord) {
        if (numord == null) {
            setAttribute(NUMORD, 0);
            return;
        }

        try {
            setAttribute(NUMORD, Integer.valueOf(numord));
        } catch (@SuppressWarnings("unused") final NumberFormatException e) {
            setAttribute(NUMORD, 0);
        }
    }

    /**
     * @return The entity
     */
    public int getEntity() {
        return (Integer) getAttribute(CODSOC);
    }

    /**
     * @return The typtie
     */
    public String getEnttyp() {
        return (String) getAttribute(TYPTIE);
    }

    /**
     * @return The nomcle
     */
    public String getKeydoc() {
        return (String) getAttribute(NOMCLE);
    }

    /**
     * @return The coddoc
     */
    public String getCoddoc() {
        return (String) getAttribute(CODDOC);
    }

    /**
     * @return The filename
     */
    public String getFilename() {
        return (String) getAttribute(FILENAME);
    }

    /**
     * @return The codlan
     */
    public String getCodlan() {
        return (String) getAttribute(CODLAN);
    }

    /**
     * @return The typmed
     */
    public String getTypmed() {
        return (String) getAttribute(TYPMED);
    }

    /**
     * @return The utimod
     */
    public String getUtimod() {
        return (String) getAttribute(UTIMOD);
    }

    /**
     * @return The lib256
     */
    public String getLib256() {
        return (String) getAttribute(LIB256);
    }

    /**
     * @return is the filekey a .ref file
     */
    public boolean isRefFile() {
        return (Boolean) getAttribute(REFFILE);
    }

    /**
     * @return The update mode
     */
    public String getUpdateMode() {
        return (String) getAttribute(UPDATE_MODE);
    }

    /**
     * @return true if the filename is absolute
     */
    public boolean isAbsolute() {
        return (boolean) getAttribute(ABSOLUTE);
    }

    /**
     * @return The category of document
     */
    public String getCatdoc() {
        return (String) getAttribute(CATDOC);
    }

    /**
     * @return The order of document in list
     */
    public int getNumord() {
        final var o = getAttribute(NUMORD);
        if (o == null) {
            return 0;
        }

        return (int) o;
    }

    /**
     * @return The type of document
     */
    public String getTypdoc() {
        return (String) getAttribute(TYPDOC);
    }

    /**
     * @return A JSON object that should be provided by the setter method
     */
    public JSONObject getJsonObject() {
        return mJsonObject;
    }

    /**
     * @param jsonObject set a optional JSON object
     */
    public void setJsonObject(final JSONObject jsonObject) {
        mJsonObject = jsonObject;
    }

    // ------------------------------------------------------------------------

    /**
     * @return true if the filename is absolute (depends on OS)
     */
    public boolean isAbsoluteFile() {
        final var filename = getFilename();
        if (filename != null) {
            final var path = FileSystems.getDefault().getPath(filename);
            return path.isAbsolute();
        }

        return false;
    }
}
