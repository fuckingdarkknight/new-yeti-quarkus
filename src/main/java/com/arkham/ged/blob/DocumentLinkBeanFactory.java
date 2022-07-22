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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory used to create {@link DocumentLinkBean}
 *
 * @author Alex / Arkham asylum
 * @version 1.0
 * @since 10 févr. 2015
 */
public final class DocumentLinkBeanFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentLinkBeanFactory.class);

    public static final String BEAN_FACTORY_PROPERTY = "arkham.ged.blob.beanfactory";

    private DocumentLinkBeanFactory() {
        // Private because it's an utility class, so we should't get an instance of this class
    }

    /**
     * Gets a new instance of {@link DocumentLinkBean}. Use <code>-D{@value #BEAN_FACTORY_PROPERTY}=classname</code> to create a new object. If this JVM parameter is not set, the default {@link DocumentLinkBean} class is
     * instancied
     * from its default constructor.
     *
     * @return A new {@link DocumentLinkBean} object
     */
    public static DocumentLinkBean create() {
        final var className = System.getProperty(BEAN_FACTORY_PROPERTY);
        if (className != null) {
            try {
                final Class<?> clazz = Class.forName(className);
                final Constructor<?> c = clazz.getConstructor();

                return (DocumentLinkBean) c.newInstance();
            } catch (ClassNotFoundException | SecurityException | NoSuchMethodException | IllegalArgumentException | InstantiationException | IllegalAccessException | InvocationTargetException | ClassCastException e) {
                LOGGER.error("createInstance() : className={} exception=", className, e);
            }
        }

        return new DocumentLinkBean();
    }
}
