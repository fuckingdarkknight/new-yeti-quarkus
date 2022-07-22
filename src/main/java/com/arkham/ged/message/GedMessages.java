
/*
 * Licensed to the Arkham asylum Software Foundation under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.arkham.ged.message;

/**
 * Main domain.
 */
public interface GedMessages {

    /**
     * Executor domain.
     */
    public interface Executor {

        /**
         * "Unable to extract text content from document"
         */
        public static final GedMessage GED_0100 = new GedMessage(false, "Unable to extract text content from document");

        /**
         * "Unable to import text content into database"
         */
        public static final GedMessage GED_0101 = new GedMessage(false, "Unable to import text content into database");

        /**
         * "A logical Indexer used but it's not defined in properties.xml"
         */
        public static final GedMessage GED_0102 = new GedMessage(false, "A logical Indexer used but it's not defined in properties.xml");

        /**
         * "The target of logical indexer is set to an undefined value"
         */
        public static final GedMessage GED_0103 = new GedMessage(false, "The target of logical indexer is set to an undefined value");

        /**
         * "Exception while exporting content to index"
         */
        public static final GedMessage GED_0104 = new GedMessage(false, "Exception while exporting content to index");

        /**
         * "Exception while reading the file to export"
         */
        public static final GedMessage GED_0105 = new GedMessage(false, "Exception while reading the file to export");

        /**
         * "Exception while creating instance of FileKeyProvider @"
         */
        public static final GedMessage GED_0106 = new GedMessage(false, "Exception while creating instance of FileKeyProvider @");

        /**
         * "Exception while updating MEDIA_BLOB.INDIDX"
         */
        public static final GedMessage sqlException = new GedMessage(false, "Exception while updating MEDIA_BLOB.INDIDX");

    }

    /**
     * Extractor domain.
     */
    public interface Extractor {

        /**
         * "Error while extracting content with Tika. Please consult logs for further informations"
         */
        public static final GedMessage tikaExtracting = new GedMessage(false, "Error while extracting content with Tika. Please consult logs for further informations");

        /**
         * "KeyMapper class name not available or not defined"
         */
        public static final GedMessage keyMapperNotDefined = new GedMessage(false, "KeyMapper class name not available or not defined");

        /**
         * "Try to read a file badly encoded in UTF-8"
         */
        public static final GedMessage charsetIncompatible = new GedMessage(false, "Try to read a file badly encoded in UTF-8");

        /**
         * "Unable to close the InputStreamReader correctly"
         */
        public static final GedMessage streamreaderCloseException = new GedMessage(false, "Unable to close the InputStreamReader correctly");

        /**
         * "Unable to read through InputStreamReader correctly"
         */
        public static final GedMessage streamreaderIOException = new GedMessage(false, "Unable to read through InputStreamReader correctly");

    }

    /**
     * Indexer domain.
     */
    public interface Indexer {

        /**
         * "Generic indexer exception"
         */
        public static final GedMessage DI_0300 = new GedMessage(false, "Generic indexer exception");

        /**
         * "SQL exception while writting to database"
         */
        public static final GedMessage DI_0301 = new GedMessage(false, "SQL exception while writting to database");

        /**
         * "URL inconsistent"
         */
        public static final GedMessage inconsistentURL = new GedMessage(false, "URL inconsistent");

        /**
         * "Problem while converting a reader in UTF-8"
         */
        public static final GedMessage charsetConversion = new GedMessage(false, "Problem while converting a reader in UTF-8");

    }

    /**
     * Scanner domain.
     */
    public interface Scanner {

        /**
         * "The primary key is not correctly defined in .ref file. Assume that codsoc/typtie/nomcle/filename are mandatory"
         */
        public static final GedMessage keyMalformed = new GedMessage(false, "The primary key is not correctly defined in .ref file. Assume that codsoc/typtie/nomcle/filename are mandatory");

        /**
         * "Unable do decode the key codsoc/typtie/nomcle from filename"
         */
        public static final GedMessage decodingError = new GedMessage(false, "Unable do decode the key codsoc/typtie/nomcle from filename");

        /**
         * "The field codsoc is mantadory and should be in an integer format"
         */
        public static final GedMessage codsocInvalid = new GedMessage(false, "The field codsoc is mantadory and should be in an integer format");

        /**
         * "Cannot manage encrypted PDF files"
         */
        public static final GedMessage encryptedPdf = new GedMessage(false, "Cannot manage encrypted PDF files");

        /**
         * "The key cannot be determined from PDF content"
         */
        public static final GedMessage GED_0004 = new GedMessage(false, "The key cannot be determined from PDF content");

