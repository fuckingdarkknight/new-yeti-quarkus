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

import java.io.Closeable;
import java.io.InputStream;

/**
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 13 nov. 2017
 * @param <T> The type of value
 * @param <O> Optional parameters
 */
public interface StreamProtocolAdapter<T, O> extends Closeable {
    /**
     * Assume UTF-8 as default streams charset
     */
    String DEFAULT_CHARSET = "UTF-8";

    /**
     * Get a stream from a name that begin (or not) with a protocol
     *
     * @return The stream provided by adapters
     * @throws StreamProtocolException Generic exception in case of troubles creating the matching stream
     * @see #getArray()
     */
    InputStream getStream() throws StreamProtocolException;

    /**
     * Get an array from a name that begin (or not) with a protocol
     *
     * @return The result provided by adapters
     * @throws StreamProtocolException Generic exception in case of troubles creating the matching stream
     * @see #getStream()
     */
    byte[] getArray() throws StreamProtocolException;

    /**
     * @return The name without protocol prefix
     */
    T getValue();

    /**
     * @return The optional stream name
     */
    String getStreamName();

    /**
     * @return The charset if streamer update it
     */
    String getCharset();

    /**
     * @return The content-type if streamer update it
     */
    String getContentType();

    /**
     * @return The optional parameters
     */
    O getOptions();

    /**
     * @return The optional status code, should be usefull for HTTP adapter for example
     */
    int getStatusCode();
}
