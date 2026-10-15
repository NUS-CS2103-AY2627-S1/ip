package org.cs2103t.marquee.cli.command;

import static java.util.function.Predicate.not;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.cs2103t.marquee.core.DuplicateKeyException;

/**
 * A {@code Code} defines the name of a command and the flags it accepts.
 * <p>
 * Do not create new instances repeatedly. All instances are tracked and may cause collisions.
 *
 * @implNote The empty flag {@code ""} is considered the argument and cannot be used
 *           as a separate flag in any command.
 */
public final class Code {
    private static final Map<String, Code> DICTIONARY = new HashMap<>();
    private static final Set<Character> SPECIAL_CHARACTERS = Set.of('-');

    private final String name;
    private Set<String> flagNames;

    /**
     * Create a new {@code Code}.
     * <p>
     * Code names can only contain alphanumeric characters {@code a-z} {@code A-Z} {@code 0-9} or hyphens {@code -}.
     * <p>
     * All code instances must have unique command names.
     * The argument is considered to be the parameter of the empty flag {@code ""}.
     *
     * @param name the name of the code, which is what would be
     *                  used to invoke the command through the command line
     * @param flagNames list of flags that the command accepts
     * @throws NullPointerException if the name is {@code null}
     * @throws DuplicateKeyException if a code with this name already exist
     * @throws IllegalArgumentException if an invalid character is found
     */
    public Code(String name, Collection<String> flagNames)
            throws NullPointerException, DuplicateKeyException, IllegalArgumentException {
        if (name == null) {
            throw new NullPointerException("Code name cannot be null");
        }
        if (DICTIONARY.containsKey(name)) {
            throw new DuplicateKeyException("Code already exists", name);
        }
        int invalidCharCodePoint = name.chars()
                .dropWhile(c -> Character.isLetterOrDigit(c) || SPECIAL_CHARACTERS.contains((char) c))
                .findAny().orElse(-1);
        if (invalidCharCodePoint != -1) {
            throw new IllegalArgumentException("Illegal character in code name: '" + (char) invalidCharCodePoint + "'");
        }

        this.name = name;
        this.flagNames = Set.copyOf(flagNames);
        DICTIONARY.put(name, this);
    }

    /**
     * Create a new {@code Code}.
     * <p>
     * Code names can only contain alphanumeric characters {@code a-z} {@code A-Z} {@code 0-9} or hyphens {@code -}.
     * <p>
     * All code instances must have unique command names.
     * The argument is considered to be the parameter of the empty flag {@code ""}.
     *
     * @param name the name of the code, which is what would be
     *                  used to invoke the command through the command line
     * @param flagNames list of flags that the command accepts
     * @throws NullPointerException if the name is {@code null}
     * @throws DuplicateKeyException if a code with this name already exist
     * @throws IllegalArgumentException if an invalid character is found
     */
    public Code(String name, String... flagNames)
            throws NullPointerException, DuplicateKeyException, IllegalArgumentException {
        if (name == null) {
            throw new NullPointerException("Code name cannot be null");
        }
        if (DICTIONARY.containsKey(name)) {
            throw new DuplicateKeyException("Code already exists", name);
        }
        int invalidCharCodePoint = name.chars()
                .dropWhile(c -> Character.isLetterOrDigit(c) || SPECIAL_CHARACTERS.contains((char) c))
                .findAny().orElse(-1);
        if (invalidCharCodePoint != -1) {
            throw new IllegalArgumentException("Illegal character in code name: '" + (char) invalidCharCodePoint + "'");
        }

        this.name = name;
        this.flagNames = Set.of(flagNames);
        DICTIONARY.put(name, this);
    }

    /**
     * If a {@code Code} already exist with the given name, checks whether the flag names
     * are a superset of the already existing flag names, then extends the flag names for this {@code Code}.
     * Otherwise, creates a new {@code Code} with the given code name and flag names.
     * Finally returns the {@code Code} associated with this name.
     *
     * @param name the label of the tag to get
     * @param flagNames the new flag names for the code
     * @return the tag associated with {@code label}
     * @throws IllegalArgumentException if the new flag names are missing some old flags
     */
    public static Code createOrGet(String name, Collection<String> flagNames) throws IllegalArgumentException {
        Code current = getCode(name);
        if (current != null) {
            Set<String> newFlagNames = Set.copyOf(flagNames);
            if (newFlagNames.containsAll(current.flagNames)) {
                current.flagNames = newFlagNames;
                return current;
            } else {
                throw new IllegalArgumentException("Missing flag: "
                        + current.flagNames.stream()
                        .filter(not(newFlagNames::contains))
                        .collect(Collectors.joining(", "))
                );
            }
        } else {
            return new Code(name, flagNames);
        }
    }

    /**
     * If a {@code Code} already exist with the given name, checks whether the flag names
     * are a superset of the already existing flag names, then extends the flag names for this {@code Code}.
     * Otherwise, creates a new {@code Code} with the given code name and flag names.
     * Finally returns the {@code Code} associated with this name.
     *
     * @param name the label of the tag to get
     * @param flagNames the new flag names for the code
     * @return the tag associated with {@code label}
     * @throws IllegalArgumentException if the new flag names are missing some old flags
     */
    public static Code createOrGet(String name, String... flagNames) throws IllegalArgumentException {
        Code current = getCode(name);
        if (current != null) {
            Set<String> newFlagNames = Set.of(flagNames);
            if (newFlagNames.containsAll(current.flagNames)) {
                current.flagNames = newFlagNames;
                return current;
            } else {
                throw new IllegalArgumentException("Missing flag: "
                        + current.flagNames.stream()
                        .filter(not(newFlagNames::contains))
                        .collect(Collectors.joining(", "))
                );
            }
        } else {
            return new Code(name, flagNames);
        }
    }

    /**
     * Gets the {@code Code} with the given name.
     *
     * @param name the name of the {@code Code}
     * @return the {@code Code} with the given name, or {@code null} if there are none
     */
    public static Code getCode(String name) {
        return DICTIONARY.get(name);
    }

    /**
     * Returns an unmodifiable view of available command codes.
     *
     * @return an unmodifiable {@link Collection} of available command codes
     */
    public static Collection<Code> getAvailableCodes() {
        return Collections.unmodifiableCollection(DICTIONARY.values());
    }

    public String getName() {
        return name;
    }

    public Set<String> getFlagNames() {
        return flagNames;
    }

    @Override
    public String toString() {
        return this.getName() + "(" + String.join(", ", this.getFlagNames()) + ")";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Code && this.getName().equals(((Code) obj).getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, flagNames);
    }

}
