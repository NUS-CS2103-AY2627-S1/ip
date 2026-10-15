package bob.parser;

import java.util.Locale;
import java.util.Optional;

import bob.BobException;
import bob.command.AddCommand;
import bob.command.AfterCommand;
import bob.command.BeforeCommand;
import bob.command.Command;
import bob.command.CommandWord;
import bob.command.DeleteCommand;
import bob.command.ExitCommand;
import bob.command.FindCommand;
import bob.command.ListCommand;
import bob.command.MarkCommand;
import bob.command.NextCommand;
import bob.command.OnCommand;

/**
 * Parses the lines the user types into the commands the chatbot acts on.
 *
 * <p>Every rule about how a command is <em>written</em> lives in this package, so
 * changing what the chatbot accepts is a change here and nowhere else. Carrying a
 * command out belongs to the {@link Command} classes, each handed what it needs,
 * such as a finished {@link bob.task.Task Task}, a number or a day.
 *
 * <p>This class recognizes the command a line begins with and builds it. Reading
 * what follows the command word is left to three helpers, one for each way the
 * rest of a command can be written:
 *
 * <ul>
 *   <li>{@link TaskParser}, for the description and dates of a new task;</li>
 *   <li>{@link EditParser}, for the changes an edit makes, in any order;</li>
 *   <li>{@link ArgumentParser}, for a single task number, day, count or keyword.</li>
 * </ul>
 *
 * <p>Every method is {@code static}. There is nothing for a parser to remember
 * between one line and the next — each line is understood on its own — so an
 * instance of this class would carry no state.
 */
public class Parser {

    /**
     * The no-break space, U+00A0, which looks like an ordinary space but is not one.
     *
     * <p>Text copied from a web page or a document often carries it where a space
     * was meant. {@code trim} does not remove it, and a command word followed by one
     * would not be recognized, so it is turned into an ordinary space before a line
     * is read.
     */
    private static final char NO_BREAK_SPACE = '\u00A0';

    /**
     * Returns the command one line of the user's asks for, ready to be run.
     *
     * <p>Surrounding whitespace is removed here rather than by whoever read the
     * line, because {@link bob.Bob#getResponse Bob.getResponse} is public and may
     * be handed a line by anyone. Every {@link #NO_BREAK_SPACE} is turned into an
     * ordinary space first, so that it is trimmed and separated like one.
     *
     * <p>Which word is which command is {@link CommandWord}'s own business; what
     * this method adds is the ways a line can fail to name one — nothing typed, a
     * command given text it does not take, a command word in the wrong case, or no
     * command at all — each with its own explanation rather than a shared
     * "bad command".
     *
     * <p>The {@code switch} below is the one place that names every command in a
     * list. Something has to turn a word into an object, and doing it here means
     * it happens once.
     *
     * @param input one whole line as the user typed it.
     * @return the command that line asks for.
     * @throws BobException if the line is empty, does not begin with a command the
     *                      chatbot knows, or is one it knows but cannot carry out
     *                      as written.
     */
    public static Command parse(String input) throws BobException {
        String line = input.replace(NO_BREAK_SPACE, ' ').trim();
        if (line.isEmpty()) {
            throw new BobException("You didn't type anything."
                    + "\nTell me about a task, or type " + CommandWord.LIST.getKeyword()
                    + " to see the ones I already have.");
        }
        // orElseThrow unwraps the Optional when a command was recognized, and
        // throws the explanation of why none was when it is empty.
        CommandWord word = CommandWord.parse(line).orElseThrow(() -> createUnrecognizedLineError(line));
        String arguments = word.getArgumentsIn(line);
        return switch (word) {
            case TODO -> new AddCommand(TaskParser.parseTodo(arguments));
            case DEADLINE -> new AddCommand(TaskParser.parseDeadline(arguments));
            case EVENT -> new AddCommand(TaskParser.parseEvent(arguments));
            case LIST -> new ListCommand();
            case ON -> new OnCommand(ArgumentParser.parseDay(arguments, word));
            case BEFORE -> new BeforeCommand(ArgumentParser.parseDay(arguments, word));
            case AFTER -> new AfterCommand(ArgumentParser.parseDay(arguments, word));
            case NEXT -> new NextCommand(ArgumentParser.parseCount(arguments));
            case FIND -> new FindCommand(ArgumentParser.parseKeyword(arguments));
            case MARK -> new MarkCommand(ArgumentParser.parseTaskNumber(arguments, word), true);
            case UNMARK -> new MarkCommand(ArgumentParser.parseTaskNumber(arguments, word), false);
            case DELETE -> new DeleteCommand(ArgumentParser.parseTaskNumber(arguments, word));
            case EDIT -> EditParser.parseEdit(arguments);
            case BYE -> new ExitCommand();
        };
    }

    /**
     * Returns the error to throw for a line that matches none of the commands the
     * chatbot knows, explaining the likeliest reason.
     *
     * <p>Two near misses are told apart from a line that names no command at all.
     * {@code list foo} is a command that takes nothing, given something, so the
     * user is shown the command on its own. {@code BYE} is a command word in the
     * wrong case, so the usual list of commands is followed by the lowercase word.
     * Case is still not folded silently: the chatbot's words are lowercase, and the
     * hint says so rather than hiding it.
     */
    private static BobException createUnrecognizedLineError(String line) {
        // Split at the same whitespace that CommandWord accepts after a keyword.
        String firstWord = line.split("\\p{javaWhitespace}+", 2)[0];

        // A line that begins with a command word and still matched no command can
        // only be a command that takes nothing, with something after it.
        Optional<CommandWord> sameWord = CommandWord.findByKeyword(firstWord);
        if (sameWord.isPresent()) {
            String keyword = sameWord.get().getKeyword();
            return BobException.withExample(keyword + " takes nothing after it.", keyword);
        }

        String message = "Sorry, I don't know what \"" + line + "\" means."
                + "\nTry one of: " + CommandWord.getAllKeywords();
        // Locale.ROOT lowercases the same way on every computer. The computer's own
        // language could differ: in Turkish, "I" lowercases to a dotless "ı", so LIST
        // would never become list.
        Optional<CommandWord> lowercaseWord = CommandWord.findByKeyword(firstWord.toLowerCase(Locale.ROOT));
        if (lowercaseWord.isPresent()) {
            message += "\nCommands are lowercase. Did yu mean " + lowercaseWord.get().getKeyword() + "?";
        }
        return new BobException(message);
    }
}
