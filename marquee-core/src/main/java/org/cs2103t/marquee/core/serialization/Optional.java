package org.cs2103t.marquee.core.serialization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates a getter/setter is optional.
 * <p>
 * If an optional getter returns {@code null}, it is excluded from the mapping.
 * <p>
 * If an optional field is empty, the setter will not be called.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Optional {
}
