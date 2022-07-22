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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic implementation of {@link StreamExportHandler}, without the input stream handler method
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 19 mai 2016
 */
public abstract class BasicStreamExportHandler implements StreamExportHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicStreamExportHandler.class);

    private DocumentLinkBean mBean;

    @Override
    public void handleCharset(String charset) {
        // Nothing to do
    }

    @Override
    public void handleContentType(String contentType) {
        // Nothing to do
    }

    @Override
    public void handleNoResult() throws IOException {
        LOGGER.info("handleNoResult() : no result for bean={}", mBean);
    }

    @Override
    public void handleNoThumb() throws IOException {
        // Nothing to do
    }

    @Override
    public void handleFilesize(int filesize) {
        // Nothing to do
    }

    @Override
    public int getFilesize() {
        return 0;
    }

    @Override
    public void handleBean(DocumentLinkBean bean) {
        mBean = bean;
    }

    @Override
    public DocumentLinkBean getBean() {
        return mBean;
    }

    /**
     * By default, all documents are authorized
     * {@inheritDoc}
     *
     * @see com.arkham.ged.blob.StreamExportHandler#isAuthorized(com.arkham.ged.blob.DocumentLinkBean)
     */
    @Override
    public boolean isAuthorized(DocumentLinkBean bean) {
        return true;
    }
}
