package org.cs2103t.marquee.core.serialization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the parameter preprocessor for field getter/setter.
 * @see FieldGetter
 * @see FieldSetter
 * @see Serializer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PreprocessWith {
    /**
     * Returns the class containing the preprocessor method,
     * or {@code void.class} if using a method on the parameter itself.
     * @return the class containing the preprocessor
     */
    Class<?> clazz() default void.class;

    /**
     * Returns the name of the preprocessor method. Default value is {@code toString}
     * @return the preprocessor name
     */
    String method() default "toString";
}
