package gatsby;

import java.util.ArrayList;
import java.util.List;

import gatsby.command.Command;
import gatsby.command.CommandType;
import gatsby.command.DeadlineCommand;
import gatsby.command.DeleteCommand;
import gatsby.command.EventCommand;
import gatsby.command.ExitCommand;
import gatsby.command.FindCommand;
import gatsby.command.HelpCommand;
import gatsby.command.ListCommand;
import gatsby.command.MarkCommand;
import gatsby.command.TodoCommand;
import gatsby.command.UnmarkCommand;
import gatsby.exception.GatsbyException;
import gatsby.exception.UnknownCommandException;
import gatsby.model.TaskList;
import gatsby.parser.Parser;
import gatsby.storage.Storage;
import gatsby.ui.Ui;

/**
 * A simple command-line chatbot that stores tasks and remembers them between runs.
 *
 * Tasks are loaded from the save file at startup and saved again after every change.
 * Every expected problem (bad input, an unknown command, a corrupted save file) is
 * reported as a friendly message; the chatbot keeps running.
 */
public class Gatsby {
    /** Tasks are loaded from the save file so the list survives between runs. */
    private final TaskList tasks;

    /** Converts raw input lines into commands and payloads. */
    private final Parser parser;

    /** Creates a Gatsby application instance. */
    public Gatsby() {
        tasks = new TaskList(Storage.load());
        parser = new Parser();
    }

    /**
     * Starts Gatsby and processes commands entered through standard input.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        Gatsby gatsby = new Gatsby();
        Ui ui = new Ui();
        ui.showWelcome();
        while (ui.hasNextLine()) {
            ui.showSeparator();
            String input = ui.readLine();
            ui.showSeparator();

            if (gatsby.handleInput(input, ui, true).isExit()) {
                break;
            }
        }
        ui.close();
    }

    /**
     * Handles one line of user input and prints the response.
     *
     * All expected problems are caught here so that a single bad command never ends
     * the session. Unexpected runtime problems are caught too, because losing the
     * whole chat over an unforeseen bug would be worse than reporting it.
     *
     * @param input the raw line the user typed
     * @return the control-flow and presentation state produced by the input
     */
    private InputResult handleInput(String input, Ui ui, boolean showSeparators) {
        try {
            if (input == null || input.isBlank()) {
                ui.printLine(" You didn't type anything! Try \"todo read book\" or \"list\".");
                return new InputResult(false, true);
            }

            Parser.ParsedCommand parsedCommand = parser.parse(input);
            Command command = createCommand(parsedCommand);
            command.execute(tasks, ui);
            return new InputResult(command.isExit(), false);
        } catch (GatsbyException e) {
            ui.printLine(e.getMessage());
            return new InputResult(false, true);
        } catch (RuntimeException e) {
            ui.printLine(" Yikes, something unexpected went wrong: " + e);
            return new InputResult(false, true);
        } finally {
            if (showSeparators) {
                ui.showSeparator();
            }
        }
    }

    /**
     * Creates the executable command represented by a parsed input line.
     *
     * @param parsedCommand the parsed command and its payload
     * @return the executable command
     * @throws UnknownCommandException when the parser cannot identify the command
     */
    private Command createCommand(Parser.ParsedCommand parsedCommand) throws UnknownCommandException {
        CommandType commandType = parsedCommand.getCommand();
        String payload = parsedCommand.getPayload();
        return switch (commandType) {
            case BYE -> new ExitCommand();
            case LIST -> new ListCommand();
            case MARK -> new MarkCommand(payload);
            case UNMARK -> new UnmarkCommand(payload);
            case HELP -> new HelpCommand();
            case TODO -> new TodoCommand(payload);
            case DEADLINE -> new DeadlineCommand(payload);
            case EVENT -> new EventCommand(payload);
            case FIND -> new FindCommand(payload);
            case DELETE -> new DeleteCommand(payload);
            case UNKNOWN -> throw new UnknownCommandException(" Wait I don't recognise that yet :(\n"
                    + " I know: todo, deadline, event, list, find, mark, unmark, delete, bye.");
        };
    }

    /**
     * Processes one command and returns the user-facing response.
     *
     * @param input the command entered by the user
     * @return Gatsby's response, without console separators
     */
    public String getResponse(String input) {
        return getResponseDetails(input).getText();
    }

    /**
     * Processes one command and includes whether Gatsby rejected it.
     *
     * @param input the command entered by the user
     * @return the response text and its error state
     */
    public Response getResponseDetails(String input) {
        List<String> messages = new ArrayList<>();
        Ui responseUi = new Ui(messages::add);
        InputResult result = handleInput(input, responseUi, false);
        responseUi.close();
        return new Response(String.join(System.lineSeparator(), messages), result.isError());
    }

    /**
     * Checks whether an input line asks Gatsby to end the session.
     *
     * @param input the command entered by the user
     * @return true when the input is one of Gatsby's goodbye aliases
     */
    public boolean isExitCommand(String input) {
        return input != null && parser.parse(input).getCommand() == CommandType.BYE;
    }

    /** Holds the user-facing text and presentation state for one response. */
    public static final class Response {
        /** The text to show in the conversation. */
        private final String text;

        /** Whether Gatsby could not process the user's command. */
        private final boolean isError;

        /** Creates a response value. */
        private Response(String text, boolean isError) {
            this.text = text;
            this.isError = isError;
        }

        /**
         * Returns the response text.
         *
         * @return the text to show in the conversation
         */
        public String getText() {
            return text;
        }

        /**
         * Returns whether the response describes a rejected command.
         *
         * @return true when the response is an error
         */
        public boolean isError() {
            return isError;
        }
    }

    /** Holds the control-flow state produced while handling one input. */
    private static final class InputResult {
        /** Whether the user asked to end the session. */
        private final boolean isExit;

        /** Whether the input could not be processed. */
        private final boolean isError;

        /** Creates an input result. */
        private InputResult(boolean isExit, boolean isError) {
            this.isExit = isExit;
            this.isError = isError;
        }

        /** Returns whether the input was an exit command. */
        private boolean isExit() {
            return isExit;
        }

        /** Returns whether the input produced an error. */
        private boolean isError() {
            return isError;
        }
    }

}
