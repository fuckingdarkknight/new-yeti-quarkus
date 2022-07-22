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

/**
 * Common abstract for blob import and export
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public interface IDocumentLinkConstants {
    String DEFAULT_UTICOD = "BATMAN";
    String DEFAULT_CODLAN = "FRA";

    String SELECT_INDSEQ = "SELECT NUMVER, INDSEQ FROM ";

    String TABLE_NAME = "MEDIA_BLOB";

    String PK_PREDICAT = "CODSOC = ? AND TYPTIE = ?  AND NOMCLE = ? AND CODDOC = ? AND FILENAME = ? AND NUMVER = ?";

    String UK_PREDICAT = "INDSEQ = ?";

    String MAXVERSION_PREDICAT = "CODSOC = ? AND TYPTIE = ? AND NOMCLE = ? AND CODDOC = ? AND FILENAME = ?";

    String MAX_STMT = SELECT_INDSEQ + TABLE_NAME + " WHERE " + MAXVERSION_PREDICAT + " ORDER BY NUMVER DESC";

    String MAX_TYPMED_STMT = SELECT_INDSEQ + TABLE_NAME + " WHERE " + MAXVERSION_PREDICAT + " AND TYPMED = ? ORDER BY NUMVER DESC";

    String LOCK_STMT = MAX_STMT + " FOR UPDATE";

    String TRAC_TABLE_NAME = "MEDIA_TRAC";
}
