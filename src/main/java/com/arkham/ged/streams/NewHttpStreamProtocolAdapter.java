package com.arkham.ged.streams;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewHttpStreamProtocolAdapter extends AbstractStreamProtocolAdapter<String, Map<String, Object>> {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(NewHttpStreamProtocolAdapter.class);

    protected static final String HEADER_CONNECTION = "Connection";
    protected static final String HEADER_USERAGENT = "User-Agent";
    protected static final String HEADER_CONTENTTYPE = "Content-Type";
    protected static final String HEADER_CONTENTDISPOSITION = "Content-Disposition";

    NewHttpStreamProtocolAdapter(final String value) {
        super(value);
    }

    @Override
    public InputStream getStream() throws StreamProtocolException {
        final var url = getValue();
        try {
            /* @formatter:off */
            final HttpRequest request = HttpRequest.newBuilder().
                    timeout(Duration.ofSeconds(5)).
                    uri(new URI(url)).
                    header(HEADER_CONNECTION, "Keep-Alive").
                    header(HEADER_USERAGENT, "Java client").
                    GET().
                    build();

            final HttpClient client = HttpClient.newBuilder().
                    connectTimeout(Duration.ofSeconds(5)).
                    followRedirects(Redirect.ALWAYS).
                    build();
            /* @formatter:on */

            final var response = client.send(request, BodyHandlers.ofInputStream());

            setStatusCode(response.statusCode());

            return response.body();
        } catch (final URISyntaxException | IOException | InterruptedException e) {
            throw new StreamProtocolException(e);
        }
    }

    @SuppressWarnings("unused")
    private static String getContentType(final String contentType) {
        if (contentType == null) {
            return "";
        }

        final var pos = contentType.indexOf(';');
        if (pos > -1) {
            return contentType.substring(0, pos);
        }

        return contentType;
    }

    @SuppressWarnings("unused")
    private static String getCharset(final String contentType) {
        if (contentType == null) {
            return DEFAULT_CHARSET;
        }

        final var pos = contentType.toUpperCase().indexOf("CHARSET=");
        if (pos > -1) {
            return contentType.substring(pos + 8);
        }

        return DEFAULT_CHARSET;
    }

    @SuppressWarnings("unused")
    private static String getFilename(final String contentDisposition) {
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
}
