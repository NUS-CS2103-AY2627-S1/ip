package org.cs2103t.marquee.core.serialization;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Utility class that provide methods for serializing and deserializing objects.
 * @see Serializable
 * @see FieldGetter
 * @see FieldSetter
 * @see PreprocessWith
 */
public class Serializer {
    public static final String CLASS_NAME_FIELD_NAME = "";

    private static final Map<Class<?>, Map<Class<? extends Annotation>, List<Method>>> ANNOTATED_METHODS =
            new HashMap<>();

    private Serializer() {}

    private static Method searchMethod(Class<?> clazz, String name, Class<?> returns, Class<?>... params)
            throws NoSuchMethodException {
        return findAnnotatedMethods(clazz, null).stream()
                .filter(method -> {
                    Class<?>[] actualParams = method.getParameterTypes();
                    return method.getName().equals(name)
                            && returns.isAssignableFrom(method.getReturnType())
                            && actualParams.length == params.length
                            && IntStream.range(0, actualParams.length)
                            .allMatch(i -> actualParams[i].isAssignableFrom(params[i]));
                })
                .findAny()
                .orElseThrow(() -> new NoSuchMethodException(
                        returns.getSimpleName()
                                + " " + clazz.getSimpleName()
                                + "#" + name + "("
                                + Arrays.stream(params)
                                .map(Class::getSimpleName)
                                .collect(Collectors.joining(", "))
                                + ")"
                ));
    }

    private static List<Method> findAnnotatedMethods(Class<?> clazz, Class<? extends Annotation> annotationType) {
        Stream<Method> annotatedMethods = Stream.empty();
        Class<?> currentClass = clazz;
        while (currentClass != null) {
            if (!ANNOTATED_METHODS.containsKey(currentClass)) {
                Class<?> constClassRef = currentClass;
                Arrays.stream(currentClass.getDeclaredMethods()).forEach(method -> {
                    Map<Class<? extends Annotation>, List<Method>> intermediateMap =
                            ANNOTATED_METHODS.computeIfAbsent(constClassRef, _ -> new HashMap<>());
                    Arrays.stream(method.getDeclaredAnnotations()).forEach(annotation ->
                            intermediateMap
                                    .computeIfAbsent(annotation.annotationType(), _ -> new LinkedList<>())
                                    .add(method)
                    );
                    intermediateMap.computeIfAbsent(null, _ -> new LinkedList<>()).add(method);
                });
            }
            Map<Class<? extends Annotation>, List<Method>> intermediateMap = ANNOTATED_METHODS.get(currentClass);
            annotatedMethods = Stream.concat(
                    annotatedMethods,
                    intermediateMap == null
                            ? Stream.empty()
                            : annotationType == null
                              ? intermediateMap.values().stream().flatMap(List::stream)
                              : intermediateMap.getOrDefault(annotationType, Collections.emptyList()).stream()
            );
            currentClass = currentClass.getSuperclass();
        }
        return annotatedMethods.toList();
    }

    private static <T, U> U preprocess(Method method, T input, Class<U> outputType)
            throws NoSuchMethodException, InvocationTargetException, ClassCastException {
        PreprocessWith annotation = method.getAnnotation(PreprocessWith.class);
        if (annotation != null) {
            if (annotation.clazz() != void.class) {
                Method preprocessor = searchMethod(
                        annotation.clazz(),
                        annotation.method(),
                        outputType,
                        input.getClass()
                );
                preprocessor.setAccessible(true);
                return outputType.cast(forceInvoke(preprocessor, null, input));
            } else {
                Method preprocessor = searchMethod(
                        input.getClass(),
                        annotation.method(),
                        outputType
                );
                preprocessor.setAccessible(true);
                return outputType.cast(forceInvoke(preprocessor, input));
            }
        } else {
            return outputType.cast(input);
        }
    }

    private static Object forceInvoke(Method method, Object target, Object... args) throws InvocationTargetException {
        try {
            method.setAccessible(true);
            return method.invoke(target, args);
        } catch (IllegalAccessException e) {
            assert false : "Unexpected IllegalAccessException on " + method.getName();
            return null;
        }
    }

