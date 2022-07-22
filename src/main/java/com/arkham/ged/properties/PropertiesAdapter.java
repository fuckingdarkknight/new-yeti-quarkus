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
import java.util.List;
import java.util.Map;

import com.arkham.common.jdbc.DatabaseConnectionDefinition;
import com.arkham.common.jdbc.DatabaseConnectionManager;
import com.arkham.common.jdbc.DatabaseConnectionManagerException;
import com.arkham.common.pattern.listener.BasicEvent;
import com.arkham.common.pattern.listener.BasicListener;

/**
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public interface PropertiesAdapter extends DatabaseConnectionDefinition {
    /**
     * Global events
     *
     * @author arocher / Arkham asylum
     * @version 1.0
     * @since 7 févr. 2020
     */
    enum GLOBAL_EVENTS {
        SHUTDOWN
    }

    /**
     * @return A list of input directories to scan, each entry has its own scanning properties
     */
    List<InputScanFileDef> getImport();

    /**
     * @return The list of output directories to scan for exporting files
     */
    List<OutputScanFileDef> getExport();

    /**
     * @return The optional list of JMS connectors
     */
    List<JmsType> getJms();

    /**
     * @return The socket relay
     */
    SocketType getSocket();

    /**
     * @return The default broker definiton
     */
    BrokerType getBroker();

    /**
     * @return The media type
     */
    MediaType getMedia();

    /**
     * @return A Map that contains couple key/value for key=file extension and value is Typmed
     */
    Map<String, String> getMediaMapping();

    /**
     * @return A new instance of {@link DatabaseConnectionManager}
     * @throws DatabaseConnectionManagerException
     */
    DatabaseConnectionManager getDCM() throws DatabaseConnectionManagerException;

    /**
     * @param name The logical name of the Indexer type
     * @return The IndexerType or <code>null</code> if it's not defined in properties.xml
     */
    IndexerFlatType getIndexerType(String name);

    /**
     * @param name The logical name of the FileKeyProvider
     * @return The FileKeyProvider or <code>null</code> if it's not defined in properties.xml
     */
    FileKeyProviderType getFKPType(String name);

    /**
     * @param filename A file name
     * @return The matching TYPMED defined in properties.xml, or <code>null</code> if filename is <code>null</code> or "INT" if the filename has no extension
     */
    String getMatchingTypmed(String filename);

    /**
     * @return The upload characteristics
     */
    UploadType getUpload();

    /**
     * @return The storage characteristics
     */
    StorageType getStorage();

    /**
     * @return true is indexer engine are authorized
     */
    boolean isIndexerAuthorized();

    /**
     * @return true if thumbing is authorized
     */
    boolean isThumbingAuthorized();

    /**
     * @return true is batch mode is authorized
     */
    boolean isBatchModeAuthorized();

    /**
     * @return The frequency of batch mode
     */
    int getBatchModeFrequency();

    /**
     * @param optl The list of parameters
     * @param key The name to lookup (not case sensitive)
     * @return The OPT if set in properties or <code>null</code> if not set or if key is <code>null</code>
     */
    OptionalParameterType getOptionalParameter(List<OptionalParameterType> optl, String key);

    /**
     * @param opt The OPT
     * @param file The current file
     * @return true is expression is not set or the result of expression
     */
    boolean isOptionalParameterActive(OptionalParameterType opt, File file);

    /**
     * @param value false to deactivate the scanning
     * @see #isActive()
     */
    void activate(boolean value);

    /**
     * @return true if the scanning is active (true by default)
     * @see #activate(boolean)
     */
    boolean isActive();

    /**
     * @return The root directory of the application
     */
    File getRootDir();

    /**
     * Register a global listener
     *
     * @param listener The listener to add
     */
    void registerListener(BasicListener<String, GLOBAL_EVENTS, PropertiesException> listener);

    /**
     * Fire an event to the global listener
     *
     * @param event The event to be fired
     */
    void fireEvent(BasicEvent<String, GLOBAL_EVENTS> event);
}
