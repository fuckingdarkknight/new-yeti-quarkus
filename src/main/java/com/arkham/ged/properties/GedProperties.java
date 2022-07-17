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
package com.arkham.ged.properties;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkham.common.jdbc.DatabaseConnectionManager;
import com.arkham.common.jdbc.DatabaseConnectionManagerException;
import com.arkham.common.pattern.listener.BasicEvent;
import com.arkham.common.pattern.listener.BasicListener;
import com.arkham.common.pattern.listener.BasicListenerManager;
import com.arkham.common.properties.FlatFilePropertiesProvider;
import com.arkham.common.properties.PropertiesProvider;
import com.arkham.common.properties.SystemPropertiesProvider;
import com.arkham.common.solver.expr.ExprException;
import com.arkham.common.solver.expr.ExprSolver;
import com.arkham.common.util.CurrentClassLoader;
import com.arkham.common.util.GxMD5;
import com.arkham.common.util.GxUtil;
import com.arkham.ged.jdbc.DatabaseConnectionManagerProxy;
import com.arkham.ged.message.GedMessages;
import com.arkham.ged.properties.Internal.Misc;
import com.arkham.ged.solver.GedExprValueProvider;
import com.arkham.ged.solver.Translator;
import com.arkham.ged.util.GedUtil;

