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
package com.arkham.ged.xlsgen.types;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import com.arkham.ged.annotation.EnumDefault;
import com.arkham.ged.annotation.EnumDefaultType;
import com.arkham.ged.solver.SlfTranslator;
import com.arkham.ged.solver.Translator;

/**
 * Common class for Enum deserializing
 *
 * @author arocher / Arkham asylum
 * @version 1.0
 * @since 13 févr. 2020
 */
public class AbstractTypeDeserializer<E extends EnumDefaultType> extends JsonDeserializer<E> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTypeDeserializer.class);

    private static final Pattern TYPE_EXTRACT_PATTERN = Pattern.compile(".*<(.*)>");

    private static final String MESSAGE = "deserialize() : {} cannot be deserialized for type {} at line {} column {}";
    private static final Translator TR = new SlfTranslator();

    private final ErrorAppender mErrorAppender;

    private final Map<String, Class<E>> mCache = new HashMap<>(32);

    /**
     * Constructor AbstractTypeDeserializer
     *
     * @param ea Appender for errors
     */
    public AbstractTypeDeserializer(final ErrorAppender ea) {
        mErrorAppender = ea;
    }

    /**
     * Add a new error/warning to the error stack
     *
     * @param log The logger
     * @param p The parser
     * @param e The enum type
     * @param value The value that can't be parsed
     */
    protected void addError(final Logger log, final JsonParser p, final Class<E> e, final String value) {
        final var m = TR.translate(MESSAGE, value, e, p.getCurrentLocation().getLineNr(), p.getCurrentLocation().getColumnNr());

        log.warn(m);

        mErrorAppender.add(m);
    }

    /**
     * @param c The class
     * @return The default valeur if EnumDefault is annotated for the class
     * @deprecated
     */
    @Deprecated
    protected String getDefaultValue(final Class<E> c) {
        final var def = c.getAnnotationsByType(EnumDefault.class);
        if (def.length > 0) {
            return def[0].value();
        }

        return null;
    }

    @Override
    public E deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final var superType = getClass().getGenericSuperclass().getTypeName();
        var clazz = mCache.get(superType);
        if (clazz == null) {
            final var m = TYPE_EXTRACT_PATTERN.matcher(getClass().getGenericSuperclass().getTypeName());
            if (m.matches()) {
                final var type = m.group(1);

                try {
                    clazz = (Class<E>) Class.forName(type);

                    mCache.put(superType, clazz);
                } catch (@SuppressWarnings("unused") final ClassNotFoundException e) { // NOSONAR
                    addError(LOGGER, p, (Class<E>) getClass(), p.getText());
                }
            } else {
                throw new IOException("Enum cannot be deserialized by " + getClass().getCanonicalName());
            }
        }

        assert clazz != null;
        return getWithDefault(clazz, p.getText().toUpperCase(), p);
    }

    protected E getWithDefault(@NonNull final Class<E> clazz, final String value, final JsonParser p) {
        if (clazz.isEnum()) {
            try {
                final var o = getValue(clazz, value, p);
                if (o == null) {
                    final var fields = clazz.getDeclaredFields();
                    for (final Field field : fields) {
                        final var ann = field.getAnnotationsByType(JsonEnumDefaultValue.class);
                        if (ann.length > 0) {
                            return (E) field.get("X"); // No mind about the parameter in this case
                        }
                    }
                }
            } catch (@SuppressWarnings("unused") IllegalArgumentException | IllegalAccessException | SecurityException e) { // NOSONAR
                addError(LOGGER, p, clazz, value);
            }
        }

        return null;
    }

    private final E getValue(@NonNull final Class<E> clazz, final String value, final JsonParser p) {
        try {
            final var m = clazz.getMethod("valueOf", String.class);
            final var o = m.invoke(clazz, value);

            return (E) o;
        } catch (@SuppressWarnings("unused") final IllegalArgumentException | IllegalAccessException | SecurityException | NoSuchMethodException | InvocationTargetException e) { // NOSONAR
            // Let's continue with default value but trace the error
            addError(LOGGER, p, clazz, value);
        }

        return null;
    }
}
