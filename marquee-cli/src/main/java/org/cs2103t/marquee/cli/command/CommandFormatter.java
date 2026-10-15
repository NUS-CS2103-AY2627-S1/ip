package org.cs2103t.marquee.cli.command;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

import org.cs2103t.marquee.core.DuplicateKeyException;

/**
 * Formatter and parser for {@code Command}
 * <h1>
 * Command format
 * <p>
 * A valid command contains the following components in order
 * <ul>
 *     <li>
 *     Command name/code name
 *     <li>
 *     Argument
 *     <li>
 *     Flag components (repeat for every flag)
 *     <ul>
 *         <li>Flag delimiter (passed to the constructor), flag name</li>
 *         <li>Flag value, defaults to empty string {@code ""}</li>
 *     </ul>
 * </ul>
 * The command components should be separated by 1 or more spaces <code>&nbsp;</code>.
 * <p>
 * To include a flag delimiter in the arguments or flag value, prepend the escape sequence
 * (defined in the constructor) before the flag delimiter.
 *
 * @see Command
 * @see Code
 */
public class CommandFormatter {
    private final String flagDelimiter;
    private final String escapeSequence;
    private final String escapedFlagDelimiter;
    private final Map<String, Code> codeByName;
    private final Pattern codePattern;
    private final Pattern flagPattern;

    /**
     * Creates a new {@code CommandFormatter} using the given flag delimiter and flag escape sequence.
     *
     * @param flagDelimiter the sequence of characters that marks the start of a flag
     * @param escapeSequence the sequence of characters that, when put in front of {@code flagDelimiter},
     *                       turn it into a literal character sequence
     * @see Code
     */
    public CommandFormatter(String flagDelimiter, String escapeSequence) {
        this.flagDelimiter = flagDelimiter;
        this.escapeSequence = escapeSequence;
        this.escapedFlagDelimiter = escapeSequence + flagDelimiter;
        this.codeByName = Code.getAvailableCodes().stream()
                .collect(Collectors.toUnmodifiableMap(
                        Code::getName,
                        code -> code
                ));
        this.codePattern = Pattern.compile(
                "\\G\\s*(?<code>"
                        + String.join("|", codeByName.keySet())
                        + ")(?:$|\\s+)"
        );
        this.flagPattern = Pattern.compile(
                "\\s*(?:(?<flagDelimiterEscaped>" + Pattern.quote(this.escapedFlagDelimiter) + ")|"
                        + Pattern.quote(this.flagDelimiter) + "(?<flagName>\\S*)(?:$|\\s+))"
        );
    }

    public String getEscapeSequence() {
        return escapeSequence;
    }

    public String getFlagDelimiter() {
        return flagDelimiter;
    }

    protected Map<String, Code> getCodeByName() {
        return codeByName;
    }

    protected Pattern getCodePattern() {
        return codePattern;
    }

    protected Pattern getFlagPattern() {
        return flagPattern;
    }

    /**
     * Parses the given string as a {@code Command} according to the class-defined format.
     *
     * @param input the string to parse
     * @return the parsed {@code Command}
     * @throws IllegalArgumentException if there are no supported command names found
     * @throws NoSuchElementException if an unrecognized flag is found
     * @throws DuplicateKeyException if a duplicate flag is found
     * @implSpec Subclasses must call this method first, only after this method
     *           throws an {@code IllegalArgumentException} can the subclass continue parsing the command
     */
    public Command parseCommand(String input)
            throws NoSuchElementException, DuplicateKeyException, IllegalArgumentException {
        Matcher codeMatcher = codePattern.matcher(input);
        if (codeByName.isEmpty() || !codeMatcher.find()) {
            throw new IllegalArgumentException("Unknown command");
        }
        Code code = codeByName.get(codeMatcher.group("code"));

        if (codeMatcher.end() == input.length()) {
            return new Command(code, Map.of());
        } else {
            Matcher flagMatcher = flagPattern.matcher(input);
            Map<String, String> parameters = new HashMap<>();
            StringBuilder argument = new StringBuilder();

            String lastFlagName = "";
            int i = codeMatcher.end();
            for (; flagMatcher.find(i); i = flagMatcher.end()) {
                argument.append(input, i, flagMatcher.start());

                if (flagMatcher.group("flagDelimiterEscaped") != null) {
                    argument.append(input, flagMatcher.start(), flagMatcher.start("flagDelimiterEscaped"))
                            .append(escapedFlagDelimiter);
                    continue;
                }

                parameters.put(lastFlagName, argument.toString());
                argument.setLength(0);
                lastFlagName = flagMatcher.group("flagName");

                if (parameters.containsKey(lastFlagName)) {
                    throw new DuplicateKeyException("Duplicate flag: " + lastFlagName, lastFlagName);
                }
            }
            parameters.put(lastFlagName, argument.append(input, i, input.length()).toString());
            if (!code.getFlagNames().contains("") && parameters.get("").isEmpty()) {
                parameters.remove("");
            }
            return new Command(code, parameters);
        }
    }


    /**
     * Formats the given {@code Command} according to the class-defined format.
     *
     * @param command the {@link Command} to format
     * @return the formatted command string
     */
    public String formatCommand(Command command) {
        return Stream.concat(
                Stream.of(command.getCode().getName(), command.hasArgument() ? command.getArgument() : ""),
                command.getParameters().entrySet().stream()
                        .flatMap(flag -> Stream.of(flag.getKey(), flag.getValue()))
        )
                .map(str -> str.replace(flagDelimiter, escapedFlagDelimiter))
                .gather(Gatherers.windowFixed(2))
                .map(pair -> pair.getLast().isEmpty()
                        ? pair.getFirst()
                        : pair.getFirst() + " " + pair.getLast())
                .collect(Collectors.joining(" -"));
    }
}