        /**
         * "The key cannot be determined from scanner optional settings. codsoc, typtie and nomcle have to be defined with corrects values"
         */
        public static final GedMessage invalidSettings = new GedMessage(false, "The key cannot be determined from scanner optional settings. codsoc, typtie and nomcle have to be defined with corrects values");

        /**
         * "The sequence used to determine NOMCLE suffix is not correctly defined"
         */
        public static final GedMessage invalidSequence = new GedMessage(false, "The sequence used to determine NOMCLE suffix is not correctly defined");

        /**
         * "The dynamic instantiation of FileKeyProvider have raised an exception for className=@"
         */
        public static final GedMessage cannotInstantiate = new GedMessage(false, "The dynamic instantiation of FileKeyProvider have raised an exception for className=@");

        /**
         * "The parameter @ is invalid or not present"
         */
        public static final GedMessage parameterInvalid = new GedMessage(false, "The parameter @ is invalid or not present");

    }

    /**
     * Properties domain.
     */
    public interface Properties {

        /**
         * "Ged properties XML file not found"
         */
        public static final GedMessage fileNotFound = new GedMessage(false, "Ged properties XML file not found");

        /**
         * "Ged properties is not compliant with XSD. Please validate your properties file>"
         */
        public static final GedMessage invalidFile = new GedMessage(false, "Ged properties is not compliant with XSD. Please validate your properties file>");

        /**
         * "The DCM cannot be created. Please validate your properties file"
         */
        public static final GedMessage dcmNotCreated = new GedMessage(false, "The DCM cannot be created. Please validate your properties file");

        /**
         * "Arkham GED cannot be started because properties file not found : @"
         */
        public static final GedMessage gedNotStartedDueTofileNotFound = new GedMessage(false, "Arkham GED cannot be started because properties file not found : @");

        /**
         * "The settings of storage are invalids : baseDir should be set if using FileSystem mode"
         */
        public static final GedMessage invalidStorageSettings = new GedMessage(false, "The settings of storage are invalids : baseDir should be set if using FileSystem mode");

        /**
         * "The directory defined for storage does not exists"
         */
        public static final GedMessage dirStorage = new GedMessage(false, "The directory defined for storage does not exists");

        /**
         * "The directory defined for storage does not have the rights permissions : R/W"
         */
        public static final GedMessage dirStorageSecurity = new GedMessage(false, "The directory defined for storage does not have the rights permissions : R/W");

    }

    /**
     * Security domain.
     */
    public interface Security {

        /**
         * "You're not allowed to use the upload/download because of your authorizations settings. Review your administrator to set the rights on documents"
         */
        public static final GedMessage unauthorized = new GedMessage(false, "You're not allowed to use the upload/download because of your authorizations settings. Review your administrator to set the rights on documents");

        /**
         * "You're not allowed to use the upload/download while not connected to application ('@' user)"
         */
        public static final GedMessage unauthorizedGuest = new GedMessage(false, "You're not allowed to use the upload/download while not connected to application ('@' user)");

        /**
         * "The contact '@' have not been found"
         */
        public static final GedMessage contactNotFound = new GedMessage(false, "The contact '@' have not been found");

        /**
         * "Cannot access to database while controling authorizations"
         */
        public static final GedMessage sqlException = new GedMessage(false, "Cannot access to database while controling authorizations");

    }

    /**
     * Action domain.
     */
    public interface Action {

        /**
         * "The dynamic instantiation of AbstractionAction have raised an exception for className=@"
         */
        public static final GedMessage cannotInstantiate = new GedMessage(false, "The dynamic instantiation of AbstractionAction have raised an exception for className=@");

        /**
         * "When action name is not specified, the type should be DATABASE or FILESYSTEM"
         */
        public static final GedMessage actionTypeUndefined = new GedMessage(false, "When action name is not specified, the type should be DATABASE or FILESYSTEM");

        /**
         * "For this action, the parameter @ should be set"
         */
        public static final GedMessage actionParameter = new GedMessage(false, "For this action, the parameter @ should be set");

        /**
         * "The target @ is a file"
         */
        public static final GedMessage actionTargetFile = new GedMessage(false, "The target @ is a file");

        /**
         * "The directory @ cannot be created"
         */
        public static final GedMessage cannotCreateDirectory = new GedMessage(false, "The directory @ cannot be created");

        /**
         * "The key store cannot be found"
         */
        public static final GedMessage cannotFindKeystore = new GedMessage(false, "The key store cannot be found");

