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
package com.arkham.ged.xlsgen;

/**
 * Runtime exception for package, just used to escalte another Exception
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 24 févr. 2020
 */
public class XslgenRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor XslgenRuntimeException
     *
     * @param cause The root cause
     */
    public XslgenRuntimeException(final Throwable cause) {
        super(cause);
    }
}
