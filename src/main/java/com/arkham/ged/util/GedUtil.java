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
package com.arkham.ged.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import com.arkham.common.properties.FlatProp;
import com.arkham.common.properties.reader.UTF8FileReader;
import com.arkham.ged.timer.LoggerMDC;
import com.arkham.ged.timer.MDC_KEY;

/**
 * Set of utility methods and Class for Ged
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public final class GedUtil {
	private static final String NODE_INSTANCE_PROPERTY = "arkham.ged.node";

	private static final Pattern SPLIT_SEQ = Pattern.compile(",|;|:|\\|");

	public static final String PAR_ENTITY = "entity";
	public static final String PAR_FILENAME = "filename";
	public static final String PAR_UTICOD = "uticod";
	public static final String PAR_USERNAME = "username";

	private GedUtil() {
		// Private because it's an utility class, so we should't get an instance of this class
	}

	/**
	 * Split the value by using the list of index
	 *
	 * @param value The value to split
	 * @param l The list of index that define the start index to split
	 * @return A string array that contains splitted values by using the list of index
	 */
	public static String[] splitKey(String value, int[] l) {
		final List<String> result = new ArrayList<>();

		int pos = 0;
		for (final int p : l) {
			if (pos > value.length()) {
				result.add(null);
			} else if (pos + p > value.length()) {
				result.add(value.substring(pos));
			} else {
				result.add(value.substring(pos, pos + p));
			}

			pos += p;
		}

		return result.toArray(new String[] {});
	}

	/**
	 * Split the value by using a tokenizer
	 *
	 * @param value The value to split using "delim"
	 * @param delim The delimiter
	 * @return The string splitted as an array of string
	 */
	public static String[] splitValue(String value, String delim) {
		if (value != null) {
			final List<String> result = new ArrayList<>();
			final StringTokenizer st = new StringTokenizer(value, delim, false);
			while (st.hasMoreTokens()) {
				result.add(st.nextToken());
			}

			return result.toArray(new String[result.size()]);
		}

		return new String[0];
	}

	/**
	 * Split integer values
	 *
	 * @param value The value to split using "delim"
	 * @param delim The delimiter
	 * @return The string splitted as an array of int (value will be -1 if the value is not numerical)
	 */
	public static int[] splitIntValue(String value, String delim) {
		final String[] s = splitValue(value, delim);
		final int[] result = new int[s.length];
		for (int i = 0; i < s.length; i++) {
			result[i] = getInt(s[i], -1);
		}

		return result;
	}

	/**
	 * <p>
	 * Example :
	 * </p>
	 *
	 * <pre>
	 * splitValues(&quot;*.jpg|*.bmp,*.jpeg;*.png&quot;);
	 * </pre>
	 *
	 * will return :
	 * <ul>
	 * <li>*.jpg
	 * <li>*.bmp
	 * <li>*.jpg
	 * <li>*.png
	 * </ul>
	 *
	 * @param value The value, if <code>null</code> an empty array is returned
	 * @return An array containing the splitted values
	 */
	public static String[] splitValues(String value) {
		if (value != null) {
			final String[] result = SPLIT_SEQ.split(value);
			for (int i = 0; i < result.length; i++) {
				result[i] = result[i].trim();
			}

			return result;
		}

		return new String[0];
	}

	/**
	 * @return The current date in yyyyMMdd format
	 */
	public static String getDatmod() {
		return getDT("yyyyMMdd");
	}

	/**
	 * @return The current date in yyyyMMdd-HHmmss format
	 */
	public static String getTimeArc() {
		return getDT("yyyyMMdd-HHmmss");
	}

	/**
	 * Get current date
	 *
	 * @param format The format converted to {@link DateFormat}
	 * @return The current date formated into "format"
	 */
	public static String getDT(String format) {
		final DateFormat dateFormat = new SimpleDateFormat(format);

		return dateFormat.format(Calendar.getInstance().getTime());
	}

	/**
	 * Returns the file name of the file independantly of the OS
	 *
	 * @param file The file
	 * @return The file name
	 */
	public static String getName(File file) {
		final String name = file.getName();
		final int lp = name.lastIndexOf('\\');
		if (lp == -1) {
			return name;
		}

		return name.substring(lp + 1);
	}

	/**
	 * The extension is always returned in lowercase
	 *
	 * @param filename A filename
	 * @return The file extension if exists or <code>null</code> if <code>filename</code> is null or if there's no extension
	 */
	public static String getFileExtension(String filename) {
		if (filename == null) {
			return null;
		}

		final int dotPos = filename.lastIndexOf('.');
		if (dotPos != -1) {
			final String result = filename.substring(dotPos + 1);
			if (result.trim().length() == 0) {
				return null;
			}

			return result.toLowerCase();
		}

		return null;
	}

	/**
	 * Extension matcher
	 *
	 * @param filename A filename
	 * @param extension The file extension to match with filename
	 * @return true if extensions matchs (case insensitive)
	 */
	public static boolean isFileExtension(String filename, String extension) {
		final String ext = getFileExtension(filename);

		return ext != null && ext.equalsIgnoreCase(extension);
	}

	/**
	 * Method that replace the file extension
	 *
	 * @param filename The file name
	 * @param extension The extension to replace with
	 * @return The new file name
	 */
	public static String replaceFileExtension(String filename, String extension) {
		// Convenience
		if (filename == null) {
			return null;
		}
		if (extension == null || "".equals(extension) || ".".equals(extension)) {
			return filename;
		}

		// If extension start with a dot, remove this dot
		String ext;
		if (extension.charAt(0) == '.') {
			ext = extension.substring(1);
		} else {
			ext = extension;
		}

		final int dotPos = filename.lastIndexOf('.');
		if (dotPos != -1) {
			final String result = filename.substring(0, dotPos);
			if (result.trim().length() == 0) {
				return null;
			}

			return result + "." + ext;
		}

		return filename;
	}

	/**
	 * Remove the file extension (after last dot)
	 *
	 * @param filename The file name
	 * @return The file name without its extension or <code>null</code> if the file name is <code>null</code>
	 */
	public static String removeFileExtension(String filename) {
		if (filename == null) {
			return null;
		}

		final int dotPos = filename.lastIndexOf('.');
		if (dotPos != -1) {
			final String result = filename.substring(0, dotPos);
			if (result.trim().length() == 0) {
				return null;
			}

			return result;
		}

		return filename;
	}

	/**
	 * Static method used to trim the leading and trailing chars that are &lt;= 32 in the ascii table
	 *
	 * @param value The value to trim
	 * @return the trimmed value or <code>null</code> if value is null
	 */
	public static String trim(String value) {
		if (value == null) {
			return null;
		}

		final int length = value.length();
		int leading = -1;
		int trailing = -1;
		for (int i = 0; i < length; i++) {
			final char c = value.charAt(i);
			if (c <= ' ') {
				// Not significative char
			} else {
				if (leading == -1) {
					leading = i;
				}

				trailing = i;
			}
		}

		// Nothing signficative in the String !
		if (leading == -1) {
			return "";
		}

		return value.substring(leading, trailing + 1);
	}

	/**
	 * Escape special chars in html entities
	 *
	 * @param value The string to escape
	 * @return The escaped value
	 */
	public static String escapeChars(String value) {
		if (value == null) {
			return null;
		}

		final int length = value.length();
		final StringBuilder result = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			final char c = value.charAt(i);
			if (c == '<') {
				result.append("&lt;");
			} else if (c == '>') {
				result.append("&gt;");
			} else if (c == '&') {
				result.append("&amp;");
			} else if (c == '\'') {
				result.append("&apos;");
			} else if (c == '"') {
				result.append("&quot;");
			} else {
				result.append(c);
			}
		}

		return result.toString();
	}

	/**
	 * <u>Examples :</u>
	 * <ul>
	 * <li>350K = 350 * 1024 bytes
	 * <li>2.7M = 2.7 * 1024 * 1024 bytes
	 * <li>without unit : in bytes format
	 * </ul>
	 *
	 * @param s The input string that represents a bytes number to convert
	 * @return The result in bytes or 0 if <code>s</code> is <code>null</code> or if the length <code>s</code> is &lt;= 1
	 * @throws NumberFormatException
	 */
	public static int convertNumberInBytes(String s) {
		int result = 0;
		if (s != null && s.trim().length() > 1) {
			int factor = 1;
			String value = s;
			if (value.endsWith("K")) {
				factor = 1024;
				value = value.substring(0, value.length() - 1).trim();
			} else if (s.endsWith("M")) {
				factor = 1024 * 1024;
				value = value.substring(0, value.length() - 1).trim();
			}

			double d = Double.parseDouble(value);
			d *= factor;
			result = (int) d;
		}

		return result;
	}

	/**
	 * Method that suffix a filename before extension
	 *
	 * @param filename The file name
	 * @param suffix The suffix to add before extension
	 * @return The new file name
	 */
	public static String suffixFilename(String filename, String suffix) {
		if (filename == null) {
			return null;
		}

		String extension = "";
		String fName = filename;
		final int dotPos = filename.lastIndexOf('.');
		if (dotPos != -1) {
			fName = filename.substring(0, dotPos);
			extension = filename.substring(dotPos);
		}

		return fName + suffix + extension;
	}

	// Fork from butterfly

	/**
	 * Copy the InputStream to the OutputStream.
	 * <p>
	 * Nether <code>is</code> nor <code>os</code> are closed. The caller of this method should manage it.
	 * </p>
	 *
	 * @param is InputStream that shouln't be null
	 * @param os outputStream that shouln't be null
	 * @param size The buffer size
	 * @return size transfered
	 * @throws IOException Could occurs if out stream is not writable
	 */
	public static int copyIs2Os(InputStream is, OutputStream os, int size) throws IOException {
		int copied = 0;
		final byte[] buffer = new byte[size];
		int chunk;
		while ((chunk = is.read(buffer)) != -1) {
			os.write(buffer, 0, chunk);
			copied += chunk;
		}

		os.flush();

		return copied;
	}

	/**
	 * Copy a file to target (force replacement) by using Java 8 {@link Files#copy(java.nio.file.Path, java.nio.file.Path, java.nio.file.CopyOption...)}
	 *
	 * @param source The source file, should not be null
	 * @param target The target file, should not be null
	 * @param nio Use Java 8 nio API if set to true, else use {@link #copyIs2Os(InputStream, OutputStream, int)}
	 * @throws IOException In case of copy problem
	 * @see #copyIs2Os(InputStream, OutputStream, int)
	 * @see Files#copy(java.nio.file.Path, java.nio.file.Path, java.nio.file.CopyOption...)
	 */
	public static void copyFile(File source, File target, boolean nio) throws IOException {
		if (nio) {
			Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} else {
			try (InputStream is = Files.newInputStream(source.toPath()); OutputStream os = Files.newOutputStream(target.toPath())) {
				copyIs2Os(is, os, 8192); // Arbitrary buffer size
			}
		}

		try {
			final Set<PosixFilePermission> perms = EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.GROUP_READ, PosixFilePermission.GROUP_WRITE,
					PosixFilePermission.OTHERS_READ, PosixFilePermission.OTHERS_WRITE);

			Files.setPosixFilePermissions(target.toPath(), perms);
		} catch (final UnsupportedOperationException e) { // NOSONAR
			// Do nothing : not supported by windows
		}
	}

	/**
	 * Copy a file to target (force replacement) by using {@link #copyIs2Os(InputStream, OutputStream, int)}
	 *
	 * @param source The source file, should not be null
	 * @param target The target file, should not be null
	 * @throws IOException In case of copy problem
	 * @see #copyIs2Os(InputStream, OutputStream, int)
	 */
	public static void copyFile(File source, File target) throws IOException {
		copyFile(source, target, false);
	}

	/**
	 * Convert a stream to a string using UTF-8
	 *
	 * @param is The stream (should not be <code>null</code>)
	 * @return The stream converted to UTF-8
	 * @throws IOException If any problem occurs while converting
	 */
	public static String getString(InputStream is) throws IOException {
		return getString(is, "UTF-8");
	}

	/**
	 * Convert a stream to a string using given charset
	 *
	 * @param is The stream (should not be <code>null</code>)
	 * @param charset Conversion target charset
	 * @return The stream converted to charset
	 * @throws IOException If any problem occurs while converting
	 */
	public static String getString(InputStream is, String charset) throws IOException {
		try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			copyIs2Os(is, baos, 8192);

			return new String(baos.toByteArray(), Charset.forName(charset));
		}
	}

	/**
	 * Sort Array of files from older to newer. If date equals sort by name. Returned array is a clone
	 *
	 * @param files Files to sort by lastModified attribute
	 * @return new array sorted
	 */
	public static File[] sortByDate(File[] files) {
		if (files.length == 0) {
			return files;
		}

		return Arrays.asList(files).stream().sorted(Comparator.comparing(File::lastModified)).toArray(File[]::new);
	}

	/**
	 * Sort Array of files according to name. Returned array is a clone
	 *
	 * @param files Files to sort by name
	 * @return new array sorted
	 */
	public static File[] sortByName(File[] files) {
		if (files.length == 0) {
			return files;
		}

		return Arrays.asList(files).stream().sorted(Comparator.comparing(File::getName)).toArray(File[]::new);
	}

	/**
	 * Helper method that could be used to translate a string value to a boolean value. The tests are case insensitive.
	 * <ul>
	 * <li><code>true</code> or <code>TRUE</code> or <code>TrUe</code> return true
	 * <li><code>ON</code> or <code>On</code> return true
	 * <li><code>1</code> return true
	 * <li>others values return false
	 * </ul>
	 *
	 * @param value The value to test
	 * @return true if option is active
	 */
	public static boolean isOptionActive(String value) {
		if (value == null) {
			return false;
		}

		return "true".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value) || "1".equals(value);
	}

	/**
	 * Get the hashcode of the byte array, the algo is the same than {@link String#hashCode()}
	 *
	 * @param buf The buffer to compute
	 * @param length The last byte to compute
	 * @return The hashcode
	 */
	public static int getHashCode(byte[] buf, int length) {
		int h = 0; // default value
		for (int i = 0; i < length; i++) {
			h = 31 * h + buf[i];
		}
		return h;
	}

	/**
	 * Get the hashcode of the stream, {@link #getHashCode(byte[], int)}
	 *
	 * @param is The stream to compute
	 * @return The hashcode
	 * @throws IOException Exception while reading stream
	 */
	public static int getHashCode(InputStream is) throws IOException {
		final byte[] buf = new byte[4096];
		int i;
		int hashcode = 0;
		while ((i = is.read(buf)) != -1) {
			hashcode += getHashCode(buf, i);
		}

		return hashcode;
	}

	/**
	 * Convert string to int
	 *
	 * @param s The string to parse
	 * @param defaultValue The default value if an error occurs while parsing
	 * @return The int value of "s"
	 */
	public static int getInt(String s, int defaultValue) {
		try {
			if (s != null) {
				return Integer.parseInt(s.trim());
			}
		} catch (final NumberFormatException e) {	// NOSONAR
			// Default value
		}

		return defaultValue;
	}

	/**
	 * Convert string to boolean
	 *
	 * @param s The string to test
	 * @param defaultValue The defaultValue
	 * @return true if string is "true", defaultValue if the string is <code>null</code> or empty, false otherwise
	 * @see #isOptionActive(String)
	 */
	public static boolean getBoolean(String s, boolean defaultValue) {
		if (s == null || s.trim().length() == 0) {
			return defaultValue;
		}

		return Boolean.parseBoolean(s);
	}

	/**
	 * Get default value if input string is <code>null</code>
	 *
	 * @param s The string to test
	 * @param defaultValue The defaultValue
	 * @return default value if "s" is <code>null</code> or is an empty string, otherwise "s"
	 */
	public static String getString(String s, String defaultValue) {
		if (s == null || s.trim().length() == 0) {
			return defaultValue;
		}

		return s;
	}

	/**
	 * Read a property file using a UTF-8 reader
	 *
	 * @param file The file to read
	 * @return The flat properties
	 * @throws IOException Occurs if problem while reading file
	 */
	public static FlatProp getProp(File file) throws IOException {
		try (Reader reader = new UTF8FileReader(file)) {
			final FlatProp p = new FlatProp();
			p.load(reader);

			return p;
		}
	}

	/**
	 * Init the MDC by adding 2 keys :
	 * <ul>
	 * <li>MDC_KEY#ARKHAM_GED_NODE
	 * <li>MDC_KEY#ARKHAM_GED_TYPE
	 * </ul>
	 */
	public static void initMDC() {
		LoggerMDC.initMDC();

		// Get the node name : usefull in case of multi JVM log4j tracing
		final String gedNodeName = System.getProperty(NODE_INSTANCE_PROPERTY, "undefined");
		LoggerMDC.putMDC(MDC_KEY.ARKHAM_GED_NODE, gedNodeName);
		LoggerMDC.putMDC(MDC_KEY.ARKHAM_GED_TYPE, "ARCHIVE");
	}

	/**
	 * Get a map from JSONObject
	 *
	 * @param o JSONObject
	 * @return The mapped first level of JSONObject
	 */
	public static Map<String, String> getMap(JSONObject o) {
		final Map<String, String> result = new HashMap<>();
		if (o != null) {
			for (final Entry<String, Object> entry : o.toMap().entrySet()) {
				final Object v = entry.getValue();
				if (v == null) {
					result.put(entry.getKey(), null);
				} else {
					result.put(entry.getKey(), v.toString());
				}
			}
		}

		return result;
	}

	/**
	 * Utility method to convert a yaml string format to json string format.
	 *
	 * @param message The yaml message to convert to json
	 * @return The jsonify string
	 * @throws IOException Should never occurs
	 */
	public static String convertYamlToJson(String message) throws IOException {
		final ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
		@SuppressWarnings("null")
		final Object yamlObject = yamlReader.readValue(message, Object.class);

		final ObjectMapper jsonWriter = new ObjectMapper();

		return jsonWriter.writeValueAsString(yamlObject);
	}

	/**
	 * Utility method to convert a JSON string format to YAML string format.
	 *
	 * @param message The yaml message to convert to json
	 * @return The jsonify string
	 * @throws IOException Should never occurs
	 */
	public static String convertJsonToYaml(String message) throws IOException {
		final JsonNode jsonNodeTree = new ObjectMapper().readTree(message);

		return new YAMLMapper().writeValueAsString(jsonNodeTree);
	}
}