/**
 * Singleton used to read upload/download/scanning properties
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public final class GedProperties implements PropertiesAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(GedProperties.class);

	/**
	 * Default properties filename
	 */
	public static final String DEFAULT_FILENAME = "ged.xml";

	/**
	 * Max parallel processing by IP. Default is 1
	 */
	public static final String MAX_PARALLEL_PROCESSING_BYIP = "servlet.parallelProcessing.limitByIp.count";

	/**
	 * Max parallel processing by UUID. Default is 1
	 */
	public static final String MAX_PARALLEL_PROCESSING_BYUUID = "servlet.parallelProcessing.limitByUuid.count";

	/**
	 * Duration in minutes for persistance of lock. Default is 30mn
	 */
	public static final String PURGE_TIMEOUT = "servlet.parallelProcessing.limit.timeout";

	/**
	 * Frequency in minutes for purge of persistance of lock. Default is 5mn
	 */
	public static final String PURGE_FREQUENCY = "servlet.parallelProcessing.purge.frequency";

	// --- Thumbs constants

	/**
	 * Thumb generation : true or other value for false
	 */
	public static final String PARAM_THUMB = "thumb";

	/**
	 * Thumb max dimension
	 */
	public static final String PARAM_THUMB_SIZE = "thumbSize";

	/**
	 * Thumb density (dpi)
	 */
	public static final String PARAM_THUMB_DPI = "dpi";

	/**
	 * Thumb format, by default "jpg". "bmp" and "png" could be used for output format
	 */
	public static final String PARAM_THUMB_FORMAT = "thumbFormat";

	// --- Misc constants

	public static final String PARAM_INTEGRATE_MAX_SIZE = "maxSize";

	/**
	 * Max integration max file size, by default 10MBytes
	 */
	private static final int DEFAULT_INTEGRATE_MAX_SIZE = 10 * 1024 * 1024;

	// --- internal constants

	private static final GedProperties mInstance = new GedProperties();

	private String mBasedir;
	private File mRootDir;
	private List<InputScanFileDef> mImport;
	private List<OutputScanFileDef> mExport;
	private List<JmsType> mJms;
	private SocketType mSocket;
	private BrokerType mBroker;
	private MediaType mMediaType;
	private Map<String, String> mMediaMapping;
	private String mJdbcUrl;
	private String mDsName;
	private boolean mIsPooled;
	private int mPoolSize;
	private int mIdleTime;
	private DatabaseConnectionManager mDcm;
	private Map<String, IndexerFlatType> mIndexerMap;
	private Map<String, FileKeyProviderType> mFkpMap;
	private UploadType mUploadType;
	private StorageType mStorageType;
	private Internal mInternal;

	private final boolean mIsIndexerAuthorized = false;
	private final boolean mIsThumbingAuthorized = false;
	private final boolean mIsBatchModeAuthorized = false;
	private final int mBatchModeFrequency = -1;

	private boolean mActive = true;

	private final BasicListenerManager<String, GLOBAL_EVENTS, PropertiesException> mListenerManager = new BasicListenerManager();

	private GedProperties() {
		// private visibility because of singleton pattern
	}

	private void $initGlobalProperties$(String xmlFilename) throws IOException {
		// System parameters
		LOGGER.info("$initGlobalProperties$() : adding System.getProperties as provider");
		PropertiesProvider.addProvider(new SystemPropertiesProvider());

		// Split xmlFilename to get a new extension : .properties
		final String propertiesFilename = GedUtil.replaceFileExtension(xmlFilename, "properties");

		// ged.properties provider
		final File file = new File(mBasedir, propertiesFilename);
		if (file.exists()) {
			LOGGER.info("$initGlobalProperties$() : adding {} as provider", file.getCanonicalPath());
			PropertiesProvider.addProvider(new FlatFilePropertiesProvider(file));
		} else {
			LOGGER.info("$initGlobalProperties$() : {} cannot be found as a provider", file.getCanonicalPath());
		}
	}

	/**
	 * @return The unique instance of this class
	 */
	public static GedProperties getInstance() {
		return mInstance;
	}

	private static String computeFilename(String filename) {
		String workingFilename;
		if (filename == null) {
			workingFilename = DEFAULT_FILENAME;
		} else {
			workingFilename = filename;
		}

		return workingFilename;
	}

	private void computeBasedir(String basedir) {
		if (basedir == null) {
			mBasedir = System.getProperty("user.dir");
		} else {
			mBasedir = basedir;
		}

		mRootDir = new File(mBasedir);
	}

	@SuppressWarnings("resource")
	private InputStream getPropertiesStream(String filename) throws IOException, PropertiesException {
		InputStream result;
		final Path file = Paths.get(mBasedir, filename);
		if (file.toFile().exists()) {
			result = Files.newInputStream(file);
		} else {
			// Search in classpath : default properties
			result = getClass().getResourceAsStream(filename);
		}

		// Convenient : if is null at this point, the Arkam-ged scanner cannot be started
		if (result == null) {
			throw new PropertiesException(GedMessages.Properties.gedNotStartedDueTofileNotFound, file.toAbsolutePath());
		}

		return result;
	}

	/**
	 * Initialize the context and read the xml file properties from disk or directly from classpath if not found with basedir/filename
	 *
	 * @param basedir The base directory of ged.properties.xml. Can be null if user.dir should be used
	 * @param filename or <code>null</code> if use default filename {@link #DEFAULT_FILENAME}
	 * @throws PropertiesException If any exception occurs while warming-up the application
	 */
	@SuppressWarnings("resource")
	public void init(String basedir, String filename) throws PropertiesException {
		InputStream is = null;
		try {
			// ged.properties.xml : file name
			final String workingFilename = computeFilename(filename);

			// base directory
			computeBasedir(basedir);

			LOGGER.info("init() : baseDir={}", basedir);
			LOGGER.info("init() : properties={}", workingFilename);

			// properties from FS or classpath
			is = getPropertiesStream(workingFilename);

			// Init external properties values
			try {
				$initGlobalProperties$(workingFilename);
			} catch (final IOException e) {
				throw new PropertiesException(e, GedMessages.Properties.gedNotStartedDueTofileNotFound, new File(mBasedir, workingFilename).getAbsolutePath());
			}

			final String contextPackage = com.arkham.ged.properties.Root.class.getPackage().getName();
			final Root root = (Root) JaxbAdapter.unmarshall(is, contextPackage);

			// First, internal optional values
			mInternal = root.getInternal();

			// Process dynamic directories values
			final Translator pt = new LikeAntPropertiesTranslator();
			mImport = root.getImport();
			if (mImport != null) {
				for (final InputScanFileDef isfd : mImport) {
					isfd.setDir(pt.translate(isfd.getDir()));
					translate(pt, isfd.getParam());
				}
			}

			// Socket relay is optional
			mSocket = root.getSocket();
			if (mSocket != null) {
				mSocket.setAddress(pt.translate(mSocket.getAddress()));
			}
			// Datasource management
			final DatasourceType ds = root.getDatasource();
			mJdbcUrl = pt.translate(ds.getJdbc());
			mDsName = "java:comp/env/" + ds.getDsName();
			// Pas simplement un Boolean.valueOf car la valeur par défaut doit être ici true et pas false (valdef d'un boolean)
			mIsPooled = !"false".equalsIgnoreCase(pt.translate(ds.getPooled()));
			mPoolSize = ds.getPoolSize();
			mIdleTime = ds.getIdleTime();
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("init() : url={} DS={} pooled={} poolSize={} idleTime={}", mJdbcUrl, mDsName, String.valueOf(mIsPooled), mPoolSize, mIdleTime);
			}

			// Now can create the DCM
			mDcm = new DatabaseConnectionManagerProxy(this);

			// FileKeyProvider mappings
			final List<FileKeyProviderType> fkpTypeList = root.getFileKeyProvider();
			mFkpMap = new HashMap<>(fkpTypeList.size());
			for (final FileKeyProviderType fkp : fkpTypeList) {
				mFkpMap.put(fkp.getName(), fkp);
			}

			mUploadType = root.getUpload();

			// Control that settings are OK
			mStorageType = root.getStorage();
			if (mStorageType.getBaseDir() != null) {
				final String baseDir = pt.translate(mStorageType.getBaseDir());
				final File baseDirFile = new File(baseDir);

				// Only blocking if the main storage is not DATABASE
				if (mStorageType.getType() != PhysicalStorageType.DATABASE) {
					if (!baseDirFile.exists() || !baseDirFile.isDirectory()) {
						throw new PropertiesException(GedMessages.Properties.dirStorage);
					}

					// Check if R/W permissions are OK on this directory
					checkStorageBaseDir(baseDirFile);
				}

				// Store directly into the bean after value translation
				mStorageType.setBaseDir(baseDir);
			} else if (mStorageType.getType() == PhysicalStorageType.FILESYSTEM) {
				throw new PropertiesException(GedMessages.Properties.invalidStorageSettings);
			}
		} catch (final IOException e) {
			throw new PropertiesException(GedMessages.Properties.fileNotFound, e);
		} catch (final JAXBException e) {
			throw new PropertiesException(GedMessages.Properties.invalidFile, e);
		} catch (final DatabaseConnectionManagerException e) {
			throw new PropertiesException(GedMessages.Properties.dcmNotCreated, e);
		} finally {
			GxUtil.closeSilently(is);
		}
	}

	public String getBasedir() {
		return mBasedir;
	}

	@Override
	public File getRootDir() {
		return mRootDir;
	}

	// ---------------------- PropertiesAdapter implementation -----------------------

	@Override
	public List<InputScanFileDef> getImport() {
		return mImport;
	}

	@Override
	public List<OutputScanFileDef> getExport() {
		return mExport;
	}

	@Override
	public List<JmsType> getJms() {
		return mJms;
	}

	@Override
	public SocketType getSocket() {
		return mSocket;
	}

	@Override
	public BrokerType getBroker() {
		return mBroker;
	}

	@Override
	public MediaType getMedia() {
		return mMediaType;
	}

	@Override
	public Map<String, String> getMediaMapping() {
		return mMediaMapping;
	}

	@Override
	public DatabaseConnectionManager getDCM() throws DatabaseConnectionManagerException {
		return mDcm;
	}

	@Override
	public IndexerFlatType getIndexerType(String name) {
		return mIndexerMap.get(name);
	}

	@Override
	public FileKeyProviderType getFKPType(String name) {
		return mFkpMap.get(name);
	}

	@Override
	public String getMatchingTypmed(String filename) {
		String ext = GedUtil.getFileExtension(filename);
		if (ext != null) {
			ext = ext.toLowerCase();
			final MediaType mt = getMedia();
			// Cannot be null because of default value in XSD
			final String defaultTypmed = mt.getDefault();

			final String typmed = getMediaMapping().get(ext);
			if (typmed == null) {
				return defaultTypmed;
			}

			return typmed;
		}

		return "INT";
	}

	@Override
	public UploadType getUpload() {
		return mUploadType;
	}

	@Override
	public StorageType getStorage() {
		return mStorageType;
	}

	@Override
	public boolean isIndexerAuthorized() {
		return mIsIndexerAuthorized;
	}

	@Override
	public boolean isThumbingAuthorized() {
		return mIsThumbingAuthorized;
	}

	@Override
	public boolean isBatchModeAuthorized() {
		return mIsBatchModeAuthorized;
	}

	@Override
	public int getBatchModeFrequency() {
		return mBatchModeFrequency;
	}

	// ---------------------- DCD implementation

	@Override
	public int getIdleTime() {
		return mIdleTime;
	}

	@Override
	public int getMonitorFrequency() {
		return 1;
	}

	@Override
	public int getPoolSize() {
		return mPoolSize;
	}

	@Override
	public String getResourceRef() {
		return mDsName;
	}

	@Override
	public String getURL() {
		return mJdbcUrl;
	}

	@Override
	public boolean isPooled() {
		return mIsPooled;
	}

	// ---------------------- End of ResourcePropertiesAdapter implementation -----------------------

	/**
	 * Get a global property from /root/internal/misc section
	 *
	 * @param name The property name
	 * @param defaultValue Default value if not set
	 * @return The value of the optional parameter or defaultValue
	 */
	public String getInternalOptionalValue(String name, String defaultValue) {
		if (mInternal == null) {
			return defaultValue;
		}

		for (final Misc misc : mInternal.getMisc()) {
			if (misc.getName().equals(name)) {
				return misc.getValue();
			}
		}

		return defaultValue;
	}

	/**
	 * Return an optional parameter
	 *
	 * @param optl optional parameter list
	 * @param name The parameter name
	 * @return The OPT if set in properties or <code>null</code> if not
	 */
	public static OptionalParameterType getOptionalParameters(List<OptionalParameterType> optl, String name) {
		if (name == null || optl == null) {
			return null;
		}

		for (final OptionalParameterType opt : optl) {
			if (name.equals(opt.getName())) {
				return opt;
			}
		}

		return null;
	}

	/**
	 * Get the maximum filesize that can be integrated.
	 *
	 * @see GedUtil#convertNumberInBytes
	 * @param optl Optional parameters context
	 * @return The maximum file size (in bytes) that can be integrated
	 */
	public static int getMaxSize(List<OptionalParameterType> optl) {
		int result = GedProperties.DEFAULT_INTEGRATE_MAX_SIZE;
		final OptionalParameterType maxSizeOpt = getOptionalParameters(optl, GedProperties.PARAM_INTEGRATE_MAX_SIZE);
		if (maxSizeOpt != null) {
			try {
				result = GedUtil.convertNumberInBytes(maxSizeOpt.getValue());
			} catch (final NumberFormatException e) {
				LOGGER.error("getMaxSize() : the maxSize parameter value is invalid, should be an integer. Value set is \"{}\"", maxSizeOpt.getValue());
			}
		}

		return result;
	}

	/**
	 * Translate all values {@link OptionalParameterType#getValue()} by using the given {@link Translator}
	 *
	 * @param pt The translator
	 * @param optl The optional parameters
	 */
	public static void translate(Translator pt, List<OptionalParameterType> optl) {
		if (pt != null && optl != null) {
			for (final OptionalParameterType opt : optl) {
				opt.setValue(pt.translate(opt.getValue()));
			}
		}
	}

	private static void checkStorageBaseDir(File baseDir) throws PropertiesException {
		try {
			// filename should be unique ... but I think that there's no need to generate a real UID for this !
			// Netherless : I do it !
			final String localhost = System.getProperty("COMPUTERNAME", "unknown");
			final String currentClassLoader = CurrentClassLoader.getCurrentClass().toString();
			final String timestamp = String.valueOf(System.nanoTime());
			final GxMD5 md5 = new GxMD5(localhost + "-" + currentClassLoader + "-" + timestamp);

			final String filename = ".ged_storage_verifyer-" + md5.getStringDigest();

			final File test = new File(baseDir, filename);
			boolean ok = true;
			if (test.exists()) {
				ok = test.delete();
			}

			if (ok) {
				try (Writer writer = new FileWriter(test)) {
					writer.append("Arkham testing : just for write rights access in the storage base directory");
				}

				if (!test.delete()) {
					LOGGER.error("checkStorageBaseDir() : cannot remove {}", filename);
				}
			} else {
				throw new PropertiesException(GedMessages.Properties.dirStorageSecurity);
			}
		} catch (final SecurityException | IOException e) {
			throw new PropertiesException(e, GedMessages.Properties.dirStorageSecurity);
		}
	}

	@Override
	public OptionalParameterType getOptionalParameter(List<OptionalParameterType> optl, String key) {
		if (key == null) {
			return null;
		}

		for (final OptionalParameterType opt : optl) {
			if (key.equalsIgnoreCase(opt.getName())) {
				return opt;
			}
		}

		return null;
	}

	@Override
	public boolean isOptionalParameterActive(OptionalParameterType opt, File file) {
		if (opt != null) {
			// If an expression is set, we have to test it
			final String expression = opt.getExpression();
			if (expression != null && expression.trim().length() > 0) {
				final GedExprValueProvider evp = new GedExprValueProvider(file);
				final ExprSolver es = new ExprSolver(evp);
				try {
					final Object result = es.solve(expression);
					if (result instanceof Boolean) {
						return ((Boolean) result).booleanValue();
					}

					LOGGER.error("isOptionalParameterActive() : unable to calculate expression=\"{}\" because expression does not return a Boolean", expression);
				} catch (final ExprException e) {
					LOGGER.error("isOptionalParameterActive() : unable to calculate expression=\"{}\" because of exception={}", expression, e);
				}
			}
		}

		return true;
	}

	@Override
	public void activate(boolean value) {
		mActive = value;

		LOGGER.warn("activate() : change value of active to value={}", value);
	}

	@Override
	public boolean isActive() {
		return mActive;
	}

	@Override
	public void registerListener(BasicListener<String, GLOBAL_EVENTS, PropertiesException> listener) {
		mListenerManager.registerListener(listener);
	}

	@Override
	public void fireEvent(BasicEvent<String, GLOBAL_EVENTS> event) {
		try {
			LOGGER.info("fireEvent() : event={} source={}", event.getType(), event.getSource());

			mListenerManager.fireEvent(event);
		} catch (final PropertiesException e) {
			LOGGER.error("fireEvent() : {}", e);
		}
	}
}
