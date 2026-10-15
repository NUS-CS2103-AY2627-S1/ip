package org.cs2103t.marquee.core.serialization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates the setter of the property to be serialized.
 * @see PreprocessWith
 * @see Serializer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FieldSetter {
    /**
     * Returns the name of the property to set.
     * @return the property name
     */
    public String value();
}
