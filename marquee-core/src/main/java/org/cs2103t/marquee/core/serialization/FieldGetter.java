package org.cs2103t.marquee.core.serialization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates the getter of the property to be serialized.
 * @see PreprocessWith
 * @see Serializer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FieldGetter {
    /**
     * Returns the name of the property to get.
     * @return the property name
     */
    public String value();
}
