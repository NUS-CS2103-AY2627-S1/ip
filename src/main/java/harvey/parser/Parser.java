package harvey.parser;

import harvey.HarveyException;
import harvey.command.AddCommand;
import harvey.command.Command;
import harvey.command.CommandType;
import harvey.command.DeleteCommand;
import harvey.command.ExitCommand;
import harvey.command.FindCommand;
import harvey.command.ListCommand;
import harvey.command.MarkCommand;
import harvey.command.UnmarkCommand;
/**
 * Works out what the user meant by what they typed.
 * <p>
 * Everything here turns text into something the rest of the program can act on: a
 * {@link CommandType}, a task number, or a {@link Task}. Keeping it in one class means the
 * rules about what counts as valid input, and the phrasing used to explain a mistake,
 * are all in one file rather than mixed in with the code that carries the command out.
 * <p>
 * The methods are static because parsing needs no memory of what came before; each call
 * depends only on the text it is given.
 */
public class Parser {
    /**
     * Any run of whitespace: spaces, tabs, or a mix. Splitting on this rather than on a
     * single space means extra spaces or a pasted tab never change what a line means.
     */
    private static final String WHITESPACE = "\\s+";
    /**
     * Turns one typed line into the command it asks for.
     * <p>
     * This is the only method the main loop needs: it decides which subclass of
     * {@link Command} the line means, and hands back something that knows how to carry
     * itself out. The loop therefore never names the individual instructions.
     *
     * @param fullCommand one line as the user typed it, with or without surrounding spaces.
     * @return the command that line asks for.
     * @throws HarveyException if the line does not name a command Harvey knows, or gives
     *                         words to a command that takes none.
     */
    public static Command parse(String fullCommand) throws HarveyException {
        assert fullCommand != null : "The UI reads a line from the user, so it is never null";
        CommandType type = parseCommandType(fullCommand);
        String argument = parseArgument(fullCommand);

        switch (type) {
            case BYE:
                requireNoArgument(type, argument);
                return new ExitCommand();
            case LIST:
                requireNoArgument(type, argument);
                return new ListCommand();
            case FIND:
                return new FindCommand(argument);
            case MARK:
                return new MarkCommand(argument);
            case UNMARK:
                return new UnmarkCommand(argument);
            case DELETE:
                return new DeleteCommand(argument);
            case TODO:
            case DEADLINE:
            case EVENT:
                // The three task-creating instructions, which AddCommand tells apart itself.
                return new AddCommand(type, argument);
            default:
                // Every constant of CommandType is named above, so this is reached only if
                // one is added without deciding here what it should build.
                throw new HarveyException("I know the word \"" + type.getKeyword()
                        + "\" but not yet what to do with it.");
        }
    }

    /**
     * Refuses words typed after a command that takes none.
     * <p>
     * Ignoring them would be friendlier in the moment but can do the wrong thing: a user
     * typing {@code bye now} might have meant anything, and quitting is not a safe guess.
     *
     * @param type     the command typed.
     * @param argument everything typed after the command word.
     * @throws HarveyException if the argument is not empty.
     */
    private static void requireNoArgument(CommandType type, String argument) throws HarveyException {
        if (!argument.isEmpty()) {
            throw new HarveyException("\"" + type.getKeyword() + "\" takes nothing after it, "
                    + "so I won't guess what \"" + argument + "\" means. Just type: " + type.getExample());
        }
    }

    /**
     * Finds which command the user typed.
     *
     * @param input one line as the user typed it.
     * @return the command that line invokes.
     * @throws HarveyException if the first word is not a command Harvey knows.
     */
    private static CommandType parseCommandType(String input) throws HarveyException {
        return CommandType.fromKeyword(splitOffKeyword(input)[0]);
    }

    /**
     * Returns everything the user typed after the command word.
     * <p>
     * Each run of whitespace inside it is collapsed to a single space, so that
     * {@code read   book} and {@code read book} are stored as the same description and a
     * date typed with two spaces before its time still reads as a date.
     *
     * @param input one line as the user typed it.
     * @return the argument, or an empty string if there was none.
     */
    private static String parseArgument(String input) {
        String[] words = splitOffKeyword(input);
        return (words.length > 1) ? words[1].replaceAll(WHITESPACE, " ") : "";
    }

    /**
     * Splits a line into the command word and everything after it.
     * Every command is a single word optionally followed by arguments, so splitting
     * once is enough; the limit of 2 keeps any further spaces inside the argument.
     * <p>
     * The line is trimmed here rather than trusting the caller to have done it. The text
     * interface trims what it reads but the window does not, and a line starting with a
     * space would otherwise split into an empty keyword.
     *
     * @param input one line as the user typed it.
     * @return an array of one or two parts.
     */
    private static String[] splitOffKeyword(String input) {
        String[] words = input.trim().split(WHITESPACE, 2);
        assert words.length == 1 || words.length == 2
                : "A limit of 2 cannot produce " + words.length + " parts";
        return words;
    }

}
