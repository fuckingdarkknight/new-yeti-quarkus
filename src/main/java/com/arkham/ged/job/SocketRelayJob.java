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
package com.arkham.ged.job;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkham.common.pattern.destroy.IDestroyable;
import com.arkham.common.pattern.listener.BasicEvent;
import com.arkham.ged.properties.PropertiesAdapter;
import com.arkham.ged.properties.PropertiesAdapter.GLOBAL_EVENTS;
import com.arkham.ged.socket.SocketRelayException;
import com.arkham.ged.util.GedUtil;

import inet.ipaddr.IPAddressString;

/**
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 18 déc. 2017
 */
public class SocketRelayJob implements IDestroyable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SocketRelayJob.class);

    private static final Pattern MASK = Pattern.compile("[ ,;|]");

    private final PropertiesAdapter mPa;

    private final List<ServerSocket> mSockets;

    /**
     * Constructor SocketRelayJob
     *
     * @param pa The properties adapter
     */
    public SocketRelayJob(final PropertiesAdapter pa) {
        mPa = pa;
        mSockets = new ArrayList<>();

        try {
            for (final InetAddress addr : getLocalAddresses()) {
                initInner(addr);
            }
        } catch (final SocketRelayException e) {
            LOGGER.error("SocketRelayJob()", e);
        }
    }

    private InetAddress[] getLocalAddresses() {
        final List<InetAddress> result = new ArrayList<>();

        try {
            final var hostname = InetAddress.getLocalHost().getHostName();
            final var mask = GedUtil.getString(mPa.getSocket().getAddress(), "*");

            LOGGER.info("getLocalAddresses() : hostname={} ({})", hostname, InetAddress.getLocalHost().getCanonicalHostName());
            LOGGER.info("getLocalAddresses() : mask(s)={}", mask);

            for (final var en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                final var intf = en.nextElement();

                // No need to listen an interface that is down
                if (intf.isUp()) {
                    for (final var enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                        final var ia = enumIpAddr.nextElement();

                        if (isAddressValid(mask, ia)) {
                            result.add(ia);

                            LOGGER.info("getLocalAddresses() : int={} ({}) virtual={} IPv4 listened={}", intf.getName(), intf.getDisplayName(), intf.isVirtual(), ia.getHostAddress());
                        } else {
                            // No need to trace IPv6
                            LOGGER.trace("getLocalAddresses() : int={} ({}) IPv6 not listened={}", intf.getName(), intf.getDisplayName(), ia);
                        }
                    }
                } else {
                    LOGGER.trace("getLocalAddresses() : int={} ({}) is down and will not be listened", intf.getName(), intf.getDisplayName());
                }
            }
        } catch (final UnknownHostException | SocketException e) {
            LOGGER.error("getLocalAddresses()", e);
        }

        return result.toArray(new InetAddress[result.size()]);
    }

    private static boolean isAddressValid(final String mask, final InetAddress ia) {
        // IPv4=4 bytes, IPv6 else : just consider IPv4
        final var ipv4 = ia.getAddress().length == 4;
        if (!ipv4) {
            return false;
        }

        // Convenience wildcard
        if ("*".equals(mask)) {
            return true;
        }

        // Mask list separated by
        final var maskList = MASK.split(mask);
        var result = false;
        for (final String s : maskList) {
            result = result || isAddressValid(s, ia.getHostAddress());
        }

        return result;
    }

    private static boolean isAddressValid(final String network, final String address) {
        final var one = new IPAddressString(network);

        // Invalid mask, just log
        if (!one.isValid()) {
            LOGGER.error("isAddressValid() : mask is invalid={}", network);

            return true;
        }

        final var two = new IPAddressString(address);

        return one.contains(two);
    }

    private void initInner(final InetAddress ia) throws SocketRelayException {
        try {
            final var st = mPa.getSocket();

            // Listener is closed after thread running
            @SuppressWarnings("resource")
            final var listener = new ServerSocket(st.getPort().intValue(), 1, ia);

            mSockets.add(listener);

            final Thread t = new Thread("ged-socket-" + ia.getHostAddress()) { // NOSONAR
                @Override
                public void run() {
                    try {
                        while (true) {
                            @SuppressWarnings("resource")
                            final var socket = listener.accept();
                            socket.setTcpNoDelay(true);
                            new SocketMessageHandler(socket).start();
                        }
                    } catch (@SuppressWarnings("unused") final SocketException e) { // NOSONAR
                        // Don't mind the bollocks to rethrow the exception, this occurs when the socket is closing
                        LOGGER.info("run() : socket handler is stopped");
                    } catch (final IOException e) {
                        LOGGER.error("run() : exception while creating new socket handler", e);
                    } finally {
                        try {
                            listener.close();
                        } catch (final IOException e) {
                            LOGGER.error("run() : exception while closing socket", e);
                        }
                    }
                }
            };

            t.start();
        } catch (final IOException e) {
            throw new SocketRelayException(e);
        }
    }

    @Override
    public void destroy() {
        for (final ServerSocket serverSocket : mSockets) {
            try {
                serverSocket.close();
            } catch (final IOException e) {
                LOGGER.error("destroy() : exception while closing socket", e);
            }
        }
    }

    private class SocketMessageHandler extends Thread {
        private final Socket mSocket;

        private SocketMessageHandler(final Socket socket) {
            mSocket = socket;

            LOGGER.info("SocketMessageHandler() : received message from {}", socket);
        }

        @Override
        public void run() {
            try {
                final var in = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));

                final var sb = new StringBuilder();
                // Truc de bourrin sans buffer, normalement ces messages sont de l'ordre de quelques kilos maxi
                // donc ça ne devrait rien changer du tout !
                int ch;
                while ((ch = in.read()) != -1) {
                    sb.append((char) ch);
                }

                relayMessage(sb.toString());

                mSocket.close();
            } catch (final IOException e) {
                LOGGER.error("run()", e);
            }
        }

        private void relayMessage(final String msg) {
            if ("".equals(msg)) {
                LOGGER.warn("relayMessage() : empty message, cannot be processed");
                return;
            }

            LOGGER.info("relayMessage() :\n{}", msg);

            try {
                final var o = getJson(msg);

                final var s = o.optString("action");
                if ("shutdown".equalsIgnoreCase(s)) {
                    mPa.fireEvent(new BasicEvent<>("yeti", GLOBAL_EVENTS.SHUTDOWN));
                }
            } catch (final JSONException | IOException e) {
                LOGGER.error("relayMessage() : problem while parsing JSON message", e);
            }
        }

        private JSONObject getJson(final String message) throws IOException {
            final var m = GedUtil.convertYamlToJson(message);

            return new JSONObject(m);
        }
    }
}
