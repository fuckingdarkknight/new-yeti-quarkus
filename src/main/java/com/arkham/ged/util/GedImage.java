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
package com.arkham.ged.util;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

/**
 * Class that contains utilities methods for images (jpg ...) processing ...
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public final class GedImage {
    private GedImage() {
        // Private because it's an utility class, so we should't get an instance of this class
    }

    private static BufferedImage scale(BufferedImage bImage, int maxDimension) {
        var destWidth = bImage.getWidth();
        var destHeight = bImage.getHeight();
        final double maxDim = Math.max(destWidth, destHeight);
        final var ratio = maxDim / maxDimension;
        destWidth = (int) (destWidth / ratio);
        destHeight = (int) (destHeight / ratio);

        // créer l'image de destination
        final var configuration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        final var bImageNew = configuration.createCompatibleImage(destWidth, destHeight);
        final var graphics = bImageNew.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        // dessiner l'image de destination
        graphics.drawImage(bImage, 0, 0, destWidth, destHeight, 0, 0, bImage.getWidth(), bImage.getHeight(), null);
        graphics.dispose();

        return bImageNew;
    }

    /**
     * @param data Input datas
     * @param format
     * @param maxDim
     * @return The resized datas
     * @throws IOException
     */
    public static byte[] resizeImage(byte[] data, String format, int maxDim) throws IOException {
        try (var baos = new ByteArrayOutputStream(); var bais = new ByteArrayInputStream(data)) {
            final var imgBase = ImageIO.read(bais);
            // Metadata contenus dans les images : http://johnbokma.com/java/obtaining-image-metadata.html
            final var imgResized = scale(imgBase, maxDim);
            ImageIO.write(imgResized, format, baos);
            baos.flush();

            return baos.toByteArray();
        }
    }

    /**
     * @param data
     * @param writer
     * @param format
     * @param maxDim
     * @throws IOException
     */
    public static void resizeImage(InputStream data, OutputStream writer, String format, int maxDim) throws IOException {
        final var imgBase = ImageIO.read(data);
        final var imgResized = scale(imgBase, maxDim);
        ImageIO.write(imgResized, format, writer);
        writer.flush();
    }
}
