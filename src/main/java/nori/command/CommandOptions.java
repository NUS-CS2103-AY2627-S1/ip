package nori.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads the option markers, such as {@code /by} and {@code /from}, out of a command's details.
 *
 * Options are located once, as whole words, and each one's value is the text
 * running up to the next option. Finding every marker in a single pass is what
 * lets a command see that an option was repeated, that one it does not accept
 * was used, or that a marker carries no value at all.
 */
final class CommandOptions {
    /** Marker introducing a deadline's due date. */
    static final String OPTION_BY = "/by";
    /** Marker introducing an event's start details. */
    static final String OPTION_FROM = "/from";
    /** Marker introducing an event's end details. */
    static final String OPTION_TO = "/to";

    /**
     * Matches an option marker standing alone as a word.
     *
     * Both boundaries reject an adjoining non-space character, so a marker is
     * recognized only where a user wrote one: {@code /to} in {@code a /to b} is
     * a marker, while the {@code /to} inside {@code a/to} is part of the text.
     */
    private static final Pattern OPTION_MARKER =
            Pattern.compile("(?<![^\s])/(?<name>from|to|by)(?![^\s])");

    /** The text before the first option marker, or all of the details when there is none. */
    private final String description;
    /** Every option the details carry, in the order they were typed, repeats included. */
    private final List<Option> options;

    /**
     * Creates a parsed view of a command's details.
     *
     * @param description the text before the first option marker.
     * @param options the options found, in the order they were typed.
     */
    private CommandOptions(String description, List<Option> options) {
        this.description = description;
        this.options = options;
    }

    /**
     * Reads the options out of a command's details.
     *
     * @param details the trimmed text after the command keyword.
     * @return the parsed details.
     */
    static CommandOptions parse(String details) {
        assert details != null : "The parser always supplies the text after the keyword.";

        List<Marker> markers = findMarkers(details);
        String parsedDescription = markers.isEmpty()
                ? details.trim()
                : details.substring(0, markers.get(0).start()).trim();

        List<Option> parsedOptions = new ArrayList<>();
        for (int index = 0; index < markers.size(); index++) {
            Marker marker = markers.get(index);
            boolean isLastMarker = index == markers.size() - 1;
            int valueEnd = isLastMarker ? details.length() : markers.get(index + 1).start();
            parsedOptions.add(new Option(marker.name(), details.substring(marker.end(), valueEnd).trim()));
        }
        return new CommandOptions(parsedDescription, List.copyOf(parsedOptions));
    }

    /**
     * Returns the text a user wrote before the first option.
     *
     * @return the description, which is empty when no text preceded the first option.
     */
    String getDescription() {
        return description;
    }

    /**
     * Returns whether an option was used at all.
     *
     * @param optionName the marker to look for, such as {@code /by}.
     * @return {@code true} when the details carry that option.
     */
    boolean hasOption(String optionName) {
        return options.stream().anyMatch(option -> option.name().equals(optionName));
    }

    /**
     * Returns the value written after an option.
     *
     * @param optionName the marker whose value to read.
     * @return the first value given for it, which is empty when the marker
     *         carries no value or was never used.
     */
    String getValue(String optionName) {
        return options.stream()
                .filter(option -> option.name().equals(optionName))
                .map(Option::value)
                .findFirst()
                .orElse("");
    }

    /**
     * Returns the markers a user typed, in order.
     *
     * @return the option names, repeats included.
     */
    List<String> getOptionNames() {
        return options.stream().map(Option::name).toList();
    }

    /**
     * Finds an option that was used more than once.
     *
     * @return the first repeated marker, or empty when every option was used at most once.
     */
    Optional<String> findRepeatedOption() {
        List<String> optionNames = getOptionNames();
        return optionNames.stream()
                .filter(optionName -> optionNames.indexOf(optionName) != optionNames.lastIndexOf(optionName))
                .findFirst();
    }

    /**
     * Finds an option that the command using these details does not accept.
     *
     * @param acceptedOptions the markers the command understands.
     * @return the first unaccepted marker, or empty when every option is accepted.
     */
    Optional<String> findUnexpectedOption(String... acceptedOptions) {
        List<String> accepted = List.of(acceptedOptions);
        return getOptionNames().stream().filter(optionName -> !accepted.contains(optionName)).findFirst();
    }

    /**
     * Locates every option marker in some command details.
     *
     * @param details the text after the command keyword.
     * @return the markers, in the order they appear.
     */
    private static List<Marker> findMarkers(String details) {
        List<Marker> markers = new ArrayList<>();
        Matcher matcher = OPTION_MARKER.matcher(details);
        while (matcher.find()) {
            markers.add(new Marker("/" + matcher.group("name"), matcher.start(), matcher.end()));
        }
        return markers;
    }

    /**
     * Records where one option marker sits inside the details.
     *
     * @param name the marker, including its leading slash.
     * @param start the index of the marker's first character.
     * @param end the index just past the marker's last character.
     */
    private record Marker(String name, int start, int end) {
    }

    /**
     * Pairs one option with the text a user wrote after it.
     *
     * @param name the marker, including its leading slash.
     * @param value the trimmed text up to the next option, empty when none was written.
     */
    private record Option(String name, String value) {
    }
}
