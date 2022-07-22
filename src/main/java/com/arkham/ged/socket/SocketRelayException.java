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
package com.arkham.ged.socket;

import com.arkham.ged.GedException;

/**
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 18 déc. 2017
 */
public class SocketRelayException extends GedException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor SocketRelayException
     *
     * @param cause The root cause
     */
    public SocketRelayException(final Throwable cause) {
        super(cause);
    }
}
