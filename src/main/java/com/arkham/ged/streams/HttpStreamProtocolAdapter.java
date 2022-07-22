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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkham.common.util.GxUtil;
import com.arkham.ged.message.GedMessages;

/**
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 23 nov. 2017
 */
public class HttpStreamProtocolAdapter extends AbstractStreamProtocolAdapter<String, Map<String, Object>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpStreamProtocolAdapter.class);

    protected static final String HEADER_CONNECTION = "Connection";
    protected static final String HEADER_USERAGENT = "User-Agent";
    protected static final String HEADER_CONTENTTYPE = "Content-Type";
    protected static final String HEADER_CONTENTDISPOSITION = "Content-Disposition";

    /**
     * Constructor HttpStreamProtocolAdapter
     *
     * @param value
     */
    HttpStreamProtocolAdapter(String value) {
        super(value);
    }

    @Override
    public InputStream getStream() throws StreamProtocolException {
        final var builder = getClientBuilder();
        final var cookieStore = new BasicCookieStore();
        final List<Header> defaultHeaders = new ArrayList<>();
        defaultHeaders.add(new BasicHeader(HEADER_USERAGENT, "Apache Http Client"));
        defaultHeaders.add(new BasicHeader(HEADER_CONNECTION, "Keep-Alive"));

        try (var client = builder.setDefaultCookieStore(cookieStore).setDefaultHeaders(defaultHeaders).build()) {
            beforeExecute(client);

            final var url = getValue();
            final var post = new HttpPost(url);
            post.setEntity(new UrlEncodedFormEntity(getPostParams(), "UTF-8"));

            final var result = execute(client, post, true);

            // Store the result array, the client could read InputStream or directly read the byte array
            setArray(result);

            afterExecute(client);

            return new ByteArrayInputStream(result);
        } catch (final IOException e) {
            throw new StreamProtocolException(e);
        }
    }

    /**
     * Execute before URL
     *
     * @param client Current http client
     * @throws StreamProtocolException
     */
    protected void beforeExecute(CloseableHttpClient client) throws StreamProtocolException {
        //
    }

    /**
     * Execute after URL
     *
     * @param client Current http client
     * @throws StreamProtocolException
     */
    protected void afterExecute(CloseableHttpClient client) throws StreamProtocolException {
        //
    }

    @SuppressWarnings("static-method")
    protected void closeSilently(CloseableHttpClient client) {
        if (client != null) {
            try {
                client.close();
            } catch (final IOException e) {
                LOGGER.error("releaseSilently()", e);
            }
        }
    }

    @SuppressWarnings("static-method")
    protected HttpClientBuilder getClientBuilder() throws StreamProtocolException {
        try {
            final var sslContextBuilder = SSLContextBuilder.create();
            sslContextBuilder.loadTrustMaterial(new TrustSelfSignedStrategy());
            final var sslContext = sslContextBuilder.build();
            final var sslSocketFactory = new SSLConnectionSocketFactory(sslContext, new DefaultHostnameVerifier());
            final var builder = HttpClients.custom().setSSLSocketFactory(sslSocketFactory);
            // Default TTL to 10s, should be OK
            builder.setConnectionTimeToLive(10, TimeUnit.SECONDS);

            return builder;
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            throw new StreamProtocolException(e, GedMessages.Streams.exceptionStreamAdapter);
        }
    }

    protected byte[] execute(CloseableHttpClient client, HttpPost post, boolean updateHeader) throws StreamProtocolException {
        try {
            final HttpResponse response = client.execute(post);

            // Gets some usefull headers
            if (updateHeader) {
                setContentType(getContentType(getHeaderValue(response, HEADER_CONTENTTYPE)));
                setCharset(getCharset(getContentType()));
                setStreamName(getFilename(getHeaderValue(response, HEADER_CONTENTDISPOSITION)));
            }

            setStatusCode(response.getStatusLine().getStatusCode());

            try (final var is = response.getEntity().getContent()) {
                try (var os = new ByteArrayOutputStream()) {
                    GxUtil.copyIs2Os(is, os);

                    return os.toByteArray();
                }
            }
        } catch (final IOException e) {
            throw new StreamProtocolException(e);
        }
    }

    protected byte[] execute(CloseableHttpClient client, HttpPost post) throws StreamProtocolException {
        return execute(client, post, false);
    }

    private static String getHeaderValue(HttpResponse response, String headerKey) {
        if (headerKey == null) {
            return null;
        }

        final var headers = response.getAllHeaders();
        for (final Header header : headers) {
            final var name = header.getName();
            if (headerKey.equalsIgnoreCase(name)) {
                return header.getValue();
            }
        }

        return null;
    }

    private static String getContentType(String contentType) {
        if (contentType == null) {
            return "";
        }

        final var pos = contentType.indexOf(';');
        if (pos > -1) {
            return contentType.substring(0, pos);
        }

        return contentType;
    }

    private static String getCharset(String contentType) {
        if (contentType == null) {
            return DEFAULT_CHARSET;
        }

        final var pos = contentType.toUpperCase().indexOf("CHARSET=");
        if (pos > -1) {
            return contentType.substring(pos + 8);
        }

        return DEFAULT_CHARSET;
    }

    private static String getFilename(String contentDisposition) {
        if (contentDisposition == null) {
            return null;
        }

        final var pos = contentDisposition.toUpperCase().indexOf("FILENAME=");
        if (pos > -1) {
            var s = contentDisposition.substring(pos + 9);
            if (s.startsWith("\"") && s.endsWith("\"")) {
                s = s.substring(1, s.length() - 1);
            }

            return s;
        }

        return null;
    }

    private List<NameValuePair> getPostParams() {
        final List<NameValuePair> result = new ArrayList<>();

        final var o = getOptions();
        if (o != null) {
            for (final Entry<String, Object> e : o.entrySet()) {
                final var v = e.getValue();
                if (v != null) {
                    result.add(new BasicNameValuePair(e.getKey(), v.toString()));
                }
            }
        }

        return result;
    }
}
