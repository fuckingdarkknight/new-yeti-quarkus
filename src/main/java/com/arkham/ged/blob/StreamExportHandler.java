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

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface that define an adapter/handler and its characteristics used to export a LOB from the database.
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public interface StreamExportHandler {
	/**
	 * @param charset The optional charset or <code>null</code>
	 */
	void handleCharset(String charset);

	/**
	 * @param contentType The content type or <code>null</code> if unknown
	 */
	void handleContentType(String contentType);

	/**
	 * @param is The input stream from LOB database
	 * @param bean The document bean
	 * @throws IOException
	 */
	void handleResult(InputStream is, DocumentLinkBean bean) throws IOException;

	/**
	 * If there's no result while retrieving DATA_BLOB from database, this method should be used to send a different message to output stream
	 *
	 * @throws IOException
	 */
	void handleNoResult() throws IOException;

	/**
	 * If there's no result while retrieving THUMB_BLOB from database, this method should be used to send a different message to output stream
	 *
	 * @throws IOException
	 */
	void handleNoThumb() throws IOException;

	/**
	 * Handle the bean while exporting
	 *
	 * @param bean The document bean
	 */
	void handleBean(DocumentLinkBean bean);

	/**
	 * @return The bean handled by the export
	 */
	DocumentLinkBean getBean();

	/**
	 * Handle the LOB size before calling {@link #handleResult(InputStream, DocumentLinkBean)}
	 *
	 * @param filesize The stream size in bytes
	 */
	void handleFilesize(int filesize);

	/**
	 * @return the size of imported file in bytes
	 */
	int getFilesize();

	/**
	 * @param bean The document bean
	 * @return true if the document is authorized to access
	 */
	boolean isAuthorized(DocumentLinkBean bean);
}
