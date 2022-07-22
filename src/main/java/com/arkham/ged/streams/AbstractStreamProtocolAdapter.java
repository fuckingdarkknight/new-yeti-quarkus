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
package com.arkham.ged.streams;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;

/**
 * Abstract stream protocol adapter that implements some common methods
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 13 nov. 2017
 * @param <T> Main type returned
 * @param <O> Optional parameters
 */
abstract class AbstractStreamProtocolAdapter<T, O> implements StreamProtocolAdapter<T, O> {
    private T mValue;
    private InputStream mInputStream;
    private byte[] mArray;
    private String mStreamName;
    private String mCharset;
    private String mContentType;
    private int mStatusCode;

    private O mOptions;
    private Connection mConnection;

    AbstractStreamProtocolAdapter(final T value) {
        mValue = value;
    }

    void initInner(final Connection con, final O options) {
        mConnection = con;
        mOptions = options;
    }

    void setStream(final InputStream inputStream) {
        mInputStream = inputStream;
    }

    void setArray(final byte[] array) {
        mArray = array; // NOSONAR
    }

    void setStreamName(final String streamName) {
        mStreamName = streamName;
    }

    void setValue(final T value) {
        mValue = value;
    }

    /**
     * @param charset the charset to set
     */
    void setCharset(final String charset) {
        mCharset = charset;
    }

    /**
     * @param contentType the contentType to set
     */
    void setContentType(final String contentType) {
        mContentType = contentType;
    }

    /**
     * @param statusCode the statusCode to set
     */
    void setStatusCode(final int statusCode) {
        mStatusCode = statusCode;
    }

    @Override
    public InputStream getStream() throws StreamProtocolException {
        return mInputStream;
    }

    @Override
    public byte[] getArray() throws StreamProtocolException {
        return mArray; // NOSONAR
    }

    @Override
    public String getStreamName() {
        return mStreamName;
    }

    @Override
    public T getValue() {
        return mValue;
    }

    @Override
    public void close() throws IOException {
        if (mInputStream != null) {
            mInputStream.close();
        }
    }

    /**
     * If charset if not known, assume that we process UTF-8
     *
     * @return the charset
     */
    @Override
    public String getCharset() {
        if (mCharset == null) {
            return "UTF-8";
        }

        return mCharset;
    }

    /**
     * @return the contentType
     */
    @Override
    public String getContentType() {
        return mContentType;
    }

    /**
     * @return the options
     */
    @Override
    public O getOptions() {
        return mOptions;
    }

    protected Connection getConnection() {
        return mConnection;
    }

    /**
     * @return the statusCode
     */
    @Override
    public int getStatusCode() {
        return mStatusCode;
    }
}