    private static <T> T forceInvoke(Constructor<T> constructor, Object... args)
            throws InvocationTargetException, InstantiationException {
        try {
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (IllegalAccessException e) {
            assert false : "Unexpected IllegalAccessException on " + constructor.getName();
            return null;
        }
    }


    /**
     * Serializes the given object.
     *
     * @param target the object to inject into
     * @param classNameEncoder the filter to obfuscate class name
     * @return the serialized field mapping
     * @throws IllegalStateException if the target class doesn't have the {@link Serializable @Serializable} annotation
     * @throws NoSuchMethodException if the parser with the appropriate parameter and return types cannot be found
     * @throws IllegalArgumentException if the getter doesn't have a return value
     * @throws NoSuchElementException if the field mapping is missing required fields
     * @throws InvocationTargetException if the parser or getter throws an exception
     * @throws ClassCastException if the attribute can't be converted to a string
     */
    public static Map<String, String> serialize(Object target, ClassNameEncoder classNameEncoder)
            throws IllegalStateException, NoSuchMethodException,
            IllegalArgumentException, InvocationTargetException, ClassCastException {
        Class<?> clazz = target.getClass();
        if (!clazz.isAnnotationPresent(Serializable.class)) {
            throw new IllegalStateException("Class " + clazz.getName() + " is not annotated with @Serializable");
        }

        Map<String, String> fields = new HashMap<>();
        fields.put(CLASS_NAME_FIELD_NAME, classNameEncoder.encode(clazz));

        for (Method getter : findAnnotatedMethods(clazz, FieldGetter.class)) {
            FieldGetter getterAnnotation = getter.getAnnotation(FieldGetter.class);
            getter.setAccessible(true);

            if (getter.getReturnType() == void.class) {
                throw new IllegalArgumentException("Getter " + getter.getName() + " doesn't have return value");
            }

            Object attrValue = forceInvoke(getter, target);
            if (attrValue == null) {
                if (!getter.isAnnotationPresent(Optional.class)) {
                    throw new NoSuchElementException(getterAnnotation.value());
                }
            } else {
                fields.put(getterAnnotation.value(), preprocess(getter, attrValue, String.class));
            }
        }
        return fields;
    }

    /**
     * Deserializes the mapping then injects the values into the given object.
     *
     * @param fields the mapping to deserialize
     * @param classNameDecoder the m
     * @return the target object after injecting the field values
     * @throws ClassNotFoundException if the class referenced in the mapping cannot be found
     * @throws InputMismatchException if the serialized class cannot be assigned to the target object
     * @throws InstantiationException if the serialized class doesn't have a nullary constructor
     * @throws IllegalStateException if the target class doesn't have the {@link Serializable @Serializable} annotation
     * @throws NoSuchMethodException if the parser with the appropriate parameter and return types cannot be found
     * @throws IllegalArgumentException if the setter doesn't accept an argument
     * @throws NoSuchElementException if the field mapping is missing required fields
     * @throws InvocationTargetException if the parser or setter throws an exception
     * @throws ClassCastException if the attribute can't be converted to a string
     */
    public static Object deserialize(Map<String, String> fields, ClassNameDecoder classNameDecoder)
            throws ClassNotFoundException, InputMismatchException, InstantiationException,
            IllegalStateException, NoSuchMethodException, IllegalArgumentException,
            NoSuchElementException, InvocationTargetException, ClassCastException {
        String className = fields.get(CLASS_NAME_FIELD_NAME);
        if (className == null) {
            throw new NoSuchElementException(CLASS_NAME_FIELD_NAME);
        }

        Class<?> clazz = classNameDecoder.decode(className);
        if (clazz == null) {
            throw new ClassNotFoundException(className);
        }

        Constructor<?> constructor;
        try {
            constructor = clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException _) {
            throw new InstantiationException("Class " + clazz.getName() + " doesn't have a nullary constructor");
        }
        constructor.setAccessible(true);
        Object target = forceInvoke(constructor);

        Serializable annotation = clazz.getAnnotation(Serializable.class);
        if (annotation == null) {
            throw new IllegalStateException("Class " + clazz.getName() + " is not annotated with @Serializable");
        }

        for (Method setter : findAnnotatedMethods(clazz, FieldSetter.class)) {
            FieldSetter setterAnnotation = setter.getAnnotation(FieldSetter.class);
            setter.setAccessible(true);

            if (setter.getParameterCount() != 1) {
                throw new IllegalArgumentException("Setter " + setter.getName() + " must only have one parameter");
            }

            String fieldValue = fields.get(setterAnnotation.value());
            if (fieldValue == null) {
                if (!setter.isAnnotationPresent(Optional.class)) {
                    throw new NoSuchElementException(setterAnnotation.value());
                }
            } else {
                if (!setter.isAnnotationPresent(Optional.class) || !fieldValue.isEmpty()) {
                    forceInvoke(setter, target, preprocess(setter, fieldValue, setter.getParameterTypes()[0]));
                }
            }
        }
        return target;
    }

    /**
     * Functional interface for class name encoder.
     */
    @FunctionalInterface
    public interface ClassNameEncoder {
        /**
         * Encodes the class name.
         * @param clazz the class token
         * @return the encoded class name
         */
        String encode(Class<?> clazz);
    }

    /**
     * Functional interface for class name decoder.
     */
    @FunctionalInterface
    public interface ClassNameDecoder {
        /**
         * Decodes the class name.
         * @param className the encoded class name
         * @return the decoded class token
         * @throws ClassNotFoundException if no matching class is found
         */
        Class<?> decode(String className) throws ClassNotFoundException;
    }
}