        /**
         * "The Signature (image) cannot be found"
         */
        public static final GedMessage cannotFindSignature = new GedMessage(false, "The Signature (image) cannot be found");

        /**
         * "Integer cannot be decoded @"
         */
        public static final GedMessage cannotDecodeInteger = new GedMessage(false, "Integer cannot be decoded @");

        /**
         * "@ : @"
         */
        public static final GedMessage resultError = new GedMessage(false, "@ : @");

    }

    /**
     * Solr domain.
     */
    public interface Solr {

        /**
         * "An exception has been raised during the http call to solr"
         */
        public static final GedMessage httpCallException = new GedMessage(false, "An exception has been raised during the http call to solr");

        /**
         * "The message sent to Solr is probably malformed, please have a look to logs"
         */
        public static final GedMessage badMessage = new GedMessage(false, "The message sent to Solr is probably malformed, please have a look to logs");

        /**
         * "Unexpected exception while reading the input"
         */
        public static final GedMessage unexpected = new GedMessage(false, "Unexpected exception while reading the input");

    }

    /**
     * Sql domain.
     */
    public interface Sql {

        /**
         * "CodsocPhy cannot be computed for : @"
         */
        public static final GedMessage codsocException = new GedMessage(false, "CodsocPhy cannot be computed for : @");

        /**
         * "Entity not defined : @"
         */
        public static final GedMessage entityException = new GedMessage(false, "Entity not defined : @");

    }

    /**
     * Init domain.
     */
    public interface Init {

        /**
         * "Exception while reading default ged.properties"
         */
        public static final GedMessage InitException = new GedMessage(false, "Exception while reading default ged.properties");

    }

    /**
     * Streams domain.
     */
    public interface Streams {

        /**
         * "Bad scheme for path @"
         */
        public static final GedMessage schemeException = new GedMessage(false, "Bad scheme for path @");

        /**
         * "Bad parameter : @ for scheme @"
         */
        public static final GedMessage invalidParameter = new GedMessage(false, "Bad parameter : @ for scheme @");

        /**
         * "Cannot create stream adapter"
         */
        public static final GedMessage exceptionStreamAdapter = new GedMessage(false, "Cannot create stream adapter");

    }

    /**
     * Spo domain.
     */
    public interface Spo {

        /**
         * "Unable to create HTTP client builder for SPO connexion"
         */
        public static final GedMessage clientBuilderException = new GedMessage(false, "Unable to create HTTP client builder for SPO connexion");

        /**
         * "Unable to authenticate to SPO"
         */
        public static final GedMessage authenticationException = new GedMessage(false, "Unable to authenticate to SPO");

        /**
         * "Unable to get SPO resource message : @"
         */
        public static final GedMessage spoMessageException = new GedMessage(false, "Unable to get SPO resource message : @");

        /**
         * "Unable to get login message at @"
         */
        public static final GedMessage spoLoginMessageException = new GedMessage(false, "Unable to get login message at @");

        /**
         * "Invalid HTTP Status : @"
         */
        public static final GedMessage invalidStatusException = new GedMessage(false, "Invalid HTTP Status : @");

        /**
         * "Invalid token while login : @"
         */
        public static final GedMessage invalidTokenException = new GedMessage(false, "Invalid token while login : @");

        /**
         * "Invalid digest while login : @"
         */
        public static final GedMessage invalidDigestException = new GedMessage(false, "Invalid digest while login : @");

        /**
         * "Invalid cookie while login : @=@ @=@"
         */
        public static final GedMessage invalidCookieException = new GedMessage(false, "Invalid cookie while login : @=@ @=@");

        /**
         * "Exception while serializing @"
         */
        public static final GedMessage serException = new GedMessage(false, "Exception while serializing @");

        /**
         * "Exception while deserializing @"
         */
        public static final GedMessage deserException = new GedMessage(false, "Exception while deserializing @");

    }

    /**
     * Crypto domain.
     */
    public interface Crypto {

        /**
         * "Unable to get keystore at @"
         */
        public static final GedMessage keystoreException = new GedMessage(false, "Unable to get keystore at @");

    }

    /**
     * Shell domain.
     */
    public interface Shell {

        /**
         * "Unable to execute command @ for @:@"
         */
        public static final GedMessage execException = new GedMessage(false, "Unable to execute command @ for @:@");

    }

    /**
     * Xls domain.
     */
    public interface Xls {

        /**
         * "File AND message are null, cannot process anything"
         */
        public static final GedMessage badMessageException = new GedMessage(false, "File AND message are null, cannot process anything");

    }

}
