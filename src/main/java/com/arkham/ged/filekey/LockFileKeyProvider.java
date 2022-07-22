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
package com.arkham.ged.filekey;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.sql.Connection;
import java.util.List;

import com.arkham.common.properties.FlatProp;
import com.arkham.ged.properties.OptionalParameterType;
import com.arkham.ged.properties.PropertiesAdapter;

/**
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public class LockFileKeyProvider extends FlatFileKeyProvider {
    @Override
    public FileKey getKey(File file, Connection con, PropertiesAdapter pa, List<OptionalParameterType> opt) throws FileKeyProviderException {
        try {
            FileLock fl = null;
            try (var raf = new RandomAccessFile(file, "rw"); var fc = raf.getChannel()) {
                fl = fc.tryLock();

                final var bb = ByteBuffer.allocate((int) file.length());
                fc.read(bb);

                final var p = new FlatProp();
                try (var reader = new StringReader(new String(bb.array()))) {
                    p.load(reader);
                }

                // If file is empty, it should have ever been processed : not an error case
                if (p.isEmpty()) {
                    return null;
                }

                // clear the file
                fc.truncate(0);

                return getFileKey(p);
            } finally {
                if (fl != null) {
                    fl.release();
                }
            }
        } catch (final IOException e) {
            throw new FileKeyProviderException(e);
        }
    }

    @Override
    public boolean isRefFile() {
        return true;
    }
}
