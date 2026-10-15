package zabud.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import zabud.commands.impl.ByeCommand;
import zabud.commands.impl.DeadlineCommand;
import zabud.commands.impl.DeleteCommand;
import zabud.commands.impl.EventCommand;
import zabud.commands.impl.FindCommand;
import zabud.commands.impl.HelpCommand;
import zabud.commands.impl.ListCommand;
import zabud.commands.impl.MarkCommand;
import zabud.commands.impl.TodoCommand;
import zabud.commands.impl.UnknownCommand;
import zabud.commands.impl.UnmarkCommand;
import zabud.session.Session;

/**
 * Converts raw command lines into ordered, named values.
 */
public final class Parser {
    /**
     * Name assigned to text between the command and the first named token.
     */
    public static final String DEFAULT_TOKEN = "default";

    private static final String COMMAND_TOKEN = "command";
    private static final Pattern NAMED_TOKEN = Pattern.compile("(?<!\\S)/([A-Za-z][A-Za-z0-9-]*)(?=\\s|$)");
    private static final Handler BYE = createHandler(new ByeCommand());
    private static final Handler DEADLINE = createHandler(new DeadlineCommand());
    private static final Handler DELETE = createHandler(new DeleteCommand());
    private static final Handler EVENT = createHandler(new EventCommand());
    private static final Handler FIND = createHandler(new FindCommand());
    private static final Handler HELP = createHandler(new HelpCommand());
    private static final Handler LIST = createHandler(new ListCommand());
    private static final Handler MARK = createHandler(new MarkCommand());
    private static final Handler TODO = createHandler(new TodoCommand());
    private static final Handler UNKNOWN = createHandler(new UnknownCommand());
    private static final Handler UNMARK = createHandler(new UnmarkCommand());

    /**
     * Prevents instantiation of this utility class.
     */
    private Parser() {
    }

    /**
     * Splits a command line into its command, default value, and named values.
     * Named values begin with a slash-prefixed name, such as {@code /by}.
     *
     * @param input complete command line entered by the user.
     * @return parser results in their original command-line order.
     */
    public static List<ParsedToken> parse(String input) {
        String normalized = input.trim();
        int commandEnd = firstWhitespace(normalized);
        String command = commandEnd < 0 ? normalized : normalized.substring(0, commandEnd);
        String arguments = commandEnd < 0 ? "" : normalized.substring(commandEnd).trim();

        List<ParsedToken> parsedTokens = new ArrayList<>();
        parsedTokens.add(new ParsedToken(COMMAND_TOKEN, command));

        Matcher matcher = NAMED_TOKEN.matcher(arguments);
        int valueStart = 0;
        String name = DEFAULT_TOKEN;
        while (matcher.find()) {
            parsedTokens.add(new ParsedToken(name, arguments.substring(valueStart, matcher.start())));
            name = matcher.group(1);
            valueStart = matcher.end();
        }
        parsedTokens.add(new ParsedToken(name, arguments.substring(valueStart)));
        return List.copyOf(parsedTokens);
    }

    /**
     * Checks the parsed values using the selected command's validator.
     *
     * @param parsedTokens complete parser result, including the command entry.
     * @param session current application session.
     * @return whether the command can be built safely.
     */
    public static boolean isValid(List<ParsedToken> parsedTokens, Session session) {
        if (parsedTokens.isEmpty() || !parsedTokens.getFirst().name().equals(COMMAND_TOKEN)) {
            return false;
        }
        Handler handler = selectHandler(parsedTokens.getFirst().value());
        return handler.validatable().isValid(getArguments(parsedTokens), session);
    }

    /**
     * Builds the command selected by a validated parser result.
     *
     * @param parsedTokens validated parser result, including the command entry.
     * @param session current application session.
     * @return executable command selected by the command name.
     */
    public static Command build(List<ParsedToken> parsedTokens, Session session) {
        assert isValid(parsedTokens, session) : "Only validated commands can be built";
        Handler handler = selectHandler(parsedTokens.getFirst().value());
        return handler.buildable().build(getArguments(parsedTokens), session);
    }

    /**
     * Parses, validates, builds, and executes one line of input.
     *
     * @param input complete command line entered by the user.
     * @param session current application session.
     * @return whether the application should continue running.
     */
    public static boolean invoke(String input, Session session) {
        List<ParsedToken> parsedTokens = parse(input);
        if (!isValid(parsedTokens, session)) {
            System.out.println(hint(parsedTokens));
            return true;
        }
        Command command = build(parsedTokens, session);
        command.execute();
        return !command.exitsApplication();
    }

    /**
     * Returns the values following the command entry.
     */
    private static List<ParsedToken> getArguments(List<ParsedToken> parsedTokens) {
        return parsedTokens.subList(1, parsedTokens.size());
    }

    /**
     * Returns validation guidance for a parser result.
     *
     * @param parsedTokens complete parser result, including the command entry.
     * @return guidance supplied by the selected command definition.
     */
    public static String hint(List<ParsedToken> parsedTokens) {
        if (parsedTokens.isEmpty() || !parsedTokens.getFirst().name().equals(COMMAND_TOKEN)) {
            return UNKNOWN.validatable().hint();
        }
        return selectHandler(parsedTokens.getFirst().value()).validatable().hint();
    }

    /**
     * Selects the command handler belonging to a command name.
     */
    private static Handler selectHandler(String command) {
        return switch (command) {
            case ByeCommand.COMMAND -> BYE;
            case DeadlineCommand.COMMAND -> DEADLINE;
            case DeleteCommand.COMMAND -> DELETE;
            case EventCommand.COMMAND -> EVENT;
            case FindCommand.COMMAND -> FIND;
            case HelpCommand.COMMAND -> HELP;
            case ListCommand.COMMAND -> LIST;
            case MarkCommand.COMMAND -> MARK;
            case TodoCommand.COMMAND -> TODO;
            case UnmarkCommand.COMMAND -> UNMARK;
            default -> UNKNOWN;
        };
    }

    /**
     * Creates a parser handler from one command type implementing both contracts.
     */
    private static <T extends Validatable & Buildable> Handler createHandler(T command) {
        return new Handler(command, command);
    }

    /**
     * Returns the index of the first whitespace character, if one exists.
     */
    private static int firstWhitespace(String input) {
        for (int index = 0; index < input.length(); index++) {
            if (Character.isWhitespace(input.charAt(index))) {
                return index;
            }
        }
        return -1;
    }

    /**
     * Associates one command type's validation and construction contracts.
     */
    private record Handler(Validatable validatable, Buildable buildable) {
    }
}
